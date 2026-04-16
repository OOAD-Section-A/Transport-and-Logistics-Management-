package entities;

import java.util.Map;
import java.util.Objects;
import exceptions.*;

/**
 * LiveTracking Entity
 * PATTERN: Observer + Adapter
 * Tracks vehicle location and detects anomalies
 */
public final class LiveTracking implements ISensorPhysicalExceptionSource, IConnectivityExceptionSource {
    public interface Port { TrackingSnapshot track(String vehicleId); void registerHandler(SCMExceptionHandler handler); }
    public interface TrackingFeed { Telemetry fetch(String vehicleId); }
    public interface LegacyGpsClient { LegacyPoint readPoint(String vehicleId); }
    public record LegacyPoint(double latitude, double longitude, int batteryPct,
                              long ageMs, double routeDeviationKm, boolean geofenceBreach) {}
    public static final class LegacyGpsAdapter implements TrackingFeed {
        private final LegacyGpsClient legacyClient;
        public LegacyGpsAdapter(LegacyGpsClient legacyClient) { this.legacyClient = Objects.requireNonNull(legacyClient, "legacyClient"); }
        @Override
        public Telemetry fetch(String vehicleId) {
            LegacyPoint point = legacyClient.readPoint(vehicleId);
            if (point == null) {
                return null;
            }
            return new Telemetry(point.latitude(), point.longitude(), point.batteryPct(),
                    point.ageMs(), point.routeDeviationKm(), point.geofenceBreach());
        }
    }
    public interface TrackingPublisher { void publish(TrackingSnapshot snapshot); }
    public record Telemetry(double latitude, double longitude, int batteryPct,
                            long ageMs, double routeDeviationKm, boolean geofenceBreach) {}
    public record TrackingSnapshot(String vehicleId, double latitude, double longitude,
                                   boolean lowConfidence) {}
    private static final Map<Integer, ExceptionSpec> CATALOG = Map.of(
            58, new ExceptionSpec("TRAFFIC_DATA_UNAVAILABLE", Severity.WARNING,
                    "Real-Time Delivery", "Live traffic data feed is unavailable."),
            59, new ExceptionSpec("GPS_SIGNAL_LOST", Severity.MAJOR,
                    "Real-Time Delivery / Transport and Logistics Management", "GPS signal from delivery vehicle is lost."),
            401, new ExceptionSpec("GEOFENCE_BREACH", Severity.MAJOR,
                    "Real-Time Delivery", "Delivery vehicle has left the permitted geofence."),
            403, new ExceptionSpec("ROUTE_DEVIATION_DETECTED", Severity.MAJOR,
                    "Real-Time Delivery", "Vehicle is deviating from the assigned route."),
            405, new ExceptionSpec("LOW_DEVICE_BATTERY", Severity.WARNING,
                    "Real-Time Delivery", "Driver's tracking device battery is critically low."),
            406, new ExceptionSpec("STALE_LOCATION_DATA", Severity.MINOR,
                    "Real-Time Delivery", "Location data has not been updated within the expected window.")
    );
    private final TrackingFeed trackingFeed;
    private final TrackingPublisher trackingPublisher;
    private final long maxStaleMs;
    private final double maxDeviationKm;
    private SCMExceptionHandler handler;
    public static Port create(TrackingFeed trackingFeed, TrackingPublisher trackingPublisher,
                              long maxStaleMs, double maxDeviationKm) {
        LiveTracking impl = new LiveTracking(trackingFeed, trackingPublisher, maxStaleMs, maxDeviationKm);
        return new Port() {
            @Override public TrackingSnapshot track(String vehicleId) { return impl.track(vehicleId); }
            @Override public void registerHandler(SCMExceptionHandler handler) { impl.registerHandler(handler); }
        };
    }
    private LiveTracking(TrackingFeed trackingFeed, TrackingPublisher trackingPublisher,
                         long maxStaleMs, double maxDeviationKm) {
        this.trackingFeed = Objects.requireNonNull(trackingFeed, "trackingFeed");
        this.trackingPublisher = Objects.requireNonNull(trackingPublisher, "trackingPublisher");
        this.maxStaleMs = maxStaleMs;
        this.maxDeviationKm = maxDeviationKm;
    }
    public TrackingSnapshot track(String vehicleId) {
        try {
            Telemetry telemetry = trackingFeed.fetch(vehicleId);
            if (telemetry == null) {
                firePartialConnectivity(59, "GPS_TRACKER", "GPS fix unavailable");
                return new TrackingSnapshot(vehicleId, 0.0, 0.0, true);
            }
            boolean lowConfidence = false;
            if (telemetry.ageMs() > maxStaleMs) {
                fireDeviceWarning(406, vehicleId, "GPS_TRACKER", "Location age is " + telemetry.ageMs() + "ms");
                lowConfidence = true;
            }
            if (telemetry.batteryPct() <= 15) {
                fireDeviceWarning(405, vehicleId, "GPS_TRACKER", "Battery at " + telemetry.batteryPct() + "%");
            }
            if (telemetry.geofenceBreach()) {
                fireSafetyAlert(401, vehicleId, telemetry.latitude(), telemetry.longitude(), "Vehicle outside geofence.");
                return publishSnapshot(vehicleId, telemetry, true);
            }
            if (Math.abs(telemetry.routeDeviationKm()) > maxDeviationKm) {
                fireSafetyAlert(403, vehicleId, telemetry.latitude(), telemetry.longitude(),
                        "Route deviation is " + telemetry.routeDeviationKm() + " km");
                return publishSnapshot(vehicleId, telemetry, true);
            }
            return publishSnapshot(vehicleId, telemetry, lowConfidence);
        } catch (RuntimeException ex) {
            raise(0, Severity.MAJOR, "Unregistered exception in LiveTracking: " + ex.getMessage());
            return new TrackingSnapshot(vehicleId, 0.0, 0.0, true);
        }
    }
    public void registerHandler(SCMExceptionHandler handler) {
        this.handler = handler;
    }
    private TrackingSnapshot publishSnapshot(String vehicleId, Telemetry telemetry, boolean lowConfidence) {
        TrackingSnapshot snapshot = new TrackingSnapshot(vehicleId, telemetry.latitude(), telemetry.longitude(), lowConfidence);
        trackingPublisher.publish(snapshot);
        return snapshot;
    }
    private void raise(int id, Severity fallback, String detail) {
        SCMEvents.emit(CATALOG, handler, id, fallback, detail,
                "Real-Time Delivery", "Unregistered live-tracking exception.");
    }
    @Override public void fireSafetyAlert(int exceptionId, String vehicleOrAssetId, double latitude, double longitude, String detail) { raise(exceptionId, Severity.MAJOR, "asset=" + vehicleOrAssetId + " lat=" + latitude + " lon=" + longitude + " detail=" + detail); }
    @Override public void fireDeviceWarning(int exceptionId, String deviceId, String deviceType, String condition) { raise(exceptionId, Severity.MINOR, "deviceId=" + deviceId + " deviceType=" + deviceType + " condition=" + condition); }
    @Override public void fireScanError(int exceptionId, String scannerLocation, String tagOrBarcode, String reason) { raise(exceptionId, Severity.MAJOR, "scanner=" + scannerLocation + " tag=" + tagOrBarcode + " reason=" + reason); }
    @Override public void fireConnectionFailed(int exceptionId, String targetSystem, String host) { raise(exceptionId, Severity.MAJOR, "target=" + targetSystem + " host=" + host); }
    @Override public void fireTimeout(int exceptionId, String targetSystem, int timeoutMs) { raise(exceptionId, Severity.MAJOR, "target=" + targetSystem + " timeoutMs=" + timeoutMs); }
    @Override public void fireServiceUnavailable(int exceptionId, String targetSystem, String reason) { raise(exceptionId, Severity.MAJOR, "target=" + targetSystem + " reason=" + reason); }
    @Override public void firePartialConnectivity(int exceptionId, String targetSystem, String degradedCapability) { raise(exceptionId, Severity.WARNING, "target=" + targetSystem + " degradedCapability=" + degradedCapability); }
}

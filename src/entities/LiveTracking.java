package entities;

import java.util.Objects;
import exceptions.*;
import com.scm.factory.SCMExceptionFactory;
import com.scm.subsystems.TransportLogisticsSubsystem;

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
    private final TrackingFeed trackingFeed;
    private final TrackingPublisher trackingPublisher;
    private final long maxStaleMs;
    private final double maxDeviationKm;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;
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
                exceptions.onGpsSignalLost(vehicleId);
                return new TrackingSnapshot(vehicleId, 0.0, 0.0, true);
            }
            boolean lowConfidence = false;
            if (telemetry.ageMs() > maxStaleMs) {
                int timeoutMs = telemetry.ageMs() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) telemetry.ageMs();
                exceptions.onCarrierApiTimeout("GPS_TRACKER", timeoutMs);
                lowConfidence = true;
            }
            if (telemetry.batteryPct() <= 15) {
                lowConfidence = true;
            }
            if (telemetry.geofenceBreach()) {
                return publishSnapshot(vehicleId, telemetry, true);
            }
            if (Math.abs(telemetry.routeDeviationKm()) > maxDeviationKm) {
                return publishSnapshot(vehicleId, telemetry, true);
            }
            return publishSnapshot(vehicleId, telemetry, lowConfidence);
        } catch (RuntimeException ex) {
            com.scm.handler.SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", "LiveTracking failed: " + ex.getMessage())
            );
            return new TrackingSnapshot(vehicleId, 0.0, 0.0, true);
        }
    }
    public void registerHandler(SCMExceptionHandler handler) {
        // No-op: real handler is managed by the exception module singleton.
    }
    private TrackingSnapshot publishSnapshot(String vehicleId, Telemetry telemetry, boolean lowConfidence) {
        TrackingSnapshot snapshot = new TrackingSnapshot(vehicleId, telemetry.latitude(), telemetry.longitude(), lowConfidence);
        trackingPublisher.publish(snapshot);
        return snapshot;
    }
    @Override public void fireSafetyAlert(int exceptionId, String vehicleOrAssetId, double latitude, double longitude, String detail) { exceptions.onGpsSignalLost(vehicleOrAssetId); }
    @Override public void fireDeviceWarning(int exceptionId, String deviceId, String deviceType, String condition) { exceptions.onCarrierApiTimeout(deviceType, 3000); }
    @Override public void fireScanError(int exceptionId, String scannerLocation, String tagOrBarcode, String reason) { exceptions.onCarrierApiTimeout("SCANNER", 3000); }
    @Override public void fireConnectionFailed(int exceptionId, String targetSystem, String host) { exceptions.onCarrierApiTimeout(targetSystem, 3000); }
    @Override public void fireTimeout(int exceptionId, String targetSystem, int timeoutMs) { exceptions.onCarrierApiTimeout(targetSystem, timeoutMs); }
    @Override public void fireServiceUnavailable(int exceptionId, String targetSystem, String reason) { exceptions.onCarrierApiTimeout(targetSystem, 3000); }
    @Override public void firePartialConnectivity(int exceptionId, String targetSystem, String degradedCapability) { exceptions.onGpsSignalLost(targetSystem); }
}

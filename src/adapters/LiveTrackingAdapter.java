package adapters;

import entities.LiveTracking;
import entities.LiveTracking.*;
import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.observer.DeliveryEventType;

import java.util.Random;

public class LiveTrackingAdapter {

    private final LiveTracking.Port liveTracking;
    private final DeliveryMonitoringFacadeDB facade;

    // ── existing no-arg constructor — mock mode ──
    public LiveTrackingAdapter() {
        this.facade = null;

        Random rand = new Random();

        LiveTracking.TrackingFeed feed = vehicleId -> {
            double lat = 40.7128 + rand.nextDouble() * 0.1;
            double lon = -74.0060 + rand.nextDouble() * 0.1;
            int battery = 50 + rand.nextInt(50);
            long age = rand.nextLong(5000);
            double deviation = rand.nextDouble() * 2;
            boolean breach = rand.nextDouble() < 0.1;

            return new Telemetry(lat, lon, battery, age, deviation, breach);
        };

        LiveTracking.TrackingPublisher publisher = snapshot ->
            System.out.println("  📍 Published: " + snapshot.vehicleId()
                + " (" + snapshot.latitude() + ", " + snapshot.longitude() + ")");

        this.liveTracking = LiveTracking.create(feed, publisher, 5000, 5.0);
    }

    // ── facade-driven constructor ──
    public LiveTrackingAdapter(DeliveryMonitoringFacadeDB facade) {
        this.facade = facade;

        LiveTracking.TrackingPublisher publisher = snapshot ->
            System.out.println("📍 Updated: " + snapshot.vehicleId()
                + " (" + snapshot.latitude() + ", " + snapshot.longitude() + ")");

        // No feed provided → tracking becomes event-driven externally
        this.liveTracking = LiveTracking.create(
            vehicleId -> null,   // feed not used in facade mode
            publisher,
            5000,
            5.0
        );

        subscribeToFacade();
    }

    private void subscribeToFacade() {
        if (facade == null) return;

        facade.subscribeToEvents(
            DeliveryEventType.LOCATION_UPDATED,
            (eventType, data) -> {
                try {
                    String riderId = (String) data.get("riderId");
                    double lat = (double) data.get("latitude");
                    double lon = (double) data.get("longitude");

                    handleIncomingUpdate(riderId, lat, lon);

                } catch (Exception e) {
                    System.err.println("❌ Location event error: " + e.getMessage());
                }
            }
        );
    }

    // ✔ FIX: no process() — LiveTracking is pull-based
    private void handleIncomingUpdate(String vehicleId, double lat, double lon) {
        System.out.println("📡 Incoming update for " + vehicleId +
                " (" + lat + ", " + lon + ")");

        // nothing to push into LiveTracking
        // LiveTracking itself pulls via track()
    }

    // ✔ correct API usage
    public TrackingSnapshot trackVehicle(String vehicleId) {
        return liveTracking.track(vehicleId);
    }

    public void registerHandler(exceptions.SCMExceptionHandler handler) {
        liveTracking.registerHandler(handler);
    }
}
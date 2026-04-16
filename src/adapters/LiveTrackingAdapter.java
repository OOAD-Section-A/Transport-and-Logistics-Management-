package adapters;

import entities.LiveTracking;
import entities.LiveTracking.*;
import java.util.Random;

/**
 * LiveTracking Adapter
 * Integrates LiveTracking with Transport Management System
 * LOW COUPLING: Implements standard interfaces
 */
public class LiveTrackingAdapter {
    private final LiveTracking.Port liveTracking;
    
    public LiveTrackingAdapter() {
        // Create with mock implementations
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
            System.out.println("  📍 Published: " + snapshot.vehicleId() + 
                             " at (" + String.format("%.4f", snapshot.latitude()) + ", " + 
                             String.format("%.4f", snapshot.longitude()) + ")");
        
        this.liveTracking = LiveTracking.create(feed, publisher, 5000, 5.0);
    }
    
    public TrackingSnapshot trackVehicle(String vehicleId) {
        return liveTracking.track(vehicleId);
    }
    
    public void registerHandler(exceptions.SCMExceptionHandler handler) {
        liveTracking.registerHandler(handler);
    }
}

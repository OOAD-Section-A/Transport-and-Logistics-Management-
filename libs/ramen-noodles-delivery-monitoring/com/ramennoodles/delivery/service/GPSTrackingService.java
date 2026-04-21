package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * Service for processing GPS pings and maintaining live location data.
 * Core component: Live GPS Tracker
 * 
 * Handles high-frequency GPS data from rider devices.
 */
public class GPSTrackingService {
    
    // In-memory storage (simulating a database)
    private final Map<String, List<GPSPing>> pingsByRider;      // rider_id -> pings
    private final Map<String, GPSPing> latestPingByRider;       // rider_id -> latest ping
    private final Map<String, Device> deviceRegistry;            // device_id -> Device
    private final DeliveryEventManager eventManager;

    public GPSTrackingService(DeliveryEventManager eventManager) {
        this.pingsByRider = new HashMap<>();
        this.latestPingByRider = new HashMap<>();
        this.deviceRegistry = new HashMap<>();
        this.eventManager = eventManager;
    }

    /**
     * Registers a device for a rider.
     */
    public Device registerDevice(String riderId) {
        Device device = Device.register(null, riderId);
        deviceRegistry.put(device.getDeviceId(), device);
        return device;
    }

    /**
     * Processes an incoming GPS ping from a device.
     * This is the main entry point for location data.
     */
    public GPSPing processGPSPing(String deviceId, double latitude, double longitude) {
        Device device = deviceRegistry.get(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("Unknown device: " + deviceId);
        }

        // Create and store the ping
        GPSPing ping = device.sendGPSPing(latitude, longitude);
        String riderId = device.getRiderId();

        pingsByRider.computeIfAbsent(riderId, k -> new ArrayList<>()).add(ping);
        latestPingByRider.put(riderId, ping);

        // Publish location update event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("riderId", riderId);
        eventData.put("latitude", latitude);
        eventData.put("longitude", longitude);
        eventData.put("pingId", ping.getPingId());
        eventData.put("timestamp", ping.getTimestampAsLocal().toString()); // Use LocalDateTime
        eventManager.publish(DeliveryEventType.LOCATION_UPDATED, eventData);

        return ping;
    }

    /**
     * Gets the latest known position of a rider.
     */
    public GPSPing getLatestPing(String riderId) {
        return latestPingByRider.get(riderId);
    }

    /**
     * Gets location history for a rider.
     */
    public List<GPSPing> getLocationHistory(String riderId) {
        return pingsByRider.getOrDefault(riderId, Collections.emptyList());
    }

    /**
     * Batch insert multiple pings (for high-frequency data).
     */
    public void batchInsert(List<GPSPing> pings) {
        for (GPSPing ping : pings) {
            String riderId = ping.getRiderId();
            pingsByRider.computeIfAbsent(riderId, k -> new ArrayList<>()).add(ping);
            latestPingByRider.put(riderId, ping);
        }
    }

    /**
     * Gets all registered devices.
     */
    public Map<String, Device> getDeviceRegistry() {
        return Collections.unmodifiableMap(deviceRegistry);
    }

    /**
     * Gets all riders currently being tracked.
     */
    public Set<String> getTrackedRiders() {
        return latestPingByRider.keySet();
    }
}

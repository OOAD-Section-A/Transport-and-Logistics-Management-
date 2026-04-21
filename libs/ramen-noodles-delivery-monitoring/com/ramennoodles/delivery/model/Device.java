package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a GPS tracking device attached to a rider's vehicle.
 * Shared with: CenterDiv (Transport & Logistics) subsystem.
 * 
 * Relationships:
 *  - Device belongs to 1 Rider
 *  - Device generates 1..* GPSPings
 */
public class Device {
    private String deviceId;           // PK
    private String riderId;            // FK -> Rider
    private LocalDateTime lastSeenTimestamp;
    private boolean online;

    // --- Factory Method ---
    public static Device register(String deviceId, String riderId) {
        Device d = new Device();
        d.deviceId = (deviceId != null) ? deviceId
                : "DEV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        d.riderId = riderId;
        d.lastSeenTimestamp = LocalDateTime.now();
        d.online = true;
        return d;
    }

    // --- Business Methods ---
    public void updateLastSeen() {
        this.lastSeenTimestamp = LocalDateTime.now();
    }

    public boolean isOnline() {
        return online;
    }

    public GPSPing sendGPSPing(double latitude, double longitude) {
        updateLastSeen();
        return GPSPing.save(latitude, longitude, this.deviceId, this.riderId);
    }

    public void goOffline() {
        this.online = false;
    }

    public void goOnline() {
        this.online = true;
        updateLastSeen();
    }

    // --- Getters & Setters ---
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public LocalDateTime getLastSeenTimestamp() { return lastSeenTimestamp; }

    @Override
    public String toString() {
        return String.format("Device[%s, rider=%s, online=%s]", deviceId, riderId, online);
    }
}

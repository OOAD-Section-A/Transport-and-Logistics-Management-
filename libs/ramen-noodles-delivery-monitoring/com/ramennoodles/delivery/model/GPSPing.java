package com.ramennoodles.delivery.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Represents a single GPS ping recorded from a device.
 * High-frequency writes — GPS ping every few seconds.
 *
 * Shared with: CenterDiv (Transport & Logistics) subsystem.
 *
 * Relationships:
 *  - GPSPing belongs to 1 Device
 *  - GPSPing belongs to 1 Rider
 *  - Append-only: never edited or deleted
 */
public class GPSPing {
    private String pingId;         // PK
    private String deviceId;       // FK -> Device
    private String riderId;        // FK -> Rider
    private double latitude;
    private double longitude;
    private Instant timestamp;     // Changed to Instant for database compatibility

    // --- Factory Method ---
    public static GPSPing save(double latitude, double longitude, String deviceId, String riderId) {
        GPSPing ping = new GPSPing();
        ping.pingId = "GPS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ping.deviceId = deviceId;
        ping.riderId = riderId;
        ping.latitude = latitude;
        ping.longitude = longitude;
        ping.timestamp = Instant.now();
        return ping;
    }

    // --- Constructor for Database Integration ---
    public GPSPing(String pingId, String deviceId, String riderId,
                   double latitude, double longitude, Instant timestamp) {
        this.pingId = pingId;
        this.deviceId = deviceId;
        this.riderId = riderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // --- Default Constructor ---
    public GPSPing() {
        this.timestamp = Instant.now();
    }

    // --- Business Methods ---
    public double calculateDistance(GPSPing other) {
        Coordinate thisCoord = new Coordinate(this.latitude, this.longitude);
        Coordinate otherCoord = new Coordinate(other.latitude, other.longitude);
        return thisCoord.distanceTo(otherCoord);
    }

    public Coordinate toCoordinate() {
        return new Coordinate(latitude, longitude);
    }

    // --- Getters & Setters ---
    public String getPingId() { return pingId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public Instant getTimestamp() { return timestamp; }

    public LocalDateTime getTimestampAsLocal() {
        return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        return String.format("GPSPing[%s, rider=%s, (%.6f, %.6f), %s]",
                pingId, riderId, latitude, longitude, timestamp);
    }
}

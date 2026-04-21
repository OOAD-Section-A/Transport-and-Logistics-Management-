package com.ramennoodles.delivery.model;

import com.ramennoodles.delivery.enums.RiderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a delivery rider/driver.
 * Shared with: CenterDiv (Transport & Logistics) subsystem.
 * 
 * Relationships:
 *  - Rider owns 1..* Devices
 *  - Rider generates 1..* GPSPings
 *  - Rider delivers 1..* Orders
 */
public class Rider {
    private String riderId;       // PK
    private String name;
    private String phone;
    private String vehicleType;
    private RiderStatus status;
    private LocalDateTime createdAt;

    // --- Factory Method ---
    public static Rider createProfile(String name, String phone, String vehicleType) {
        Rider r = new Rider();
        r.riderId = "RDR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        r.name = name;
        r.phone = phone;
        r.vehicleType = vehicleType;
        r.status = RiderStatus.OFFLINE;
        r.createdAt = LocalDateTime.now();
        return r;
    }

    // --- Business Methods ---
    public void updateStatus(RiderStatus status) {
        this.status = status;
    }

    public void activate() {
        this.status = RiderStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = RiderStatus.OFFLINE;
    }

    public Device registerDevice(String deviceId) {
        return Device.register(deviceId, this.riderId);
    }

    public boolean isAvailable() {
        return this.status == RiderStatus.ACTIVE;
    }

    // --- Getters & Setters ---
    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public RiderStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Rider[%s, %s, %s, %s]", riderId, name, vehicleType, status);
    }
}

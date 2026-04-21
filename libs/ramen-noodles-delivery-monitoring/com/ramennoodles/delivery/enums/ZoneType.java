package com.ramennoodles.delivery.enums;

/**
 * Represents the type of a geofence zone.
 * Used by: GeofenceZone
 */
public enum ZoneType {
    PICKUP("Pickup location zone"),
    DROPOFF("Dropoff/delivery location zone"),
    WAREHOUSE("Warehouse or hub zone"),
    RESTRICTED("Restricted or no-go zone");

    private final String description;

    ZoneType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

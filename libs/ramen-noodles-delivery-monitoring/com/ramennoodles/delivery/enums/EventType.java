package com.ramennoodles.delivery.enums;

/**
 * Represents the type of geofence event triggered.
 * Used by: GeofenceEvent
 */
public enum EventType {
    ENTRY("Rider entered the geofence zone"),
    EXIT("Rider exited the geofence zone");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

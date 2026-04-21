package com.ramennoodles.delivery.enums;

/**
 * Represents the availability status of a delivery rider.
 * Used by: Rider
 */
public enum RiderStatus {
    ACTIVE("Rider is active and available"),
    OFFLINE("Rider is offline"),
    ON_DELIVERY("Rider is currently on a delivery"),
    ON_BREAK("Rider is on a break");

    private final String description;

    RiderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

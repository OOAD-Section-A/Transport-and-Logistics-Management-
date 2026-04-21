package com.ramennoodles.delivery.enums;

/**
 * Represents the delivery status of a notification message.
 * Used by: NotificationLog
 */
public enum DeliveryStatus {
    PENDING("Notification pending delivery"),
    SENT("Notification has been sent"),
    DELIVERED("Notification delivered to recipient"),
    FAILED("Notification delivery failed"),
    RETRYING("Retrying notification delivery");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

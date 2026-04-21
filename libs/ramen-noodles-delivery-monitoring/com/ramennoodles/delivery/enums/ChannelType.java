package com.ramennoodles.delivery.enums;

/**
 * Represents the notification delivery channel.
 * Used by: NotificationLog, NotificationStrategy
 */
public enum ChannelType {
    SMS("SMS text message"),
    EMAIL("Email notification"),
    PUSH("Push notification");

    private final String description;

    ChannelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

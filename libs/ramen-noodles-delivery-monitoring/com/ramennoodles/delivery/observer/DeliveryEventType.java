package com.ramennoodles.delivery.observer;

/**
 * Types of events that can be published in the delivery monitoring system.
 * Used by the Observer pattern to categorize events.
 */
public enum DeliveryEventType {
    ORDER_CREATED("A new delivery order was created"),
    RIDER_ASSIGNED("A rider was assigned to an order"),
    RIDER_REASSIGNED("An order was reassigned to a different rider"),
    LOCATION_UPDATED("Rider's GPS location was updated"),
    GEOFENCE_ENTRY("Rider entered a geofence zone"),
    GEOFENCE_EXIT("Rider exited a geofence zone"),
    ETA_UPDATED("Estimated time of arrival was recalculated"),
    STATUS_CHANGED("Order delivery status changed"),
    POD_SUBMITTED("Proof of delivery was submitted"),
    ORDER_DELIVERED("Order was successfully delivered"),
    ORDER_FAILED("Delivery attempt failed"),
    EXCEPTION_DETECTED("An anomaly or exception was detected");

    private final String description;

    DeliveryEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

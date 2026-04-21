package com.ramennoodles.delivery.enums;

/**
 * Represents the various states an order can be in during its lifecycle.
 * Used by: Order, DeliveryStatusLog, StatusUpdateService
 */
public enum OrderStatus {
    CREATED("Order has been created"),
    ASSIGNED("Rider has been assigned"),
    PICKED_UP("Order picked up from sender"),
    IN_TRANSIT("Order is in transit"),
    ARRIVING("Rider is near the destination"),
    DELIVERED("Order has been delivered"),
    FAILED("Delivery attempt failed"),
    CANCELLED("Order has been cancelled"),
    RETURNED("Order is being returned");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

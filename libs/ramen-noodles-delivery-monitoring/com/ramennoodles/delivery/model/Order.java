package com.ramennoodles.delivery.model;

import com.ramennoodles.delivery.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a delivery order in the system.
 * Shared with: VERTEX (Order Fulfillment) and DEI Hires (Delivery Orders).
 *
 * Relationships:
 *  - Order belongs to 1 Customer
 *  - Order assigned to 1 Rider
 *  - Order has 1..* GeofenceZones
 *  - Order has 0..1 RoutePlan
 *  - Order has 1..* ETARecords
 *  - Order has 1..* DeliveryStatusLogs
 *  - Order has 0..1 PODRecord
 *  - Soft-delete support: never hard-deleted
 */
public class Order {
    private String orderId;
    private String customerId;
    private String riderId;
    private String pickupAddress;
    private String dropoffAddress;
    private Coordinate pickupCoordinate;
    private Coordinate dropoffCoordinate;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public static Order create(String customerId, String pickupAddress,
                               String dropoffAddress, Coordinate pickupCoord,
                               Coordinate dropoffCoord) {
        Order o = new Order();
        o.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        o.customerId = customerId;
        o.pickupAddress = pickupAddress;
        o.dropoffAddress = dropoffAddress;
        o.pickupCoordinate = pickupCoord;
        o.dropoffCoordinate = dropoffCoord;
        o.status = OrderStatus.CREATED;
        o.createdAt = LocalDateTime.now();
        o.isDeleted = false;
        return o;
    }

    public void assignRider(String riderId) {
        this.riderId = riderId;
        this.status = OrderStatus.ASSIGNED;
    }

    public void reassignRider(String newRiderId) {
        this.riderId = newRiderId;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public boolean isActive() {
        return !isDeleted && status != OrderStatus.DELIVERED
                && status != OrderStatus.CANCELLED && status != OrderStatus.RETURNED;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getRiderId() { return riderId; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public Coordinate getPickupCoordinate() { return pickupCoordinate; }
    public void setPickupCoordinate(Coordinate pickupCoordinate) { this.pickupCoordinate = pickupCoordinate; }

    public Coordinate getDropoffCoordinate() { return dropoffCoordinate; }
    public void setDropoffCoordinate(Coordinate dropoffCoordinate) { this.dropoffCoordinate = dropoffCoordinate; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isDeleted() { return isDeleted; }

    @Override
    public String toString() {
        return String.format("Order[%s, customer=%s, rider=%s, status=%s, from='%s', to='%s']",
                orderId, customerId, riderId, status, pickupAddress, dropoffAddress);
    }
}

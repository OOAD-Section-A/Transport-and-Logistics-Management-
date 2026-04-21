package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the mapping between an order and an assigned rider.
 * Tracks assignment history (when assigned, when unassigned).
 * 
 * Relationships:
 *  - OrderRiderMapping links 1 Order to 1 Rider
 */
public class OrderRiderMapping {
    private String mappingId;       // PK
    private String orderId;         // FK -> Order
    private String riderId;         // FK -> Rider
    private LocalDateTime assignedAt;
    private LocalDateTime unassignedAt;

    // --- Factory Method ---
    public static OrderRiderMapping assign(String orderId, String riderId) {
        OrderRiderMapping mapping = new OrderRiderMapping();
        mapping.mappingId = "MAP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        mapping.orderId = orderId;
        mapping.riderId = riderId;
        mapping.assignedAt = LocalDateTime.now();
        mapping.unassignedAt = null;
        return mapping;
    }

    // --- Business Methods ---
    public void unassign() {
        this.unassignedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return unassignedAt == null;
    }

    // --- Getters ---
    public String getMappingId() { return mappingId; }
    public String getOrderId() { return orderId; }
    public String getRiderId() { return riderId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public LocalDateTime getUnassignedAt() { return unassignedAt; }

    @Override
    public String toString() {
        return String.format("OrderRiderMapping[%s, order=%s, rider=%s, active=%s]",
                mappingId, orderId, riderId, isActive());
    }
}

package com.ramennoodles.delivery.model;

import com.ramennoodles.delivery.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Append-only log of all delivery status changes for an order.
 * Never edited or deleted — full audit trail preserved.
 * No update/delete methods!
 * 
 * Shared with: VERTEX (Order Fulfillment) and DEI Hires (Delivery Orders).
 * 
 * Relationships:
 *  - DeliveryStatusLog belongs to 1 Order
 */
public class DeliveryStatusLog {
    private String logId;           // PK
    private String orderId;         // FK -> Order
    private OrderStatus status;
    private LocalDateTime changedAt;
    private String triggerSource;   // e.g., "GEOFENCE", "MANUAL", "SYSTEM"
    private String changedBy;       // rider_id, system, or admin

    // --- Factory Method ---
    public static DeliveryStatusLog logStatusChange(String orderId, OrderStatus status,
                                                     String triggerSource, String changedBy) {
        DeliveryStatusLog log = new DeliveryStatusLog();
        log.logId = "LOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.orderId = orderId;
        log.status = status;
        log.changedAt = LocalDateTime.now();
        log.triggerSource = triggerSource;
        log.changedBy = changedBy;
        return log;
    }

    // --- Getters (NO setters — append only!) ---
    public String getLogId() { return logId; }
    public String getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public String getTriggerSource() { return triggerSource; }
    public String getChangedBy() { return changedBy; }

    @Override
    public String toString() {
        return String.format("DeliveryStatusLog[%s, order=%s, status=%s, source=%s, by=%s, at=%s]",
                logId, orderId, status, triggerSource, changedBy, changedAt);
    }
}

package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * Service for managing delivery order status transitions.
 * Core component: Status Update Manager
 * 
 * Manages the lifecycle of delivery statuses and maintains an
 * immutable audit trail of all status changes.
 */
public class StatusUpdateService {

    // In-memory storage
    private final Map<String, OrderStatus> currentStatus;              // order_id -> current status
    private final Map<String, List<DeliveryStatusLog>> statusHistory;  // order_id -> logs
    private final DeliveryEventManager eventManager;

    // Valid status transitions
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new HashMap<>();
    
    static {
        VALID_TRANSITIONS.put(OrderStatus.CREATED,
                new HashSet<>(Arrays.asList(OrderStatus.ASSIGNED, OrderStatus.CANCELLED)));
        VALID_TRANSITIONS.put(OrderStatus.ASSIGNED,
                new HashSet<>(Arrays.asList(OrderStatus.PICKED_UP, OrderStatus.CANCELLED)));
        VALID_TRANSITIONS.put(OrderStatus.PICKED_UP,
                new HashSet<>(Arrays.asList(OrderStatus.IN_TRANSIT, OrderStatus.CANCELLED)));
        VALID_TRANSITIONS.put(OrderStatus.IN_TRANSIT,
                new HashSet<>(Arrays.asList(OrderStatus.ARRIVING, OrderStatus.FAILED)));
        VALID_TRANSITIONS.put(OrderStatus.ARRIVING,
                new HashSet<>(Arrays.asList(OrderStatus.DELIVERED, OrderStatus.FAILED)));
        VALID_TRANSITIONS.put(OrderStatus.FAILED,
                new HashSet<>(Arrays.asList(OrderStatus.RETURNED, OrderStatus.IN_TRANSIT)));
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, Collections.emptySet());
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, Collections.emptySet());
        VALID_TRANSITIONS.put(OrderStatus.RETURNED, Collections.emptySet());
    }

    public StatusUpdateService(DeliveryEventManager eventManager) {
        this.currentStatus = new HashMap<>();
        this.statusHistory = new HashMap<>();
        this.eventManager = eventManager;
    }

    /**
     * Initializes the status for a new order.
     */
    public DeliveryStatusLog initializeOrder(String orderId) {
        currentStatus.put(orderId, OrderStatus.CREATED);
        DeliveryStatusLog log = DeliveryStatusLog.logStatusChange(
                orderId, OrderStatus.CREATED, "SYSTEM", "system");
        statusHistory.computeIfAbsent(orderId, k -> new ArrayList<>()).add(log);
        return log;
    }

    /**
     * Updates the status of an order with validation.
     */
    public DeliveryStatusLog updateStatus(String orderId, OrderStatus newStatus,
                                           String triggerSource, String changedBy) {
        OrderStatus current = currentStatus.get(orderId);
        if (current == null) {
            throw new IllegalArgumentException("Order not tracked: " + orderId);
        }

        // Validate transition
        Set<OrderStatus> validNext = VALID_TRANSITIONS.getOrDefault(current, Collections.emptySet());
        if (!validNext.contains(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s -> %s for order %s",
                            current, newStatus, orderId));
        }

        // Update status
        currentStatus.put(orderId, newStatus);
        DeliveryStatusLog log = DeliveryStatusLog.logStatusChange(
                orderId, newStatus, triggerSource, changedBy);
        statusHistory.computeIfAbsent(orderId, k -> new ArrayList<>()).add(log);

        // Publish event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", orderId);
        eventData.put("previousStatus", current.toString());
        eventData.put("newStatus", newStatus.toString());
        eventData.put("triggerSource", triggerSource);
        eventData.put("changedBy", changedBy);
        eventManager.publish(DeliveryEventType.STATUS_CHANGED, eventData);

        // Publish specific events for terminal states
        if (newStatus == OrderStatus.DELIVERED) {
            eventManager.publish(DeliveryEventType.ORDER_DELIVERED, eventData);
        } else if (newStatus == OrderStatus.FAILED) {
            eventManager.publish(DeliveryEventType.ORDER_FAILED, eventData);
        }

        return log;
    }

    /**
     * Gets the current status of an order.
     */
    public OrderStatus getCurrentStatus(String orderId) {
        return currentStatus.get(orderId);
    }

    /**
     * Gets the latest status log entry for an order.
     */
    public DeliveryStatusLog getLatestStatusLog(String orderId) {
        List<DeliveryStatusLog> history = statusHistory.get(orderId);
        if (history == null || history.isEmpty()) return null;
        return history.get(history.size() - 1);
    }

    /**
     * Gets the full status history for an order.
     */
    public List<DeliveryStatusLog> getStatusHistory(String orderId) {
        return statusHistory.getOrDefault(orderId, Collections.emptyList());
    }

    /**
     * Gets the complete audit trail for an order.
     */
    public List<DeliveryStatusLog> getAuditTrail(String orderId) {
        return Collections.unmodifiableList(
                statusHistory.getOrDefault(orderId, Collections.emptyList()));
    }
}

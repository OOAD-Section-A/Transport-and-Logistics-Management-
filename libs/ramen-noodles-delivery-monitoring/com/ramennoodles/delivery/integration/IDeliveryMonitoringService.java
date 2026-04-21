package com.ramennoodles.delivery.integration;

import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * OUR EXPOSED INTERFACE — For VERTEX and DEI Hires to consume
 * ═══════════════════════════════════════════════════════════════
 * 
 * This interface is exposed BY our system (Ramen Noodles) for 
 * other teams to call into our delivery monitoring capabilities.
 * 
 * VERTEX uses this to check delivery status and get tracking URLs.
 * DEI Hires uses this to get real-time order status and ETAs.
 */
public interface IDeliveryMonitoringService {

    /**
     * Start real-time tracking for an order.
     */
    void startTracking(String orderId, String riderId);

    /**
     * Stop tracking for an order (delivery complete or cancelled).
     */
    void stopTracking(String orderId);

    /**
     * Get the current delivery status of an order.
     */
    OrderStatus getCurrentStatus(String orderId);

    /**
     * Get the latest ETA for an order.
     */
    ETARecord getETA(String orderId);

    /**
     * Get the proof of delivery for a completed order.
     */
    PODRecord getProofOfDelivery(String orderId);

    /**
     * Get the full notification history for an order.
     */
    List<NotificationLog> getNotificationHistory(String orderId);

    /**
     * Get the full status change history for an order (audit trail).
     */
    List<DeliveryStatusLog> getStatusHistory(String orderId);

    /**
     * Get a tracking URL for a customer to view their delivery.
     */
    String getTrackingURL(String orderId);
}

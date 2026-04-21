package com.ramennoodles.delivery.integration;

import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * INTEGRATION INTERFACE — DEI Hires (Team #6, Delivery Orders)
 * ═══════════════════════════════════════════════════════════════
 * 
 * This interface is exposed BY the DEI Hires team for OUR system to consume.
 * They create delivery orders; we monitor and track them.
 * 
 * Data flow: DEI Hires → Ramen Noodles
 * - We receive delivery orders with customer and address info
 * - We send status updates back to them
 */
public interface IDeliveryOrderService {

    /**
     * Get details of a delivery order.
     */
    Order getDeliveryOrder(String orderId);

    /**
     * Get customer details for notifications.
     */
    Customer getCustomerDetails(String customerId);

    /**
     * Update the status of a delivery order in their system.
     * Called when our tracking changes the status.
     */
    void updateOrderStatus(String orderId, OrderStatus status);

    /**
     * Get all active delivery orders that need tracking.
     */
    List<Order> getActiveDeliveryOrders();

    /**
     * Notify their system about a new notification sent to a customer.
     */
    void logNotification(String orderId, NotificationLog notification);

    /**
     * Attach proof of delivery to the order record.
     */
    void attachPODRecord(String orderId, PODRecord pod);
}

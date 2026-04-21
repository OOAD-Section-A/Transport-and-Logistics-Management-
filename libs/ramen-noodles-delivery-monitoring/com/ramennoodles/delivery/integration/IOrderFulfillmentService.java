package com.ramennoodles.delivery.integration;

import com.ramennoodles.delivery.model.*;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * INTEGRATION INTERFACE — VERTEX (Team #17, Order Fulfillment)
 * ═══════════════════════════════════════════════════════════════
 * 
 * This interface is exposed BY the VERTEX team for OUR system to consume.
 * We call these methods to get order details and notify about delivery completion.
 * 
 * Data flow: VERTEX → Ramen Noodles
 * - We receive fulfilled orders ready for delivery tracking
 * - We notify them when delivery is complete (with POD)
 */
public interface IOrderFulfillmentService {

    /**
     * Get details of a specific order that has been fulfilled.
     * Called when we need order information to start tracking.
     */
    Order getOrderDetails(String orderId);

    /**
     * Get all orders that have been dispatched and are ready for tracking.
     */
    List<Order> getDispatchedOrders();

    /**
     * Notify the fulfillment system that an order has been picked up by the rider.
     */
    void notifyOrderPickedUp(String orderId, String riderId);

    /**
     * Notify the fulfillment system that an order has been delivered,
     * including the proof of delivery.
     */
    void notifyOrderDelivered(String orderId, PODRecord pod);

    /**
     * Notify the fulfillment system that a delivery attempt has failed.
     */
    void notifyDeliveryFailed(String orderId, String reason);

    /**
     * Get customer details associated with an order.
     */
    Customer getCustomerForOrder(String orderId);
}

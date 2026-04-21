package com.ramennoodles.delivery.strategy;

import com.ramennoodles.delivery.model.NotificationLog;

/**
 * Strategy interface for sending notifications.
 * Different notification channels (SMS, Email, Push) implement this.
 * 
 * Design Pattern: Strategy
 */
public interface NotificationStrategy {
    
    /**
     * Send a notification to a customer.
     * 
     * @param orderId The order this notification is about
     * @param customerId The customer to notify
     * @param message The notification message
     * @return NotificationLog record of the sent notification
     */
    NotificationLog send(String orderId, String customerId, String message);

    /**
     * Get the name of this notification channel.
     */
    String getChannelName();
}

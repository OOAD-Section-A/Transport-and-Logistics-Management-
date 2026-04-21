package com.ramennoodles.delivery.strategy;

import com.ramennoodles.delivery.model.NotificationLog;

/**
 * Concrete strategy for sending Email notifications.
 * 
 * Design Pattern: Strategy (ConcreteStrategy)
 */
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationLog send(String orderId, String customerId, String message) {
        // In a real system, this would integrate with an email service (SendGrid, SES, etc.)
        System.out.println("[EMAIL] Sending to customer " + customerId + ": " + message);
        return NotificationLog.sendEmail(orderId, customerId, message);
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}

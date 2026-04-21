package com.ramennoodles.delivery.strategy;

import com.ramennoodles.delivery.model.NotificationLog;

/**
 * Concrete strategy for sending SMS notifications.
 * 
 * Design Pattern: Strategy (ConcreteStrategy)
 */
public class SMSNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationLog send(String orderId, String customerId, String message) {
        // In a real system, this would integrate with an SMS gateway (Twilio, etc.)
        System.out.println("[SMS] Sending to customer " + customerId + ": " + message);
        return NotificationLog.sendSMS(orderId, customerId, message);
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}

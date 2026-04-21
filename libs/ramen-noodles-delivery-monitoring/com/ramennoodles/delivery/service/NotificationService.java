package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.strategy.*;

import java.util.*;

/**
 * Service for sending notifications to customers.
 * Core component: Customer Notification Gateway
 * 
 * Sends real-time updates via SMS or email at key milestones.
 * Uses the Strategy pattern for different notification channels.
 */
public class NotificationService {

    // In-memory storage
    private final Map<String, List<NotificationLog>> notifsByOrder;       // order_id -> notifications
    private final Map<String, List<NotificationLog>> notifsByCustomer;    // customer_id -> notifications
    
    // Strategy pattern: default and available strategies
    private NotificationStrategy defaultStrategy;
    private final Map<String, NotificationStrategy> strategies;

    public NotificationService() {
        this.notifsByOrder = new HashMap<>();
        this.notifsByCustomer = new HashMap<>();
        this.strategies = new HashMap<>();
        
        // Register default strategies
        SMSNotificationStrategy sms = new SMSNotificationStrategy();
        EmailNotificationStrategy email = new EmailNotificationStrategy();
        strategies.put("SMS", sms);
        strategies.put("Email", email);
        this.defaultStrategy = sms; // SMS is default
    }

    /**
     * Sets the default notification strategy.
     */
    public void setDefaultStrategy(NotificationStrategy strategy) {
        this.defaultStrategy = strategy;
    }

    /**
     * Sends a notification using the default strategy.
     */
    public NotificationLog sendNotification(String orderId, String customerId, String message) {
        return sendNotification(orderId, customerId, message, defaultStrategy);
    }

    /**
     * Sends a notification using a specific strategy.
     */
    public NotificationLog sendNotification(String orderId, String customerId,
                                             String message, NotificationStrategy strategy) {
        NotificationLog log = strategy.send(orderId, customerId, message);
        notifsByOrder.computeIfAbsent(orderId, k -> new ArrayList<>()).add(log);
        notifsByCustomer.computeIfAbsent(customerId, k -> new ArrayList<>()).add(log);
        return log;
    }

    /**
     * Sends notification via all channels (SMS + Email).
     */
    public List<NotificationLog> sendViaAllChannels(String orderId, String customerId, String message) {
        List<NotificationLog> logs = new ArrayList<>();
        for (NotificationStrategy strategy : strategies.values()) {
            logs.add(sendNotification(orderId, customerId, message, strategy));
        }
        return logs;
    }

    /**
     * Sends a specific milestone notification.
     */
    public NotificationLog notifyMilestone(String orderId, String customerId, String milestone) {
        String message = String.format("Order %s: %s", orderId, milestone);
        return sendNotification(orderId, customerId, message);
    }

    /**
     * Gets all notifications for an order.
     */
    public List<NotificationLog> getNotificationsByOrder(String orderId) {
        return notifsByOrder.getOrDefault(orderId, Collections.emptyList());
    }

    /**
     * Gets all notifications for a customer.
     */
    public List<NotificationLog> getNotificationsByCustomer(String customerId) {
        return notifsByCustomer.getOrDefault(customerId, Collections.emptyList());
    }

    /**
     * Retries all failed notifications.
     */
    public void retryFailed() {
        for (List<NotificationLog> logs : notifsByOrder.values()) {
            for (NotificationLog log : logs) {
                log.retryFailed();
            }
        }
    }
}

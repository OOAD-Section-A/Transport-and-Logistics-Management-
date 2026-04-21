package com.ramennoodles.delivery.observer;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Central event manager for the Delivery Monitoring system.
 * Manages subscriptions and publishes events to registered listeners.
 * 
 * Design Pattern: Observer (Subject/Publisher)
 * 
 * This allows external subsystems (VERTEX, DEI Hires, CenterDiv) to
 * subscribe to specific event types and receive real-time notifications.
 */
public class DeliveryEventManager {
    
    // Map of event types to their registered listeners
    private final Map<DeliveryEventType, List<DeliveryEventListener>> listeners;
    
    // Event history for audit trail
    private final List<EventRecord> eventHistory;

    public DeliveryEventManager() {
        this.listeners = new HashMap<>();
        this.eventHistory = new ArrayList<>();
        
        // Initialize listener lists for all event types
        for (DeliveryEventType type : DeliveryEventType.values()) {
            listeners.put(type, new ArrayList<>());
        }
    }

    /**
     * Subscribe a listener to a specific event type.
     */
    public void subscribe(DeliveryEventType eventType, DeliveryEventListener listener) {
        List<DeliveryEventListener> eventListeners = listeners.get(eventType);
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    /**
     * Subscribe a listener to multiple event types at once.
     */
    public void subscribe(List<DeliveryEventType> eventTypes, DeliveryEventListener listener) {
        for (DeliveryEventType type : eventTypes) {
            subscribe(type, listener);
        }
    }

    /**
     * Unsubscribe a listener from a specific event type.
     */
    public void unsubscribe(DeliveryEventType eventType, DeliveryEventListener listener) {
        listeners.get(eventType).remove(listener);
    }

    /**
     * Publish an event to all registered listeners for that event type.
     */
    public void publish(DeliveryEventType eventType, Map<String, Object> data) {
        // Record the event
        EventRecord record = new EventRecord(eventType, data, LocalDateTime.now());
        eventHistory.add(record);

        // Notify all listeners
        List<DeliveryEventListener> eventListeners = listeners.get(eventType);
        for (DeliveryEventListener listener : eventListeners) {
            try {
                listener.onEvent(eventType, data);
            } catch (Exception e) {
                System.err.println("[EventManager] Error notifying listener: " + e.getMessage());
            }
        }
    }

    /**
     * Get the complete event history.
     */
    public List<EventRecord> getEventHistory() {
        return Collections.unmodifiableList(eventHistory);
    }

    /**
     * Get event history filtered by event type.
     */
    public List<EventRecord> getEventHistory(DeliveryEventType eventType) {
        List<EventRecord> filtered = new ArrayList<>();
        for (EventRecord record : eventHistory) {
            if (record.getEventType() == eventType) {
                filtered.add(record);
            }
        }
        return filtered;
    }

    /**
     * Internal record of a published event.
     */
    public static class EventRecord {
        private final DeliveryEventType eventType;
        private final Map<String, Object> data;
        private final LocalDateTime timestamp;

        public EventRecord(DeliveryEventType eventType, Map<String, Object> data, LocalDateTime timestamp) {
            this.eventType = eventType;
            this.data = data;
            this.timestamp = timestamp;
        }

        public DeliveryEventType getEventType() { return eventType; }
        public Map<String, Object> getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", timestamp, eventType, data);
        }
    }
}

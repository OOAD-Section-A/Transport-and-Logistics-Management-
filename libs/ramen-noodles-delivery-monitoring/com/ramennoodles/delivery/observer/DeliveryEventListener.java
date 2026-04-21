package com.ramennoodles.delivery.observer;

import java.util.Map;

/**
 * Observer interface for the Delivery Monitoring system.
 * Any subsystem (internal or external) that wants to receive
 * real-time delivery events must implement this interface.
 * 
 * Design Pattern: Observer
 * 
 * Integration partners (VERTEX, DEI Hires, CenterDiv) can implement
 * this interface to receive real-time updates from our system.
 */
public interface DeliveryEventListener {
    
    /**
     * Called when a delivery event occurs.
     * 
     * @param eventType The type of event that occurred
     * @param data A map of event data (key-value pairs with contextual information)
     */
    void onEvent(DeliveryEventType eventType, Map<String, Object> data);
}

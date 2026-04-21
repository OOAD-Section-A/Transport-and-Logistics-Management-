package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * Service for calculating and updating Estimated Time of Arrival.
 * Core component: ETA Calculator
 * 
 * Employs calculations to estimate accurate ETAs by factoring in
 * distance, traffic conditions, and speed.
 */
public class ETAService {

    private static final double DEFAULT_AVG_SPEED_KMH = 30.0; // Average urban delivery speed
    
    // In-memory storage
    private final Map<String, List<ETARecord>> etaHistory;      // order_id -> ETA records
    private final Map<String, ETARecord> latestETA;             // order_id -> latest ETA
    private final Map<String, Coordinate> orderDestinations;    // order_id -> destination
    private final DeliveryEventManager eventManager;

    public ETAService(DeliveryEventManager eventManager) {
        this.etaHistory = new HashMap<>();
        this.latestETA = new HashMap<>();
        this.orderDestinations = new HashMap<>();
        this.eventManager = eventManager;
    }

    /**
     * Registers the destination for an order.
     */
    public void registerOrderDestination(String orderId, Coordinate destination) {
        orderDestinations.put(orderId, destination);
    }

    /**
     * Calculates ETA based on current rider position.
     */
    public ETARecord calculateETA(String orderId, double currentLat, double currentLng) {
        Coordinate destination = orderDestinations.get(orderId);
        if (destination == null) {
            throw new IllegalArgumentException("No destination registered for order: " + orderId);
        }

        ETARecord eta = ETARecord.calculate(orderId, currentLat, currentLng,
                destination, DEFAULT_AVG_SPEED_KMH);
        
        etaHistory.computeIfAbsent(orderId, k -> new ArrayList<>()).add(eta);
        latestETA.put(orderId, eta);

        // Publish ETA update event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", orderId);
        eventData.put("etaId", eta.getEtaId());
        eventData.put("estimatedArrival", eta.getEstimatedArrival().toString());
        eventData.put("remainingMinutes", eta.getRemainingTimeMinutes());
        eventManager.publish(DeliveryEventType.ETA_UPDATED, eventData);

        return eta;
    }

    /**
     * Calculates ETA with a custom traffic factor.
     */
    public ETARecord calculateETAWithTraffic(String orderId, double currentLat, double currentLng,
                                              double trafficFactor) {
        ETARecord eta = calculateETA(orderId, currentLat, currentLng);
        eta.updateTrafficFactor(trafficFactor);
        return eta;
    }

    /**
     * Gets the latest ETA for an order.
     */
    public ETARecord getLatestETA(String orderId) {
        return latestETA.get(orderId);
    }

    /**
     * Gets the full ETA history for an order.
     */
    public List<ETARecord> getETAHistory(String orderId) {
        return etaHistory.getOrDefault(orderId, Collections.emptyList());
    }

    /**
     * Gets remaining time in minutes for an order.
     */
    public long getRemainingTime(String orderId) {
        ETARecord eta = latestETA.get(orderId);
        return (eta != null) ? eta.getRemainingTimeMinutes() : -1;
    }
}

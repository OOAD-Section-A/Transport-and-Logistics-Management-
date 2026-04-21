package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

/**
 * Represents an Estimated Time of Arrival calculation.
 * Factors in current location, destination, and traffic conditions.
 * 
 * Shared with: VERTEX (Order Fulfillment) and CenterDiv (Transport & Logistics).
 * 
 * Relationships:
 *  - ETARecord belongs to 1 Order
 */
public class ETARecord {
    private String etaId;              // PK
    private String orderId;            // FK -> Order
    private LocalDateTime calculatedAt;
    private double currentLat;
    private double currentLng;
    private LocalDateTime estimatedArrival;
    private double trafficFactor;      // 1.0 = normal, >1.0 = heavy traffic

    // --- Factory Method ---
    public static ETARecord calculate(String orderId, double currentLat, double currentLng,
                                       Coordinate destination, double avgSpeedKmh) {
        ETARecord eta = new ETARecord();
        eta.etaId = "ETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        eta.orderId = orderId;
        eta.calculatedAt = LocalDateTime.now();
        eta.currentLat = currentLat;
        eta.currentLng = currentLng;
        eta.trafficFactor = 1.0;

        // Calculate distance and ETA
        Coordinate current = new Coordinate(currentLat, currentLng);
        double distanceMeters = current.distanceTo(destination);
        double distanceKm = distanceMeters / 1000.0;

        // Effective speed considering traffic
        double effectiveSpeed = avgSpeedKmh / eta.trafficFactor;
        double hoursToArrive = distanceKm / effectiveSpeed;
        long minutesToArrive = (long) (hoursToArrive * 60);

        eta.estimatedArrival = eta.calculatedAt.plusMinutes(minutesToArrive);
        return eta;
    }

    // --- Business Methods ---
    public void updateTrafficFactor(double factor) {
        this.trafficFactor = factor;
        // Recalculation would be done by the service layer
    }

    /**
     * Returns the remaining time until estimated arrival in minutes.
     */
    public long getRemainingTimeMinutes() {
        Duration remaining = Duration.between(LocalDateTime.now(), estimatedArrival);
        return Math.max(0, remaining.toMinutes());
    }

    // --- Getters ---
    public String getEtaId() { return etaId; }
    public String getOrderId() { return orderId; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public double getCurrentLat() { return currentLat; }
    public double getCurrentLng() { return currentLng; }
    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public double getTrafficFactor() { return trafficFactor; }

    @Override
    public String toString() {
        return String.format("ETARecord[%s, order=%s, arrival=%s, traffic=%.1fx, remaining=%dmin]",
                etaId, orderId, estimatedArrival, trafficFactor, getRemainingTimeMinutes());
    }
}

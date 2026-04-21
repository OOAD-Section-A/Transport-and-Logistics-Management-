package com.ramennoodles.delivery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a planned route for an order delivery.
 * 
 * Shared with: CenterDiv (Transport & Logistics) for route optimization.
 * 
 * Relationships:
 *  - RoutePlan belongs to 1 Order
 */
public class RoutePlan {
    private String routeId;          // PK
    private String orderId;          // FK -> Order
    private List<Coordinate> waypoints;
    private LocalDateTime createdAt;

    // --- Factory Method ---
    public static RoutePlan create(String orderId, List<Coordinate> waypoints) {
        RoutePlan rp = new RoutePlan();
        rp.routeId = "RTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        rp.orderId = orderId;
        rp.waypoints = new ArrayList<>(waypoints);
        rp.createdAt = LocalDateTime.now();
        return rp;
    }

    // --- Business Methods ---
    public void addWaypoint(double lat, double lng) {
        waypoints.add(new Coordinate(lat, lng));
    }

    public void removeWaypoint(int index) {
        if (index >= 0 && index < waypoints.size()) {
            waypoints.remove(index);
        }
    }

    public List<Coordinate> getWaypoints() {
        return new ArrayList<>(waypoints);
    }

    /**
     * Calculates total route distance by summing segments.
     * @return total distance in meters
     */
    public double getTotalDistance() {
        double totalDistance = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            totalDistance += waypoints.get(i).distanceTo(waypoints.get(i + 1));
        }
        return totalDistance;
    }

    /**
     * Optimizes route by simple nearest-neighbor heuristic.
     * In a real system, this would use advanced routing algorithms.
     */
    public void optimizeRoute() {
        if (waypoints.size() <= 2) return;

        List<Coordinate> optimized = new ArrayList<>();
        List<Coordinate> remaining = new ArrayList<>(waypoints);

        // Keep first and last waypoints fixed (pickup and dropoff)
        Coordinate start = remaining.remove(0);
        Coordinate end = remaining.remove(remaining.size() - 1);
        optimized.add(start);

        Coordinate current = start;
        while (!remaining.isEmpty()) {
            int nearestIdx = 0;
            double nearestDist = Double.MAX_VALUE;
            for (int i = 0; i < remaining.size(); i++) {
                double dist = current.distanceTo(remaining.get(i));
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearestIdx = i;
                }
            }
            current = remaining.remove(nearestIdx);
            optimized.add(current);
        }
        optimized.add(end);
        this.waypoints = optimized;
    }

    // --- Getters & Setters ---
    public String getRouteId() { return routeId; }
    public String getOrderId() { return orderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("RoutePlan[%s, order=%s, waypoints=%d, distance=%.0fm]",
                routeId, orderId, waypoints.size(), getTotalDistance());
    }
}

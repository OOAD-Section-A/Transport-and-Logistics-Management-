package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;

import java.util.*;

/**
 * Service for managing route plans.
 * Core component: Order-Journey Mapper
 * 
 * Creates and optimizes delivery routes with waypoints.
 * Integrates with CenterDiv (Transport & Logistics) for route optimization.
 */
public class RoutePlanService {

    // In-memory storage
    private final Map<String, RoutePlan> routesByOrder;     // order_id -> route plan
    private final Map<String, OrderRiderMapping> activeMappings;  // order_id -> active mapping
    private final List<OrderRiderMapping> allMappings;

    public RoutePlanService() {
        this.routesByOrder = new HashMap<>();
        this.activeMappings = new HashMap<>();
        this.allMappings = new ArrayList<>();
    }

    /**
     * Creates a route plan for an order.
     */
    public RoutePlan createRoutePlan(String orderId, Coordinate pickup, Coordinate dropoff) {
        List<Coordinate> waypoints = new ArrayList<>();
        waypoints.add(pickup);
        waypoints.add(dropoff);
        RoutePlan route = RoutePlan.create(orderId, waypoints);
        routesByOrder.put(orderId, route);
        return route;
    }

    /**
     * Creates a route plan with intermediate waypoints.
     */
    public RoutePlan createRoutePlan(String orderId, List<Coordinate> waypoints) {
        RoutePlan route = RoutePlan.create(orderId, waypoints);
        routesByOrder.put(orderId, route);
        return route;
    }

    /**
     * Optimizes the route for an order.
     */
    public RoutePlan optimizeRoute(String orderId) {
        RoutePlan route = routesByOrder.get(orderId);
        if (route != null) {
            route.optimizeRoute();
        }
        return route;
    }

    /**
     * Assigns a rider to an order.
     */
    public OrderRiderMapping assignRider(String orderId, String riderId) {
        // Unassign previous rider if exists
        OrderRiderMapping existing = activeMappings.get(orderId);
        if (existing != null) {
            existing.unassign();
        }

        OrderRiderMapping mapping = OrderRiderMapping.assign(orderId, riderId);
        activeMappings.put(orderId, mapping);
        allMappings.add(mapping);
        return mapping;
    }

    /**
     * Gets the route plan for an order.
     */
    public RoutePlan getRoutePlan(String orderId) {
        return routesByOrder.get(orderId);
    }

    /**
     * Gets the active rider mapping for an order.
     */
    public OrderRiderMapping getActiveMapping(String orderId) {
        return activeMappings.get(orderId);
    }

    /**
     * Gets all assignment history for an order.
     */
    public List<OrderRiderMapping> getAssignmentHistory(String orderId) {
        List<OrderRiderMapping> history = new ArrayList<>();
        for (OrderRiderMapping mapping : allMappings) {
            if (mapping.getOrderId().equals(orderId)) {
                history.add(mapping);
            }
        }
        return history;
    }

    /**
     * Gets total distance for an order's route.
     */
    public double getRouteDistance(String orderId) {
        RoutePlan route = routesByOrder.get(orderId);
        return (route != null) ? route.getTotalDistance() : 0;
    }
}

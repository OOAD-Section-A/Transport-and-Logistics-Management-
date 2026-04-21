package com.ramennoodles.delivery.integration;

import com.ramennoodles.delivery.model.*;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * INTEGRATION INTERFACE — CenterDiv (Team #2, Transport & Logistics)
 * ═══════════════════════════════════════════════════════════════
 * 
 * This interface is exposed BY the CenterDiv team for OUR system to consume.
 * They manage fleet, routes, and logistics; we provide tracking data.
 * 
 * Data flow: CenterDiv → Ramen Noodles (bidirectional)
 * - We receive rider details and optimized routes
 * - We provide live GPS data and ETAs back to them
 */
public interface ITransportLogisticsService {

    /**
     * Get details of a specific rider from their fleet registry.
     */
    Rider getRiderDetails(String riderId);

    /**
     * Get all riders available in a specific zone/area.
     */
    List<Rider> getAvailableRiders(String zone);

    /**
     * Request an optimized route from their routing engine.
     */
    RoutePlan calculateOptimalRoute(String orderId, Coordinate pickup,
                                     Coordinate dropoff, List<Coordinate> waypoints);

    /**
     * Get geofence zones for logistics hubs/warehouses.
     */
    List<GeofenceZone> getLogisticsHubZones();

    /**
     * Report vehicle health data back to their fleet management.
     */
    void reportVehicleHealth(String riderId, String healthReport);

    /**
     * Notify their system that a rider is now available (delivery complete).
     */
    void notifyRiderAvailable(String riderId);

    /**
     * Get the vehicle type/details for a rider.
     */
    String getVehicleType(String riderId);
}

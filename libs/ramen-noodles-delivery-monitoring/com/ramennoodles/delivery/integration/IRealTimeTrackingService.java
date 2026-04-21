package com.ramennoodles.delivery.integration;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.service.FleetDashboardService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 * OUR EXPOSED INTERFACE — For CenterDiv to consume
 * ═══════════════════════════════════════════════════════════════
 * 
 * This interface is exposed BY our system (Ramen Noodles) for
 * CenterDiv to get real-time tracking data from our system.
 */
public interface IRealTimeTrackingService {

    /**
     * Get the latest GPS position of a rider.
     */
    GPSPing getLatestPosition(String riderId);

    /**
     * Get location history for a rider within a time range.
     */
    List<GPSPing> getLocationHistory(String riderId, LocalDateTime start, LocalDateTime end);

    /**
     * Get the current ETA for an order.
     */
    ETARecord getETA(String orderId);

    /**
     * Check if a rider is currently inside a specific geofence zone.
     */
    boolean isRiderInZone(String riderId, String zoneId);

    /**
     * Get a live fleet dashboard overview.
     */
    FleetDashboardService.FleetOverview getLiveFleetDashboard();

    /**
     * Get all riders currently being tracked with their positions.
     */
    java.util.Map<String, GPSPing> getAllRiderPositions();
}

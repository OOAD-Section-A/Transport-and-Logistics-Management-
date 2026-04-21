package com.ramennoodles.delivery.model;

import com.ramennoodles.delivery.enums.ZoneType;
import java.util.UUID;

/**
 * Represents a virtual geographic boundary (geofence) for an order.
 * Triggers events when a rider enters or exits the zone.
 * 
 * Relationships:
 *  - GeofenceZone belongs to 1 Order
 *  - GeofenceZone triggers 1..* GeofenceEvents
 */
public class GeofenceZone {
    private String zoneId;          // PK
    private String orderId;         // FK -> Order
    private double centerLat;
    private double centerLng;
    private int radiusMeters;
    private ZoneType zoneType;

    // --- Factory Method ---
    public static GeofenceZone create(String orderId, double centerLat, double centerLng,
                                       int radiusMeters, ZoneType type) {
        GeofenceZone zone = new GeofenceZone();
        zone.zoneId = "GEO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        zone.orderId = orderId;
        zone.centerLat = centerLat;
        zone.centerLng = centerLng;
        zone.radiusMeters = radiusMeters;
        zone.zoneType = type;
        return zone;
    }

    // --- Business Methods ---
    /**
     * Checks whether a given point (lat, lng) is inside this geofence zone.
     */
    public boolean isPointInside(double lat, double lng) {
        Coordinate center = new Coordinate(centerLat, centerLng);
        Coordinate point = new Coordinate(lat, lng);
        double distance = center.distanceTo(point);
        return distance <= radiusMeters;
    }

    public Coordinate getCenter() {
        return new Coordinate(centerLat, centerLng);
    }

    public int getRadius() {
        return radiusMeters;
    }

    public void delete() {
        // Mark zone as inactive (in a real system, remove from active monitoring)
    }

    // --- Getters & Setters ---
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getCenterLat() { return centerLat; }
    public double getCenterLng() { return centerLng; }

    public int getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(int radiusMeters) { this.radiusMeters = radiusMeters; }

    public ZoneType getZoneType() { return zoneType; }

    @Override
    public String toString() {
        return String.format("GeofenceZone[%s, order=%s, type=%s, center=(%.6f,%.6f), radius=%dm]",
                zoneId, orderId, zoneType, centerLat, centerLng, radiusMeters);
    }
}

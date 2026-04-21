package com.ramennoodles.delivery.model;

/**
 * Represents a geographical coordinate (latitude, longitude).
 * Used by: RoutePlan, GeofenceZone, GPSPing
 */
public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate() {}

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- Getters & Setters ---
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    /**
     * Calculates distance to another coordinate using Haversine formula.
     * @return distance in meters
     */
    public double distanceTo(Coordinate other) {
        final double R = 6371000; // Earth radius in meters
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLng = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", latitude, longitude);
    }
}

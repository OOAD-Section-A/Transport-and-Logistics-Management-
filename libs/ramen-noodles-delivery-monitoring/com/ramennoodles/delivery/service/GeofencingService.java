package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.enums.EventType;
import com.ramennoodles.delivery.enums.ZoneType;
import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * Service for managing geofence zones and detecting entry/exit events.
 * Core component: Geofencing Engine
 * 
 * Sets virtual boundaries that trigger instant notifications when a
 * shipment enters or exits specific zones.
 */
public class GeofencingService {

    // In-memory storage
    private final Map<String, List<GeofenceZone>> zonesByOrder;    // order_id -> zones
    private final Map<String, List<GeofenceEvent>> eventsByZone;   // zone_id -> events
    private final Map<String, Boolean> riderInsideZone;            // "rider_zone" -> inside?
    private final DeliveryEventManager eventManager;

    public GeofencingService(DeliveryEventManager eventManager) {
        this.zonesByOrder = new HashMap<>();
        this.eventsByZone = new HashMap<>();
        this.riderInsideZone = new HashMap<>();
        this.eventManager = eventManager;
    }

    /**
     * Creates geofence zones for an order (pickup and dropoff zones).
     */
    public List<GeofenceZone> createZonesForOrder(Order order, int radiusMeters) {
        List<GeofenceZone> zones = new ArrayList<>();

        // Create pickup zone
        GeofenceZone pickupZone = GeofenceZone.create(
                order.getOrderId(),
                order.getPickupCoordinate().getLatitude(),
                order.getPickupCoordinate().getLongitude(),
                radiusMeters,
                ZoneType.PICKUP
        );
        zones.add(pickupZone);

        // Create dropoff zone
        GeofenceZone dropoffZone = GeofenceZone.create(
                order.getOrderId(),
                order.getDropoffCoordinate().getLatitude(),
                order.getDropoffCoordinate().getLongitude(),
                radiusMeters,
                ZoneType.DROPOFF
        );
        zones.add(dropoffZone);

        zonesByOrder.put(order.getOrderId(), zones);
        return zones;
    }

    /**
     * Checks a GPS ping against all active geofence zones for an order.
     * Returns any triggered events.
     */
    public List<GeofenceEvent> checkGeofences(String orderId, String riderId,
                                               double latitude, double longitude) {
        List<GeofenceEvent> triggeredEvents = new ArrayList<>();
        List<GeofenceZone> zones = zonesByOrder.getOrDefault(orderId, Collections.emptyList());

        for (GeofenceZone zone : zones) {
            String key = riderId + "_" + zone.getZoneId();
            boolean wasInside = riderInsideZone.getOrDefault(key, false);
            boolean isInside = zone.isPointInside(latitude, longitude);

            if (!wasInside && isInside) {
                // Rider entered the zone
                GeofenceEvent event = GeofenceEvent.logEntry(zone.getZoneId(), riderId);
                triggeredEvents.add(event);
                eventsByZone.computeIfAbsent(zone.getZoneId(), k -> new ArrayList<>()).add(event);
                riderInsideZone.put(key, true);

                // Publish event
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("orderId", orderId);
                eventData.put("riderId", riderId);
                eventData.put("zoneId", zone.getZoneId());
                eventData.put("zoneType", zone.getZoneType().toString());
                eventData.put("eventType", EventType.ENTRY.toString());
                eventManager.publish(DeliveryEventType.GEOFENCE_ENTRY, eventData);

            } else if (wasInside && !isInside) {
                // Rider exited the zone
                GeofenceEvent event = GeofenceEvent.logExit(zone.getZoneId(), riderId);
                triggeredEvents.add(event);
                eventsByZone.computeIfAbsent(zone.getZoneId(), k -> new ArrayList<>()).add(event);
                riderInsideZone.put(key, false);

                // Publish event
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("orderId", orderId);
                eventData.put("riderId", riderId);
                eventData.put("zoneId", zone.getZoneId());
                eventData.put("zoneType", zone.getZoneType().toString());
                eventData.put("eventType", EventType.EXIT.toString());
                eventManager.publish(DeliveryEventType.GEOFENCE_EXIT, eventData);
            }
        }

        return triggeredEvents;
    }

    /**
     * Checks if a rider is currently inside a specific zone.
     */
    public boolean isRiderInZone(String riderId, String zoneId) {
        String key = riderId + "_" + zoneId;
        return riderInsideZone.getOrDefault(key, false);
    }

    /**
     * Gets all zones for an order.
     */
    public List<GeofenceZone> getZonesForOrder(String orderId) {
        return zonesByOrder.getOrDefault(orderId, Collections.emptyList());
    }

    /**
     * Gets all events for a zone.
     */
    public List<GeofenceEvent> getEventsForZone(String zoneId) {
        return eventsByZone.getOrDefault(zoneId, Collections.emptyList());
    }
}

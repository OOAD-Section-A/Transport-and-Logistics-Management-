package com.ramennoodles.delivery.model;

import com.ramennoodles.delivery.enums.EventType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a geofence trigger event (entry/exit).
 * Append-only: never edited or deleted — full audit trail preserved.
 * 
 * Relationships:
 *  - GeofenceEvent belongs to 1 GeofenceZone
 *  - GeofenceEvent triggered by 1 Rider
 */
public class GeofenceEvent {
    private String eventId;       // PK
    private String zoneId;        // FK -> GeofenceZone
    private String riderId;       // FK -> Rider
    private EventType eventType;
    private LocalDateTime triggeredAt;

    // --- Factory Methods ---
    public static GeofenceEvent logEntry(String zoneId, String riderId) {
        return createEvent(zoneId, riderId, EventType.ENTRY);
    }

    public static GeofenceEvent logExit(String zoneId, String riderId) {
        return createEvent(zoneId, riderId, EventType.EXIT);
    }

    private static GeofenceEvent createEvent(String zoneId, String riderId, EventType type) {
        GeofenceEvent event = new GeofenceEvent();
        event.eventId = "GEV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        event.zoneId = zoneId;
        event.riderId = riderId;
        event.eventType = type;
        event.triggeredAt = LocalDateTime.now();
        return event;
    }

    // --- Getters ---
    public String getEventId() { return eventId; }
    public String getZoneId() { return zoneId; }
    public String getRiderId() { return riderId; }
    public EventType getEventType() { return eventType; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }

    @Override
    public String toString() {
        return String.format("GeofenceEvent[%s, zone=%s, rider=%s, type=%s, at=%s]",
                eventId, zoneId, riderId, eventType, triggeredAt);
    }
}

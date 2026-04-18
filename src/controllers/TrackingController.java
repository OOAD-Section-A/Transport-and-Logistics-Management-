package controllers;

import services.TransportService;
import java.util.Map;

/**
 * REST Controller for Tracking APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class TrackingController {

    private TransportService transportService;

    public TrackingController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * GET /tracking/{shipmentId}
     * Retrieves real-time tracking data.
     */
    public Map<String, Object> getTrackingData(String shipmentId) {
        return transportService.getTrackingData(shipmentId);
    }
}
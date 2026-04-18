package controllers;

import services.TransportService;
import java.util.Map;

/**
 * REST Controller for Route Optimization APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class RouteController {

    private TransportService transportService;

    public RouteController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * GET /routes/optimize
     * Requests optimized route calculation.
     */
    public Map<String, Object> optimizeRoute(String origin, String destination, String constraints) {
        return transportService.optimizeRoute(origin, destination, constraints);
    }
}
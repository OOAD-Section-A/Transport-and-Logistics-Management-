package controllers;

import entities.Carrier;
import services.TransportService;
import java.util.List;

/**
 * REST Controller for Carrier-related APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class CarrierController {

    private TransportService transportService;

    public CarrierController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * GET /carriers
     * Lists all available carriers.
     */
    public List<Carrier> getAllCarriers(String mode) {
        return transportService.getAllCarriers(mode);
    }

    /**
     * POST /carriers
     * Registers a new carrier.
     */
    public Carrier createCarrier(Carrier carrier) {
        return transportService.createCarrier(carrier);
    }
}
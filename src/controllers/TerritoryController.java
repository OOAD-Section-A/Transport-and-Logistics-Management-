package controllers;

import entities.Territory;
import services.TransportService;
import java.util.List;

/**
 * REST Controller for Territory Management APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class TerritoryController {

    private TransportService transportService;

    public TerritoryController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * GET /territories
     * Lists geographic territories.
     */
    public List<Territory> getAllTerritories() {
        return transportService.getAllTerritories();
    }
}
package controllers;

import entities.Shipment;
import services.TransportService;
import java.util.List;

/**
 * REST Controller for Shipment-related APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 * Methods call existing TransportService for business logic.
 */
public class ShipmentController {

    private TransportService transportService;

    public ShipmentController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * GET /shipments/{id}
     * Retrieves shipment details by ID.
     */
    public Shipment getShipment(String id) {
        return transportService.getShipment(id);
    }

    /**
     * POST /shipments
     * Creates a new shipment.
     */
    public Shipment createShipment(Shipment shipment) {
        return transportService.createShipment(shipment);
    }

    /**
     * PUT /shipments/{id}/status
     * Updates shipment status.
     */
    public boolean updateShipmentStatus(String id, String status) {
        return transportService.updateShipmentStatus(id, status);
    }

    /**
     * GET /shipments
     * Lists all shipments with optional filtering.
     */
    public List<Shipment> getAllShipments(String status, int page, int size) {
        return transportService.getAllShipments(status, page, size);
    }
}
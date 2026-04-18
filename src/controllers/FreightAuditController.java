package controllers;

import entities.FreightAudit;
import services.TransportService;

/**
 * REST Controller for Freight Audit APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class FreightAuditController {

    private TransportService transportService;

    public FreightAuditController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * POST /freight-audit
     * Performs freight cost audit.
     */
    public FreightAudit performAudit(String shipmentId, double invoicedAmount, double contractAmount) {
        return transportService.auditFreight(shipmentId, invoicedAmount, contractAmount);
    }
}
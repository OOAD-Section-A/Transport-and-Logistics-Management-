package controllers;

import facade.TransportFacade;
import entities.Shipment;
import entities.Supplier;
import entities.Carrier;
import behavioral.command.CreateShipmentCommand;
import behavioral.command.UpdateShipmentCommand;
import behavioral.chain.Handler;
import behavioral.chain.ValidationHandler;
import behavioral.chain.LoggingHandler;
import behavioral.iterator.ShipmentIterator;
import factories.ShipmentBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller: TransportController
 * MVC: Controller layer - Handles user requests
 * GRASP: Controller - Entry point for all transport operations
 * SOLID: SRP - Delegates actual logic to service/facade
 * 
 * Responsibilities:
 * - Accept user requests
 * - Call facade for business logic
 * - Does NOT contain business logic
 * - Does NOT interact directly with repositories
 */
public class TransportController {
    private TransportFacade facade;

    public TransportController() {
        this.facade = new TransportFacade();
    }

    /**
     * Handle create shipment request using Command pattern
     * Validates using Chain of Responsibility before creation
     */
    public void handleCreateShipment(Shipment shipment) {
        System.out.println("\n=== CREATE SHIPMENT ===");
        
        // Step 1: Validate using Chain of Responsibility
        Handler validationChain = buildValidationChain();
        validationChain.handle(shipment);
        
        // Step 2: Create command and execute
        CreateShipmentCommand command = new CreateShipmentCommand(
            new services.TransportService(facade.getRepository()), 
            shipment
        );
        command.execute();
    }

    /**
     * Handle update shipment status request using Command pattern
     */
    public void handleUpdateShipment(String shipmentId, String newStatus) {
        System.out.println("\n=== UPDATE SHIPMENT ===");
        
        UpdateShipmentCommand command = new UpdateShipmentCommand(
            new services.TransportService(facade.getRepository()), 
            shipmentId, 
            newStatus
        );
        command.execute();
    }

    /**
     * Handle get shipment request
     */
    public Shipment handleGetShipment(String shipmentId) {
        System.out.println("\n=== GET SHIPMENT ===");
        return facade.getShipment(shipmentId);
    }

    /**
     * Handle list all shipments using Iterator pattern
     */
    public void handleListAllShipments() {
        System.out.println("\n=== LIST ALL SHIPMENTS (using Iterator) ===");
        
        ShipmentIterator iterator = new ShipmentIterator(facade.getRepository());
        System.out.println("Total shipments: " + iterator.getTotalCount());
        
        while (iterator.hasNext()) {
            Shipment shipment = iterator.next();
            System.out.println("  " + iterator.getIndex() + ". " + shipment);
        }
    }

    /**
     * Handle external system integration
     */
    public void handleFetchExternalData(String shipmentId) {
        System.out.println("\n=== FETCH EXTERNAL SHIPMENT DATA ===");
        String data = facade.getShipmentWithExternalData(shipmentId);
        System.out.println(data);
    }

    // New controller methods
    public entities.FreightAudit handleAuditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        System.out.println("\n=== AUDIT FREIGHT ===");
        return facade.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
    }

    public entities.ConstraintPlanner handlePlanConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) {
        System.out.println("\n=== PLAN CONSTRAINTS ===");
        return facade.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
    }

    public entities.Territory handleManageTerritory(String territoryId, String zoneName, String area, int drivers) {
        System.out.println("\n=== MANAGE TERRITORY ===");
        return facade.manageTerritory(territoryId, zoneName, area, drivers);
    }

    public entities.OrderOrchestrator handleOrchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        System.out.println("\n=== ORCHESTRATE ORDER ===");
        return facade.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId);
    }

    public entities.SupplierPortal handleIntegrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        System.out.println("\n=== INTEGRATE SUPPLIER PORTAL ===");
        return facade.integrateSupplierPortal(portalId, supplierId, orderDetails);
    }

    public entities.TrackingSync handleSyncTracking(String syncId, String orderId, String trackingNumber) {
        System.out.println("\n=== SYNC TRACKING ===");
        return facade.syncTracking(syncId, orderId, trackingNumber);
    }

    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        System.out.println("\n=== HANDLE REVERSE LOGISTICS ===");
        return facade.handleReverseLogistics(returnId, orderId, supplierId, refund);
    }

    /**
     * Build validation chain using Chain of Responsibility pattern
     */
    private Handler buildValidationChain() {
        Handler validation = new ValidationHandler();
        Handler logging = new LoggingHandler();
        validation.setNextHandler(logging);
        return validation;
    }

    /**
     * Get facade info
     */
    public String getSystemInfo() {
        return facade.getFacadeInfo();
    }
}

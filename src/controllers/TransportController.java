package controllers;

import java.util.List;

import behavioral.chain.Handler;
import behavioral.chain.LoggingHandler;
import behavioral.chain.ValidationHandler;
import behavioral.command.CreateShipmentCommand;
import behavioral.command.UpdateShipmentCommand;
import behavioral.iterator.ShipmentIterator;
import entities.Shipment;
import facade.TransportFacade;

public class TransportController {
    private final TransportFacade facade;

    public TransportController() {
        this.facade = new TransportFacade();
    }

    public void handleCreateShipment(Shipment shipment) {
        System.out.println("\n=== CREATE SHIPMENT ===");
        Handler validationChain = buildValidationChain();
        validationChain.handle(shipment);
        CreateShipmentCommand command = new CreateShipmentCommand(facade.getTransportService(), shipment);
        command.execute();
    }

    public void handleUpdateShipment(String shipmentId, String newStatus) {
        System.out.println("\n=== UPDATE SHIPMENT ===");
        UpdateShipmentCommand command = new UpdateShipmentCommand(facade.getTransportService(), shipmentId, newStatus);
        command.execute();
    }

    public Shipment handleGetShipment(String shipmentId) {
        System.out.println("\n=== GET SHIPMENT ===");
        return facade.getShipment(shipmentId);
    }

    public void handleListAllShipments() {
        System.out.println("\n=== LIST ALL SHIPMENTS (using Iterator) ===");
        ShipmentIterator iterator = new ShipmentIterator(facade.getRepository());
        System.out.println("Total shipments: " + iterator.getTotalCount());
        while (iterator.hasNext()) {
            Shipment shipment = iterator.next();
            System.out.println("  " + iterator.getIndex() + ". " + shipment);
        }
    }

    public void handleFetchExternalData(String shipmentId) {
        System.out.println("\n=== FETCH EXTERNAL SHIPMENT DATA ===");
        String data = facade.getShipmentWithExternalData(shipmentId);
        System.out.println(data);
    }

    public entities.FreightAudit handleAuditFreight(String auditId, String shipmentId, double invoicedAmount,
                                                    double contractAmount) {
        System.out.println("\n=== AUDIT FREIGHT ===");
        return facade.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
    }

    public entities.ConstraintPlanner handlePlanConstraints(String planId, String shipmentId, double weightLimit,
                                                            double heightLimit, int shiftHours, String window) {
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

    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId,
                                                            String supplierId, double refund) {
        System.out.println("\n=== HANDLE REVERSE LOGISTICS ===");
        return facade.handleReverseLogistics(returnId, orderId, supplierId, refund);
    }

    private Handler buildValidationChain() {
        Handler validation = new ValidationHandler();
        Handler logging = new LoggingHandler();
        validation.setNextHandler(logging);
        return validation;
    }

    public String getSystemInfo() { return facade.getFacadeInfo(); }
}

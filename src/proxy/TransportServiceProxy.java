package proxy;

import interfaces.ITransportService;
import entities.Shipment;
import java.util.List;
import com.scm.subsystems.TransportLogisticsSubsystem;

/**
 * Proxy: TransportServiceProxy
 * STRUCTURAL PATTERN: Proxy Pattern
 * Wraps TransportService to add logging/monitoring capabilities
 * SOLID: SRP - Single responsibility (cross-cutting concerns)
 * 
 * Benefits:
 * - Adds logging without modifying original service
 * - Can add security checks, caching, etc.
 * - Controls access to real service
 */
public class TransportServiceProxy implements ITransportService {
    private final ITransportService realService;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;

    public TransportServiceProxy(ITransportService realService) {
        this.realService = realService;
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        long startTime = System.currentTimeMillis();
        System.out.println("[PROXY LOG] Creating shipment: " + shipment.getShipmentId());
        
        try {
            Shipment created = realService.createShipment(shipment);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[PROXY LOG] Shipment created successfully in " + duration + "ms");
            return created;
        } catch (Exception e) {
            exceptions.onNoViableRouteFound(
                shipment != null ? shipment.getShipmentId() : "UNKNOWN",
                "Proxy createShipment failed"
            );
            return null;
        }
    }

    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        long startTime = System.currentTimeMillis();
        System.out.println("[PROXY LOG] Updating shipment " + shipmentId + " to status: " + status);
        
        try {
            realService.updateShipmentStatus(shipmentId, status);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[PROXY LOG] Status updated successfully in " + duration + "ms");
        } catch (Exception e) {
            exceptions.onCarrierApiTimeout("STATUS_UPDATE", 3000);
            return;
        }
    }

    @Override
    public Shipment getShipment(String shipmentId) {
        System.out.println("[PROXY LOG] Retrieving shipment: " + shipmentId);
        Shipment result = realService.getShipment(shipmentId);
        if (result != null) {
            System.out.println("[PROXY LOG] Shipment found");
        } else {
            System.out.println("[PROXY LOG] Shipment not found");
        }
        return result;
    }

    @Override
    public List<Shipment> getAllShipments() {
        System.out.println("[PROXY LOG] Retrieving all shipments");
        List<Shipment> results = realService.getAllShipments();
        System.out.println("[PROXY LOG] Retrieved " + results.size() + " shipments");
        return results;
    }

    // New proxy methods with logging
    @Override
    public entities.FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        System.out.println("[PROXY LOG] Auditing freight for shipment: " + shipmentId);
        entities.FreightAudit result = realService.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
        System.out.println("[PROXY LOG] Audit completed: " + result.getDiscrepancyFlag());
        return result;
    }

    @Override
    public entities.ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) {
        System.out.println("[PROXY LOG] Planning constraints for shipment: " + shipmentId);
        entities.ConstraintPlanner result = realService.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
        System.out.println("[PROXY LOG] Constraints planned");
        return result;
    }

    @Override
    public entities.Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        System.out.println("[PROXY LOG] Managing territory: " + territoryId);
        entities.Territory result = realService.manageTerritory(territoryId, zoneName, area, drivers);
        System.out.println("[PROXY LOG] Territory managed");
        return result;
    }

    @Override
    public entities.OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        System.out.println("[PROXY LOG] Orchestrating order: " + orderId);
        entities.OrderOrchestrator result = realService.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId);
        System.out.println("[PROXY LOG] Order orchestrated");
        return result;
    }

    @Override
    public entities.SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        System.out.println("[PROXY LOG] Integrating supplier portal: " + portalId);
        entities.SupplierPortal result = realService.integrateSupplierPortal(portalId, supplierId, orderDetails);
        System.out.println("[PROXY LOG] Portal integrated");
        return result;
    }

    @Override
    public entities.TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        System.out.println("[PROXY LOG] Syncing tracking for order: " + orderId);
        entities.TrackingSync result = realService.syncTracking(syncId, orderId, trackingNumber);
        System.out.println("[PROXY LOG] Tracking synced");
        return result;
    }

    @Override
    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        System.out.println("[PROXY LOG] Handling reverse logistics for return: " + returnId);
        entities.ReverseLogistics result = realService.handleReverseLogistics(returnId, orderId, supplierId, refund);
        System.out.println("[PROXY LOG] Reverse logistics handled");
        return result;
    }
}

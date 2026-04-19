package proxy;

import interfaces.ITransportService;
import entities.Shipment;
import java.util.List;
import com.scm.subsystems.TransportLogisticsSubsystem;

public class TransportServiceProxy implements ITransportService {
    private final ITransportService realService;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;
    private final TransportProxyFeatureOperations featureOperations;

    public TransportServiceProxy(ITransportService realService) {
        this.realService = realService;
        this.featureOperations = new TransportProxyFeatureOperations(realService);
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        String shipmentId = shipment == null ? "UNKNOWN" : shipment.getShipmentId();
        long startTime = System.currentTimeMillis();
        log("[PROXY LOG] Creating shipment: " + shipmentId);
        
        try {
            Shipment created = realService.createShipment(shipment);
            long duration = System.currentTimeMillis() - startTime;
            log("[PROXY LOG] Shipment created successfully in " + duration + "ms");
            return created;
        } catch (RuntimeException ex) {
            exceptions.onNoViableRouteFound(shipmentId, "Proxy createShipment failed");
            return null;
        }
    }

    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        long startTime = System.currentTimeMillis();
        log("[PROXY LOG] Updating shipment " + shipmentId + " to status: " + status);
        
        try {
            realService.updateShipmentStatus(shipmentId, status);
            long duration = System.currentTimeMillis() - startTime;
            log("[PROXY LOG] Status updated successfully in " + duration + "ms");
        } catch (RuntimeException ex) {
            exceptions.onCarrierApiTimeout("STATUS_UPDATE", 3000);
        }
    }

    @Override
    public Shipment getShipment(String shipmentId) {
        log("[PROXY LOG] Retrieving shipment: " + shipmentId);
        Shipment result = realService.getShipment(shipmentId);
        if (result != null) {
            log("[PROXY LOG] Shipment found");
        } else {
            log("[PROXY LOG] Shipment not found");
        }
        return result;
    }

    @Override
    public List<Shipment> getAllShipments() {
        log("[PROXY LOG] Retrieving all shipments");
        List<Shipment> results = realService.getAllShipments();
        log("[PROXY LOG] Retrieved " + results.size() + " shipments");
        return results;
    }

    @Override
    public entities.FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        return featureOperations.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
    }

    @Override
    public entities.ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) {
        return featureOperations.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
    }

    @Override
    public entities.Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        return featureOperations.manageTerritory(territoryId, zoneName, area, drivers);
    }

    @Override
    public entities.OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        return featureOperations.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId);
    }

    @Override
    public entities.SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        return featureOperations.integrateSupplierPortal(portalId, supplierId, orderDetails);
    }

    @Override
    public entities.TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        return featureOperations.syncTracking(syncId, orderId, trackingNumber);
    }

    @Override
    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        return featureOperations.handleReverseLogistics(returnId, orderId, supplierId, refund);
    }

    private static void log(String message) {
        System.out.println(message);
    }
}

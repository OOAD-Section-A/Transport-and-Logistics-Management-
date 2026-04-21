package interfaces;

import entities.*;
import java.util.List;

/**
 * Interface: ITransportService
 * SOLID: DIP - Dependency Inversion Principle
 * Used by Controller to interact with service layer
 */
public interface ITransportService {
    Shipment createShipment(Shipment shipment);
    void updateShipmentStatus(String shipmentId, String status);
    Shipment getShipment(String shipmentId);
    List<Shipment> getAllShipments();

    // New features
    FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount);
    ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window);
    Territory manageTerritory(String territoryId, String zoneName, String area, int drivers);
    OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId);
    SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails);
    TrackingSync syncTracking(String syncId, String orderId, String trackingNumber);
    ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund);
    Rider getRiderDetails(String riderId);
    List<Rider> getAvailableRiders(String zone);
    RoutePlan calculateOptimalRoute(String pickup, String dropoff, List<String> waypoints);
    void reportVehicleHealth(String riderId, VehicleHealthReport report);
    List<GeofenceZone> getLogisticsHubZones();
    void notifyRiderAvailable(String riderId);
}

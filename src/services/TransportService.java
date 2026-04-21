package services;

import com.scm.core.SCMException;
import com.scm.factory.SCMExceptionFactory;
import com.scm.handler.SCMExceptionHandler;
import com.scm.subsystems.TransportLogisticsSubsystem;
import entities.*;
import interfaces.ITransportService;
import repositories.TransportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransportService implements ITransportService {
    private static final double MAX_SHIPMENT_WEIGHT = 100.0;
    private final TransportRepository repository;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;
    private final TransportFeatureFactory featureFactory = new TransportFeatureFactory();
    private final TransportQueryOperations queryOperations;

    public TransportService(TransportRepository repository) {
        this.repository = repository;
        this.queryOperations = new TransportQueryOperations(repository, exceptions);
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        if (shipment == null) {
            handleUnregistered("shipment cannot be null");
            return null;
        }
        if (shipment.getDestination() == null || !shipment.getDestination().matches("\\d{6}")) {
            exceptions.onInvalidDestPincode(shipment.getShipmentId(), shipment.getDestination());
            return null;
        }
        if (shipment.getWeight() > MAX_SHIPMENT_WEIGHT) {
            exceptions.onWeightLimitExceeded(shipment.getShipmentId(), shipment.getWeight(), MAX_SHIPMENT_WEIGHT);
            return null;
        }
        if (shipment.getSupplierId() == null || shipment.getSupplierId().isBlank()) {
            exceptions.onSupplierOutOfStock("UNKNOWN", "UNSPECIFIED", 1);
            return null;
        }
        try {
            repository.addShipment(shipment);
            return shipment;
        } catch (RuntimeException ex) {
            handleUnregistered("createShipment failed: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        Shipment shipment = repository.getShipment(shipmentId);
        if (shipment != null) {
            shipment.setStatus(status);
            repository.updateShipment(shipmentId, shipment);
        }
    }

    @Override
    public Shipment getShipment(String shipmentId) { return repository.getShipment(shipmentId); }

    @Override
    public List<Shipment> getAllShipments() { return repository.getAllShipments(); }

    @Override
    public FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) { return featureFactory.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount); }

    @Override
    public ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) { return featureFactory.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window); }

    @Override
    public Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) { return featureFactory.manageTerritory(territoryId, zoneName, area, drivers); }

    @Override
    public OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) { return featureFactory.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId); }

    @Override
    public SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) { return featureFactory.integrateSupplierPortal(portalId, supplierId, orderDetails); }

    @Override
    public TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) { return featureFactory.syncTracking(syncId, orderId, trackingNumber); }

    @Override
    public ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) { return featureFactory.handleReverseLogistics(returnId, orderId, supplierId, refund); }
    @Override
public Rider getRiderDetails(String riderId) {
    // Query repository or external system for rider details
    return transportRepository.getRider(riderId);
}

@Override
public List<Rider> getAvailableRiders(String zone) {
    // Filter riders by zone and availability
    return transportRepository.getAllRiders().stream()
        .filter(r -> r.getZone().equals(zone) && r.isAvailable())
        .collect(Collectors.toList());
}

@Override
public RoutePlan calculateOptimalRoute(String pickup, String dropoff, List<String> waypoints) {
    // Implement route calculation logic (e.g., using a routing algorithm or external API)
    // For simplicity, mock a plan
    RoutePlan plan = new RoutePlan(pickup, dropoff, waypoints, 15.5, 45.0);
    return plan;
}

@Override
public void reportVehicleHealth(String riderId, VehicleHealthReport report) {
    // Validate and store/report health data
    if (report.getFuelLevel() < 0.1) {
        throw new IllegalArgumentException("Fuel level too low");
    }
    transportRepository.saveVehicleHealthReport(riderId, report);
}
@Override
public List<GeofenceZone> getLogisticsHubZones() {
    // Return predefined hub zones (e.g., warehouses)
    List<GeofenceZone> zones = new ArrayList<>();
    zones.add(new GeofenceZone("HUB1", "Main Warehouse", 12.9716, 77.5946, 1000.0));
    return zones;
}

@Override
public void notifyRiderAvailable(String riderId) {
    Rider_info rider = transportRepository.getRider(riderId);
    if (rider != null) {
        rider.setAvailable(true);
        transportRepository.saveRider(rider);
    }
}

    public List<Shipment> getAllShipments(String status, int page, int size) { return queryOperations.getAllShipments(status, page, size); }
    public List<Carrier> getAllCarriers(String mode) { return queryOperations.getAllCarriers(mode); }
    public Carrier createCarrier(Carrier carrier) { return queryOperations.createCarrier(carrier); }
    public Map<String, Object> optimizeRoute(String origin, String destination, String constraints) { return queryOperations.optimizeRoute(origin, destination, constraints); }
    public List<Territory> getAllTerritories() { return queryOperations.getAllTerritories(); }
    public String reportException(SCMException exception) { return queryOperations.reportException(exception); }
    public Map<String, Object> getTrackingData(String shipmentId) { return queryOperations.getTrackingData(shipmentId); }

    private static void handleUnregistered(String message) {
        SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", message)
        );
    }
}

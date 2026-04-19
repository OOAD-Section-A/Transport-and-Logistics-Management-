package services;

import entities.*;
import interfaces.ITransportService;
import repositories.TransportRepository;
import com.scm.core.SCMException;
import com.scm.factory.SCMExceptionFactory;
import com.scm.handler.SCMExceptionHandler;
import com.scm.subsystems.TransportLogisticsSubsystem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service: TransportService
 * SOLID: SRP - Single responsibility (business logic only)
 * MVC: Model layer - handles business operations
 */
public class TransportService implements ITransportService {
    private final TransportRepository repository;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;

    public TransportService(TransportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Shipment createShipment(Shipment shipment) {
        if (shipment == null) {
            SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", "shipment cannot be null")
            );
            return null;
        }

        if (shipment.getDestination() == null || !shipment.getDestination().matches("\\d{6}")) {
            exceptions.onInvalidDestPincode(shipment.getShipmentId(), shipment.getDestination());
            return null;
        }

        if (shipment.getWeight() > 100.0) {
            exceptions.onWeightLimitExceeded(shipment.getShipmentId(), shipment.getWeight(), 100.0);
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
            SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", "createShipment failed: " + ex.getMessage())
            );
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
    public Shipment getShipment(String shipmentId) {
        return repository.getShipment(shipmentId);
    }

    @Override
    public List<Shipment> getAllShipments() {
        return repository.getAllShipments();
    }

    // New implementations
    @Override
    public FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        String flag = (invoicedAmount > contractAmount) ? "OVERCHARGE" : (invoicedAmount < contractAmount) ? "UNDERCHARGE" : "NONE";
        return new FreightAudit(auditId, shipmentId, invoicedAmount, contractAmount, flag);
    }

    @Override
    public ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) {
        return new ConstraintPlanner(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
    }

    @Override
    public Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        return new Territory(territoryId, zoneName, area, drivers);
    }

    @Override
    public OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        String poNumber = isThirdParty ? "PO-" + orderId : null;
        return new OrderOrchestrator(orderId, salesOrderId, isThirdParty, supplierId, poNumber);
    }

    @Override
    public SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        return new SupplierPortal(portalId, supplierId, orderDetails, "ASN-" + portalId, "PackingSlip-" + portalId);
    }

    @Override
    public TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        return new TrackingSync(syncId, orderId, trackingNumber, "Updated to customer");
    }

    @Override
    public ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        return new ReverseLogistics(returnId, orderId, supplierId, refund, "PENDING");
    }

    // API-specific methods
    public List<Shipment> getAllShipments(String status, int page, int size) {
        List<Shipment> all = repository.getAllShipments();
        if (status != null) {
            all = all.stream().filter(s -> status.equals(s.getStatus())).collect(Collectors.toList());
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    public List<Carrier> getAllCarriers(String mode) {
        return repository.getAllCarriers(mode);
    }

    public Carrier createCarrier(Carrier carrier) {
        repository.addCarrier(carrier);
        return carrier;
    }

    public Map<String, Object> optimizeRoute(String origin, String destination, String constraints) {
        if (origin == null || destination == null || origin.isBlank() || destination.isBlank()) {
            exceptions.onNoViableRouteFound("UNKNOWN", "UNKNOWN");
            return Collections.emptyMap();
        }
        if (constraints != null && constraints.toLowerCase(Locale.ROOT).contains("timeout")) {
            exceptions.onCarrierApiTimeout("CARRIER_OPTIMIZER_API", 3000);
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("route", Arrays.asList(origin, "Intermediate Point", destination));
        result.put("estimatedTime", "4 hours");
        result.put("cost", 120.0);
        return result;
    }

    public List<Territory> getAllTerritories() {
        return repository.getAllTerritories();
    }

    public String reportException(SCMException exception) {
        SCMExceptionHandler.INSTANCE.handle(exception);
        return "Exception ID: " + exception.getExceptionId();
    }

    public Map<String, Object> getTrackingData(String shipmentId) {
        if (shipmentId == null || shipmentId.isBlank()) {
            exceptions.onGpsSignalLost("UNKNOWN_VEHICLE");
            return Collections.emptyMap();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("currentLocation", "En route to destination");
        data.put("eta", "2026-04-20T14:00:00Z");
        data.put("status", "On time");
        return data;
    }
}

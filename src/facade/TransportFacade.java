package facade;

import entities.Supplier;
import entities.Carrier;
import entities.Shipment;
import interfaces.ITransportService;
import repositories.TransportRepository;
import services.TransportService;
import proxy.TransportServiceProxy;
import factories.ShipmentBuilder;
import factories.PrototypeRegistry;
import adapters.ExternalTransportAdapter;
import interfaces.IExternalTransportSystem;
import flyweight.CarrierFlyweightFactory;
import java.util.List;

/**
 * Facade: TransportFacade
 * STRUCTURAL PATTERN: Facade Pattern
 * Provides simplified interface for controller
 * SOLID: SRP - Single responsibility (subsystem coordination)
 * GRASP: Low Coupling - Controller only calls facade
 * 
 * Benefits:
 * - Hides complex subsystem interactions
 * - Provides single entry point
 * - Decouples controller from implementation details
 */
public class TransportFacade {
    private ITransportService transportService;
    private TransportRepository repository;
    private IExternalTransportSystem externalAdapter;
    private CarrierFlyweightFactory carrierFactory;
    private PrototypeRegistry prototypeRegistry;

    public TransportFacade() {
        // Initialize repository
        this.repository = new TransportRepository();
        
        // Initialize service
        TransportService realService = new TransportService(repository);
        
        // Wrap service with proxy for logging
        this.transportService = new TransportServiceProxy(realService);
        
        // Initialize external system adapter
        this.externalAdapter = new ExternalTransportAdapter();
        
        // Initialize flyweight factory
        this.carrierFactory = CarrierFlyweightFactory.getInstance();
        
        // Initialize prototype registry
        this.prototypeRegistry = PrototypeRegistry.getInstance();
    }

    /**
     * Simple facade method: Create shipment
     * Handles all internal coordination
     */
    public void createShipment(Shipment shipment) {
        transportService.createShipment(shipment);
    }

    /**
     * Simple facade method: Update shipment status
     */
    public void updateShipmentStatus(String shipmentId, String status) {
        transportService.updateShipmentStatus(shipmentId, status);
    }

    /**
     * Simple facade method: Get shipment
     */
    public Shipment getShipment(String shipmentId) {
        return transportService.getShipment(shipmentId);
    }

    /**
     * Simple facade method: Get all shipments
     */
    public List<Shipment> getAllShipments() {
        return transportService.getAllShipments();
    }

    /**
     * Facade method: Get shipment with external data
     * Demonstrates integration with external system
     */
    public String getShipmentWithExternalData(String shipmentId) {
        System.out.println("\n[FACADE] Fetching shipment with external data");
        String internalData = transportService.getShipment(shipmentId) != null ? 
                              transportService.getShipment(shipmentId).toString() : "Not found";
        String externalData = externalAdapter.fetchExternalShipmentData(shipmentId);
        return internalData + "\n" + externalData;
    }

    /**
     * Facade method: Get or create carrier using flyweight
     */
    public Carrier getCarrier(String carrierId, String carrierName, String transportMode, double capacity) {
        return carrierFactory.getCarrier(carrierId, carrierName, transportMode, capacity);
    }

    /**
     * Facade method: Build shipment using builder
     */
    public ShipmentBuilder createShipmentBuilder(String shipmentId) {
        return new ShipmentBuilder(shipmentId);
    }

    /**
     * Facade method: Register shipment template for cloning
     */
    public void registerShipmentTemplate(String templateName, Shipment shipment) {
        prototypeRegistry.registerPrototype(templateName, shipment);
        System.out.println("[FACADE] Template '" + templateName + "' registered for cloning");
    }

    /**
     * Facade method: Clone shipment from template
     */
    public Shipment cloneShipmentFromTemplate(String templateName, String newShipmentId) {
        Shipment cloned = prototypeRegistry.clonePrototype(templateName);
        System.out.println("[FACADE] Cloned shipment '" + templateName + "' as " + newShipmentId);
        return cloned;
    }

    /**
     * Get repository for iterator pattern
     */
    public TransportRepository getRepository() {
        return repository;
    }

    /**
     * Get facade info
     */
    public String getFacadeInfo() {
        return "TransportFacade - Simplified API for Transport subsystem\n" +
               "Cached Carriers: " + carrierFactory.getCachedCarrierCount() + "\n" +
               "Shipments stored: " + repository.size();
    }

    // New facade methods
    public entities.FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        return transportService.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
    }

    public entities.ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit, double heightLimit, int shiftHours, String window) {
        return transportService.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
    }

    public entities.Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        return transportService.manageTerritory(territoryId, zoneName, area, drivers);
    }

    public entities.OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        return transportService.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId);
    }

    public entities.SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        return transportService.integrateSupplierPortal(portalId, supplierId, orderDetails);
    }

    public entities.TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        return transportService.syncTracking(syncId, orderId, trackingNumber);
    }

    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        return transportService.handleReverseLogistics(returnId, orderId, supplierId, refund);
    }
}

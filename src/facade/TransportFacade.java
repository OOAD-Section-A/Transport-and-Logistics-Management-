package facade;

import java.util.List;

import adapters.ExternalTransportAdapter;
import entities.Carrier;
import entities.Shipment;
import factories.PrototypeRegistry;
import factories.ShipmentBuilder;
import flyweight.CarrierFlyweightFactory;
import interfaces.IExternalTransportSystem;
import interfaces.ITransportService;
import proxy.TransportServiceProxy;
import repositories.TransportRepository;
import repositories.DatabaseTransportRepository;
import services.TransportService;

public class TransportFacade {
    private final ITransportService transportService;
    private final TransportRepository repository;
    private final IExternalTransportSystem externalAdapter;
    private final CarrierFlyweightFactory carrierFactory;
    private final PrototypeRegistry prototypeRegistry;

    public TransportFacade() {
        repository = new DatabaseTransportRepository();
        TransportService realService = new TransportService(repository);
        transportService = new TransportServiceProxy(realService);
        externalAdapter = new ExternalTransportAdapter();
        carrierFactory = CarrierFlyweightFactory.getInstance();
        prototypeRegistry = PrototypeRegistry.getInstance();
    }

    public ITransportService getTransportService() { return transportService; }
    public void createShipment(Shipment shipment) { transportService.createShipment(shipment); }
    public void updateShipmentStatus(String shipmentId, String status) { transportService.updateShipmentStatus(shipmentId, status); }
    public Shipment getShipment(String shipmentId) { return transportService.getShipment(shipmentId); }
    public List<Shipment> getAllShipments() { return transportService.getAllShipments(); }

    public String getShipmentWithExternalData(String shipmentId) {
        System.out.println("\n[FACADE] Fetching shipment with external data");
        Shipment shipment = transportService.getShipment(shipmentId);
        String internalData = shipment == null ? "Not found" : shipment.toString();
        String externalData = externalAdapter.fetchExternalShipmentData(shipmentId);
        return internalData + "\n" + externalData;
    }

    public Carrier getCarrier(String carrierId, String carrierName, String transportMode, double capacity) {
        return carrierFactory.getCarrier(carrierId, carrierName, transportMode, capacity);
    }

    public ShipmentBuilder createShipmentBuilder(String shipmentId) { return new ShipmentBuilder(shipmentId); }

    public void registerShipmentTemplate(String templateName, Shipment shipment) {
        prototypeRegistry.registerPrototype(templateName, shipment);
        System.out.println("[FACADE] Template '" + templateName + "' registered for cloning");
    }

    public Shipment cloneShipmentFromTemplate(String templateName, String newShipmentId) {
        Shipment cloned = prototypeRegistry.clonePrototype(templateName);
        System.out.println("[FACADE] Cloned shipment '" + templateName + "' as " + newShipmentId);
        return cloned;
    }

    public TransportRepository getRepository() { return repository; }

    public String getFacadeInfo() {
        return "TransportFacade - Simplified API for Transport subsystem\n" +
               "Cached Carriers: " + carrierFactory.getCachedCarrierCount() + "\n" +
               "Shipments stored: " + repository.size();
    }

    public entities.FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount,
                                              double contractAmount) {
        return transportService.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount);
    }

    public entities.ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit,
                                                     double heightLimit, int shiftHours, String window) {
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

    public entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId,
                                                            String supplierId, double refund) {
        return transportService.handleReverseLogistics(returnId, orderId, supplierId, refund);
    }
}

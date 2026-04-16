package proxy;

import interfaces.ITransportService;
import entities.Shipment;
import java.util.List;

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
    private ITransportService realService;

    public TransportServiceProxy(ITransportService realService) {
        this.realService = realService;
    }

    @Override
    public void createShipment(Shipment shipment) {
        long startTime = System.currentTimeMillis();
        System.out.println("[PROXY LOG] Creating shipment: " + shipment.getShipmentId());
        
        try {
            realService.createShipment(shipment);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[PROXY LOG] Shipment created successfully in " + duration + "ms");
        } catch (Exception e) {
            System.out.println("[PROXY LOG] ERROR creating shipment: " + e.getMessage());
            throw e;
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
            System.out.println("[PROXY LOG] ERROR updating status: " + e.getMessage());
            throw e;
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
}

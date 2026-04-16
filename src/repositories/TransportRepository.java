package repositories;

import entities.Shipment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Repository: TransportRepository
 * SOLID: SRP - Single responsibility (persistence logic)
 * GRASP: Information Expert - Manages shipment data storage
 * Uses in-memory storage (HashMap) for demo purposes
 */
public class TransportRepository {
    private Map<String, Shipment> shipmentStore = new HashMap<>();

    /**
     * Store a shipment in repository
     */
    public void addShipment(Shipment shipment) {
        shipmentStore.put(shipment.getShipmentId(), shipment);
    }

    /**
     * Retrieve a specific shipment by ID
     */
    public Shipment getShipment(String shipmentId) {
        return shipmentStore.get(shipmentId);
    }

    /**
     * Get all shipments
     */
    public List<Shipment> getAllShipments() {
        return new ArrayList<>(shipmentStore.values());
    }

    /**
     * Update a shipment (idempotent)
     */
    public void updateShipment(String shipmentId, Shipment shipment) {
        if (shipmentStore.containsKey(shipmentId)) {
            shipmentStore.put(shipmentId, shipment);
        }
    }

    /**
     * Delete a shipment
     */
    public void deleteShipment(String shipmentId) {
        shipmentStore.remove(shipmentId);
    }

    /**
     * Check if shipment exists
     */
    public boolean exists(String shipmentId) {
        return shipmentStore.containsKey(shipmentId);
    }

    /**
     * Get total number of shipments
     */
    public int size() {
        return shipmentStore.size();
    }
}

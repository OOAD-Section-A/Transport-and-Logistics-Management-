package repositories;

import entities.Shipment;
import entities.Carrier;
import entities.Territory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Repository: TransportRepository
 * SOLID: SRP - Single responsibility (persistence logic)
 * GRASP: Information Expert - Manages shipment data storage
 * Uses in-memory storage (HashMap) for demo purposes
 */
public class TransportRepository {
    private Map<String, Shipment> shipmentStore = new HashMap<>();
    private Map<String, Carrier> carrierStore = new HashMap<>();
    private Map<String, Territory> territoryStore = new HashMap<>();

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

    // Carrier methods
    public void addCarrier(Carrier carrier) {
        carrierStore.put(carrier.getCarrierId(), carrier);
    }

    public List<Carrier> getAllCarriers(String mode) {
        List<Carrier> all = new ArrayList<>(carrierStore.values());
        if (mode != null) {
            all = all.stream().filter(c -> mode.equals(c.getTransportMode())).collect(Collectors.toList());
        }
        return all;
    }

    // Territory methods
    public List<Territory> getAllTerritories() {
        return new ArrayList<>(territoryStore.values());
    }
    private Map<String, Rider> riders = new HashMap<>();
    private Map<String, VehicleHealthReport> healthReports = new HashMap<>();

    public Rider getRider(String riderId) { return riders.get(riderId); }
    public List<Rider> getAllRiders() { return new ArrayList<>(riders.values());}
    public void saveVehicleHealthReport(String riderId, VehicleHealthReport report) { healthReports.put(riderId, report); }
}

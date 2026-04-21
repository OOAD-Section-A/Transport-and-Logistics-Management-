package com.scm.subsystems;

import adapters.DatabasePersistenceAdapter;

public enum TransportLogisticsSubsystem {
    INSTANCE;
    
    private final DatabasePersistenceAdapter dbAdapter;

    TransportLogisticsSubsystem() {
        this.dbAdapter = new DatabasePersistenceAdapter();
    }
    
    public void onInvalidDestPincode(String shipmentId, String pin) {
        dbAdapter.persistShipmentAlert(shipmentId, "Invalid destination pincode: " + pin, "HIGH");
    }
    public void onWeightLimitExceeded(String shipmentId, double weight, double max) {
        dbAdapter.persistShipmentAlert(shipmentId, "Weight limit exceeded: " + weight + " > " + max, "CRITICAL");
    }
    public void onSupplierOutOfStock(String val1, String val2, int val3) {
        dbAdapter.persistShipmentAlert("SYS-SUPPLIER", "Supplier out of stock", "HIGH");
    }
    public void onNoViableRouteFound(String shipmentId, String reason) {
        dbAdapter.persistShipmentAlert(shipmentId, "No viable route found: " + reason, "CRITICAL");
    }
    public void onCarrierApiTimeout(String id, int timeout) {
        dbAdapter.persistShipmentAlert("SYS-CARRIER-" + id, "Carrier API timeout after " + timeout + "ms", "MEDIUM");
    }
    public void onCarrierUnavailable(String resourceId) {
        dbAdapter.persistShipmentAlert("SYS-CARRIER-" + resourceId, "Carrier unavailable", "HIGH");
    }
    public void onCriticalTransitDelay(String entityId, long elapsedMs) {
        dbAdapter.persistShipmentAlert(entityId, "Critical transit delay: " + elapsedMs + "ms", "CRITICAL");
    }
    public void onGpsSignalLost(String vehicleId) {
        dbAdapter.persistShipmentAlert("SYS-VEHICLE-" + vehicleId, "GPS signal lost", "HIGH");
    }
}

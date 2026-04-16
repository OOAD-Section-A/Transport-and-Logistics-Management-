package adapters;

import interfaces.IExternalTransportSystem;

/**
 * Adapter: ExternalTransportAdapter
 * STRUCTURAL PATTERN: Adapter Pattern
 * Adapts incompatible external transport system to our interface
 * SOLID: SRP - Single responsibility (interface adaptation)
 * 
 * Problem: External system has incompatible interface
 * Solution: Create adapter that translates between our interface and external system
 */
public class ExternalTransportAdapter implements IExternalTransportSystem {

    // Mock external transport system with incompatible interface
    private MockExternalSystem externalSystem;

    public ExternalTransportAdapter() {
        this.externalSystem = new MockExternalSystem();
    }

    /**
     * Adapter method: Convert our format to external format
     */
    @Override
    public String fetchExternalShipmentData(String shipmentId) {
        // Translate shipment ID to external system format
        String externalId = "EXT-" + shipmentId;
        // Call external system using its interface
        return externalSystem.getShipmentInfo(externalId);
    }

    /**
     * Adapter method: Convert update data to external format
     */
    @Override
    public void sendShipmentUpdate(String shipmentId, String updateData) {
        // Translate our update format to external format
        String externalId = "EXT-" + shipmentId;
        String externalData = "UPDATE:" + updateData;
        // Call external system using its interface
        externalSystem.pushUpdate(externalId, externalData);
    }

    /**
     * Mock External System with incompatible interface
     * (In real scenario, this would be actual external API)
     */
    private static class MockExternalSystem {
        public String getShipmentInfo(String externalId) {
            return "External Data for " + externalId + " [MOCK SYSTEM]";
        }

        public void pushUpdate(String externalId, String externalData) {
            System.out.println("Mock External System received: " + externalData + " for " + externalId);
        }
    }
}

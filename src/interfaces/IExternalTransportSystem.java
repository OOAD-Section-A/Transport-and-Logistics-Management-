package interfaces;

/**
 * Interface: IExternalTransportSystem
 * SOLID: DIP - Dependency Inversion Principle
 * Used by Adapter pattern to integrate external system
 * Represents incompatible interface from external system
 */
public interface IExternalTransportSystem {
    String fetchExternalShipmentData(String shipmentId);
    void sendShipmentUpdate(String shipmentId, String updateData);
}

package interfaces;

import entities.Shipment;
import java.util.List;

/**
 * Interface: ITransportService
 * SOLID: DIP - Dependency Inversion Principle
 * Used by Controller to interact with service layer
 */
public interface ITransportService {
    void createShipment(Shipment shipment);
    void updateShipmentStatus(String shipmentId, String status);
    Shipment getShipment(String shipmentId);
    List<Shipment> getAllShipments();
}

package interfaces;

import entities.Shipment;

/**
 * Interface: ITransportObserver
 * SOLID: DIP - Dependency Inversion Principle
 * Observer pattern for shipment status updates
 */
public interface ITransportObserver {
    void onShipmentUpdated(Shipment shipment);
}

package services;

import entities.Shipment;
import interfaces.ITransportService;
import repositories.TransportRepository;
import java.util.List;

/**
 * Service: TransportService
 * SOLID: SRP - Single responsibility (business logic only)
 * MVC: Model layer - handles business operations
 */
public class TransportService implements ITransportService {
    private TransportRepository repository;

    public TransportService(TransportRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createShipment(Shipment shipment) {
        repository.addShipment(shipment);
    }

    @Override
    public void updateShipmentStatus(String shipmentId, String status) {
        Shipment shipment = repository.getShipment(shipmentId);
        if (shipment != null) {
            shipment.setStatus(status);
            repository.updateShipment(shipmentId, shipment);
        }
    }

    @Override
    public Shipment getShipment(String shipmentId) {
        return repository.getShipment(shipmentId);
    }

    @Override
    public List<Shipment> getAllShipments() {
        return repository.getAllShipments();
    }
}

package behavioral.command;

import entities.Shipment;
import interfaces.ITransportService;

/**
 * Command: CreateShipmentCommand
 * BEHAVIORAL PATTERN: Command Pattern
 * Encapsulates "create shipment" operation
 * SOLID: SRP - Single responsibility (shipment creation command)
 * 
 * Benefits:
 * - Decouples command from execution
 * - Allows queuing, logging, undo operations
 * - Can be executed asynchronously
 */
public class CreateShipmentCommand implements Command {
    private Shipment shipment;
    private ITransportService service;
    private boolean executed = false;

    public CreateShipmentCommand(ITransportService service, Shipment shipment) {
        this.service = service;
        this.shipment = shipment;
    }

    @Override
    public void execute() {
        if (!executed) {
            System.out.println("[COMMAND] Executing CreateShipmentCommand for: " + shipment.getShipmentId());
            service.createShipment(shipment);
            executed = true;
        }
    }

    @Override
    public void undo() {
        if (executed) {
            System.out.println("[COMMAND] Undoing CreateShipmentCommand for: " + shipment.getShipmentId());
            // In real scenario, would remove from service
            executed = false;
        }
    }

    public Shipment getShipment() {
        return shipment;
    }
}

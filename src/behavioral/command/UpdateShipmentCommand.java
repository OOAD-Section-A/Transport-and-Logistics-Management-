package behavioral.command;

import interfaces.ITransportService;

/**
 * Command: UpdateShipmentCommand
 * BEHAVIORAL PATTERN: Command Pattern
 * Encapsulates "update shipment status" operation
 * SOLID: SRP - Single responsibility (status update command)
 */
public class UpdateShipmentCommand implements Command {
    private String shipmentId;
    private String newStatus;
    private String previousStatus;
    private ITransportService service;
    private boolean executed = false;

    public UpdateShipmentCommand(ITransportService service, String shipmentId, String newStatus) {
        this.service = service;
        this.shipmentId = shipmentId;
        this.newStatus = newStatus;
    }

    @Override
    public void execute() {
        if (!executed) {
            System.out.println("[COMMAND] Executing UpdateShipmentCommand for: " + shipmentId + " -> " + newStatus);
            // Store previous status for undo
            var shipment = service.getShipment(shipmentId);
            if (shipment != null) {
                previousStatus = shipment.getStatus();
            }
            service.updateShipmentStatus(shipmentId, newStatus);
            executed = true;
        }
    }

    @Override
    public void undo() {
        if (executed && previousStatus != null) {
            System.out.println("[COMMAND] Undoing UpdateShipmentCommand for: " + shipmentId + " -> " + previousStatus);
            service.updateShipmentStatus(shipmentId, previousStatus);
            executed = false;
        }
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getNewStatus() {
        return newStatus;
    }
}

package behavioral.chain;

import entities.Shipment;

/**
 * Handler: ValidationHandler
 * BEHAVIORAL PATTERN: Chain of Responsibility
 * Validates shipment data integrity
 */
public class ValidationHandler extends Handler {

    @Override
    protected boolean validate(Shipment shipment) {
        // Validate required fields
        if (shipment.getShipmentId() == null || shipment.getShipmentId().isEmpty()) {
            return false;
        }
        if (shipment.getSupplierId() == null || shipment.getSupplierId().isEmpty()) {
            return false;
        }
        if (shipment.getCarrierId() == null || shipment.getCarrierId().isEmpty()) {
            return false;
        }
        if (shipment.getWeight() <= 0) {
            return false;
        }
        return true;
    }

    @Override
    protected String getHandlerName() {
        return "ValidationHandler (Data Integrity)";
    }
}

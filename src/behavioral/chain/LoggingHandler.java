package behavioral.chain;

import entities.Shipment;

/**
 * Handler: LoggingHandler
 * BEHAVIORAL PATTERN: Chain of Responsibility
 * Logs shipment details and business rules
 */
public class LoggingHandler extends Handler {

    @Override
    protected boolean validate(Shipment shipment) {
        // Log shipment details
        System.out.println("[CHAIN] Logging shipment details:");
        System.out.println("  - ID: " + shipment.getShipmentId());
        System.out.println("  - Supplier: " + shipment.getSupplierId());
        System.out.println("  - Carrier: " + shipment.getCarrierId());
        System.out.println("  - Weight: " + shipment.getWeight() + " tons");
        System.out.println("  - Cost: $" + shipment.getCost());
        
        // Business rule: Cost must be reasonable for weight
        double costPerTon = shipment.getCost() / shipment.getWeight();
        if (costPerTon > 10000) {
            System.out.println("[CHAIN] WARNING: Cost per ton is unusually high: $" + costPerTon);
        }
        
        return true;
    }

    @Override
    protected String getHandlerName() {
        return "LoggingHandler (Audit)";
    }
}

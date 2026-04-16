package behavioral.chain;

import entities.Shipment;

/**
 * Handler: Base class for Chain of Responsibility
 * BEHAVIORAL PATTERN: Chain of Responsibility
 * Abstract handler for validation chains
 * SOLID: SRP - Single responsibility (part of validation chain)
 */
public abstract class Handler {
    protected Handler nextHandler;

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Handle the request and pass to next handler if valid
     */
    public void handle(Shipment shipment) {
        if (validate(shipment)) {
            System.out.println("[CHAIN] ✓ " + getHandlerName() + " validation passed");
            if (nextHandler != null) {
                nextHandler.handle(shipment);
            } else {
                System.out.println("[CHAIN] ✓ All validations passed");
            }
        } else {
            System.out.println("[CHAIN] ✗ " + getHandlerName() + " validation failed");
        }
    }

    /**
     * Validate shipment - implemented by subclasses
     */
    protected abstract boolean validate(Shipment shipment);

    /**
     * Get handler name for logging
     */
    protected abstract String getHandlerName();
}

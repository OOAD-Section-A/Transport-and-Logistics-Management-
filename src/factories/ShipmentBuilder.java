package factories;

import entities.Shipment;
import java.util.Date;

/**
 * Builder: ShipmentBuilder
 * CREATIONAL PATTERN: Builder Pattern
 * GRASP: Creator - Step-by-step construction of complex Shipment objects
 * SOLID: SRP - Single responsibility (complex object construction)
 * Allows building Shipment with optional fields in a fluent interface
 */
public class ShipmentBuilder {
    private String shipmentId;
    private String supplierId;
    private String carrierId;
    private String destination;
    private double weight;
    private String status = "Pending"; // Default value
    private Date createdDate = new Date(); // Default: current date
    private Date deliveryDate;
    private double cost = 0.0; // Default value
    private String origin;

    public ShipmentBuilder(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public ShipmentBuilder withSupplierId(String supplierId) {
        this.supplierId = supplierId;
        return this;
    }

    public ShipmentBuilder withCarrierId(String carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public ShipmentBuilder withDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public ShipmentBuilder withWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public ShipmentBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public ShipmentBuilder withCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public ShipmentBuilder withDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public ShipmentBuilder withCost(double cost) {
        this.cost = cost;
        return this;
    }

    public ShipmentBuilder withOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    /**
     * Build the Shipment object
     */
    public Shipment build() {
        if (shipmentId == null || supplierId == null || carrierId == null) {
            throw new IllegalArgumentException("Required fields: shipmentId, supplierId, carrierId");
        }
        return new Shipment(shipmentId, supplierId, carrierId, destination, weight, 
                          status, createdDate, deliveryDate, cost, origin);
    }
}

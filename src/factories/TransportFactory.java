package factories;

import entities.Supplier;
import entities.Carrier;
import entities.Shipment;
import java.util.Date;

/**
 * Factory: TransportFactory
 * CREATIONAL PATTERN: Factory Pattern
 * GRASP: Creator - Responsible for creating basic entities
 * SOLID: SRP - Single responsibility (entity creation)
 */
public class TransportFactory {

    /**
     * Factory method for creating Supplier
     */
    public static Supplier createSupplier(String supplierId, String supplierName, String location, String contactInfo) {
        return new Supplier(supplierId, supplierName, location, contactInfo);
    }

    /**
     * Factory method for creating Carrier
     */
    public static Carrier createCarrier(String carrierId, String carrierName, String transportMode, double capacity) {
        return new Carrier(carrierId, carrierName, transportMode, capacity);
    }

    /**
     * Factory method for creating basic Shipment
     * For complex shipments, use ShipmentBuilder instead
     */
    public static Shipment createShipment(String shipmentId, String supplierId, String carrierId, 
                                         String destination, double weight, String status, 
                                         Date createdDate, Date deliveryDate, double cost, String origin) {
        return new Shipment(shipmentId, supplierId, carrierId, destination, weight, status, 
                          createdDate, deliveryDate, cost, origin);
    }
}

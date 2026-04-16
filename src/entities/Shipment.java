package entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity: Shipment
 * GRASP: Information Expert - Shipment manages its own data
 * SOLID: SRP - Single responsibility (represent shipment)
 * NOTE: Cloneable for Prototype pattern
 */
public class Shipment implements Cloneable, Serializable {
    private String shipmentId;
    private String supplierId;
    private String carrierId;
    private String destination;
    private double weight; // in tons
    private String status; // Pending, In-Transit, Delivered
    private Date createdDate;
    private Date deliveryDate;
    private double cost;
    private String origin;

    // Constructor (for Builder pattern)
    public Shipment(String shipmentId, String supplierId, String carrierId, String destination,
                    double weight, String status, Date createdDate, Date deliveryDate,
                    double cost, String origin) {
        this.shipmentId = shipmentId;
        this.supplierId = supplierId;
        this.carrierId = carrierId;
        this.destination = destination;
        this.weight = weight;
        this.status = status;
        this.createdDate = createdDate;
        this.deliveryDate = deliveryDate;
        this.cost = cost;
        this.origin = origin;
    }

    // Getters
    public String getShipmentId() {
        return shipmentId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getCarrierId() {
        return carrierId;
    }

    public String getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public double getCost() {
        return cost;
    }

    public String getOrigin() {
        return origin;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    // Prototype Pattern: Deep copy implementation
    @Override
    public Shipment clone() {
        try {
            return (Shipment) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed for Shipment", e);
        }
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId='" + shipmentId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", carrierId='" + carrierId + '\'' +
                ", destination='" + destination + '\'' +
                ", weight=" + weight +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", deliveryDate=" + deliveryDate +
                ", cost=" + cost +
                ", origin='" + origin + '\'' +
                '}';
    }
}

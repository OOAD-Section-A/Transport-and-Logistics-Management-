package entities;

import java.io.Serializable;

/**
 * Entity: SupplierPortal
 * Represents supplier portal integration.
 * GRASP: Information Expert - Manages supplier portal data.
 * SOLID: SRP - Single responsibility (represent supplier portal).
 */
public class SupplierPortal implements Serializable {
    private String portalId;
    private String supplierId;
    private String orderDetails;
    private String asn; // Advance Shipment Notification
    private String packingSlip;

    public SupplierPortal(String portalId, String supplierId, String orderDetails, String asn, String packingSlip) {
        this.portalId = portalId;
        this.supplierId = supplierId;
        this.orderDetails = orderDetails;
        this.asn = asn;
        this.packingSlip = packingSlip;
    }

    // Getters and Setters
    public String getPortalId() { return portalId; }
    public void setPortalId(String portalId) { this.portalId = portalId; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getOrderDetails() { return orderDetails; }
    public void setOrderDetails(String orderDetails) { this.orderDetails = orderDetails; }

    public String getAsn() { return asn; }
    public void setAsn(String asn) { this.asn = asn; }

    public String getPackingSlip() { return packingSlip; }
    public void setPackingSlip(String packingSlip) { this.packingSlip = packingSlip; }

    @Override
    public String toString() {
        return "SupplierPortal{" +
                "portalId='" + portalId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", orderDetails='" + orderDetails + '\'' +
                ", asn='" + asn + '\'' +
                ", packingSlip='" + packingSlip + '\'' +
                '}';
    }
}
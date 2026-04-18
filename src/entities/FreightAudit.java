package entities;

import java.io.Serializable;

/**
 * Entity: FreightAudit
 * Represents freight audit and payment operations.
 * GRASP: Information Expert - Manages freight audit data.
 * SOLID: SRP - Single responsibility (represent freight audit).
 */
public class FreightAudit implements Serializable {
    private String auditId;
    private String shipmentId;
    private double invoicedAmount;
    private double contractAmount;
    private String discrepancyFlag; // "NONE", "OVERCHARGE", "UNDERCHARGE"

    public FreightAudit(String auditId, String shipmentId, double invoicedAmount, double contractAmount, String discrepancyFlag) {
        this.auditId = auditId;
        this.shipmentId = shipmentId;
        this.invoicedAmount = invoicedAmount;
        this.contractAmount = contractAmount;
        this.discrepancyFlag = discrepancyFlag;
    }

    // Getters and Setters
    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

    public double getInvoicedAmount() { return invoicedAmount; }
    public void setInvoicedAmount(double invoicedAmount) { this.invoicedAmount = invoicedAmount; }

    public double getContractAmount() { return contractAmount; }
    public void setContractAmount(double contractAmount) { this.contractAmount = contractAmount; }

    public String getDiscrepancyFlag() { return discrepancyFlag; }
    public void setDiscrepancyFlag(String discrepancyFlag) { this.discrepancyFlag = discrepancyFlag; }

    @Override
    public String toString() {
        return "FreightAudit{" +
                "auditId='" + auditId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", invoicedAmount=" + invoicedAmount +
                ", contractAmount=" + contractAmount +
                ", discrepancyFlag='" + discrepancyFlag + '\'' +
                '}';
    }
}
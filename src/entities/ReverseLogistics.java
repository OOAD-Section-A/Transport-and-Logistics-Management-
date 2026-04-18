package entities;

import java.io.Serializable;

/**
 * Entity: ReverseLogistics
 * Represents reverse logistics for drop-shipping returns.
 * GRASP: Information Expert - Manages reverse logistics data.
 * SOLID: SRP - Single responsibility (represent reverse logistics).
 */
public class ReverseLogistics implements Serializable {
    private String returnId;
    private String orderId;
    private String supplierId;
    private double refundAmount;
    private String reconciliationStatus; // "PENDING", "COMPLETED"

    public ReverseLogistics(String returnId, String orderId, String supplierId, double refundAmount, String reconciliationStatus) {
        this.returnId = returnId;
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.refundAmount = refundAmount;
        this.reconciliationStatus = reconciliationStatus;
    }

    // Getters and Setters
    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }

    public String getReconciliationStatus() { return reconciliationStatus; }
    public void setReconciliationStatus(String reconciliationStatus) { this.reconciliationStatus = reconciliationStatus; }

    @Override
    public String toString() {
        return "ReverseLogistics{" +
                "returnId='" + returnId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", refundAmount=" + refundAmount +
                ", reconciliationStatus='" + reconciliationStatus + '\'' +
                '}';
    }
}
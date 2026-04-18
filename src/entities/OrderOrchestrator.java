package entities;

import java.io.Serializable;

/**
 * Entity: OrderOrchestrator
 * Represents order orchestration for drop-shipping.
 * GRASP: Information Expert - Manages order orchestration data.
 * SOLID: SRP - Single responsibility (represent order orchestration).
 */
public class OrderOrchestrator implements Serializable {
    private String orderId;
    private String salesOrderId;
    private boolean isThirdPartyItem;
    private String supplierId;
    private String poNumber; // Purchase Order

    public OrderOrchestrator(String orderId, String salesOrderId, boolean isThirdPartyItem, String supplierId, String poNumber) {
        this.orderId = orderId;
        this.salesOrderId = salesOrderId;
        this.isThirdPartyItem = isThirdPartyItem;
        this.supplierId = supplierId;
        this.poNumber = poNumber;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSalesOrderId() { return salesOrderId; }
    public void setSalesOrderId(String salesOrderId) { this.salesOrderId = salesOrderId; }

    public boolean isThirdPartyItem() { return isThirdPartyItem; }
    public void setThirdPartyItem(boolean thirdPartyItem) { isThirdPartyItem = thirdPartyItem; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    @Override
    public String toString() {
        return "OrderOrchestrator{" +
                "orderId='" + orderId + '\'' +
                ", salesOrderId='" + salesOrderId + '\'' +
                ", isThirdPartyItem=" + isThirdPartyItem +
                ", supplierId='" + supplierId + '\'' +
                ", poNumber='" + poNumber + '\'' +
                '}';
    }
}
package entities;

import java.io.Serializable;

/**
 * Entity: TrackingSync
 * Represents automated tracking sync for drop-shipping.
 * GRASP: Information Expert - Manages tracking sync data.
 * SOLID: SRP - Single responsibility (represent tracking sync).
 */
public class TrackingSync implements Serializable {
    private String syncId;
    private String orderId;
    private String trackingNumber;
    private String customerUpdate;

    public TrackingSync(String syncId, String orderId, String trackingNumber, String customerUpdate) {
        this.syncId = syncId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.customerUpdate = customerUpdate;
    }

    // Getters and Setters
    public String getSyncId() { return syncId; }
    public void setSyncId(String syncId) { this.syncId = syncId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCustomerUpdate() { return customerUpdate; }
    public void setCustomerUpdate(String customerUpdate) { this.customerUpdate = customerUpdate; }

    @Override
    public String toString() {
        return "TrackingSync{" +
                "syncId='" + syncId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", customerUpdate='" + customerUpdate + '\'' +
                '}';
    }
}
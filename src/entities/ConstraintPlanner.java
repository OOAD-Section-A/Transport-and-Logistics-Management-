package entities;

import java.io.Serializable;

/**
 * Entity: ConstraintPlanner
 * Represents constraint-aware planning for shipments.
 * GRASP: Information Expert - Manages planning constraints.
 * SOLID: SRP - Single responsibility (represent planning constraints).
 */
public class ConstraintPlanner implements Serializable {
    private String planId;
    private String shipmentId;
    private double vehicleWeightLimit;
    private double vehicleHeightLimit;
    private int driverShiftHours;
    private String deliveryWindow; // e.g., "9AM-5PM"

    public ConstraintPlanner(String planId, String shipmentId, double vehicleWeightLimit, double vehicleHeightLimit, int driverShiftHours, String deliveryWindow) {
        this.planId = planId;
        this.shipmentId = shipmentId;
        this.vehicleWeightLimit = vehicleWeightLimit;
        this.vehicleHeightLimit = vehicleHeightLimit;
        this.driverShiftHours = driverShiftHours;
        this.deliveryWindow = deliveryWindow;
    }

    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

    public double getVehicleWeightLimit() { return vehicleWeightLimit; }
    public void setVehicleWeightLimit(double vehicleWeightLimit) { this.vehicleWeightLimit = vehicleWeightLimit; }

    public double getVehicleHeightLimit() { return vehicleHeightLimit; }
    public void setVehicleHeightLimit(double vehicleHeightLimit) { this.vehicleHeightLimit = vehicleHeightLimit; }

    public int getDriverShiftHours() { return driverShiftHours; }
    public void setDriverShiftHours(int driverShiftHours) { this.driverShiftHours = driverShiftHours; }

    public String getDeliveryWindow() { return deliveryWindow; }
    public void setDeliveryWindow(String deliveryWindow) { this.deliveryWindow = deliveryWindow; }

    @Override
    public String toString() {
        return "ConstraintPlanner{" +
                "planId='" + planId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", vehicleWeightLimit=" + vehicleWeightLimit +
                ", vehicleHeightLimit=" + vehicleHeightLimit +
                ", driverShiftHours=" + driverShiftHours +
                ", deliveryWindow='" + deliveryWindow + '\'' +
                '}';
    }
}
package entities;

import java.io.Serializable;

/**
 * Entity: Territory
 * Represents geographic service zones for territory management.
 * GRASP: Information Expert - Manages territory data.
 * SOLID: SRP - Single responsibility (represent territories).
 */
public class Territory implements Serializable {
    private String territoryId;
    private String zoneName;
    private String geographicArea; // e.g., "Northeast US"
    private int assignedDrivers;

    public Territory(String territoryId, String zoneName, String geographicArea, int assignedDrivers) {
        this.territoryId = territoryId;
        this.zoneName = zoneName;
        this.geographicArea = geographicArea;
        this.assignedDrivers = assignedDrivers;
    }

    // Getters and Setters
    public String getTerritoryId() { return territoryId; }
    public void setTerritoryId(String territoryId) { this.territoryId = territoryId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getGeographicArea() { return geographicArea; }
    public void setGeographicArea(String geographicArea) { this.geographicArea = geographicArea; }

    public int getAssignedDrivers() { return assignedDrivers; }
    public void setAssignedDrivers(int assignedDrivers) { this.assignedDrivers = assignedDrivers; }

    @Override
    public String toString() {
        return "Territory{" +
                "territoryId='" + territoryId + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", geographicArea='" + geographicArea + '\'' +
                ", assignedDrivers=" + assignedDrivers +
                '}';
    }
}
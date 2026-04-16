package entities;

/**
 * Entity: Carrier
 * GRASP: Information Expert - Carrier manages its own data
 * SOLID: SRP - Single responsibility (represent carrier)
 * NOTE: Used with Flyweight pattern to reduce memory usage
 */
public class Carrier {
    private String carrierId;
    private String carrierName;
    private String transportMode; // Road, Air, Sea, Rail
    private double capacity; // in tons
    private boolean available;

    // Constructor
    public Carrier(String carrierId, String carrierName, String transportMode, double capacity) {
        this.carrierId = carrierId;
        this.carrierName = carrierName;
        this.transportMode = transportMode;
        this.capacity = capacity;
        this.available = true;
    }

    // Getters
    public String getCarrierId() {
        return carrierId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public double getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setters
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Carrier{" +
                "carrierId='" + carrierId + '\'' +
                ", carrierName='" + carrierName + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", capacity=" + capacity +
                ", available=" + available +
                '}';
    }
}
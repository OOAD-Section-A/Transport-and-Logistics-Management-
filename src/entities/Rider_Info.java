package entities;
import java.util.List;

public class Rider_Info {
    private String riderId;
    private String name;
    private String phone;
    private String vehicleType;
    private String zone;
    private boolean available;
}

class VehicleHealthReport {
    private String riderId;
    private String status; // e.g., "Healthy", "Needs Maintenance"
    private double fuelLevel;
    private String timestamp;
}

class RoutePlanEntity {
    private String pickup;
    private String dropoff;
    private List<String> waypoints;
    private double estimatedDistance;
    private double estimatedTime;
}

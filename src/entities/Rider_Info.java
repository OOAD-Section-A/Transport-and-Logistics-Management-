package entities;

public class Rider {
    private String riderId;
    private String name;
    private String phone;
    private String vehicleType;
    private String zone;
    private boolean available;

    // Constructors, getters, setters
}

// filepath: c:\Users\bhara\OneDrive\Desktop\OOAD Jackfruit\Transport-and-Logistics-Management-\src\entities\VehicleHealthReport.java
package entities;

public class VehicleHealthReport {
    private String riderId;
    private String status; // e.g., "Healthy", "Needs Maintenance"
    private double fuelLevel;
    private String timestamp;

    // Constructors, getters, setters
}

// filepath: c:\Users\bhara\OneDrive\Desktop\OOAD Jackfruit\Transport-and-Logistics-Management-\src\entities\RoutePlan.java
package entities;

import java.util.List;

public class RoutePlan {
    private String pickup;
    private String dropoff;
    private List<String> waypoints;
    private double estimatedDistance;
    private double estimatedTime;

    // Constructors, getters, setters
} {
    
}

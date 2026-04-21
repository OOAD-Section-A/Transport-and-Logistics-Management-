package integration;

import com.ramennoodles.delivery.integration.ITransportLogisticsService;
import entities.Rider_info;
import entities.RoutePlan;
import entities.GeofenceZone;
import entities.Coordinate;
import services.TransportService;
import java.util.List;

public class CenterDivLogisticsService implements ITransportLogisticsService {
    private TransportService transportService;

    public CenterDivLogisticsService(TransportService transportService) {
        this.transportService = transportService;
    }

    @Override
    public Rider getRiderDetails(String riderId) {
        Rider_info riderInfo = transportService.getRiderDetails(riderId);
        return new Rider(riderId, riderInfo.getName(), riderInfo.getPhone(), "ACTIVE"); // Map to their Rider model
    }

    @Override
    public List<Rider> getAvailableRiders(String zone) {
        List<Rider_info> available = transportService.getAvailableRiders(zone);
        return available.stream().map(r -> new Rider(r.getRiderId(), r.getName(), r.getPhone(), "AVAILABLE")).toList();
    }

    @Override
    public RoutePlan calculateOptimalRoute(String orderId, Coordinate pickup, Coordinate dropoff, List<Coordinate> waypoints) {
        // Delegate to your service (adjust parameters as needed)
        return transportService.calculateOptimalRoute(pickup.toString(), dropoff.toString(), waypoints.stream().map(Object::toString).toList());
    }

    @Override
    public List<GeofenceZone> getLogisticsHubZones() {
        return transportService.getLogisticsHubZones();
    }

    @Override
    public void reportVehicleHealth(String riderId, String healthReport) {
        // Store in repository or log
        transportService.reportVehicleHealth(riderId, healthReport);
    }

    @Override
    public void notifyRiderAvailable(String riderId) {
        transportService.notifyRiderAvailable(riderId);
    }
}
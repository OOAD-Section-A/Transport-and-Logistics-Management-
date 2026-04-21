package integration;

import com.ramennoodles.delivery.integration.ITransportLogisticsService;
import com.ramennoodles.delivery.model.*;
import facade.TransportFacade;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import entities.Carrier;

public class TransportLogisticsServiceImpl implements ITransportLogisticsService {
    private final TransportFacade facade;

    public TransportLogisticsServiceImpl(TransportFacade facade) {
        this.facade = facade;
    }

    @Override
    public Rider getRiderDetails(String riderId) {
        System.out.println("[TMS-ADAPTER] Ramen Noodles requested details for: " + riderId);
        List<Carrier> carriers = facade.getRepository().getAllCarriers(null);
        for(Carrier c : carriers) {
            if(c.getCarrierId().equals(riderId)) {
                return Rider.createProfile(c.getCarrierName(), "+123456789", c.getTransportMode());
            }
        }
        // Fallback for demo
        return Rider.createProfile("Carrier-" + riderId, "+123456789", "Van");
    }

    @Override
    public List<Rider> getAvailableRiders(String zone) {
        System.out.println("[TMS-ADAPTER] Ramen Noodles getting available riders for zone: " + zone);
        List<Carrier> carriers = facade.getRepository().getAllCarriers(null);
        List<Rider> riders = new ArrayList<>();
        
        for(Carrier c : carriers) {
            Rider r = Rider.createProfile(c.getCarrierName(), "555-0000", c.getTransportMode());
            r.setRiderId(c.getCarrierId());
            r.activate();
            riders.add(r);
        }
        
        if (riders.isEmpty()) {
            Rider r1 = Rider.createProfile("Fleet Driver 1", "555-0100", "Van");
            r1.activate();
            riders.add(r1);
            
            Rider r2 = Rider.createProfile("Fleet Driver 2", "555-0200", "Motorcycle");
            r2.activate();
            riders.add(r2);
        }
        return riders;
    }

    @Override
    public RoutePlan calculateOptimalRoute(String orderId, Coordinate pickup, Coordinate dropoff, List<Coordinate> waypoints) {
        System.out.println("[TMS-ADAPTER] Ramen Noodles requested route optimization for order: " + orderId);
        List<Coordinate> optimized = new ArrayList<>();
        optimized.add(pickup);
        if (waypoints != null) {
            optimized.addAll(waypoints);
        }
        optimized.add(dropoff);
        RoutePlan plan = RoutePlan.create(orderId, optimized);
        plan.optimizeRoute();
        
        if (facade.getDbAdapter() != null) {
            facade.getDbAdapter().persistRoute(plan.getRouteId(), orderId);
        }
        
        return plan;
    }

    @Override
    public List<GeofenceZone> getLogisticsHubZones() {
        return Collections.emptyList(); // Mocked empty
    }

    @Override
    public void reportVehicleHealth(String riderId, String healthReport) {
        System.out.println("[TMS-ADAPTER] Health report from Ramen Noodles for " + riderId + ": " + healthReport);
        if (facade.getDbAdapter() != null) {
            facade.getDbAdapter().persistTrackingEvent("SYS-HEALTH", riderId, 0.0, 0.0, "HEALTH_REPORT");
        }
    }

    @Override
    public void notifyRiderAvailable(String riderId) {
        System.out.println("[TMS-ADAPTER] Rider now available: " + riderId);
    }

    @Override
    public String getVehicleType(String riderId) {
        return "Truck";
    }
}

package adapters;

import entities.RouteOptimizer;
import entities.RouteOptimizer.*;
import java.util.Random;

/**
 * RouteOptimizer Adapter
 * Integrates RouteOptimizer with Transport Management System
 * LOW COUPLING: Implements standard interfaces
 */
public class RouteOptimizerAdapter {
    private final RouteOptimizer.Port routeOptimizer;
    
    public RouteOptimizerAdapter() {
        // Create with mock implementations
        RouteOptimizer.RoutingEngine engine = request -> 
            new RoutePlan("ROUTE-" + System.nanoTime(), 
                         new Random().nextInt(240) + 60, false);
        
        RouteOptimizer.FallbackPolicy fallback = request -> 
            new RoutePlan("FALLBACK-" + request.corridorId(), 
                         request.slaMinutes() + 30, true);
        
        RouteOptimizer.CarrierGateway gateway = corridorId -> 
            new Random().nextInt(5) + 1;
        
        this.routeOptimizer = RouteOptimizer.create(engine, fallback, gateway);
    }
    
    public RoutePlan optimizeRoute(String shipmentId, String corridor, 
                                   int load, int sla) {
        RouteRequest request = new RouteRequest.Builder()
            .shipmentId(shipmentId)
            .corridorId(corridor)
            .loadUnits(load)
            .slaMinutes(sla)
            .build();
        
        return routeOptimizer.optimize(request);
    }
    
    public void registerHandler(exceptions.SCMExceptionHandler handler) {
        routeOptimizer.registerHandler(handler);
    }
}

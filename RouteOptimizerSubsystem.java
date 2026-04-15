// =============================================================================
// FILE: RouteOptimizerSubsystem.java
// SUBSYSTEM: Route Optimizer & Router Integration
// AUTHOR: [Your Name] — Senior Software Architect Role
// PATTERNS: Factory Method, Adapter, Facade, Strategy, Observer
// =============================================================================

package logistics.routeoptimizer;

import java.util.*;


// =============================================================================
// SECTION 1: DATA CONTRACT INTERFACES (Dependency Inversion)
// The DB team MUST implement these. My subsystem only depends on these
// interfaces, never on the DB team's concrete classes.
// =============================================================================

/**
 * CONTRACT FOR DB TEAM.
 * Represents a single delivery stop. The DB team implements this from their
 * ORM/entity layer. My subsystem will never import their concrete class.
 */
interface IDeliveryStop {
    String getStopId();
    double getLatitude();
    double getLongitude();
    String getAddress();
    int getPriorityLevel();           // 1 (highest) to 5 (lowest) — map Standard=3, Express=2, Next-Day=1
    String getPriorityLabel();        // Human-readable: "Standard" | "Express" | "Next-Day"
    double getPackageWeightKg();
}

/**
 * CONTRACT FOR DB TEAM.
 * The full metadata object passed into the subsystem facade.
 * The DB team populates this and hands it to RouteOptimizerFacade.optimizeDeliveryCycle().
 *
 * FIELD ALIGNMENT WITH CENTER DIV DOCUMENT:
 *   getShipmentId()        → shipmentId  (doc row 1)
 *   getOptimizationMode()  → drives constraint selection (doc row 9)
 *   isMinCost()            → constraints.MinCost flag (doc row 9)
 *   isMinTime()            → constraints.MinTime flag (doc row 9)
 *   isAvoidTolls()         → constraints.AvoidTolls flag (doc row 9)
 *   isDropShip()           → isDropShip flag (doc row 5)
 */
interface IRouteData {
    String getShipmentId();           // renamed from getDeliveryId() — matches doc "shipmentId"
    String getDeliveryId();           // kept as alias for backward compatibility
    String getVehicleId();
    String getOptimizationMode();     // "DISTANCE" | "COST" | "PRIORITY"

    // --- Constraint flags (CENTER DIV doc row 9) ---
    boolean isMinCost();              // true = minimise monetary cost
    boolean isMinTime();              // true = minimise travel time
    boolean isAvoidTolls();           // true = route must skip toll roads

    // --- Drop-ship flag (CENTER DIV doc row 5) ---
    boolean isDropShip();             // true = bypass warehouse, dispatch direct from supplier

    List<IDeliveryStop> getStops();
    double getMaxVehicleCapacityKg();
    String getDepotAddress();         // Starting/ending warehouse location
}


// =============================================================================
// SECTION 2: OBSERVER PATTERN — Event Bus for other subsystems
// Fleet Tracking and Warehouse teams register as listeners here.
// =============================================================================

/**
 * The event payload published whenever a route is recalculated.
 */
class RouteRecalculatedEvent {
    private final String deliveryId;
    private final String vehicleId;
    private final List<String> orderedStopIds;
    private final double totalDistanceKm;
    private final double estimatedCostUSD;
    private final long timestampEpochMs;

    public RouteRecalculatedEvent(String deliveryId, String vehicleId,
                                   List<String> orderedStopIds,
                                   double totalDistanceKm,
                                   double estimatedCostUSD) {
        this.deliveryId       = deliveryId;
        this.vehicleId        = vehicleId;
        this.orderedStopIds   = Collections.unmodifiableList(orderedStopIds);
        this.totalDistanceKm  = totalDistanceKm;
        this.estimatedCostUSD = estimatedCostUSD;
        this.timestampEpochMs = System.currentTimeMillis();
    }

    public String getDeliveryId()             { return deliveryId; }
    public String getVehicleId()              { return vehicleId; }
    public List<String> getOrderedStopIds()   { return orderedStopIds; }
    public double getTotalDistanceKm()        { return totalDistanceKm; }
    public double getEstimatedCostUSD()       { return estimatedCostUSD; }
    public long getTimestampEpochMs()         { return timestampEpochMs; }

    @Override
    public String toString() {
        return String.format("[RouteEvent] delivery=%s vehicle=%s stops=%d dist=%.1fkm cost=$%.2f",
                deliveryId, vehicleId, orderedStopIds.size(), totalDistanceKm, estimatedCostUSD);
    }
}

/**
 * CONTRACT FOR FLEET & WAREHOUSE TEAMS.
 * Any subsystem that wants route-change notifications implements this.
 */
interface IRouteEventListener {
    void onRouteRecalculated(RouteRecalculatedEvent event);
}

/**
 * Internal event bus — a thread-safe observer registry.
 * Follows the classic Observer/Publisher-Subscriber pattern.
 */
class RouteEventBus {
    private static RouteEventBus instance;
    private final List<IRouteEventListener> listeners = new ArrayList<>();

    private RouteEventBus() {}

    /** Singleton — one bus for the whole subsystem. */
    public static synchronized RouteEventBus getInstance() {
        if (instance == null) instance = new RouteEventBus();
        return instance;
    }

    public synchronized void subscribe(IRouteEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("[EventBus] Subscribed: " + listener.getClass().getSimpleName());
        }
    }

    public synchronized void unsubscribe(IRouteEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void publish(RouteRecalculatedEvent event) {
        System.out.println("[EventBus] Publishing: " + event);
        for (IRouteEventListener listener : listeners) {
            try {
                listener.onRouteRecalculated(event);
            } catch (Exception e) {
                System.err.println("[EventBus] Listener error in "
                        + listener.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}


// =============================================================================
// SECTION 3: STRUCTURAL — Adapter Pattern for External Map APIs
// Wraps any external routing API behind a stable internal interface.
// Swap Google Maps ↔ OSRM ↔ HERE Maps without touching business logic.
// =============================================================================

/** Internal result returned by any map adapter. */
class RouteSegment {
    public final String fromStopId;
    public final String toStopId;
    public final double distanceKm;
    public final double durationMinutes;
    public final double estimatedFuelCostUSD;

    public RouteSegment(String fromStopId, String toStopId,
                         double distanceKm, double durationMinutes,
                         double estimatedFuelCostUSD) {
        this.fromStopId           = fromStopId;
        this.toStopId             = toStopId;
        this.distanceKm           = distanceKm;
        this.durationMinutes      = durationMinutes;
        this.estimatedFuelCostUSD = estimatedFuelCostUSD;
    }
}

/**
 * The stable TARGET interface my subsystem uses internally.
 * All adapters must produce this shape.
 */
interface IMapRouterPort {
    /**
     * Returns an ordered list of segments from stop-to-stop.
     * @param stops   ordered list of stops (as the optimizer has arranged them)
     * @return        list of RouteSegment, one per consecutive pair
     */
    List<RouteSegment> calculateSegments(List<IDeliveryStop> stops);
}

// --- ADAPTEE: Mock External Google Maps SDK (simulates their proprietary API) ---
class GoogleMapsApiClient {
    /** Simulates the Google Maps Directions API response structure. */
    public Map<String, Object> getDirections(double originLat, double originLng,
                                              double destLat, double destLng,
                                              String apiKey) {
        // Simulate network call with Haversine approximation
        double distKm = haversineKm(originLat, originLng, destLat, destLng);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "OK");
        result.put("distance_meters", (int)(distKm * 1000));
        result.put("duration_seconds", (int)(distKm * 2.5 * 60));  // ~24km/h avg urban
        return result;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}

// --- ADAPTEE: Mock External OSRM API (open-source routing machine) ---
class OsrmHttpClient {
    /** Simulates OSRM's JSON response for the /route/v1/driving endpoint. */
    public String getRouteJson(String coordinates) {
        // In reality this would be an HTTP GET; we simulate it
        return "{\"code\":\"Ok\",\"routes\":[{\"distance\":12500,\"duration\":1400}]}";
    }
}

/**
 * ADAPTER 1: Wraps GoogleMapsApiClient → IMapRouterPort
 */
class GoogleMapsAdapter implements IMapRouterPort {
    private static final String API_KEY = "MOCK_GOOGLE_API_KEY";
    private final GoogleMapsApiClient client;

    public GoogleMapsAdapter(GoogleMapsApiClient client) {
        this.client = client;
    }

    @Override
    public List<RouteSegment> calculateSegments(List<IDeliveryStop> stops) {
        List<RouteSegment> segments = new ArrayList<>();
        for (int i = 0; i < stops.size() - 1; i++) {
            IDeliveryStop from = stops.get(i);
            IDeliveryStop to   = stops.get(i + 1);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = client.getDirections(
                    from.getLatitude(), from.getLongitude(),
                    to.getLatitude(), to.getLongitude(), API_KEY);

            double distKm    = ((Integer) resp.get("distance_meters")) / 1000.0;
            double durMin    = ((Integer) resp.get("duration_seconds")) / 60.0;
            double fuelCost  = distKm * 0.18;  // $0.18/km fuel estimate

            segments.add(new RouteSegment(from.getStopId(), to.getStopId(),
                                          distKm, durMin, fuelCost));
        }
        return segments;
    }
}

/**
 * ADAPTER 2: Wraps OsrmHttpClient → IMapRouterPort
 */
class OsrmAdapter implements IMapRouterPort {
    private final OsrmHttpClient client;

    public OsrmAdapter(OsrmHttpClient client) {
        this.client = client;
    }

    @Override
    public List<RouteSegment> calculateSegments(List<IDeliveryStop> stops) {
        List<RouteSegment> segments = new ArrayList<>();
        for (int i = 0; i < stops.size() - 1; i++) {
            IDeliveryStop from = stops.get(i);
            IDeliveryStop to   = stops.get(i + 1);

            // Build OSRM coordinate string: lon,lat;lon,lat
            String coords = String.format("%.6f,%.6f;%.6f,%.6f",
                    from.getLongitude(), from.getLatitude(),
                    to.getLongitude(), to.getLatitude());

            String json = client.getRouteJson(coords);
            // Parse mock JSON (hardcoded values for simulation)
            double distKm  = 12.5;   // extracted from json in a real impl
            double durMin  = 23.3;
            double fuelCost = distKm * 0.18;

            segments.add(new RouteSegment(from.getStopId(), to.getStopId(),
                                          distKm, durMin, fuelCost));
        }
        return segments;
    }
}


// =============================================================================
// SECTION 4: BEHAVIORAL — Strategy Pattern for Optimization Algorithms
// Algorithms are interchangeable at runtime via IRouteData.getOptimizationMode()
// =============================================================================

/** Encapsulates the result of running an optimization strategy. */
class OptimizationResult {
    public final List<IDeliveryStop> orderedStops;
    public final double totalDistanceKm;
    public final double calculatedCost;   // renamed from totalCostUSD — matches doc "calculatedCost"
    public final String routeId;          // unique ID for this computed route (doc row 8)

    public OptimizationResult(List<IDeliveryStop> orderedStops,
                               double totalDistanceKm, double calculatedCost) {
        this.orderedStops    = orderedStops;
        this.totalDistanceKm = totalDistanceKm;
        this.calculatedCost  = calculatedCost;
        // Generate a deterministic routeId from stop sequence
        this.routeId = "ROUTE-" + Math.abs(orderedStops.stream()
                .map(IDeliveryStop::getStopId)
                .reduce("", String::concat)
                .hashCode());
    }
}

/**
 * STRATEGY INTERFACE — all optimization algorithms implement this.
 */
interface IRouteStrategy {
    OptimizationResult optimize(List<IDeliveryStop> stops,
                                 List<RouteSegment> segments);
    String getStrategyName();
}

/**
 * STRATEGY 1: Nearest-Neighbor greedy algorithm minimizing total distance.
 */
class DistanceOptimizationStrategy implements IRouteStrategy {

    @Override
    public OptimizationResult optimize(List<IDeliveryStop> stops,
                                        List<RouteSegment> segments) {
        System.out.println("[Strategy] Running Distance Optimization (Nearest Neighbor)...");

        // Build adjacency map for O(1) lookup: "fromId->toId" => segment
        Map<String, RouteSegment> segMap = new HashMap<>();
        for (RouteSegment s : segments) {
            segMap.put(s.fromStopId + "->" + s.toStopId, s);
        }

        // Greedy nearest-neighbor: always go to the closest unvisited stop
        List<IDeliveryStop> ordered  = new ArrayList<>();
        Set<String> visited          = new HashSet<>();
        IDeliveryStop current        = stops.get(0);
        ordered.add(current);
        visited.add(current.getStopId());

        while (ordered.size() < stops.size()) {
            double bestDist = Double.MAX_VALUE;
            IDeliveryStop bestNext = null;
            for (IDeliveryStop candidate : stops) {
                if (!visited.contains(candidate.getStopId())) {
                    RouteSegment seg = segMap.get(current.getStopId() + "->" + candidate.getStopId());
                    double dist = (seg != null) ? seg.distanceKm : haversine(current, candidate);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestNext = candidate;
                    }
                }
            }
            if (bestNext != null) {
                ordered.add(bestNext);
                visited.add(bestNext.getStopId());
                current = bestNext;
            }
        }

        double totalDist       = segments.stream().mapToDouble(s -> s.distanceKm).sum();
        double calculatedCost  = segments.stream().mapToDouble(s -> s.estimatedFuelCostUSD).sum();
        return new OptimizationResult(ordered, totalDist, calculatedCost);
    }

    private double haversine(IDeliveryStop a, IDeliveryStop b) {
        final double R = 6371.0;
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double h = Math.sin(dLat/2)*Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(a.getLatitude()))
                 * Math.cos(Math.toRadians(b.getLatitude()))
                 * Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1-h));
    }

    @Override
    public String getStrategyName() { return "DISTANCE_NEAREST_NEIGHBOR"; }
}

/**
 * STRATEGY 2: Minimizes total fuel + operational cost.
 * Applies a cost matrix weighted by fuel, tolls, and package priority.
 */
class CostOptimizationStrategy implements IRouteStrategy {
    private static final double PRIORITY_WEIGHT = 5.0;  // $ penalty per priority level skipped
    private static final double TOLL_RATE_PER_KM = 0.05;

    @Override
    public OptimizationResult optimize(List<IDeliveryStop> stops,
                                        List<RouteSegment> segments) {
        System.out.println("[Strategy] Running Cost Optimization (Weighted Cost Matrix)...");

        // Sort stops by a composite cost score: lower priority number = higher urgency
        List<IDeliveryStop> ordered = new ArrayList<>(stops);
        ordered.sort(Comparator.comparingInt(IDeliveryStop::getPriorityLevel));

        double totalDist      = segments.stream().mapToDouble(s -> s.distanceKm).sum();
        double fuelCost       = segments.stream().mapToDouble(s -> s.estimatedFuelCostUSD).sum();
        double tollCost       = totalDist * TOLL_RATE_PER_KM;
        double priorityPenalty = ordered.stream()
                .mapToDouble(s -> (s.getPriorityLevel() - 1) * PRIORITY_WEIGHT)
                .sum();

        double calculatedCost = fuelCost + tollCost - priorityPenalty;
        return new OptimizationResult(ordered, totalDist, Math.max(0, calculatedCost));
    }

    @Override
    public String getStrategyName() { return "COST_WEIGHTED_MATRIX"; }
}

/**
 * STRATEGY 3: Optimizes by delivery priority — high-priority stops first.
 */
class PriorityOptimizationStrategy implements IRouteStrategy {

    @Override
    public OptimizationResult optimize(List<IDeliveryStop> stops,
                                        List<RouteSegment> segments) {
        System.out.println("[Strategy] Running Priority Optimization (SLA-first)...");

        List<IDeliveryStop> ordered = new ArrayList<>(stops);
        ordered.sort(Comparator.comparingInt(IDeliveryStop::getPriorityLevel));

        double totalDist      = segments.stream().mapToDouble(s -> s.distanceKm).sum();
        double calculatedCost = segments.stream().mapToDouble(s -> s.estimatedFuelCostUSD).sum();
        return new OptimizationResult(ordered, totalDist, calculatedCost);
    }

    @Override
    public String getStrategyName() { return "PRIORITY_SLA_FIRST"; }
}


// =============================================================================
// SECTION 5: CREATIONAL — Factory Method Pattern
// Decouples strategy instantiation from the facade. Extend by adding new
// strategy types without touching existing code (Open/Closed Principle).
// =============================================================================

abstract class RouteStrategyFactory {
    /** Template method — subclasses override createStrategy(). */
    public final IRouteStrategy getStrategy(IRouteData routeData) {
        System.out.println("[Factory] Creating strategy for mode: "
                + routeData.getOptimizationMode());
        return createStrategy(routeData);
    }

    protected abstract IRouteStrategy createStrategy(IRouteData routeData);
}

/**
 * Concrete factory — reads metadata and selects the correct strategy.
 * The DB team populates IRouteData.getOptimizationMode() with a known string.
 */
class DeliveryRouteStrategyFactory extends RouteStrategyFactory {
    @Override
    protected IRouteStrategy createStrategy(IRouteData routeData) {
        switch (routeData.getOptimizationMode().toUpperCase()) {
            case "DISTANCE": return new DistanceOptimizationStrategy();
            case "COST":     return new CostOptimizationStrategy();
            case "PRIORITY": return new PriorityOptimizationStrategy();
            default:
                System.out.println("[Factory] Unknown mode '" + routeData.getOptimizationMode()
                        + "', defaulting to DISTANCE.");
                return new DistanceOptimizationStrategy();
        }
    }
}


// =============================================================================
// SECTION 6: STRUCTURAL — Facade Pattern
// Single entry point for ALL other teams. They only ever call this class.
// Hides the factory, strategy, adapter, and observer complexity completely.
// =============================================================================

/**
 * PUBLIC API OF THIS SUBSYSTEM.
 *
 * Other teams integrate by:
 *   1. DB Team         → implements IRouteData and IDeliveryStop, passes to optimizeDeliveryCycle()
 *   2. Fleet/WH        → implements IRouteEventListener, calls subscribeToRouteEvents()
 *   3. Real-Time Mon.  → calls onExternalAlert() when an anomaly is detected on a route
 *
 * That's it. No other classes need to be touched.
 */
public class RouteOptimizerFacade {

    private final RouteStrategyFactory strategyFactory;
    private final IMapRouterPort       mapRouter;
    private final RouteEventBus        eventBus;

    // Tracks active shipmentId → routeData so alerts can trigger re-optimization
    private final Map<String, IRouteData> activeRoutes = new HashMap<>();

    /**
     * Default constructor uses Google Maps adapter.
     * To swap routing APIs, use the overloaded constructor.
     */
    public RouteOptimizerFacade() {
        this.strategyFactory = new DeliveryRouteStrategyFactory();
        this.mapRouter       = new GoogleMapsAdapter(new GoogleMapsApiClient());
        this.eventBus        = RouteEventBus.getInstance();
    }

    /**
     * Overloaded constructor for dependency injection (testing / API swap).
     */
    public RouteOptimizerFacade(IMapRouterPort customRouter) {
        this.strategyFactory = new DeliveryRouteStrategyFactory();
        this.mapRouter       = customRouter;
        this.eventBus        = RouteEventBus.getInstance();
    }

    // ------------------------------------------------------------------
    // PRIMARY INTEGRATION POINT FOR DB TEAM
    // ------------------------------------------------------------------

    /**
     * THE main method other teams call.
     *
     * Handles drop-ship flag: if isDropShip() is true, warehouse routing is
     * skipped and a direct supplier-to-destination route is computed instead.
     *
     * @param routeData  Populated by the DB team using their implementation of IRouteData
     * @return           OptimizationResult with ordered stops, routeId, distance, and calculatedCost
     */
    public OptimizationResult optimizeDeliveryCycle(IRouteData routeData) {
        System.out.println("\n====== RouteOptimizerFacade: optimizeDeliveryCycle ======");
        System.out.println("Shipment : " + routeData.getShipmentId()
                + " | Vehicle : " + routeData.getVehicleId()
                + " | Mode    : " + routeData.getOptimizationMode());
        System.out.println("Constraints → MinCost=" + routeData.isMinCost()
                + "  MinTime=" + routeData.isMinTime()
                + "  AvoidTolls=" + routeData.isAvoidTolls()
                + "  DropShip=" + routeData.isDropShip());

        // Step 0: Drop-ship check — bypass warehouse if flag is set (doc row 5 / fn 13)
        if (routeData.isDropShip()) {
            System.out.println("[Facade] DROP-SHIP detected — bypassing warehouse routing. "
                    + "Dispatching directly from supplier.");
            return handleDropShipRoute(routeData);
        }

        // Step 1: Select strategy via Factory Method
        IRouteStrategy strategy = strategyFactory.getStrategy(routeData);

        // Step 2: Fetch routing data from external Map API via Adapter
        List<RouteSegment> segments = mapRouter.calculateSegments(routeData.getStops());

        // Step 3: Run the optimization algorithm via Strategy
        OptimizationResult result = strategy.optimize(routeData.getStops(), segments);

        // Step 4: Register this shipment as active (so alerts can re-trigger it)
        activeRoutes.put(routeData.getShipmentId(), routeData);

        // Step 5: Publish event to all observers (Fleet, Warehouse, Real-Time Monitoring)
        List<String> stopIds = new ArrayList<>();
        for (IDeliveryStop stop : result.orderedStops) stopIds.add(stop.getStopId());

        eventBus.publish(new RouteRecalculatedEvent(
                routeData.getShipmentId(),
                routeData.getVehicleId(),
                stopIds,
                result.totalDistanceKm,
                result.calculatedCost));

        System.out.println("[Facade] Optimization complete."
                + " RouteId: "   + result.routeId
                + " | Strategy: " + strategy.getStrategyName()
                + " | Distance: " + String.format("%.2f", result.totalDistanceKm) + "km"
                + " | Cost: $"    + String.format("%.2f", result.calculatedCost));
        System.out.println("=========================================================\n");

        return result;
    }

    /**
     * Handles drop-ship routing — computes a direct supplier → destination path,
     * skipping all warehouse stops. Implements confirmDropShipOrder() from doc fn 13.
     */
    private OptimizationResult handleDropShipRoute(IRouteData routeData) {
        // For drop-ship, we still optimise the supplier→stop segments — just no depot
        List<RouteSegment> segments = mapRouter.calculateSegments(routeData.getStops());
        IRouteStrategy strategy     = new DistanceOptimizationStrategy();
        OptimizationResult result   = strategy.optimize(routeData.getStops(), segments);

        List<String> stopIds = new ArrayList<>();
        for (IDeliveryStop stop : result.orderedStops) stopIds.add(stop.getStopId());

        eventBus.publish(new RouteRecalculatedEvent(
                routeData.getShipmentId(), routeData.getVehicleId(),
                stopIds, result.totalDistanceKm, result.calculatedCost));

        System.out.println("[Facade] Drop-ship route published. RouteId: " + result.routeId);
        System.out.println("=========================================================\n");
        return result;
    }

    // ------------------------------------------------------------------
    // INBOUND ALERT HANDLER — FOR REAL-TIME MONITORING TEAM (Ramen Noodles)
    // Called by Team "Ramen Noodles" when an anomaly is detected (doc row 15 / fn 11)
    // ------------------------------------------------------------------

    /**
     * Receives an external alert from the Real-Time Monitoring subsystem.
     * If the affected shipment is currently active, its route is automatically
     * re-optimised and all observers are notified.
     *
     * @param alertId          Unique alert identifier (doc field "alertId")
     * @param message          Human-readable description, e.g. "Accident on NH-48" (doc field "message")
     * @param affectedShipmentId  The shipmentId whose route is impacted
     */
    public void onExternalAlert(String alertId, String message, String affectedShipmentId) {
        System.out.println("\n[Facade] ⚠ ALERT RECEIVED — id=" + alertId + " | " + message);
        IRouteData affected = activeRoutes.get(affectedShipmentId);
        if (affected == null) {
            System.out.println("[Facade] Shipment " + affectedShipmentId
                    + " not currently active. Alert logged, no re-optimisation needed.");
            return;
        }
        System.out.println("[Facade] Re-optimising route for shipment: " + affectedShipmentId);
        optimizeDeliveryCycle(affected);   // triggers full re-optimisation + observer notifications
    }

    // ------------------------------------------------------------------
    // PRIMARY INTEGRATION POINT FOR FLEET & WAREHOUSE TEAMS
    // ------------------------------------------------------------------

    /**
     * Fleet Tracking and Warehouse teams call this once at startup
     * to register for live route-change notifications.
     *
     * @param listener  Their class that implements IRouteEventListener
     */
    public void subscribeToRouteEvents(IRouteEventListener listener) {
        eventBus.subscribe(listener);
    }

    public void unsubscribeFromRouteEvents(IRouteEventListener listener) {
        eventBus.unsubscribe(listener);
    }
}


// =============================================================================
// SECTION 7: MOCK IMPLEMENTATIONS — Simulating other teams' concrete classes
// These would live in the other teams' codebases, not in this subsystem.
// =============================================================================

// --- DB TEAM'S concrete implementation of IDeliveryStop ---
class DbDeliveryStop implements IDeliveryStop {
    private final String stopId; private final double lat; private final double lng;
    private final String address; private final int priority; private final double weightKg;
    private final String priorityLabel;

    public DbDeliveryStop(String stopId, double lat, double lng,
                           String address, int priority, double weightKg) {
        this.stopId = stopId; this.lat = lat; this.lng = lng;
        this.address = address; this.priority = priority; this.weightKg = weightKg;
        // Auto-derive label from int level: 1=Next-Day, 2=Express, else=Standard
        this.priorityLabel = (priority == 1) ? "Next-Day" : (priority == 2) ? "Express" : "Standard";
    }

    @Override public String getStopId()             { return stopId; }
    @Override public double getLatitude()           { return lat; }
    @Override public double getLongitude()          { return lng; }
    @Override public String getAddress()            { return address; }
    @Override public int getPriorityLevel()         { return priority; }
    @Override public String getPriorityLabel()      { return priorityLabel; }
    @Override public double getPackageWeightKg()    { return weightKg; }
}

// --- DB TEAM'S concrete implementation of IRouteData ---
class DbRouteData implements IRouteData {
    private String shipmentId, vehicleId, mode, depotAddress;
    private double maxCapacity;
    private List<IDeliveryStop> stops;
    private boolean minCost, minTime, avoidTolls, dropShip;

    public DbRouteData(String shipmentId, String vehicleId, String mode,
                        String depotAddress, double maxCapacity,
                        List<IDeliveryStop> stops,
                        boolean minCost, boolean minTime,
                        boolean avoidTolls, boolean dropShip) {
        this.shipmentId = shipmentId; this.vehicleId = vehicleId;
        this.mode = mode; this.depotAddress = depotAddress;
        this.maxCapacity = maxCapacity; this.stops = stops;
        this.minCost = minCost; this.minTime = minTime;
        this.avoidTolls = avoidTolls; this.dropShip = dropShip;
    }

    @Override public String getShipmentId()              { return shipmentId; }
    @Override public String getDeliveryId()              { return shipmentId; }  // alias
    @Override public String getVehicleId()               { return vehicleId; }
    @Override public String getOptimizationMode()        { return mode; }
    @Override public boolean isMinCost()                 { return minCost; }
    @Override public boolean isMinTime()                 { return minTime; }
    @Override public boolean isAvoidTolls()              { return avoidTolls; }
    @Override public boolean isDropShip()                { return dropShip; }
    @Override public List<IDeliveryStop> getStops()      { return stops; }
    @Override public double getMaxVehicleCapacityKg()    { return maxCapacity; }
    @Override public String getDepotAddress()            { return depotAddress; }
}

// --- FLEET TRACKING TEAM'S listener ---
class FleetTrackingListener implements IRouteEventListener {
    @Override
    public void onRouteRecalculated(RouteRecalculatedEvent event) {
        System.out.println("[FleetTracking] 🚚 Vehicle " + event.getVehicleId()
                + " route updated! New stop order: " + event.getOrderedStopIds()
                + " | ETA recalculated.");
    }
}

// --- WAREHOUSE TEAM'S listener ---
class WarehouseListener implements IRouteEventListener {
    @Override
    public void onRouteRecalculated(RouteRecalculatedEvent event) {
        System.out.println("[Warehouse] 📦 Delivery " + event.getDeliveryId()
                + " route changed. Notifying dispatch dock for vehicle "
                + event.getVehicleId() + ". First stop: "
                + (event.getOrderedStopIds().isEmpty() ? "N/A" : event.getOrderedStopIds().get(0)));
    }
}


// =============================================================================
// SECTION 8: DEMO MAIN — Wires everything together
// =============================================================================

class RouteOptimizerDemo {
    public static void main(String[] args) {

        // ---- STEP 1: Fleet & Warehouse teams register their listeners at startup ----
        RouteOptimizerFacade facade = new RouteOptimizerFacade();
        facade.subscribeToRouteEvents(new FleetTrackingListener());
        facade.subscribeToRouteEvents(new WarehouseListener());

        // ---- STEP 2: DB team prepares route data from their database ----
        List<IDeliveryStop> stops = Arrays.asList(
            new DbDeliveryStop("STOP-001", 40.7128, -74.0060, "1 Wall St, NY",       2, 12.5),
            new DbDeliveryStop("STOP-002", 40.7580, -73.9855, "Times Square, NY",    1,  8.0),
            new DbDeliveryStop("STOP-003", 40.6892, -74.0445, "Liberty Island, NY",  3, 20.0),
            new DbDeliveryStop("STOP-004", 40.7282, -73.7949, "JFK Airport, NY",     2, 15.0)
        );

        // ---- TEST A: Distance-based, MinTime constraint, standard warehouse route ----
        IRouteData routeDataA = new DbRouteData(
                "SHP-2024-001", "VEH-TRUCK-7", "DISTANCE",
                "200 Water St, NY", 500.0, stops,
                false, true, false, false);   // minCost=false, minTime=true, avoidTolls=false, dropShip=false
        facade.optimizeDeliveryCycle(routeDataA);

        // ---- TEST B: Cost-based, MinCost + AvoidTolls constraints ----
        IRouteData routeDataB = new DbRouteData(
                "SHP-2024-002", "VEH-VAN-3", "COST",
                "200 Water St, NY", 300.0, stops,
                true, false, true, false);    // minCost=true, avoidTolls=true
        facade.optimizeDeliveryCycle(routeDataB);

        // ---- TEST C: Priority-based, Next-Day SLA ----
        IRouteData routeDataC = new DbRouteData(
                "SHP-2024-003", "VEH-BIKE-1", "PRIORITY",
                "200 Water St, NY", 100.0, stops,
                false, true, false, false);
        facade.optimizeDeliveryCycle(routeDataC);

        // ---- TEST D: Drop-ship — bypasses warehouse entirely ----
        List<IDeliveryStop> dropStops = Arrays.asList(
            new DbDeliveryStop("STOP-DS1", 40.7128, -74.0060, "BlueDart Depot, NY", 1, 5.0)
        );
        IRouteData routeDataD = new DbRouteData(
                "SHP-2024-004", "VEH-VAN-5", "DISTANCE",
                "Supplier Warehouse, NJ", 200.0, dropStops,
                false, false, false, true);   // dropShip=true
        facade.optimizeDeliveryCycle(routeDataD);

        // ---- TEST E: Real-Time Monitoring team fires an alert mid-route ----
        System.out.println(">>> Ramen Noodles team fires an alert for SHP-2024-001...");
        facade.onExternalAlert("ALT-009", "Accident on NH-48 — major delay", "SHP-2024-001");

        // ---- TEST F: Swapping to OSRM adapter at runtime ----
        System.out.println(">>> Switching to OSRM adapter...");
        RouteOptimizerFacade osrmFacade = new RouteOptimizerFacade(
                new OsrmAdapter(new OsrmHttpClient()));
        osrmFacade.subscribeToRouteEvents(new FleetTrackingListener());
        IRouteData routeDataF = new DbRouteData(
                "SHP-2024-005", "VEH-TRUCK-2", "DISTANCE",
                "200 Water St, NY", 500.0, stops,
                false, true, false, false);
        osrmFacade.optimizeDeliveryCycle(routeDataF);
    }
}
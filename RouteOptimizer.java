import java.util.Map;
import java.util.Objects;

public final class RouteOptimizer implements IMLAlgorithmicExceptionSource, IResourceAvailabilityExceptionSource {
    public interface Port { RoutePlan optimize(RouteRequest request); void registerHandler(SCMExceptionHandler handler); }
    public interface RoutingEngine { RoutePlan solve(RouteRequest request); }
    public interface FallbackPolicy { RoutePlan fallback(RouteRequest request); }
    public interface CarrierGateway { int availableCarriers(String corridorId); }
    public record RoutePlan(String routeId, int etaMinutes, boolean lowConfidence) {}
    public record RouteRequest(String shipmentId, String corridorId, int loadUnits, int slaMinutes) {
        public static final class Builder {
            private String shipmentId;
            private String corridorId;
            private int loadUnits;
            private int slaMinutes;
            public Builder shipmentId(String value) { this.shipmentId = value; return this; }
            public Builder corridorId(String value) { this.corridorId = value; return this; }
            public Builder loadUnits(int value) { this.loadUnits = value; return this; }
            public Builder slaMinutes(int value) { this.slaMinutes = value; return this; }
            public RouteRequest build() {
                return new RouteRequest(Objects.requireNonNull(shipmentId, "shipmentId"),
                        Objects.requireNonNull(corridorId, "corridorId"), loadUnits, slaMinutes);
            }
        }
    }
    private static final Map<Integer, ExceptionSpec> CATALOG = Map.of(
            161, new ExceptionSpec("CARRIER_UNAVAILABLE", Severity.MAJOR,
                    "Transport and Logistics Management", "No carrier is available for the route."),
            170, new ExceptionSpec("ROUTE_INFORMATION_UNAVAILABLE", Severity.MINOR,
                    "Delivery Orders", "Route information for the delivery agent could not be retrieved."),
            462, new ExceptionSpec("NO_VIABLE_ROUTE_FOUND", Severity.WARNING,
                    "Transport and Logistics Management", "Routing algorithm could not find any valid route."),
            463, new ExceptionSpec("CRITICAL_TRANSIT_DELAY", Severity.WARNING,
                    "Transport and Logistics Management", "Transit delay exceeds critical threshold.")
    );
    private final RoutingEngine engine;
    private final FallbackPolicy fallbackPolicy;
    private final CarrierGateway carrierGateway;
    private SCMExceptionHandler handler;
    public static Port create(RoutingEngine routingEngine, FallbackPolicy fallbackPolicy, CarrierGateway carrierGateway) {
        RouteOptimizer impl = new RouteOptimizer(routingEngine, fallbackPolicy, carrierGateway);
        return new Port() {
            @Override public RoutePlan optimize(RouteRequest request) { return impl.optimize(request); }
            @Override public void registerHandler(SCMExceptionHandler handler) { impl.registerHandler(handler); }
        };
    }
    private RouteOptimizer(RoutingEngine routingEngine, FallbackPolicy fallbackPolicy, CarrierGateway carrierGateway) {
        this.engine = Objects.requireNonNull(routingEngine, "routingEngine");
        this.fallbackPolicy = Objects.requireNonNull(fallbackPolicy, "fallbackPolicy");
        this.carrierGateway = Objects.requireNonNull(carrierGateway, "carrierGateway");
    }
    public RoutePlan optimize(RouteRequest request) {
        try {
            if (carrierGateway.availableCarriers(request.corridorId()) <= 0) {
                fireResourceExhausted(161, "Carrier", request.corridorId(), 1, 0);
                return fallbackPolicy.fallback(request);
            }
            RoutePlan candidate = engine.solve(request);
            if (candidate == null || candidate.routeId().isBlank()) {
                fireModelFailure(462, "RouteOptimizerV1", "Model returned no viable route.");
                return fallbackPolicy.fallback(request);
            }
            if (candidate.etaMinutes() > request.slaMinutes()) {
                fireAlgorithmicAlert(463, "RouteOptimizer", request.shipmentId(), "ETA " + candidate.etaMinutes()
                        + " exceeds SLA " + request.slaMinutes() + ".");
                return new RoutePlan(candidate.routeId(), candidate.etaMinutes(), true);
            }
            return candidate;
        } catch (RuntimeException ex) {
            raise(0, Severity.MAJOR, "Unregistered exception in RouteOptimizer: " + ex.getMessage());
            return fallbackPolicy.fallback(request);
        }
    }

    @Override
    public void registerHandler(SCMExceptionHandler handler) {
        this.handler = handler;
    }
    private void raise(int id, Severity fallback, String detail) {
        SCMEvents.emit(CATALOG, handler, id, fallback, detail,
                "Transport and Logistics Management", "Unregistered transport exception.");
    }
    @Override public void fireModelFailure(int exceptionId, String modelName, String reason) { raise(exceptionId, Severity.MAJOR, modelName + " failure: " + reason); }
    @Override public void fireModelDegradation(int exceptionId, String modelName, String metric, double threshold, double actual) { raise(exceptionId, Severity.MINOR, modelName + " degraded on " + metric + " threshold=" + threshold + " actual=" + actual); }
    @Override public void fireMissingInputData(int exceptionId, String modelName, String missingDataType, String affectedPeriod) { raise(exceptionId, Severity.MINOR, modelName + " missing " + missingDataType + " for period " + affectedPeriod); }
    @Override public void fireAlgorithmicAlert(int exceptionId, String processName, String entityId, String detail) { raise(exceptionId, Severity.WARNING, processName + " entity=" + entityId + " detail=" + detail); }
    @Override public void fireResourceNotFound(int exceptionId, String resourceType, String resourceId) { raise(exceptionId, Severity.MINOR, resourceType + " not found: " + resourceId); }
    @Override public void fireResourceExhausted(int exceptionId, String resourceType, String resourceId, int requested, int available) { raise(exceptionId, Severity.MAJOR, resourceType + " exhausted for " + resourceId + " requested=" + requested + " available=" + available); }
    @Override public void fireResourceBlocked(int exceptionId, String resourceType, String resourceId, String reason) { raise(exceptionId, Severity.MINOR, resourceType + " blocked for " + resourceId + " reason=" + reason); }
    @Override public void fireCapacityExceeded(int exceptionId, String resourceType, String resourceId, int limit) { raise(exceptionId, Severity.MAJOR, resourceType + " limit exceeded for " + resourceId + " limit=" + limit); }
}

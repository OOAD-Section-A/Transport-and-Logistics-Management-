package entities;

import java.util.Objects;
import exceptions.*;
import com.scm.factory.SCMExceptionFactory;
import com.scm.subsystems.TransportLogisticsSubsystem;

/**
 * RouteOptimizer Entity
 * PATTERN: Strategy + Adapter
 * Optimizes routes based on carrier availability and SLA
 */
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
    private final RoutingEngine engine;
    private final FallbackPolicy fallbackPolicy;
    private final CarrierGateway carrierGateway;
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;
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
                exceptions.onCarrierUnavailable(request.corridorId());
                return fallbackPolicy.fallback(request);
            }
            RoutePlan candidate = engine.solve(request);
            if (candidate == null || candidate.routeId().isBlank()) {
                exceptions.onNoViableRouteFound(request.shipmentId(), request.corridorId());
                return fallbackPolicy.fallback(request);
            }
            if (candidate.etaMinutes() > request.slaMinutes()) {
                exceptions.onCriticalTransitDelay(
                        request.shipmentId(),
                        candidate.etaMinutes() - request.slaMinutes()
                );
                return new RoutePlan(candidate.routeId(), candidate.etaMinutes(), true);
            }
            return candidate;
        } catch (RuntimeException ex) {
            com.scm.handler.SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", "RouteOptimizer failed: " + ex.getMessage())
            );
            return fallbackPolicy.fallback(request);
        }
    }

    // Multi-Stop & Multi-Modal Routing
    public RoutePlan optimizeMultiStop(java.util.List<RouteRequest> requests) {
        // Minimal implementation: Optimize each request separately and combine
        int totalEta = 0;
        String combinedRouteId = "";
        boolean lowConfidence = false;
        for (RouteRequest req : requests) {
            RoutePlan plan = optimize(req);
            totalEta += plan.etaMinutes();
            combinedRouteId += plan.routeId() + ";";
            if (plan.lowConfidence()) lowConfidence = true;
        }
        return new RoutePlan(combinedRouteId, totalEta, lowConfidence);
    }

    public void registerHandler(SCMExceptionHandler handler) {
        // No-op: real handler is managed by the exception module singleton.
    }
    @Override public void fireModelFailure(int exceptionId, String modelName, String reason) { exceptions.onNoViableRouteFound(modelName, reason); }
    @Override public void fireModelDegradation(int exceptionId, String modelName, String metric, double threshold, double actual) { exceptions.onNoViableRouteFound(modelName, metric); }
    @Override public void fireMissingInputData(int exceptionId, String modelName, String missingDataType, String affectedPeriod) { exceptions.onNoViableRouteFound(modelName, missingDataType); }
    @Override public void fireAlgorithmicAlert(int exceptionId, String processName, String entityId, String detail) { exceptions.onCriticalTransitDelay(entityId, 0); }
    @Override public void fireResourceNotFound(int exceptionId, String resourceType, String resourceId) { exceptions.onCarrierUnavailable(resourceId); }
    @Override public void fireResourceExhausted(int exceptionId, String resourceType, String resourceId, int requested, int available) { exceptions.onCarrierUnavailable(resourceId); }
    @Override public void fireResourceBlocked(int exceptionId, String resourceType, String resourceId, String reason) { exceptions.onCarrierUnavailable(resourceId); }
    @Override public void fireCapacityExceeded(int exceptionId, String resourceType, String resourceId, int limit) { exceptions.onWeightLimitExceeded(resourceId, limit + 1, limit); }
}

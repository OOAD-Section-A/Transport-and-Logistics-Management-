package transport;

import entities.AlertManager;
import entities.LiveTracking;
import entities.RouteOptimizer;
import factories.ShipmentBuilder;
import repositories.TransportRepository;
import services.TransportService;

public class ExceptionDbIntegrationSmokeTest {
    public static void main(String[] args) {
        checkDatabaseModule();

        TransportService svc = new TransportService(new TransportRepository());
        runCase("invalid destination pincode", () ->
                svc.createShipment(new ShipmentBuilder("E1").withSupplierId("SUP1").withCarrierId("CAR1")
                        .withOrigin("A").withDestination("ABC").withWeight(10).withCost(100).withStatus("Pending").build()));

        runCase("weight limit exceeded", () ->
                svc.createShipment(new ShipmentBuilder("E2").withSupplierId("SUP1").withCarrierId("CAR1")
                        .withOrigin("A").withDestination("560001").withWeight(120).withCost(100).withStatus("Pending").build()));

        runCase("supplier out of stock", () ->
                svc.createShipment(new ShipmentBuilder("E3").withSupplierId("").withCarrierId("CAR1")
                        .withOrigin("A").withDestination("560001").withWeight(10).withCost(100).withStatus("Pending").build()));

        runCase("no viable route", () -> svc.optimizeRoute("", "560001", null));
        runCase("carrier API timeout", () -> svc.optimizeRoute("560001", "560002", "timeout"));
        runCase("GPS signal lost", () -> svc.getTrackingData(""));

        runCase("carrier unavailable", () -> {
            RouteOptimizer.Port roUnavailable = RouteOptimizer.create(
                    req -> new RouteOptimizer.RoutePlan("R1", 100, false),
                    req -> new RouteOptimizer.RoutePlan("FB1", req.slaMinutes() + 10, true),
                    corridor -> 0
            );
            roUnavailable.optimize(new RouteOptimizer.RouteRequest.Builder()
                    .shipmentId("E4").corridorId("COR-1").loadUnits(10).slaMinutes(120).build());
        });

        runCase("critical transit delay", () -> {
            RouteOptimizer.Port roDelay = RouteOptimizer.create(
                    req -> new RouteOptimizer.RoutePlan("R2", req.slaMinutes() + 30, false),
                    req -> new RouteOptimizer.RoutePlan("FB2", req.slaMinutes() + 10, true),
                    corridor -> 2
            );
            roDelay.optimize(new RouteOptimizer.RouteRequest.Builder()
                    .shipmentId("E5").corridorId("COR-2").loadUnits(10).slaMinutes(120).build());
        });

        runCase("live tracking GPS lost", () -> {
            LiveTracking.Port ltGpsLost = LiveTracking.create(
                    vehicleId -> null,
                    snapshot -> {},
                    5000,
                    5.0
            );
            ltGpsLost.track("VEH-1");
        });

        runCase("live tracking timeout", () -> {
            LiveTracking.Port ltTimeout = LiveTracking.create(
                    vehicleId -> new LiveTracking.Telemetry(0.0, 0.0, 80, 9000, 0.0, false),
                    snapshot -> {},
                    5000,
                    5.0
            );
            ltTimeout.track("VEH-2");
        });

        runCase("alert critical transit delay", () -> {
            AlertManager.Port alerts = AlertManager.create(e -> {}, e -> {}, e -> {});
            alerts.emit(new AlertManager.AlertEnvelope("E6", "delivery", AlertManager.AlertLevel.CRITICAL,
                    "carrier unavailable", 5000, 3000));
        });

        System.out.println("DONE: exception and DB smoke test completed");
    }

    private static void runCase(String name, Runnable action) {
        System.out.println("Trigger: " + name);
        try {
            action.run();
            System.out.println("Result: completed");
        } catch (Throwable ex) {
            if (isHeadlessPopup(ex)) {
                System.out.println("Result: completed (popup suppressed in headless mode)");
                return;
            }
            System.out.println("Result: failed in runtime path -> " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private static boolean isHeadlessPopup(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof java.awt.HeadlessException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static void checkDatabaseModule() {
        try {
            Class<?> facadeClass = Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            Object facade = facadeClass.getDeclaredConstructor().newInstance();
            try {
                facadeClass.getMethod("close").invoke(facade);
            } catch (NoSuchMethodException ignored) {
                // close method not required for this availability check
            }
            System.out.println("DB module check: facade loaded successfully");
        } catch (Exception ex) {
            System.out.println("DB module check failed: " + ex.getMessage());
        }
    }
}

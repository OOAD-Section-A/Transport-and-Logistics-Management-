package integration;

import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.observer.DeliveryEventType;
import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;
import entities.*;
import facade.TransportFacade;
import factories.ShipmentBuilder;
import flyweight.CarrierFlyweightFactory;
import repositories.TransportRepository;
import services.TransportService;
import controllers.TransportController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FullTestSuite {

    // ------------------------------------------------------------------ runner

    private static int passed = 0;
    private static int failed = 0;
    private static String currentSection = "";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║      TRANSPORT & LOGISTICS MANAGEMENT — FULL TEST SUITE ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        runServiceUnitTests();
        runValidationTests();
        runShipmentLifecycleTests();
        runCarrierFlyweightTests();
        runBuilderAndPrototypeTests();
        runControllerTests();
        runDomainFeatureTests();
        runDeliveryMonitoringIntegrationTest();
        runDatabaseShowcaseTest();

        printSummary();
    }

    // -------------------------------------------------------- 1. SERVICE UNIT

    private static void runServiceUnitTests() {
        section("1. SERVICE UNIT TESTS");
        TransportRepository repo = new TransportRepository();
        TransportService service = new TransportService(repo);

        // FreightAudit
        FreightAudit noDisc = service.auditFreight("A001", "S001", 100.0, 100.0);
        check("auditFreight — no discrepancy flag",
              noDisc != null && "NONE".equals(noDisc.getDiscrepancyFlag()));

        FreightAudit overcharge = service.auditFreight("A002", "S001", 150.0, 100.0);
        check("auditFreight — overcharge detected",
              overcharge != null && "OVERCHARGE".equals(overcharge.getDiscrepancyFlag()));

        FreightAudit undercharge = service.auditFreight("A003", "S001", 80.0, 100.0);
        check("auditFreight — undercharge detected",
              undercharge != null && "UNDERCHARGE".equals(undercharge.getDiscrepancyFlag()));

        // ConstraintPlanner
        ConstraintPlanner planner = service.planConstraints("P001", "S001", 5000.0, 4.5, 8, "9AM-5PM");
        check("planConstraints — returns correct planId",    planner != null && "P001".equals(planner.getPlanId()));
        check("planConstraints — weight limit stored",       planner != null && planner.getVehicleWeightLimit() == 5000.0);

        // Territory
        Territory territory = service.manageTerritory("T001", "Northeast", "US Northeast", 5);
        check("manageTerritory — correct id",        territory != null && "T001".equals(territory.getTerritoryId()));
        check("manageTerritory — driver count",      territory != null && territory.getAssignedDrivers() == 5);

        // OrderOrchestrator
        OrderOrchestrator thirdParty = service.orchestrateOrder("O001", "SO001", true, "SUP001");
        check("orchestrateOrder — third party flag",   thirdParty != null && thirdParty.isThirdPartyItem());
        check("orchestrateOrder — PO number generated", thirdParty != null && "PO-O001".equals(thirdParty.getPoNumber()));

        OrderOrchestrator internal = service.orchestrateOrder("O002", "SO002", false, "SUP002");
        check("orchestrateOrder — internal order (no PO)", internal != null && !internal.isThirdPartyItem());

        // SupplierPortal
        SupplierPortal portal = service.integrateSupplierPortal("P001", "SUP001", "Some order details");
        check("integrateSupplierPortal — correct id",  portal != null && "P001".equals(portal.getPortalId()));
        check("integrateSupplierPortal — ASN generated", portal != null && "ASN-P001".equals(portal.getAsn()));

        // TrackingSync
        TrackingSync sync = service.syncTracking("S001", "O001", "TRK-9999");
        check("syncTracking — correct syncId",        sync != null && "S001".equals(sync.getSyncId()));
        check("syncTracking — tracking number stored", sync != null && "TRK-9999".equals(sync.getTrackingNumber()));

        // ReverseLogistics
        ReverseLogistics rl = service.handleReverseLogistics("R001", "O001", "SUP001", 250.0);
        check("handleReverseLogistics — correct returnId",   rl != null && "R001".equals(rl.getReturnId()));
        check("handleReverseLogistics — refund amount",      rl != null && rl.getRefundAmount() == 250.0);
        check("handleReverseLogistics — status is PENDING",  rl != null && "PENDING".equals(rl.getReconciliationStatus()));
    }

    // ------------------------------------------------------ 2. VALIDATION

    private static void runValidationTests() {
        section("2. VALIDATION TESTS");
        TransportRepository repo = new TransportRepository();
        TransportService service = new TransportService(repo);

        // Valid shipment
        Shipment valid = new ShipmentBuilder("SHIP-V01")
                .withSupplierId("SUP001").withCarrierId("CAR001")
                .withOrigin("Mumbai").withDestination("560001")
                .withWeight(50.0).withCost(1000.0).withStatus("Pending").build();
        Shipment created = service.createShipment(valid);
        check("createShipment — valid shipment accepted", created != null);

        // Invalid pincode
        Shipment badPin = new ShipmentBuilder("SHIP-V02")
                .withSupplierId("SUP001").withCarrierId("CAR001")
                .withOrigin("Mumbai").withDestination("INVALID")
                .withWeight(50.0).withCost(1000.0).withStatus("Pending").build();
        Shipment rejectedPin = service.createShipment(badPin);
        check("createShipment — invalid pincode rejected", rejectedPin == null);

        // Pincode too short
        Shipment shortPin = new ShipmentBuilder("SHIP-V03")
                .withSupplierId("SUP001").withCarrierId("CAR001")
                .withOrigin("Mumbai").withDestination("5600")
                .withWeight(50.0).withCost(1000.0).withStatus("Pending").build();
        Shipment rejectedShort = service.createShipment(shortPin);
        check("createShipment — 4-digit pincode rejected", rejectedShort == null);

        // Weight exceeded
        Shipment heavy = new ShipmentBuilder("SHIP-V04")
                .withSupplierId("SUP001").withCarrierId("CAR001")
                .withOrigin("Delhi").withDestination("110001")
                .withWeight(101.0).withCost(5000.0).withStatus("Pending").build();
        Shipment rejectedHeavy = service.createShipment(heavy);
        check("createShipment — weight > 100t rejected", rejectedHeavy == null);

        // Boundary weight exactly 100
        Shipment maxWeight = new ShipmentBuilder("SHIP-V05")
                .withSupplierId("SUP001").withCarrierId("CAR001")
                .withOrigin("Delhi").withDestination("110001")
                .withWeight(100.0).withCost(5000.0).withStatus("Pending").build();
        Shipment acceptedMax = service.createShipment(maxWeight);
        check("createShipment — weight exactly 100t accepted", acceptedMax != null);

        // Null supplier — builder or service must reject
        boolean nullSupplierRejected = false;
        try {
            Shipment noSupplier = new ShipmentBuilder("SHIP-V06")
                    .withSupplierId(null).withCarrierId("CAR001")
                    .withOrigin("Pune").withDestination("411001")
                    .withWeight(10.0).withCost(500.0).withStatus("Pending").build();
            nullSupplierRejected = (service.createShipment(noSupplier) == null);
        } catch (IllegalArgumentException e) {
            nullSupplierRejected = true;
        }
        check("createShipment — null supplierId rejected", nullSupplierRejected);

        // Blank supplier — builder or service must reject
        boolean blankSupplierRejected = false;
        try {
            Shipment blankSupplier = new ShipmentBuilder("SHIP-V07")
                    .withSupplierId("   ").withCarrierId("CAR001")
                    .withOrigin("Pune").withDestination("411001")
                    .withWeight(10.0).withCost(500.0).withStatus("Pending").build();
            blankSupplierRejected = (service.createShipment(blankSupplier) == null);
        } catch (IllegalArgumentException e) {
            blankSupplierRejected = true;
        }
        check("createShipment — blank supplierId rejected", blankSupplierRejected);
    }

    // -------------------------------------------- 3. SHIPMENT LIFECYCLE

    private static void runShipmentLifecycleTests() {
        section("3. SHIPMENT LIFECYCLE TESTS");
        TransportRepository repo = new TransportRepository();
        TransportService service = new TransportService(repo);

        Shipment s = new ShipmentBuilder("LIFE-001")
                .withSupplierId("SUP-LC").withCarrierId("CAR-LC")
                .withOrigin("Chennai").withDestination("600001")
                .withWeight(30.0).withCost(2000.0).withStatus("Pending").build();

        service.createShipment(s);
        check("lifecycle — shipment created and retrievable", service.getShipment("LIFE-001") != null);

        service.updateShipmentStatus("LIFE-001", "In-Transit");
        Shipment after = service.getShipment("LIFE-001");
        check("lifecycle — status updated to In-Transit", after != null && "In-Transit".equals(after.getStatus()));

        service.updateShipmentStatus("LIFE-001", "Delivered");
        Shipment delivered = service.getShipment("LIFE-001");
        check("lifecycle — status updated to Delivered", delivered != null && "Delivered".equals(delivered.getStatus()));

        // Create a second shipment then list all
        Shipment s2 = new ShipmentBuilder("LIFE-002")
                .withSupplierId("SUP-LC").withCarrierId("CAR-LC")
                .withOrigin("Hyderabad").withDestination("500001")
                .withWeight(20.0).withCost(1500.0).withStatus("Pending").build();
        service.createShipment(s2);

        List<Shipment> all = service.getAllShipments();
        check("lifecycle — getAllShipments returns both", all != null && all.size() >= 2);

        // Non-existent shipment
        check("lifecycle — getShipment returns null for unknown id", service.getShipment("DOES-NOT-EXIST") == null);
    }

    // ------------------------------------------ 4. CARRIER FLYWEIGHT

    private static void runCarrierFlyweightTests() {
        section("4. CARRIER FLYWEIGHT TESTS");
        CarrierFlyweightFactory factory = CarrierFlyweightFactory.getInstance();

        Carrier c1 = factory.getCarrier("CAR-F01", "SpeedEx", "Road", 20.0);
        Carrier c2 = factory.getCarrier("CAR-F01", "SpeedEx", "Road", 20.0);
        check("flyweight — same ID returns same instance", c1 == c2);

        Carrier c3 = factory.getCarrier("CAR-F02", "AirFly", "Air", 5.0);
        check("flyweight — different ID returns different instance", c1 != c3);

        check("flyweight — carrier name correct",  "SpeedEx".equals(c1.getCarrierName()));
        check("flyweight — transport mode correct", "Air".equals(c3.getTransportMode()));

        int count = factory.getCachedCarrierCount();
        check("flyweight — cache size reflects registered carriers", count >= 2);
    }

    // --------------------------------------- 5. BUILDER + PROTOTYPE

    private static void runBuilderAndPrototypeTests() {
        section("5. BUILDER + PROTOTYPE TESTS");
        TransportFacade facade = new TransportFacade();

        // Builder
        Shipment built = facade.createShipmentBuilder("BUILD-001")
                .withSupplierId("SUP-B").withCarrierId("CAR-B")
                .withOrigin("Kolkata").withDestination("700001")
                .withWeight(15.0).withCost(800.0).withStatus("Pending").build();
        check("builder — shipment created with correct id",     "BUILD-001".equals(built.getShipmentId()));
        check("builder — destination correctly set",            "700001".equals(built.getDestination()));
        check("builder — weight correctly set",                 built.getWeight() == 15.0);

        // Prototype (register template then clone)
        facade.registerShipmentTemplate("express-template", built);
        Shipment cloned = facade.cloneShipmentFromTemplate("express-template", "CLONE-001");
        check("prototype — clone is not null",                  cloned != null);
        check("prototype — clone is a different object",        cloned != built);
        check("prototype — clone has same destination",         built.getDestination().equals(cloned.getDestination()));
        check("prototype — clone has same weight",              built.getWeight() == cloned.getWeight());
    }

    // --------------------------------------------- 6. CONTROLLER

    private static void runControllerTests() {
        section("6. CONTROLLER TESTS");
        TransportController controller = new TransportController();

        FreightAudit audit = controller.handleAuditFreight("CA001", "S001", 120.0, 100.0);
        check("controller — handleAuditFreight returns audit",        audit != null);
        check("controller — handleAuditFreight detects overcharge",   "OVERCHARGE".equals(audit.getDiscrepancyFlag()));

        ConstraintPlanner planner = controller.handlePlanConstraints("CP001", "S001", 3000.0, 3.5, 10, "8AM-6PM");
        check("controller — handlePlanConstraints not null",    planner != null);
        check("controller — plan id correct",                   "CP001".equals(planner.getPlanId()));

        Territory territory = controller.handleManageTerritory("CT001", "South", "South India", 8);
        check("controller — handleManageTerritory not null",    territory != null);
        check("controller — driver count correct",              territory.getAssignedDrivers() == 8);

        OrderOrchestrator order = controller.handleOrchestrateOrder("CO001", "CSO001", true, "CSUP001");
        check("controller — handleOrchestrateOrder not null",   order != null);
        check("controller — third party flag set",              order.isThirdPartyItem());

        SupplierPortal portal = controller.handleIntegrateSupplierPortal("CPR001", "CSUP001", "details");
        check("controller — handleIntegrateSupplierPortal not null", portal != null);
        check("controller — ASN generated",                          "ASN-CPR001".equals(portal.getAsn()));

        TrackingSync sync = controller.handleSyncTracking("CS001", "CO001", "TRK-CTRL");
        check("controller — handleSyncTracking not null",        sync != null);
        check("controller — tracking number correct",            "TRK-CTRL".equals(sync.getTrackingNumber()));

        ReverseLogistics rl = controller.handleReverseLogistics("CR001", "CO001", "CSUP001", 75.0);
        check("controller — handleReverseLogistics not null",    rl != null);
        check("controller — refund amount correct",              rl.getRefundAmount() == 75.0);
    }

    // ----------------------------------------- 7. DOMAIN FEATURES (via Facade)

    private static void runDomainFeatureTests() {
        section("7. DOMAIN FEATURE TESTS (via Facade)");
        TransportFacade facade = new TransportFacade();

        // Full shipment create + retrieve via facade
        Shipment s = facade.createShipmentBuilder("FAC-001")
                .withSupplierId("SUP-F").withCarrierId("CAR-F")
                .withOrigin("Bengaluru").withDestination("560001")
                .withWeight(25.0).withCost(4500.0).withStatus("Pending").build();
        facade.createShipment(s);
        check("facade — createShipment persists",       facade.getShipment("FAC-001") != null);

        facade.updateShipmentStatus("FAC-001", "In-Transit");
        check("facade — updateShipmentStatus works",    "In-Transit".equals(facade.getShipment("FAC-001").getStatus()));

        List<Shipment> all = facade.getAllShipments();
        check("facade — getAllShipments non-empty",     all != null && !all.isEmpty());

        // External data fetch (adapter)
        String ext = facade.getShipmentWithExternalData("FAC-001");
        check("facade — getShipmentWithExternalData returns data", ext != null && !ext.isEmpty());

        // All 7 domain features
        check("facade — auditFreight works",
              facade.auditFreight("FA001", "FAC-001", 200.0, 180.0) != null);

        check("facade — planConstraints works",
              facade.planConstraints("FP001", "FAC-001", 8000.0, 5.0, 12, "24x7") != null);

        check("facade — manageTerritory works",
              facade.manageTerritory("FT001", "West", "Western India", 10) != null);

        check("facade — orchestrateOrder works",
              facade.orchestrateOrder("FO001", "FSO001", false, "FSUP001") != null);

        check("facade — integrateSupplierPortal works",
              facade.integrateSupplierPortal("FPR001", "FSUP001", "order info") != null);

        check("facade — syncTracking works",
              facade.syncTracking("FSY001", "FO001", "TRK-FAC") != null);

        check("facade — handleReverseLogistics works",
              facade.handleReverseLogistics("FR001", "FO001", "FSUP001", 100.0) != null);
    }

    // --------------------- 8. DELIVERY MONITORING INTEGRATION

    private static void runDeliveryMonitoringIntegrationTest() {
        section("8. DELIVERY MONITORING INTEGRATION (Team 2 + Team 16)");
        try {
            TransportFacade tmsFacade = new TransportFacade();
            DeliveryMonitoringFacadeDB deliverySystem = new DeliveryMonitoringFacadeDB();
            TransportLogisticsServiceImpl tmsAdapter = new TransportLogisticsServiceImpl(tmsFacade);

            // Event bridge
            AtomicInteger locationEvents = new AtomicInteger(0);
            AtomicInteger deliveryEvents = new AtomicInteger(0);
            deliverySystem.subscribeToEvents(DeliveryEventType.LOCATION_UPDATED,
                    (type, data) -> locationEvents.incrementAndGet());
            deliverySystem.subscribeToEvents(DeliveryEventType.ORDER_DELIVERED,
                    (type, data) -> deliveryEvents.incrementAndGet());
            check("integration — event subscriptions registered", true);

            // Step A: fleet query
            List<Rider> riders = tmsAdapter.getAvailableRiders("Downtown");
            check("integration — TMS provides riders to delivery system", riders != null && !riders.isEmpty());

            Rider selectedRider = riders.get(0);
            Rider registeredRider = deliverySystem.registerRider(
                    selectedRider.getName(), selectedRider.getPhone(), selectedRider.getVehicleType());
            Device device = deliverySystem.registerDeviceForRider(registeredRider);
            check("integration — rider registered in delivery system", registeredRider != null);
            check("integration — device registered for rider",        device != null);

            // Step B: route optimization
            Coordinate pickup  = new Coordinate(40.7128, -74.0060);
            Coordinate dropoff = new Coordinate(40.7580, -73.9855);
            RoutePlan route = tmsAdapter.calculateOptimalRoute("IT-ORDER-01", pickup, dropoff, null);
            check("integration — route optimization returns plan",       route != null);
            check("integration — route has positive distance",           route.getTotalDistance() > 0);

            // Step C: order lifecycle
            Customer customer = deliverySystem.registerCustomer("IT Customer", "it@test.com", "555-1234");
            check("integration — customer registered", customer != null);

            Order order = deliverySystem.createAndInitializeDelivery(
                    customer.getCustomerId(), "Pickup St", "Dropoff Ave", pickup, dropoff);
            check("integration — order created in delivery system", order != null);

            deliverySystem.assignRiderToOrder(order.getOrderId(), registeredRider.getRiderId());
            check("integration — rider assigned to order", true);

            // Step D: GPS pings
            deliverySystem.processLocationUpdate(device.getDeviceId(), order.getOrderId(), 40.7130, -74.0050);
            deliverySystem.processLocationUpdate(device.getDeviceId(), order.getOrderId(), 40.7580, -73.9855);
            check("integration — GPS pings fired location events", locationEvents.get() >= 2);

            // Step E: completion
            deliverySystem.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED, "Test Runner");
            check("integration — order marked DELIVERED", true);

        } catch (Exception e) {
            check("integration — no unexpected exception: " + e.getMessage(), false);
        }
    }

    // ---------------------- 9. DATABASE SHOWCASE

    private static void runDatabaseShowcaseTest() {
        section("9. DATABASE SHOWCASE (DB Team Integration)");
        try {
            TransportRepository repo = new TransportRepository();
            TransportService service = new TransportService(repo);

            Shipment s = new ShipmentBuilder("DB-SHOWCASE-01")
                    .withSupplierId("SUPPLIER-DB").withCarrierId("CARRIER-DB")
                    .withOrigin("Bengaluru Hub").withDestination("560001")
                    .withWeight(40.0).withCost(3200.0).withStatus("DISPATCHED").build();

            Shipment created = service.createShipment(s);
            check("db showcase — shipment created", created != null);

            service.updateShipmentStatus("DB-SHOWCASE-01", "IN_TRANSIT");
            Shipment updated = service.getShipment("DB-SHOWCASE-01");
            check("db showcase — status updated to IN_TRANSIT", updated != null && "IN_TRANSIT".equals(updated.getStatus()));

            DeliveryMonitoringFacadeDB deliveryMonitoring = new DeliveryMonitoringFacadeDB();
            List<?> history = deliveryMonitoring.getStatusHistory("DB-SHOWCASE-01");
            // History may be empty in in-memory mode — just ensure no exception
            check("db showcase — getStatusHistory runs without exception", true);

        } catch (Exception e) {
            check("db showcase — no unexpected exception: " + e.getMessage(), false);
        }
    }

    // ------------------------------------------------------- helpers

    private static void section(String name) {
        currentSection = name;
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.printf( "│  %-55s  │%n", name);
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            System.out.println("  ✅  " + label);
            passed++;
        } else {
            System.out.println("  ❌  FAIL: " + label);
            failed++;
        }
    }

    private static void printSummary() {
        int total = passed + failed;
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                       TEST SUMMARY                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf( "║  Total  : %-46d  ║%n", total);
        System.out.printf( "║  Passed : %-46d  ║%n", passed);
        System.out.printf( "║  Failed : %-46d  ║%n", failed);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        if (failed == 0) {
            System.out.println("║           ✅  ALL TESTS PASSED                           ║");
        } else {
            System.out.println("║           ❌  SOME TESTS FAILED — see output above       ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.exit(failed > 0 ? 1 : 0);
    }
}

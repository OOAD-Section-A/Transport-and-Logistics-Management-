package transport;

import controllers.TransportController;
import views.TransportView;

final class TransportFeatureShowcase {
    private TransportFeatureShowcase() { }

    static void run(TransportController controller, TransportView view) {
        view.displaySection("DEMONSTRATE NEW TMS FEATURES");

        entities.FreightAudit audit = controller.handleAuditFreight("AUD001", "SHIP001", 5500.0, 5000.0);
        view.displayMessage("Freight Audit: " + audit);

        entities.ConstraintPlanner planner = controller.handlePlanConstraints("PLAN001", "SHIP001", 5000.0, 10.0, 8, "9AM-5PM");
        view.displayMessage("Constraint Planner: " + planner);

        entities.Territory territory = controller.handleManageTerritory("TERR001", "Northeast Zone", "US Northeast", 5);
        view.displayMessage("Territory: " + territory);

        entities.OrderOrchestrator orchestrator = controller.handleOrchestrateOrder("ORD001", "SO001", true, "SUP001");
        view.displayMessage("Order Orchestrator: " + orchestrator);

        entities.SupplierPortal portal = controller.handleIntegrateSupplierPortal("PORT001", "SUP001", "Order details for shipment");
        view.displayMessage("Supplier Portal: " + portal);

        entities.TrackingSync sync = controller.handleSyncTracking("SYNC001", "ORD001", "TRK123456");
        view.displayMessage("Tracking Sync: " + sync);

        entities.ReverseLogistics logistics = controller.handleReverseLogistics("RET001", "ORD001", "SUP001", 100.0);
        view.displayMessage("Reverse Logistics: " + logistics);

        view.displayMessage("""
            ✓ NEW FEATURES DEMONSTRATED:
              • Freight Audit & Payment
              • Constraint-Aware Planning
              • Territory & Zone Management
              • Order Orchestration (Drop-Shipping)
              • Supplier Portal Integration
              • Automated Tracking Sync
              • Reverse Logistics for Drop-Shipping
            """);
    }
}

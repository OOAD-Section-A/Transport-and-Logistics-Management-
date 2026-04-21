package transport;

import controllers.TransportController;
import entities.Shipment;
import views.TransportView;

final class TransportDemoFlow {
    private TransportDemoFlow() { }

    static Shipment[] prepareDemoData(TransportView view) {
      return TransportDemoDataSetup.prepareDemoData(view);
    }

    static void runCoreFlow(TransportController controller, TransportView view, Shipment[] shipments) {
        view.displaySection("STEP 6: CREATE SHIPMENTS (Facade + Chain of Responsibility Validation)");
        for (Shipment shipment : shipments) {
            controller.handleCreateShipment(shipment);
        }

        view.displaySection("STEP 7: UPDATE SHIPMENT STATUS (Command Pattern)");
        controller.handleUpdateShipment("SHIP001", "In-Transit");
        controller.handleUpdateShipment("SHIP002", "Delivered");
        controller.handleUpdateShipment("SHIP003", "In-Transit");

        view.displaySection("STEP 8: LIST ALL SHIPMENTS (Iterator Pattern)");
        controller.handleListAllShipments();

        view.displaySection("STEP 9: INTEGRATE EXTERNAL SYSTEM (Adapter Pattern)");
        controller.handleFetchExternalData("SHIP001");

        view.displaySection("STEP 10: GET SHIPMENT DETAILS (Proxy Pattern Logging)");
        Shipment retrievedShipment = controller.handleGetShipment("SHIP001");
        if (retrievedShipment != null) {
            view.displayShipment(retrievedShipment);
        }


	view.displaySection("STEP 11: WMS INTEGRATION (Polling Shipment Events)");
        integration.wms.WMSIntegration wms = new integration.wms.WMSIntegration();
        wms.pollAndProcess();
	

        view.displaySection("FINAL SYSTEM STATUS");
        view.displaySystemInfo(controller.getSystemInfo());
        printPatternSummary(view);
    }

    private static void printPatternSummary(TransportView view) {
        view.displaySection("DESIGN PATTERNS SUMMARY");
        System.out.println("""
            CREATIONAL PATTERNS:
              • Factory Pattern - Created Suppliers and Carriers
              • Builder Pattern - Built complex Shipment objects
              • Prototype Pattern - Cloned shipment templates

            STRUCTURAL PATTERNS:
              • Adapter Pattern - Integrated external transport system
              • Facade Pattern - Simplified subsystem access
              • Proxy Pattern - Added logging to service calls
              • Flyweight Pattern - Reused Carrier objects to save memory

            BEHAVIORAL PATTERNS:
              • Command Pattern - Encapsulated create/update operations
              • Chain of Responsibility - Multi-step shipment validation
              • Iterator Pattern - Iterated through shipments safely

            ARCHITECTURE:
              • MVC - Controller -> Facade -> Service -> Repository
              • SOLID - SRP, OCP, LSP, ISP, DIP applied
              • GRASP - Information Expert, Creator, Controller applied
            """);
    }
}

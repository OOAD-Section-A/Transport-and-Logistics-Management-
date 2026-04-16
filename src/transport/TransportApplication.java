package transport;

import controllers.TransportController;
import views.TransportView;
import entities.Shipment;
import entities.Supplier;
import entities.Carrier;
import factories.TransportFactory;
import factories.ShipmentBuilder;
import factories.PrototypeRegistry;
import java.util.Date;

/**
 * TransportApplication: Main entry point
 * 
 * DEMONSTRATION OF:
 * ✓ MVC Architecture (Controller -> Facade -> Service -> Repository)
 * ✓ SOLID Principles (SRP, OCP, LSP, ISP, DIP)
 * ✓ GRASP Principles (Information Expert, Creator, Controller, etc.)
 * ✓ All 7 Design Patterns:
 *   - Factory Pattern (TransportFactory)
 *   - Builder Pattern (ShipmentBuilder)
 *   - Prototype Pattern (PrototypeRegistry)
 *   - Adapter Pattern (ExternalTransportAdapter)
 *   - Facade Pattern (TransportFacade)
 *   - Proxy Pattern (TransportServiceProxy)
 *   - Flyweight Pattern (CarrierFlyweightFactory)
 * ✓ Behavioral Patterns:
 *   - Command Pattern (CreateShipmentCommand, UpdateShipmentCommand)
 *   - Chain of Responsibility (ValidationHandler -> LoggingHandler)
 *   - Iterator Pattern (ShipmentIterator)
 */
public class TransportApplication {

    public static void main(String[] args) {
        TransportView view = new TransportView();
        TransportController controller = new TransportController();

        view.displayWelcome();

        // ===== STEP 1: CREATE DATA USING FACTORY PATTERN =====
        view.displaySection("STEP 1: CREATE SUPPLIERS (Factory Pattern)");
        Supplier supplier1 = TransportFactory.createSupplier(
            "SUP001", "TechSupply Corp", "New York", "contact@techsupply.com"
        );
        Supplier supplier2 = TransportFactory.createSupplier(
            "SUP002", "GlobalTrade Inc", "Los Angeles", "info@globaltrade.com"
        );
        view.displaySupplier(supplier1);
        view.displaySupplier(supplier2);

        // ===== STEP 2: CREATE CARRIERS USING FLYWEIGHT PATTERN =====
        view.displaySection("STEP 2: CREATE CARRIERS (Flyweight Pattern - Reuse)");
        flyweight.CarrierFlyweightFactory carrierFactory = flyweight.CarrierFlyweightFactory.getInstance();
        
        // First call creates new carriers
        Carrier carrier1 = carrierFactory.getCarrier(
            "CAR001", "FastFreight", "Road", 50.0
        );
        // Second call reuses from cache (same ID)
        Carrier carrier1_cached = carrierFactory.getCarrier(
            "CAR001", "FastFreight", "Road", 50.0
        );
        
        Carrier carrier2 = carrierFactory.getCarrier(
            "CAR002", "AirCargo Express", "Air", 100.0
        );
        
        view.displayCarrier(carrier1);
        view.displayCarrier(carrier2);
        System.out.println("\nCached carriers (memory optimized): " + carrierFactory.getCachedCarrierCount());

        // ===== STEP 3: BUILD SHIPMENTS USING BUILDER PATTERN =====
        view.displaySection("STEP 3: BUILD SHIPMENTS (Builder Pattern)");
        
        Shipment shipment1 = new ShipmentBuilder("SHIP001")
            .withSupplierId("SUP001")
            .withCarrierId("CAR001")
            .withOrigin("New York")
            .withDestination("Chicago")
            .withWeight(25.5)
            .withCost(5000)
            .withStatus("Pending")
            .build();
        view.displayMessage("Built shipment using Builder: " + shipment1.getShipmentId());

        Shipment shipment2 = new ShipmentBuilder("SHIP002")
            .withSupplierId("SUP002")
            .withCarrierId("CAR002")
            .withOrigin("Los Angeles")
            .withDestination("Miami")
            .withWeight(40.0)
            .withCost(8000)
            .withStatus("Pending")
            .build();
        view.displayMessage("Built shipment using Builder: " + shipment2.getShipmentId());

        // ===== STEP 4: REGISTER TEMPLATES FOR PROTOTYPE PATTERN =====
        view.displaySection("STEP 4: REGISTER SHIPMENT TEMPLATES (Prototype Pattern)");
        PrototypeRegistry.getInstance().registerPrototype("standard-road-template", shipment1);
        view.displayMessage("Registered shipment1 as template");

        // ===== STEP 5: CLONE SHIPMENT USING PROTOTYPE PATTERN =====
        view.displaySection("STEP 5: CLONE SHIPMENT FROM TEMPLATE (Prototype Pattern)");
        Shipment shipment3Cloned = PrototypeRegistry.getInstance().clonePrototype("standard-road-template");
        view.displayMessage("Cloned shipment3 from template (deep copy)");
        System.out.println("Original template: " + shipment1.getShipmentId());
        System.out.println("Cloned copy: " + shipment3Cloned.getShipmentId());

        // Rename the cloned shipment for uniqueness
        try {
            java.lang.reflect.Field field = Shipment.class.getDeclaredField("shipmentId");
            field.setAccessible(true);
            field.set(shipment3Cloned, "SHIP003");
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.displayMessage("Renamed cloned shipment to: SHIP003");

        // ===== STEP 6: CREATE SHIPMENTS VIA FACADE & CHAIN OF RESPONSIBILITY =====
        view.displaySection("STEP 6: CREATE SHIPMENTS (Facade + Chain of Responsibility Validation)");
        controller.handleCreateShipment(shipment1);
        controller.handleCreateShipment(shipment2);
        controller.handleCreateShipment(shipment3Cloned);

        // ===== STEP 7: UPDATE SHIPMENT STATUS USING COMMAND PATTERN =====
        view.displaySection("STEP 7: UPDATE SHIPMENT STATUS (Command Pattern)");
        controller.handleUpdateShipment("SHIP001", "In-Transit");
        controller.handleUpdateShipment("SHIP002", "Delivered");
        controller.handleUpdateShipment("SHIP003", "In-Transit");

        // ===== STEP 8: LIST ALL SHIPMENTS USING ITERATOR PATTERN =====
        view.displaySection("STEP 8: LIST ALL SHIPMENTS (Iterator Pattern)");
        controller.handleListAllShipments();

        // ===== STEP 9: FETCH EXTERNAL DATA USING ADAPTER PATTERN =====
        view.displaySection("STEP 9: INTEGRATE EXTERNAL SYSTEM (Adapter Pattern)");
        controller.handleFetchExternalData("SHIP001");

        // ===== STEP 10: GET SINGLE SHIPMENT (Proxy logs the operation) =====
        view.displaySection("STEP 10: GET SHIPMENT DETAILS (Proxy Pattern Logging)");
        Shipment retrievedShipment = controller.handleGetShipment("SHIP001");
        if (retrievedShipment != null) {
            view.displayShipment(retrievedShipment);
        }

        // ===== FINAL SYSTEM INFO =====
        view.displaySection("FINAL SYSTEM STATUS");
        view.displaySystemInfo(controller.getSystemInfo());

        // ===== DESIGN SUMMARY =====
        view.displaySection("DESIGN PATTERNS SUMMARY");
        System.out.println("""
            ✓ CREATIONAL PATTERNS:
              • Factory Pattern - Created Suppliers, Carriers factories
              • Builder Pattern - Built complex Shipment objects
              • Prototype Pattern - Cloned shipment templates
            
            ✓ STRUCTURAL PATTERNS:
              • Adapter Pattern - Integrated external transport system
              • Facade Pattern - Simplified subsystem interface for controller
              • Proxy Pattern - Added logging to service calls
              • Flyweight Pattern - Reused Carrier objects to save memory
            
            ✓ BEHAVIORAL PATTERNS:
              • Command Pattern - Encapsulated create/update operations
              • Chain of Responsibility - Multi-step shipment validation
              • Iterator Pattern - Iterated through shipments safely
            
            ✓ ARCHITECTURE:
              • MVC - Controller -> Facade -> Service -> Repository
              • SOLID - Every principle followed (SRP, OCP, LSP, ISP, DIP)
              • GRASP - Information Expert, Creator, Controller applied
            """);

        view.displaySuccess("Transport Management System Demo Completed Successfully!");
    }
}

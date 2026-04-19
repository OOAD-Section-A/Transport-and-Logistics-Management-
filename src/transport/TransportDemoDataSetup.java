package transport;

import com.scm.factory.SCMExceptionFactory;
import com.scm.handler.SCMExceptionHandler;
import entities.Carrier;
import entities.Shipment;
import entities.Supplier;
import factories.PrototypeRegistry;
import factories.ShipmentBuilder;
import factories.TransportFactory;
import flyweight.CarrierFlyweightFactory;
import views.TransportView;

final class TransportDemoDataSetup {
    private TransportDemoDataSetup() { }

    static Shipment[] prepareDemoData(TransportView view) {
        view.displaySection("STEP 1: CREATE SUPPLIERS (Factory Pattern)");
        Supplier supplier1 = TransportFactory.createSupplier("SUP001", "TechSupply Corp", "New York", "contact@techsupply.com");
        Supplier supplier2 = TransportFactory.createSupplier("SUP002", "GlobalTrade Inc", "Los Angeles", "info@globaltrade.com");
        view.displaySupplier(supplier1);
        view.displaySupplier(supplier2);

        view.displaySection("STEP 2: CREATE CARRIERS (Flyweight Pattern - Reuse)");
        CarrierFlyweightFactory carrierFactory = CarrierFlyweightFactory.getInstance();
        Carrier carrier1 = carrierFactory.getCarrier("CAR001", "FastFreight", "Road", 50.0);
        carrierFactory.getCarrier("CAR001", "FastFreight", "Road", 50.0);
        Carrier carrier2 = carrierFactory.getCarrier("CAR002", "AirCargo Express", "Air", 100.0);

        view.displayCarrier(carrier1);
        view.displayCarrier(carrier2);
        System.out.println("\nCached carriers (memory optimized): " + carrierFactory.getCachedCarrierCount());

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

        Shipment shipment2 = new ShipmentBuilder("SHIP002")
                .withSupplierId("SUP002")
                .withCarrierId("CAR002")
                .withOrigin("Los Angeles")
                .withDestination("Miami")
                .withWeight(40.0)
                .withCost(8000)
                .withStatus("Pending")
                .build();

        view.displayMessage("Built shipment using Builder: " + shipment1.getShipmentId());
        view.displayMessage("Built shipment using Builder: " + shipment2.getShipmentId());

        view.displaySection("STEP 4: REGISTER SHIPMENT TEMPLATES (Prototype Pattern)");
        PrototypeRegistry.getInstance().registerPrototype("standard-road-template", shipment1);
        view.displayMessage("Registered shipment1 as template");

        view.displaySection("STEP 5: CLONE SHIPMENT FROM TEMPLATE (Prototype Pattern)");
        Shipment shipment3 = PrototypeRegistry.getInstance().clonePrototype("standard-road-template");
        view.displayMessage("Cloned shipment3 from template (deep copy)");
        System.out.println("Original template: " + shipment1.getShipmentId());
        System.out.println("Cloned copy: " + shipment3.getShipmentId());

        if (!renameShipmentId(shipment3, "SHIP003")) {
            return null;
        }

        view.displayMessage("Renamed cloned shipment to: SHIP003");
        return new Shipment[]{shipment1, shipment2, shipment3};
    }

    private static boolean renameShipmentId(Shipment shipment, String newShipmentId) {
        try {
            java.lang.reflect.Field field = Shipment.class.getDeclaredField("shipmentId");
            field.setAccessible(true);
            field.set(shipment, newShipmentId);
            return true;
        } catch (Exception e) {
            SCMExceptionHandler.INSTANCE.handle(
                    SCMExceptionFactory.createUnregistered(
                            "Transport and Logistics Management",
                            "Failed to rename cloned shipment: " + e.getMessage()
                    )
            );
            return false;
        }
    }
}

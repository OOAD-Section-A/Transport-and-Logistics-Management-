package integration;

import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.observer.DeliveryEventType;
import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;
import facade.TransportFacade;
import java.util.List;

public class DeliveryMonitoringIntegrationTest {
    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("  TMS <-> TARGET SYSTEM INTEGRATION TEST (TEAM 2 + TEAM 16)");
        System.out.println("======================================================");

        try {
            // 1. Initialize both systems
            TransportFacade tmsFacade = new TransportFacade();
            DeliveryMonitoringFacadeDB deliverySystem = new DeliveryMonitoringFacadeDB();
            TransportLogisticsServiceImpl tmsAdapter = new TransportLogisticsServiceImpl(tmsFacade);

            // 2. Subscribe our TMS to their Events (Event Bridge setup)
            System.out.println("\n[SETUP] CenterDiv subscribing to Ramen Noodles Live Events...");
            deliverySystem.subscribeToEvents(DeliveryEventType.LOCATION_UPDATED, (eventType, data) -> {
                String riderId = (String) data.get("riderId");
                Double lat = (Double) data.get("latitude");
                Double lng = (Double) data.get("longitude");
                System.out.println("[EVENT BRIDGE] TMS received GPS ping from Ramen Noodles! Rider=" + riderId + " at (" + lat + ", " + lng + ")");
            });

            deliverySystem.subscribeToEvents(DeliveryEventType.ORDER_DELIVERED, (eventType, data) -> {
                String orderId = (String) data.get("orderId");
                System.out.println("[EVENT BRIDGE] TMS received Order Delivered event! Order=" + orderId);
            });

            // 3. Test Bidirectional Data Flow
            System.out.println("\n--- Step A. Ramen Noodles queries TMS for available Fleet...");
            List<Rider> availableRiders = tmsAdapter.getAvailableRiders("Downtown");
            System.out.println("Result: " + availableRiders.size() + " riders provided by TMS.");

            if (availableRiders.isEmpty()) return;
            Rider selectedRider = availableRiders.get(0);

            // Register the TMS rider with the delivery system so it knows about them
            Rider registeredRider = deliverySystem.registerRider(selectedRider.getName(), selectedRider.getPhone(), selectedRider.getVehicleType());
            Device riderDevice = deliverySystem.registerDeviceForRider(registeredRider);
            String selectedDevice = riderDevice.getDeviceId();
            System.out.println("Registered TMS rider in delivery system: " + registeredRider.getRiderId() + ", device: " + selectedDevice);

            System.out.println("\n--- Step B. Ramen Noodles requests route optimization from TMS...");
            Coordinate pickup = new Coordinate(40.7128, -74.0060);
            Coordinate dropoff = new Coordinate(40.7580, -73.9855);
            RoutePlan route = tmsAdapter.calculateOptimalRoute("ORDER-123", pickup, dropoff, null);
            System.out.println("Result: Route created with distance " + route.getTotalDistance() + "m");

            System.out.println("\n--- Step C. Simulating Delivery Orchestration...");
            
            // Register a test customer to satisfy their database
            Customer testCustomer = deliverySystem.registerCustomer("Test Customer", "test@test.com", "555-0999");

            // Create an order inside their system using the registered customer's actual ID
            Order fakeOrder = deliverySystem.createAndInitializeDelivery(testCustomer.getCustomerId(), "Pickup Address", "Dropoff Address", pickup, dropoff);
            System.out.println("Order created in Ramen Noodles System: " + fakeOrder.getOrderId());
            
            // Assign our TMS rider (now registered in delivery system) to their order
            deliverySystem.assignRiderToOrder(fakeOrder.getOrderId(), registeredRider.getRiderId());

            System.out.println("\n--- Step D. Simulate Live Tracking Data (GPS generation)...");
            System.out.println("Sending GPS Ping 1...");
            deliverySystem.processLocationUpdate(selectedDevice, fakeOrder.getOrderId(), 40.7130, -74.0050);
            
            System.out.println("Sending GPS Ping 2...");
            deliverySystem.processLocationUpdate(selectedDevice, fakeOrder.getOrderId(), 40.7580, -73.9855);

            System.out.println("\n--- Step E. Simulate Delivery Completion...");
            deliverySystem.updateOrderStatus(fakeOrder.getOrderId(), OrderStatus.DELIVERED, "Rider App");

            System.out.println("\n======================================================");
            System.out.println("  ✅ BIDIRECTIONAL INTEGRATION TEST PASSED SUCCESSFULLY ");
            System.out.println("======================================================");
            
        } catch (Exception e) {
            System.err.println("\n❌ INTEGRATION TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

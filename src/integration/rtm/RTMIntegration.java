package integration.rtm;

import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.model.*;

import java.util.List;

public class RTMIntegration {

    private final DeliveryMonitoringFacadeDB facade;

    // Facade must be injected from outside (single shared instance)
    public RTMIntegration(DeliveryMonitoringFacadeDB facade) {
        this.facade = facade;
        pollAndProcess();
    }

    public void pollAndProcess() {

        System.out.println("[RTM] Initializing full RTM graph...");

        // =========================
        // 1. Customers (CRITICAL FIX)
        // =========================
        Customer c1 = facade.registerCustomer("Cust-1", "c1@mail.com", "9999991111");
        Customer c2 = facade.registerCustomer("Cust-2", "c2@mail.com", "9999992222");
        Customer c3 = facade.registerCustomer("Cust-3", "c3@mail.com", "9999993333");

        if (c1 == null || c2 == null || c3 == null) {
            System.out.println("[RTM] ERROR: Customer creation failed. Aborting RTM simulation.");
            return;
        }

        // =========================
        // 2. Riders
        // =========================
        Rider r1 = facade.registerRider("RIDER-1", "9999999991", "Bike");
        Rider r2 = facade.registerRider("RIDER-2", "9999999992", "Bike");
        Rider r3 = facade.registerRider("RIDER-3", "9999999993", "Bike");

        // =========================
        // 3. Orders (must be created AFTER customers exist)
        // =========================
        Order o1 = facade.createAndInitializeDelivery(
                c1.getCustomerId(), "Pickup-1", "Drop-1",
                new Coordinate(12.9716, 77.5946),
                new Coordinate(12.9816, 77.6046)
        );

        Order o2 = facade.createAndInitializeDelivery(
                c2.getCustomerId(), "Pickup-2", "Drop-2",
                new Coordinate(12.9726, 77.5956),
                new Coordinate(12.9826, 77.6056)
        );

        Order o3 = facade.createAndInitializeDelivery(
                c3.getCustomerId(), "Pickup-3", "Drop-3",
                new Coordinate(12.9736, 77.5966),
                new Coordinate(12.9836, 77.6066)
        );

        if (o1 == null || o2 == null || o3 == null) {
            System.out.println("[RTM] ERROR: Order creation failed. Aborting RTM simulation.");
            return;
        }

        // =========================
        // 4. Assign Riders
        // =========================
        facade.assignRiderToOrder(o1.getOrderId(), r1.getRiderId());
        facade.assignRiderToOrder(o2.getOrderId(), r2.getRiderId());
        facade.assignRiderToOrder(o3.getOrderId(), r3.getRiderId());

        // =========================
        // 5. Devices
        // =========================
        Device d1 = facade.registerDeviceForRider(r1);
        Device d2 = facade.registerDeviceForRider(r2);
        Device d3 = facade.registerDeviceForRider(r3);

        List<Device> devices = List.of(d1, d2, d3);
        String[] orders = {
                o1.getOrderId(),
                o2.getOrderId(),
                o3.getOrderId()
        };

        // =========================
        // 6. Live Tracking Simulation
        // =========================
        System.out.println("[RTM] Simulating live tracking...");

        for (int i = 0; i < devices.size(); i++) {

            Device device = devices.get(i);
            String orderId = orders[i];

            double lat = 12.9716 + i * 0.001;
            double lon = 77.5946 + i * 0.001;

            System.out.println("[RTM] LOCATION_UPDATED: " + device.getDeviceId());

            facade.processLocationUpdate(
                    device.getDeviceId(),
                    orderId,
                    lat,
                    lon
            );

            try {
                Thread.sleep(300);
            } catch (Exception ignored) {}
        }

        System.out.println("[RTM] Complete.");
    }
}
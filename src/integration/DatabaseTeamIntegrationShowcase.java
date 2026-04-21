package integration;

import entities.Shipment;
import factories.ShipmentBuilder;
import repositories.TransportRepository;
import services.TransportService;

import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.model.DeliveryStatusLog;
import java.util.List;

public class DatabaseTeamIntegrationShowcase {

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("  TRANSPORT & RAMEN NOODLES DATABASE INTEGRATION");
        System.out.println("=====================================================");
        
        System.out.println("\n[1] INITIALIZING TRANSPORT SERVICES AND CREATING SHIPMENT...");
        TransportRepository repo = new TransportRepository();
        TransportService transportService = new TransportService(repo);

        String deliveryId = "TEST-DB-007";
        System.out.println("Creating Shipment with ID: " + deliveryId);
        
        Shipment shipmentInfo = new ShipmentBuilder(deliveryId)
            .withSupplierId("SUPPLIER-X")
            .withCarrierId("CARRIER-1")
            .withOrigin("Bangalore Hub")
            .withDestination("Mysore Warehouse")
            .withWeight(50.0)
            .withCost(5000.0)
            .withStatus("DISPATCHED")
            .build();
            
        // This will write to DB internally via DatabasePersistenceAdapter
        transportService.createShipment(shipmentInfo);
        System.out.println("Shipment successfully created in Transport Module.");

        System.out.println("\n[2] UPDATING SHIPMENT STATUS VIA TRANSPORT MODULE...");
        transportService.updateShipmentStatus(deliveryId, "IN_TRANSIT");

        System.out.println("\n[3] TEAM RAMEN NOODLES (Delivery Monitoring) FETCHING DATA...");
        // Initialize the DeliveryMonitoringFacadeDB to demonstrate reading
        DeliveryMonitoringFacadeDB deliveryMonitoring = new DeliveryMonitoringFacadeDB();

        List<DeliveryStatusLog> history = deliveryMonitoring.getStatusHistory(deliveryId);

        if (history != null && !history.isEmpty()) {
            System.out.println("\nData successfully fetched by Ramen Noodles module from shared database!");
            System.out.println("--- STATUS HISTORY LOGS ---");
            for (int i = 0; i < history.size(); i++) {
                DeliveryStatusLog log = history.get(i);
                System.out.println(String.format("  [%d] Status: %s | Source: %s | Time: %s", 
                    (i + 1), log.getStatus(), log.getTriggerSource(), log.getChangedAt()));
            }
        } else {
            System.out.println("\nNo history found in database. This is expected if running in FALLBACK (no MySQL) mode.");
            System.out.println("When MySQL is running and configured, the logs will show here bidirectionally.");
        }
        
        System.out.println("=====================================================");
        System.out.println("  DEMONSTRATION COMPLETE");
        System.out.println("=====================================================");
    }
}

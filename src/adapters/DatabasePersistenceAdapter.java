package adapters;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.LogisticsModels.LogisticsShipment;
import com.jackfruit.scm.database.model.LogisticsModels.LogisticsRoute;
import com.jackfruit.scm.database.model.LogisticsModels.ShipmentAlert;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingEvent;
import entities.Shipment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DB Team Integration - Persists Transport state to shared MySQL database.
 * Includes a graceful fallback if MySQL is offline (e.g. during a single laptop demo presentation).
 */
public class DatabasePersistenceAdapter {
    private SupplyChainDatabaseFacade facade;

    public DatabasePersistenceAdapter() {
        try {
            this.facade = new SupplyChainDatabaseFacade();
            System.out.println("[DB-ADAPTER] Connected to Shared Database (Module v1.0.0)");
        } catch (Throwable t) {
            System.out.println("[DB-FALLBACK] Database unavailable. Running in robust Fallback Mode.");
            this.facade = null;
        }
    }

    public void persistShipment(Shipment shipment) {
        if (facade == null) {
            System.out.println("[DB-FALLBACK] (Simulated Write) Persisted LogisticsShipment: " + shipment.getShipmentId());
            return;
        }
        try {
            // Write to team-specific logistics schema
            LogisticsShipment ls = new LogisticsShipment(
                    shipment.getShipmentId(),
                    "ORD-" + shipment.getShipmentId(),
                    shipment.getOrigin(),
                    shipment.getDestination(),
                    BigDecimal.valueOf(shipment.getWeight()),
                    false, // dropShip
                    "STANDARD",
                    shipment.getStatus(),
                    shipment.getSupplierId(),
                    100, // mock inventory level
                    "R-" + shipment.getShipmentId(),
                    shipment.getCarrierId(),
                    "TRK-" + shipment.getShipmentId(),
                    false, false, false,
                    BigDecimal.valueOf(shipment.getCost()),
                    LocalDateTime.now()
            );
            facade.logistics().createShipment(ls);
            
            // Also write to top-level shared schema
            com.jackfruit.scm.database.model.Shipment sharedRecord = new com.jackfruit.scm.database.model.Shipment();
            sharedRecord.setDeliveryId(shipment.getShipmentId());
            sharedRecord.setOrderId("ORD-" + shipment.getShipmentId());
            sharedRecord.setCustomerId("CUST-001");
            sharedRecord.setDeliveryAddress(shipment.getDestination());
            sharedRecord.setDeliveryStatus(shipment.getStatus());
            sharedRecord.setDeliveryType("Transport/Road");
            sharedRecord.setDeliveryCost(BigDecimal.valueOf(shipment.getCost()));
            sharedRecord.setAssignedAgent(shipment.getCarrierId());
            sharedRecord.setCreatedAt(LocalDateTime.now());
            facade.createShipment(sharedRecord);

            System.out.println("[DB-ADAPTER] Successfully persisted shipment to database: " + shipment.getShipmentId());
        } catch (Throwable t) {
            System.err.println("[DB-ADAPTER] Failed to persist shipment: " + t.getMessage());
        }
    }

    public void updateShipmentStatus(String shipmentId, String status) {
        if (facade == null) return;
        try {
            com.jackfruit.scm.database.model.Shipment update = new com.jackfruit.scm.database.model.Shipment();
            update.setDeliveryId(shipmentId);
            update.setDeliveryStatus(status);
            facade.updateShipment(update);
            System.out.println("[DB-ADAPTER] Updated shipment status in DB: " + shipmentId + " -> " + status);
        } catch (Throwable t) {
            System.err.println("[DB-ADAPTER] Failed to update shipment: " + t.getMessage());
        }
    }
    
    public void persistRoute(String routeId, String shipmentId) {
        if (facade == null) {
            System.out.println("[DB-FALLBACK] (Simulated Write) Persisted LogisticsRoute: " + routeId);
            return;
        }
        try {
            LogisticsRoute lr = new LogisticsRoute(
                routeId,
                shipmentId,
                "Coordinates N/A",
                LocalDateTime.now().plusHours(24),
                "PLANNING",
                "ACTIVE",
                false
            );
            facade.logistics().createRoute(lr);
        } catch (Throwable t) {
            // swallow to prevent demo crash
        }
    }

    public void persistShipmentAlert(String shipmentId, String message, String severity) {
        if (facade == null) {
            System.out.println("[DB-FALLBACK] (Simulated Write) Alert raised for " + shipmentId + " [" + severity + "]");
            return;
        }
        try {
            String alertId = "ALRT-" + System.currentTimeMillis();
            ShipmentAlert sa = new ShipmentAlert(alertId, shipmentId, message, severity, LocalDateTime.now());
            facade.logistics().createShipmentAlert(sa);
            System.out.println("[DB-ADAPTER] Persisted exception alert to database.");
        } catch(Throwable t) {}
    }

    public void persistTrackingEvent(String deliveryId, String riderId, double lat, double lng, String stage) {
        if (facade == null) return;
        try {
            DeliveryTrackingEvent dte = new DeliveryTrackingEvent(
                "EVT-" + System.currentTimeMillis(),
                deliveryId,
                riderId,
                "VEH-UNKNOWN",
                stage,
                lat + "," + lng,
                LocalDateTime.now(),
                "Info",
                false
            );
            facade.deliveryMonitoring().createTrackingEvent(dte);
        } catch (Throwable t) {}
    }
}

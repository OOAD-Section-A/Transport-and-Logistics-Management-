package integration.wms;

import wms.integration.TMSIntegrationService;
import wms.integration.ShipmentReadyEvent;

import java.util.List;

public class WMSIntegration {

    private final TMSIntegrationService tms;

    public WMSIntegration() {
        this.tms = TMSIntegrationService.getInstance();
        seedTestEvents();
    }

    private void seedTestEvents() {

        ShipmentReadyEvent ready = new ShipmentReadyEvent(
            "SHIP001",
            List.of("ORD001", "ORD002"),
            25.5,
            3,
            "SCM|SHIP001|ORD001|2026",
            List.of(
                new ShipmentReadyEvent.ShipmentItem("PROD001", 2, "Laptop"),
                new ShipmentReadyEvent.ShipmentItem("PROD002", 1, "Monitor")
            )
        );

        tms.publishShipmentReady(ready);

        ShipmentReadyEvent rejected = new ShipmentReadyEvent(
            "SHIP002",
            List.of("ORD003"),
            "QC failed: damaged goods detected"
        );

        tms.publishShipmentRejection(rejected);

        System.out.println("[WMS] Test events seeded.");
    }

    public void pollAndProcess() {

        System.out.println("[WMS] Polling for shipment events...");

        List<ShipmentReadyEvent> ready = tms.getReadyShipments();

        System.out.println("[WMS] Ready shipments found: " + ready.size());

        for (ShipmentReadyEvent event : ready) {
            System.out.println("[WMS] READY_FOR_PICKUP: " + event.getShipmentId());
            tms.acknowledgeShipment(event.getShipmentId());
            System.out.println("[WMS] Acknowledged: " + event.getShipmentId());
        }

        List<ShipmentReadyEvent> rejected = tms.getRejectedShipments();

        System.out.println("[WMS] Rejected shipments found: " + rejected.size());

        for (ShipmentReadyEvent event : rejected) {
            System.out.println("[WMS] CANNOT_SHIP: " + event.getShipmentId());
            System.out.println("      Reason: " + event.getRejectionReason());
            tms.acknowledgeShipment(event.getShipmentId());
            System.out.println("[WMS] Acknowledged: " + event.getShipmentId());
        }

        System.out.println("[WMS] Polling complete.");
    }
}
package transport;

import controllers.TransportController;
import entities.Shipment;
import views.TransportView;
import com.ramennoodles.delivery.facade.DeliveryMonitoringFacade;
import com.ramennoodles.delivery.enums.DeliveryEventType;
import integration.CenterDivLogisticsService;

public static void main(String[] args) {
    // ...existing code...
    DeliveryMonitoringFacade deliverySystem = new DeliveryMonitoringFacade();
    ITransportLogisticsService centerDivService = new CenterDivLogisticsService(transportService);
    deliverySystem.setTransportLogisticsService(centerDivService);

    // Subscribe to GPS events
    deliverySystem.subscribeToEvents(DeliveryEventType.LOCATION_UPDATED,
        (eventType, data) -> {
            String riderId = (String) data.get("riderId");
            Double latitude = (Double) data.get("latitude");
            Double longitude = (Double) data.get("longitude");
            // Update your fleet tracking
            transportService.updateRiderLocation(riderId, latitude, longitude);
        });

public class TransportApplication {
    public static void main(String[] args) {
        TransportView view = new TransportView();
        TransportController controller = new TransportController();

        view.displayWelcome();
        Shipment[] shipments = TransportDemoFlow.prepareDemoData(view);
        if (shipments == null) {
            return;
        }

        TransportDemoFlow.runCoreFlow(controller, view, shipments);
        TransportFeatureShowcase.run(controller, view);
        view.displaySuccess("Transport Management System Demo Completed Successfully!");
    }
}

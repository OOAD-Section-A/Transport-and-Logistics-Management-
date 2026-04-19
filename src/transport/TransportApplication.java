package transport;

import controllers.TransportController;
import entities.Shipment;
import views.TransportView;

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

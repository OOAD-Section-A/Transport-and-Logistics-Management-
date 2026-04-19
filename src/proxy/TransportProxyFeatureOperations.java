package proxy;

import java.util.function.Function;
import java.util.function.Supplier;

import interfaces.ITransportService;

final class TransportProxyFeatureOperations {
    private final ITransportService realService;

    TransportProxyFeatureOperations(ITransportService realService) {
        this.realService = realService;
    }

    entities.FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        return logCall("[PROXY LOG] Auditing freight for shipment: " + shipmentId,
                () -> realService.auditFreight(auditId, shipmentId, invoicedAmount, contractAmount),
                result -> "[PROXY LOG] Audit completed: " + result.getDiscrepancyFlag());
    }

    entities.ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit,
                                               double heightLimit, int shiftHours, String window) {
        return logCall("[PROXY LOG] Planning constraints for shipment: " + shipmentId,
                () -> realService.planConstraints(planId, shipmentId, weightLimit, heightLimit, shiftHours, window),
                "[PROXY LOG] Constraints planned");
    }

    entities.Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        return logCall("[PROXY LOG] Managing territory: " + territoryId,
                () -> realService.manageTerritory(territoryId, zoneName, area, drivers),
                "[PROXY LOG] Territory managed");
    }

    entities.OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        return logCall("[PROXY LOG] Orchestrating order: " + orderId,
                () -> realService.orchestrateOrder(orderId, salesOrderId, isThirdParty, supplierId),
                "[PROXY LOG] Order orchestrated");
    }

    entities.SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        return logCall("[PROXY LOG] Integrating supplier portal: " + portalId,
                () -> realService.integrateSupplierPortal(portalId, supplierId, orderDetails),
                "[PROXY LOG] Portal integrated");
    }

    entities.TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        return logCall("[PROXY LOG] Syncing tracking for order: " + orderId,
                () -> realService.syncTracking(syncId, orderId, trackingNumber),
                "[PROXY LOG] Tracking synced");
    }

    entities.ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        return logCall("[PROXY LOG] Handling reverse logistics for return: " + returnId,
                () -> realService.handleReverseLogistics(returnId, orderId, supplierId, refund),
                "[PROXY LOG] Reverse logistics handled");
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private <T> T logCall(String startMessage, Supplier<T> action, String successMessage) {
        log(startMessage);
        T result = action.get();
        log(successMessage);
        return result;
    }

    private <T> T logCall(String startMessage, Supplier<T> action, Function<T, String> successMessage) {
        log(startMessage);
        T result = action.get();
        log(successMessage.apply(result));
        return result;
    }
}

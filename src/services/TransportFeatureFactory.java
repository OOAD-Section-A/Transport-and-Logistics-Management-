package services;

import entities.ConstraintPlanner;
import entities.FreightAudit;
import entities.OrderOrchestrator;
import entities.ReverseLogistics;
import entities.SupplierPortal;
import entities.Territory;
import entities.TrackingSync;

final class TransportFeatureFactory {
    FreightAudit auditFreight(String auditId, String shipmentId, double invoicedAmount, double contractAmount) {
        String flag = invoicedAmount > contractAmount ? "OVERCHARGE"
                : invoicedAmount < contractAmount ? "UNDERCHARGE" : "NONE";
        return new FreightAudit(auditId, shipmentId, invoicedAmount, contractAmount, flag);
    }

    ConstraintPlanner planConstraints(String planId, String shipmentId, double weightLimit,
                                      double heightLimit, int shiftHours, String window) {
        return new ConstraintPlanner(planId, shipmentId, weightLimit, heightLimit, shiftHours, window);
    }

    Territory manageTerritory(String territoryId, String zoneName, String area, int drivers) {
        return new Territory(territoryId, zoneName, area, drivers);
    }

    OrderOrchestrator orchestrateOrder(String orderId, String salesOrderId, boolean isThirdParty, String supplierId) {
        String poNumber = isThirdParty ? "PO-" + orderId : null;
        return new OrderOrchestrator(orderId, salesOrderId, isThirdParty, supplierId, poNumber);
    }

    SupplierPortal integrateSupplierPortal(String portalId, String supplierId, String orderDetails) {
        return new SupplierPortal(portalId, supplierId, orderDetails, "ASN-" + portalId, "PackingSlip-" + portalId);
    }

    TrackingSync syncTracking(String syncId, String orderId, String trackingNumber) {
        return new TrackingSync(syncId, orderId, trackingNumber, "Updated to customer");
    }

    ReverseLogistics handleReverseLogistics(String returnId, String orderId, String supplierId, double refund) {
        return new ReverseLogistics(returnId, orderId, supplierId, refund, "PENDING");
    }
}

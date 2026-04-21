package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.enums.OrderStatus;
import com.ramennoodles.delivery.model.*;

import java.util.*;

/**
 * Service for providing a live fleet dashboard overview.
 * Core component: Live Fleet Dashboard
 * 
 * Provides a real-time visual map and delivery overview
 * using data from all other services.
 */
public class FleetDashboardService {

    private final GPSTrackingService gpsService;
    private final StatusUpdateService statusService;
    private final ETAService etaService;

    // In-memory storage for orders and riders
    private final Map<String, Order> activeOrders;
    private final Map<String, Rider> activeRiders;

    public FleetDashboardService(GPSTrackingService gpsService,
                                  StatusUpdateService statusService,
                                  ETAService etaService) {
        this.gpsService = gpsService;
        this.statusService = statusService;
        this.etaService = etaService;
        this.activeOrders = new HashMap<>();
        this.activeRiders = new HashMap<>();
    }

    /**
     * Registers an order for dashboard tracking.
     */
    public void trackOrder(Order order) {
        activeOrders.put(order.getOrderId(), order);
    }

    /**
     * Registers a rider for dashboard tracking.
     */
    public void trackRider(Rider rider) {
        activeRiders.put(rider.getRiderId(), rider);
    }

    /**
     * Gets a complete fleet overview.
     */
    public FleetOverview getFleetOverview() {
        FleetOverview overview = new FleetOverview();

        // Count orders by status
        for (Order order : activeOrders.values()) {
            OrderStatus status = statusService.getCurrentStatus(order.getOrderId());
            if (status != null) {
                overview.ordersByStatus.merge(status, 1, Integer::sum);
            }
        }

        // Get live rider positions
        for (String riderId : gpsService.getTrackedRiders()) {
            GPSPing latestPing = gpsService.getLatestPing(riderId);
            if (latestPing != null) {
                overview.riderPositions.put(riderId, latestPing);
            }
        }

        overview.totalActiveOrders = activeOrders.size();
        overview.totalActiveRiders = activeRiders.size();
        overview.totalTrackedRiders = gpsService.getTrackedRiders().size();

        return overview;
    }

    /**
     * Gets details for a specific delivery (for dashboard drill-down).
     */
    public DeliveryDetail getDeliveryDetail(String orderId) {
        Order order = activeOrders.get(orderId);
        if (order == null) return null;

        DeliveryDetail detail = new DeliveryDetail();
        detail.order = order;
        detail.currentStatus = statusService.getCurrentStatus(orderId);
        detail.statusHistory = statusService.getStatusHistory(orderId);
        detail.latestETA = etaService.getLatestETA(orderId);

        if (order.getRiderId() != null) {
            detail.riderPosition = gpsService.getLatestPing(order.getRiderId());
            detail.rider = activeRiders.get(order.getRiderId());
        }

        return detail;
    }

    /**
     * Generates a text-based dashboard summary.
     */
    public String generateDashboardSummary() {
        FleetOverview overview = getFleetOverview();
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════╗\n");
        sb.append("║          🚚 LIVE FLEET DASHBOARD                    ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Active Orders:  %-5d    Active Riders: %-5d     ║\n",
                overview.totalActiveOrders, overview.totalActiveRiders));
        sb.append(String.format("║  Tracked Riders: %-5d                              ║\n",
                overview.totalTrackedRiders));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║  Orders by Status:                                  ║\n");
        for (Map.Entry<OrderStatus, Integer> entry : overview.ordersByStatus.entrySet()) {
            sb.append(String.format("║    %-15s : %-5d                          ║\n",
                    entry.getKey(), entry.getValue()));
        }
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║  Live Rider Positions:                              ║\n");
        for (Map.Entry<String, GPSPing> entry : overview.riderPositions.entrySet()) {
            GPSPing ping = entry.getValue();
            sb.append(String.format("║    %s : (%.4f, %.4f)              ║\n",
                    entry.getKey(), ping.getLatitude(), ping.getLongitude()));
        }
        sb.append("╚══════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    // --- Inner classes for dashboard data ---

    public static class FleetOverview {
        public int totalActiveOrders;
        public int totalActiveRiders;
        public int totalTrackedRiders;
        public Map<OrderStatus, Integer> ordersByStatus = new HashMap<>();
        public Map<String, GPSPing> riderPositions = new HashMap<>();
    }

    public static class DeliveryDetail {
        public Order order;
        public Rider rider;
        public OrderStatus currentStatus;
        public List<DeliveryStatusLog> statusHistory;
        public ETARecord latestETA;
        public GPSPing riderPosition;
    }
}

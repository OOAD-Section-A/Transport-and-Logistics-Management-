package com.ramennoodles.delivery;

import com.ramennoodles.delivery.enums.*;
import com.ramennoodles.delivery.facade.DeliveryMonitoringFacade;
import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * ═══════════════════════════════════════════════════════════════
 * REAL-TIME DELIVERY MONITORING SYSTEM
 * Team: Ramen Noodles (Team #9)
 * Subject: OOAD Lab — Supply Chain Management
 * ═══════════════════════════════════════════════════════════════
 * 
 * Members:
 *   - Aaron Thomas Mathew   (PES1UG23AM005)
 *   - Preetham V J          (PES1UG23AM913)
 *   - G. Pranav Ganesh      (PES1UG24AM804)
 *   - Aman Kumar Mishra     (PES1UG23AM040)
 * 
 * Subsystem: Real-Time Delivery Monitoring (#7)
 * 
 * Integration Partners:
 *   - VERTEX (Team #17)     → Order Fulfillment
 *   - DEI Hires (Team #6)   → Delivery Orders
 *   - CenterDiv (Team #2)   → Transport & Logistics Management
 * 
 * Design Patterns Used:
 *   1. Observer Pattern   — Real-time event notifications
 *   2. Strategy Pattern   — Notification channels (SMS/Email)
 *   3. Facade Pattern     — Simplified interface for subsystems
 * 
 * This Main class demonstrates a full delivery lifecycle simulation.
 * ═══════════════════════════════════════════════════════════════
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   🍜 RAMEN NOODLES — Real-Time Delivery Monitoring System   ║");
        System.out.println("║   OOAD Lab Project — Supply Chain Management                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Initialize the facade (single entry point)
        DeliveryMonitoringFacade system = new DeliveryMonitoringFacade();

        // ═══════════════════════════════════════════════════════
        // DEMO: Simulating external subsystem listeners
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📡 STEP 0: Setting up Observer listeners for partner subsystems");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // VERTEX (Order Fulfillment) subscribes to delivery completion events
        DeliveryEventListener vertexListener = (eventType, data) -> {
            System.out.println("   [VERTEX] 📦 Received event: " + eventType
                    + " | Data: " + data);
        };
        system.subscribeToEvents(DeliveryEventType.ORDER_DELIVERED, vertexListener);
        system.subscribeToEvents(DeliveryEventType.ORDER_FAILED, vertexListener);
        system.subscribeToEvents(DeliveryEventType.POD_SUBMITTED, vertexListener);
        System.out.println("   ✅ VERTEX subscribed to: ORDER_DELIVERED, ORDER_FAILED, POD_SUBMITTED");

        // DEI Hires (Delivery Orders) subscribes to status changes
        DeliveryEventListener deiHiresListener = (eventType, data) -> {
            System.out.println("   [DEI Hires] 📋 Received event: " + eventType
                    + " | Order: " + data.get("orderId"));
        };
        system.subscribeToEvents(DeliveryEventType.STATUS_CHANGED, deiHiresListener);
        system.subscribeToEvents(DeliveryEventType.ORDER_CREATED, deiHiresListener);
        System.out.println("   ✅ DEI Hires subscribed to: STATUS_CHANGED, ORDER_CREATED");

        // CenterDiv (Transport & Logistics) subscribes to location updates
        DeliveryEventListener centerDivListener = (eventType, data) -> {
            if (eventType == DeliveryEventType.LOCATION_UPDATED) {
                System.out.println("   [CenterDiv] 🗺️  GPS Update: rider=" + data.get("riderId")
                        + " pos=(" + data.get("latitude") + ", " + data.get("longitude") + ")");
            } else {
                System.out.println("   [CenterDiv] 🚚 Received event: " + eventType);
            }
        };
        system.subscribeToEvents(DeliveryEventType.LOCATION_UPDATED, centerDivListener);
        system.subscribeToEvents(DeliveryEventType.RIDER_ASSIGNED, centerDivListener);
        system.subscribeToEvents(DeliveryEventType.GEOFENCE_ENTRY, centerDivListener);
        system.subscribeToEvents(DeliveryEventType.GEOFENCE_EXIT, centerDivListener);
        System.out.println("   ✅ CenterDiv subscribed to: LOCATION_UPDATED, RIDER_ASSIGNED, GEOFENCE_ENTRY/EXIT");
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 1: Register Customer & Rider
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 STEP 1: Registering Customer & Rider");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Customer customer = system.registerCustomer(
                "Pranav Ganesh", "pranav@pes.edu", "+91-9876543210"
        );

        Rider rider = system.registerRider(
                "Rajesh Kumar", "+91-9988776655", "Motorcycle"
        );

        // Register GPS device for the rider
        Device device = system.registerDeviceForRider(rider);
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 2: Create Delivery Order
        // (Simulating call from DEI Hires / VERTEX)
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 STEP 2: Creating Delivery Order (from DEI Hires)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Bangalore coordinates for realistic demo
        Coordinate pickupCoord = new Coordinate(12.9352, 77.6245);   // Koramangala
        Coordinate dropoffCoord = new Coordinate(12.9716, 77.5946);  // MG Road

        Order order = system.createAndInitializeDelivery(
                customer.getCustomerId(),
                "123 Koramangala 5th Block, Bangalore",
                "456 MG Road, Brigade Road Junction, Bangalore",
                pickupCoord,
                dropoffCoord
        );
        System.out.println("   📎 Tracking URL: " + system.getTrackingURL(order.getOrderId()));
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 3: Assign Rider
        // (Simulating CenterDiv providing an available rider)
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏍️  STEP 3: Assigning Rider (from CenterDiv fleet)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        system.assignRiderToOrder(order.getOrderId(), rider.getRiderId());
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 4: Simulate GPS Tracking (Rider moving)
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📍 STEP 4: Simulating Live GPS Tracking");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Rider picks up the order
        system.updateOrderStatus(order.getOrderId(), OrderStatus.PICKED_UP, rider.getRiderId());
        System.out.println("   📦 Order picked up by rider");
        System.out.println();

        // Rider starts moving — transition to IN_TRANSIT
        system.updateOrderStatus(order.getOrderId(), OrderStatus.IN_TRANSIT, rider.getRiderId());

        // Simulate rider moving from pickup to dropoff
        double[][] riderPath = {
                {12.9352, 77.6245},  // Start: Koramangala (pickup zone - should trigger entry)
                {12.9400, 77.6200},  // Moving north
                {12.9450, 77.6150},  // En route
                {12.9500, 77.6100},  // Halfway
                {12.9550, 77.6050},  // Getting closer
                {12.9600, 77.6000},  // Almost there
                {12.9650, 77.5970},  // Approaching
                {12.9716, 77.5946},  // Destination: MG Road (dropoff zone - should trigger entry)
        };

        for (int i = 0; i < riderPath.length; i++) {
            System.out.println("\n   📡 GPS Ping #" + (i + 1) + ": (" + riderPath[i][0] + ", " + riderPath[i][1] + ")");
            GPSPing ping = system.processLocationUpdate(
                    device.getDeviceId(), order.getOrderId(),
                    riderPath[i][0], riderPath[i][1]
            );

            // Show ETA after each ping
            ETARecord eta = system.getLatestETA(order.getOrderId());
            if (eta != null) {
                System.out.println("   ⏱️  ETA: " + eta.getEstimatedArrival()
                        + " (remaining: " + eta.getRemainingTimeMinutes() + " min)");
            }
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 5: Submit Proof of Delivery
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✍️  STEP 5: Submitting Proof of Delivery");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        PODRecord pod = system.completeDelivery(
                order.getOrderId(),
                "customer_signature.png",
                "delivery_photo.jpg",
                "Delivered to customer at front door. Customer verified."
        );
        System.out.println("   📄 POD: " + pod);
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 6: Display Dashboard & Audit Trail
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 STEP 6: Fleet Dashboard & Audit Trail");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Print dashboard
        system.printDashboard();

        // Print status history (audit trail)
        System.out.println("📜 Status History (Audit Trail):");
        List<DeliveryStatusLog> history = system.getStatusHistory(order.getOrderId());
        for (int i = 0; i < history.size(); i++) {
            DeliveryStatusLog log = history.get(i);
            System.out.println("   " + (i + 1) + ". " + log.getStatus()
                    + " [" + log.getTriggerSource() + "] at " + log.getChangedAt());
        }
        System.out.println();

        // Print notifications sent
        System.out.println("📬 Notifications Sent:");
        List<NotificationLog> notifications = system.getNotificationService()
                .getNotificationsByOrder(order.getOrderId());
        for (NotificationLog notif : notifications) {
            System.out.println("   📨 [" + notif.getChannel() + "] " + notif.getMessage());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 7: Display Event History (Observer Pattern)
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📡 STEP 7: Event History (Observer Pattern Activity)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<DeliveryEventManager.EventRecord> events = system.getEventManager().getEventHistory();
        System.out.println("   Total events published: " + events.size());
        System.out.println("   Event types breakdown:");
        Map<DeliveryEventType, Integer> eventCounts = new HashMap<>();
        for (DeliveryEventManager.EventRecord record : events) {
            eventCounts.merge(record.getEventType(), 1, Integer::sum);
        }
        for (Map.Entry<DeliveryEventType, Integer> entry : eventCounts.entrySet()) {
            System.out.println("     • " + entry.getKey() + ": " + entry.getValue() + " events");
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // SUMMARY
        // ═══════════════════════════════════════════════════════

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ✅ SIMULATION COMPLETE                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  Design Patterns Demonstrated:                               ║");
        System.out.println("║    🔔 Observer  — Event-driven updates to 3 subsystems       ║");
        System.out.println("║    📧 Strategy  — SMS/Email notification channels            ║");
        System.out.println("║    🏢 Facade    — Single entry point for all operations      ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Integration Partners:                                       ║");
        System.out.println("║    📦 VERTEX     — Order Fulfillment (Team #17)              ║");
        System.out.println("║    📋 DEI Hires  — Delivery Orders (Team #6)                 ║");
        System.out.println("║    🚚 CenterDiv  — Transport & Logistics (Team #2)           ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Team: Ramen Noodles 🍜                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}

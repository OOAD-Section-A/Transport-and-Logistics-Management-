package com.ramennoodles.delivery;

import com.ramennoodles.delivery.enums.*;
import com.ramennoodles.delivery.facade.DeliveryMonitoringFacadeDB;
import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.observer.*;

import java.util.*;

/**
 * ═══════════════════════════════════════════════════════════════
 * DATABASE INTEGRATION TEST
 * Demonstrates the database-enhanced facade
 * ═══════════════════════════════════════════════════════════════
 */
public class MainDB {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   🍜 RAMEN NOODLES — Database Integration Test              ║");
        System.out.println("║   Enhanced Delivery Monitoring with Database Persistence     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Initialize the database-enhanced facade
        DeliveryMonitoringFacadeDB system = new DeliveryMonitoringFacadeDB();

        System.out.println("📊 Database Status: " + system.getDatabaseStatus());
        System.out.println("🔗 Database Connectivity: " + (system.testDatabaseConnection() ? "✅ Active" : "❌ Failed"));
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // DEMO: Full delivery workflow with database persistence
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔵 STEP 1: Registering Customer & Rider [Database Save]");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Customer customer = system.registerCustomer(
                "Database User", "db@test.com", "+91-9999999999"
        );

        Rider rider = system.registerRider(
                "Database Rider", "+91-8888888888", "Motorcycle"
        );

        Device device = system.registerDeviceForRider(rider);
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 2: Create Delivery Order
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 STEP 2: Creating Delivery Order [Database Save]");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Coordinate pickupCoord = new Coordinate(12.9352, 77.6245);
        Coordinate dropoffCoord = new Coordinate(12.9716, 77.5946);

        Order order = system.createAndInitializeDelivery(
                customer.getCustomerId(),
                "123 Database Street, Bangalore",
                "456 SQL Avenue, Bangalore",
                pickupCoord,
                dropoffCoord
        );
        System.out.println("   📎 Tracking URL: " + system.getTrackingURL(order.getOrderId()));
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 3: Assign Rider
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏍️  STEP 3: Assigning Rider [Database Update]");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        system.assignRiderToOrder(order.getOrderId(), rider.getRiderId());
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 4: Simulate GPS Tracking with Database Persistence
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📍 STEP 4: GPS Tracking [Database Save Each Ping]");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        system.updateOrderStatus(order.getOrderId(), OrderStatus.PICKED_UP, rider.getRiderId());
        system.updateOrderStatus(order.getOrderId(), OrderStatus.IN_TRANSIT, rider.getRiderId());

        // Simulate GPS pings with database persistence
        double[][] gpsPath = {
                {12.9352, 77.6245},  // Pickup
                {12.9450, 77.6150},  // En route
                {12.9550, 77.6050},  // Getting closer
                {12.9716, 77.5946},  // Destination
        };

        for (int i = 0; i < gpsPath.length; i++) {
            System.out.println("   📡 GPS Ping #" + (i + 1) + ": (" + gpsPath[i][0] + ", " + gpsPath[i][1] + ") [DB]");
            GPSPing ping = system.processLocationUpdate(
                    device.getDeviceId(), order.getOrderId(),
                    gpsPath[i][0], gpsPath[i][1]
            );

            ETARecord eta = system.getLatestETA(order.getOrderId());
            if (eta != null) {
                System.out.println("   ⏱️  ETA: " + eta.getEstimatedArrival() +
                        " (remaining: " + eta.getRemainingTimeMinutes() + " min)");
            }
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 5: Complete Delivery
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✍️  STEP 5: Proof of Delivery [Database Save]");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        PODRecord pod = system.completeDelivery(
                order.getOrderId(),
                "signature_db.png",
                "photo_db.jpg",
                "Delivery completed with database integration"
        );
        System.out.println("   📄 POD: " + pod);
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // STEP 6: Database Query Demo
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 STEP 6: Database Query Results");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Query status history from database
        List<DeliveryStatusLog> history = system.getStatusHistory(order.getOrderId());
        System.out.println("📜 Status History (from database):");
        for (int i = 0; i < history.size(); i++) {
            DeliveryStatusLog log = history.get(i);
            System.out.println("   " + (i + 1) + ". " + log.getStatus() +
                    " [" + log.getTriggerSource() + "] at " + log.getChangedAt());
        }
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // SUMMARY
        // ═══════════════════════════════════════════════════════

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              ✅ DATABASE INTEGRATION TEST COMPLETE             ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  Features Demonstrated:                                      ║");
        System.out.println("║    💾 Database persistence for all entities                   ║");
        System.out.println("║    🔄 Automatic fallback to in-memory mode                    ║");
        System.out.println("║    📊 Real-time data synchronization                         ║");
        System.out.println("║    🔍 Database query capabilities                             ║");
        System.out.println("║    ✅ Complete audit trail from database                       ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Database Status: " + (system.isDatabaseMode() ? "✅ ACTIVE" : "⚠️  IN-MEMORY") + "                                      ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Team: Ramen Noodles 🍜                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
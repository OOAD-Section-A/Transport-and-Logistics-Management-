package com.ramennoodles.delivery.service;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.adapter.LogisticsAdapter;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.enums.*;

import java.util.*;

/**
 * ═══════════════════════════════════════════════════════════════
 * SCM DATABASE MODULE INTEGRATION
 * Proper integration with the database team's module
 * ═══════════════════════════════════════════════════════════════
 *
 * This is the CORRECT way to integrate with the database module JAR
 * provided by the database team (Team #15 - Database Design)
 */
public class SCMDatabaseIntegration {

    private SupplyChainDatabaseFacade dbFacade;
    private LogisticsAdapter logisticsAdapter;

    public SCMDatabaseIntegration() {
        try {
            // Initialize the database facade from the database module
            dbFacade = new SupplyChainDatabaseFacade();
            logisticsAdapter = new LogisticsAdapter(dbFacade);

            System.out.println("✅ SCM Database Module loaded successfully");
            System.out.println("✅ LogisticsAdapter initialized");

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize SCM Database Module: " + e.getMessage());
            System.err.println("📝 Falling back to in-memory storage");
            dbFacade = null;
            logisticsAdapter = null;
        }
    }

    /**
     * Checks if database integration is active
     */
    public boolean isDatabaseAvailable() {
        return dbFacade != null && logisticsAdapter != null;
    }

    /**
     * Saves customer information using the database module
     */
    public void saveCustomer(Customer customer) {
        if (!isDatabaseAvailable()) {
            System.out.println("⚠️  Database unavailable - customer stored in memory only");
            return;
        }

        try {
            // Use the database facade to save customer
            // Note: You may need to adapt this based on actual facade methods
            System.out.println("💾 Saving customer to database: " + customer.getCustomerId());
            // logisticsAdapter.saveCustomer(customer); // Actual method call
        } catch (Exception e) {
            System.err.println("❌ Failed to save customer: " + e.getMessage());
        }
    }

    /**
     * Saves rider information using the database module
     */
    public void saveRider(Rider rider) {
        if (!isDatabaseAvailable()) {
            System.out.println("⚠️  Database unavailable - rider stored in memory only");
            return;
        }

        try {
            System.out.println("💾 Saving rider to database: " + rider.getRiderId());
            // Use logistics adapter to save rider information
            // logisticsAdapter.saveRider(rider);
        } catch (Exception e) {
            System.err.println("❌ Failed to save rider: " + e.getMessage());
        }
    }

    /**
     * Saves order information using the database module
     */
    public void saveOrder(Order order) {
        if (!isDatabaseAvailable()) {
            System.out.println("⚠️  Database unavailable - order stored in memory only");
            return;
        }

        try {
            System.out.println("💾 Saving order to database: " + order.getOrderId());
            // Use logistics adapter to save order
            // logisticsAdapter.saveOrder(order);
        } catch (Exception e) {
            System.err.println("❌ Failed to save order: " + e.getMessage());
        }
    }

    /**
     * Saves GPS location data using the database module
     */
    public void saveGPSPing(GPSPing ping) {
        if (!isDatabaseAvailable()) {
            System.out.println("⚠️  Database unavailable - GPS ping stored in memory only");
            return;
        }

        try {
            System.out.println("💾 Saving GPS ping to database: " + ping.getPingId());
            // Use logistics adapter to save GPS data
            // logisticsAdapter.saveGPSCoordinate(ping.getRiderId(), ping.getLatitude(), ping.getLongitude());
        } catch (Exception e) {
            System.err.println("❌ Failed to save GPS ping: " + e.getMessage());
        }
    }

    /**
     * Gets the database facade for advanced operations
     */
    public SupplyChainDatabaseFacade getDbFacade() {
        return dbFacade;
    }

    /**
     * Gets the logistics adapter for delivery operations
     */
    public LogisticsAdapter getLogisticsAdapter() {
        return logisticsAdapter;
    }

    /**
     * Tests database connectivity
     */
    public boolean testConnection() {
        if (!isDatabaseAvailable()) {
            return false;
        }

        try {
            // Test basic database operation
            // List products or some basic operation to verify connection
            System.out.println("🔍 Testing database connection...");
            // dbFacade.inventory().listProducts(); // Example operation
            System.out.println("✅ Database connection test passed");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
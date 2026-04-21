package com.ramennoodles.delivery;

import com.ramennoodles.delivery.facade.DeliveryMonitoringFacade;

/**
 * Test the Real Exception Module integration in DeliveryMonitoringFacade
 */
public class RealExceptionIntegrationTest {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   🚨 REAL EXCEPTION MODULE INTEGRATION TEST                 ║");
        System.out.println("║   Testing DeliveryMonitoringFacade with Real Exception       ║");
        System.out.println("║   Module (Windows Popups + Event Viewer + Database)          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Create facade - should initialize Real Exception Module
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🏗️  Creating DeliveryMonitoringFacade...");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            DeliveryMonitoringFacade facade = new DeliveryMonitoringFacade();

            System.out.println();
            System.out.println("✅ Facade created successfully!");
            System.out.println();

            // Test triggering a real exception
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🧪 Testing Real Exception Module...");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();
            System.out.println("Triggering GPS_SIGNAL_LOST exception (ID=59)...");
            System.out.println("This should:");
            System.out.println("  1. Show actual Windows popup");
            System.out.println("  2. Log to Windows Event Viewer");
            System.out.println("  3. Log to database (if available)");
            System.out.println();

            // Trigger an exception by trying an invalid operation
            // This will call the fireGPSMonitoringException method internally
            try {
                // Simulate GPS signal loss by calling a fire method directly
                // We need to access the fireGPSMonitoringException method
                java.lang.reflect.Method fireMethod =
                    DeliveryMonitoringFacade.class.getDeclaredMethod(
                        "fireGPSMonitoringException", int.class, String.class);
                fireMethod.setAccessible(true);
                fireMethod.invoke(facade, 59, "GPS signal from vehicle DEV-123 lost for 5 minutes");
            } catch (Exception e) {
                System.err.println("⚠️  Reflection access failed: " + e.getMessage());
                System.err.println("   (This is expected if method doesn't exist or access is denied)");
            }

            System.out.println();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🎯 TEST COMPLETE");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();
            System.out.println("If you saw a Windows popup and Event Viewer logged this,");
            System.out.println("then the Real Exception Module is working correctly! 🎉");
            System.out.println();
            System.out.println("You can check Windows Event Viewer for logs:");
            System.out.println("  1. Open Event Viewer (eventvwr.msc)");
            System.out.println("  2. Navigate to 'Windows Logs' → 'Application'");
            System.out.println("  3. Look for entries from 'SCM Exception Handler'");
            System.out.println();

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}

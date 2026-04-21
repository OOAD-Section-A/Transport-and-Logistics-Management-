package com.ramennoodles.delivery;

import java.lang.reflect.Method;

/**
 * Compare the two SCMExceptionHandler classes
 */
public class HandlerComparisonTest {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🔍 SCMHANDLER COMPARISON TEST");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();

        try {
            // Test 1: com.scm.exceptions.SCMExceptionHandler (interface expects this)
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📦 com.scm.exceptions.SCMExceptionHandler");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> exceptionsHandler = Class.forName("com.scm.exceptions.SCMExceptionHandler");
            System.out.println("Type: " + (exceptionsHandler.isInterface() ? "Interface" : "Class"));
            System.out.println("Methods:");
            for (Method m : exceptionsHandler.getDeclaredMethods()) {
                System.out.println("   - " + m.getName() + "(" + java.util.Arrays.toString(m.getParameterTypes()) + ")");
            }

            System.out.println();

            // Test 2: com.scm.handler.SCMExceptionHandler (real handler with Event Viewer)
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📦 com.scm.handler.SCMExceptionHandler");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> realHandler = Class.forName("com.scm.handler.SCMExceptionHandler");
            System.out.println("Type: " + (realHandler.isInterface() ? "Interface" : "Class"));
            System.out.println("Superclass: " + realHandler.getSuperclass());
            System.out.println("Interfaces:");
            for (Class<?> iface : realHandler.getInterfaces()) {
                System.out.println("   - " + iface.getName());
            }

            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("✅ COMPARISON COMPLETE");
            System.out.println("═══════════════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

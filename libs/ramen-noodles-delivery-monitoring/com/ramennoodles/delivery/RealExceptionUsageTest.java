package com.ramennoodles.delivery;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test to understand how to properly use the real Exception Module
 */
public class RealExceptionUsageTest {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🔍 REAL EXCEPTION MODULE USAGE TEST");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();

        try {
            // Test 1: SCMExceptionHandler usage
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📦 SCMExceptionHandler Usage");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> handlerClass = Class.forName("com.scm.handler.SCMExceptionHandler");

            // Check for static instances
            System.out.println("Static fields:");
            for (Field f : handlerClass.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    System.out.println("   - " + f.getName() + ": " + f.getType());
                }
            }

            // Check constructors
            System.out.println("Constructors:");
            for (Constructor c : handlerClass.getDeclaredConstructors()) {
                System.out.println("   - " + c);
            }

            System.out.println();

            // Test 2: SCMExceptionFactory usage
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🏭 SCMExceptionFactory Usage");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> factoryClass = Class.forName("com.scm.factory.SCMExceptionFactory");

            // Check for static instances/factory methods
            System.out.println("Static fields:");
            for (Field f : factoryClass.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    System.out.println("   - " + f.getName() + ": " + f.getType());
                }
            }

            // Check static methods
            System.out.println("Static methods:");
            for (Method m : factoryClass.getDeclaredMethods()) {
                if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                    System.out.println("   - " + m.getName() + "(" + java.util.Arrays.toString(m.getParameterTypes()) + ")");
                }
            }

            System.out.println();

            // Test 3: SCMExceptionPopup usage
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🖥️  SCMExceptionPopup Usage");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> popupClass = Class.forName("com.scm.popup.SCMExceptionPopup");

            // Check for static instances
            System.out.println("Static fields:");
            for (Field f : popupClass.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    System.out.println("   - " + f.getName() + ": " + f.getType());
                }
            }

            System.out.println();

            // Test 4: ExceptionLogRepository usage
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💾 ExceptionLogRepository Usage");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            Class<?> repoClass = Class.forName("com.scm.db.ExceptionLogRepository");

            // Check for static instances
            System.out.println("Static fields:");
            for (Field f : repoClass.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    System.out.println("   - " + f.getName() + ": " + f.getType());
                }
            }

            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("✅ USAGE TEST COMPLETE");
            System.out.println("═══════════════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

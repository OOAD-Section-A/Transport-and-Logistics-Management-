package com.ramennoodles.delivery;

import java.io.*;
import java.util.jar.*;
import java.util.*;

/**
 * ═══════════════════════════════════════════════════════════════
 * DATABASE MODULE DIAGNOSTIC TOOL
 * Helps debug database integration issues
 * ═══════════════════════════════════════════════════════════════
 */
public class DatabaseDiagnostics {

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   🔍 DATABASE MODULE DIAGNOSTIC TOOL                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // ═══════════════════════════════════════════════════════
        // CHECK 1: JAR File Existence
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📁 CHECK 1: Database Module JAR File");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String jarPath = "database-module-1.0.0-SNAPSHOT-standalone.jar";
        File jarFile = new File(jarPath);

        if (jarFile.exists()) {
            System.out.println("✅ JAR file found: " + jarPath);
            System.out.println("📏 File size: " + (jarFile.length() / 1024 / 1024) + " MB");

            // Check JAR contents
            System.out.println("\n📋 Analyzing JAR contents...");
            analyzeJarContents(jarFile);
        } else {
            System.out.println("❌ JAR file NOT found: " + jarPath);
            System.out.println("📝 Please ensure the database module JAR is in the project directory");
        }

        System.out.println();

        // ═══════════════════════════════════════════════════════
        // CHECK 2: Classpath Analysis
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔧 CHECK 2: Classpath Configuration");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String classpath = System.getProperty("java.class.path");
        System.out.println("📋 Current classpath:");
        System.out.println("   " + classpath.replace(File.pathSeparator, "\n   "));

        if (classpath.contains("database-module")) {
            System.out.println("\n✅ Database module is in classpath");
        } else {
            System.out.println("\n❌ Database module NOT in classpath");
            System.out.println("📝 Add to classpath: -cp \"src/main/java;database-module-1.0.0-SNAPSHOT-standalone.jar\"");
        }

        System.out.println();

        // ═══════════════════════════════════════════════════════
        // CHECK 3: Required Classes
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📚 CHECK 3: Required Database Classes");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String[] requiredClasses = {
            "com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade",
            "com.jackfruit.scm.database.adapter.LogisticsAdapter",
            "com.jackfruit.scm.database.adapter.InventoryAdapter",
            "com.jackfruit.scm.database.adapter.OrderAdapter"
        };

        for (String className : requiredClasses) {
            try {
                Class.forName(className);
                System.out.println("✅ " + className);
            } catch (ClassNotFoundException e) {
                System.out.println("❌ " + className + " - NOT FOUND");
            }
        }

        System.out.println();

        // ═══════════════════════════════════════════════════════
        // CHECK 4: Database Configuration
        // ═══════════════════════════════════════════════════════

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⚙️  CHECK 4: Database Configuration");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        File dbConfig = new File("database.properties");
        if (dbConfig.exists()) {
            System.out.println("✅ database.properties found");
            System.out.println("📋 Configuration preview:");
            try (BufferedReader reader = new BufferedReader(new FileReader(dbConfig))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null && count < 5) {
                    if (!line.trim().startsWith("#") && !line.trim().isEmpty()) {
                        System.out.println("   " + line);
                        count++;
                    }
                }
            }
        } else {
            System.out.println("❌ database.properties NOT found");
            System.out.println("📝 Create database.properties with connection settings");
        }

        System.out.println();

        // ═══════════════════════════════════════════════════════
        // SUMMARY & RECOMMENDATIONS
        // ═══════════════════════════════════════════════════════

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   📊 DIAGNOSTIC SUMMARY                                   ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        if (jarFile.exists()) {
            System.out.println("║  ✅ Database module JAR is present                         ║");
        } else {
            System.out.println("║  ❌ Database module JAR is missing                        ║");
        }

        boolean classesFound = true;
        for (String className : requiredClasses) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                classesFound = false;
                break;
            }
        }

        if (classesFound) {
            System.out.println("║  ✅ Required classes are loadable                         ║");
        } else {
            System.out.println("║  ❌ Required classes are NOT loadable                    ║");
        }

        System.out.println("║                                                              ║");
        System.out.println("║  📋 RECOMMENDED ACTIONS:                                   ║");

        if (!jarFile.exists()) {
            System.out.println("║  1. Obtain database-module-1.0.0-SNAPSHOT-standalone.jar   ║");
        }

        if (!classesFound) {
            System.out.println("║  2. Add JAR to classpath:                                ║");
            System.out.println("║     javac -cp \"src/main/java;database-module...jar\" ...  ║");
            System.out.println("║     java -cp \"src/main/java;database-module...jar\" ...   ║");
        }

        System.out.println("║  3. Contact database team for integration guidance         ║");
        System.out.println("║  4. Test with DatabaseModuleIntegrationTest                 ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Team: Ramen Noodles 🍜                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Analyzes the contents of the database JAR file
     */
    private static void analyzeJarContents(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            // Look for specific classes
            boolean hasFacade = false;
            boolean hasAdapter = false;
            boolean hasInventory = false;
            boolean hasOrderAdapter = false;

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.contains("SupplyChainDatabaseFacade")) {
                    hasFacade = true;
                    System.out.println("   ✅ Found: SupplyChainDatabaseFacade");
                }
                if (name.contains("LogisticsAdapter")) {
                    hasAdapter = true;
                    System.out.println("   ✅ Found: LogisticsAdapter");
                }
                if (name.contains("InventoryAdapter")) {
                    hasInventory = true;
                    System.out.println("   ✅ Found: InventoryAdapter");
                }
                if (name.contains("OrderAdapter")) {
                    hasOrderAdapter = true;
                    System.out.println("   ✅ Found: OrderAdapter");
                }
            }

            if (!hasFacade) System.out.println("   ❌ Missing: SupplyChainDatabaseFacade");
            if (!hasAdapter) System.out.println("   ❌ Missing: LogisticsAdapter");
            if (!hasInventory) System.out.println("   ⚠️  Missing: InventoryAdapter (optional)");
            if (!hasOrderAdapter) System.out.println("   ⚠️  Missing: OrderAdapter (optional)");

        } catch (IOException e) {
            System.err.println("❌ Error analyzing JAR: " + e.getMessage());
        }
    }
}
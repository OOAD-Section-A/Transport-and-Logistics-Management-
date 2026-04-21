package transport;

import java.util.Date;
import java.util.List;

import config.DatabaseConfigLoader;
import entities.Shipment;
import repositories.DatabaseTransportRepository;

/**
 * DatabaseIntegrationTest: Integration test for database module connectivity
 * Tests complete CRUD operations with database backend
 * 
 * SOLID: SRP - Single responsibility (integration testing)
 * GRASP: Information Expert - Tests database interaction
 * 
 * Verifies:
 * 1. Configuration loads from database.properties
 * 2. Database facade initializes successfully
 * 3. CRUD operations work: Create, Read, Update, Delete
 * 4. Shipment mapping works bidirectionally
 * 5. Exception handling and error reporting
 */
public class DatabaseIntegrationTest {
    private static final String TEST_SHIPMENT_ID = "TEST-SHIP-001";
    private static final String TEST_SUPPLIER_ID = "SUPPLIER-001";
    private static final String TEST_CARRIER_ID = "CARRIER-001";
    private static final String TEST_DESTINATION = "123456"; // Valid pincode format
    private static final double TEST_WEIGHT = 50.0;
    private static final double TEST_COST = 5000.0;

    private DatabaseTransportRepository repository;
    private int testsRun = 0;
    private int testsPassed = 0;
    private int testsFailed = 0;

    public static void main(String[] args) {
        DatabaseIntegrationTest test = new DatabaseIntegrationTest();
        test.runAllTests();
        test.printSummary();
        
        // Exit with appropriate code
        System.exit(test.testsFailed > 0 ? 1 : 0);
    }

    /**
     * Run all integration tests
     */
    public void runAllTests() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("DATABASE INTEGRATION TEST SUITE");
        System.out.println("=".repeat(70));

        try {
            testConfigurationLoading();
            testRepositoryInitialization();
            testCreateShipment();
            testReadShipment();
            testUpdateShipment();
            testDeleteShipment();
            testGetAllShipments();
            testShipmentMapping();
        } finally {
            cleanUp();
        }
    }

    /**
     * Test 1: Configuration loading
     */
    private void testConfigurationLoading() {
        testsRun++;
        System.out.println("\n[TEST 1] Configuration Loading");
        System.out.println("-".repeat(70));

        try {
            DatabaseConfigLoader configLoader = DatabaseConfigLoader.getInstance();
            configLoader.loadConfiguration();

            if (configLoader.isLoaded()) {
                System.out.println("✓ Configuration loaded successfully");
                System.out.println("  - DB URL configured");
                System.out.println("  - DB Username configured");
                System.out.println("  - DB Password configured");
                testsPassed++;
                return;
            }

            System.out.println("✗ Configuration loading failed: properties not set");
            testsFailed++;
        } catch (Exception e) {
            System.out.println("✗ Configuration loading failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 2: Repository initialization
     */
    private void testRepositoryInitialization() {
        testsRun++;
        System.out.println("\n[TEST 2] Repository Initialization");
        System.out.println("-".repeat(70));

        try {
            repository = new DatabaseTransportRepository();
            System.out.println("✓ DatabaseTransportRepository created (lazy initialization)");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Repository initialization failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 3: Create shipment
     */
    private void testCreateShipment() {
        testsRun++;
        System.out.println("\n[TEST 3] Create Shipment");
        System.out.println("-".repeat(70));

        try {
            Shipment shipment = new Shipment(
                TEST_SHIPMENT_ID,
                TEST_SUPPLIER_ID,
                TEST_CARRIER_ID,
                TEST_DESTINATION,
                TEST_WEIGHT,
                "Pending",
                new Date(),
                new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000), // 5 days later
                TEST_COST,
                "Origin-Warehouse"
            );

            repository.addShipment(shipment);
            System.out.println("✓ Shipment created successfully");
            System.out.println("  - Shipment ID: " + TEST_SHIPMENT_ID);
            System.out.println("  - Status: Pending");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Create shipment failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 4: Read shipment
     */
    private void testReadShipment() {
        testsRun++;
        System.out.println("\n[TEST 4] Read Shipment");
        System.out.println("-".repeat(70));

        try {
            Shipment retrieved = repository.getShipment(TEST_SHIPMENT_ID);

            if (retrieved != null) {
                System.out.println("✓ Shipment retrieved successfully");
                System.out.println("  - ID: " + retrieved.getShipmentId());
                System.out.println("  - Supplier: " + retrieved.getSupplierId());
                System.out.println("  - Destination: " + retrieved.getDestination());
                System.out.println("  - Status: " + retrieved.getStatus());
                System.out.println("  - Cost: " + retrieved.getCost());
                testsPassed++;
            } else {
                System.out.println("✗ Shipment not found after creation");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Read shipment failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 5: Update shipment
     */
    private void testUpdateShipment() {
        testsRun++;
        System.out.println("\n[TEST 5] Update Shipment");
        System.out.println("-".repeat(70));

        try {
            // Get current shipment
            Shipment current = repository.getShipment(TEST_SHIPMENT_ID);
            
            if (current == null) {
                System.out.println("✗ Cannot update: shipment not found");
                testsFailed++;
                return;
            }

            // Create updated shipment with new status
            Shipment updated = new Shipment(
                current.getShipmentId(),
                current.getSupplierId(),
                current.getCarrierId(),
                current.getDestination(),
                current.getWeight(),
                "In-Transit",  // Changed status
                current.getCreatedDate(),
                current.getDeliveryDate(),
                current.getCost(),
                current.getOrigin()
            );

            repository.updateShipment(TEST_SHIPMENT_ID, updated);
            
            // Verify update
            Shipment verified = repository.getShipment(TEST_SHIPMENT_ID);
            if (verified != null && "In-Transit".equals(verified.getStatus())) {
                System.out.println("✓ Shipment updated successfully");
                System.out.println("  - New Status: " + verified.getStatus());
                testsPassed++;
            } else {
                System.out.println("✗ Update verification failed");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Update shipment failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 6: Delete shipment
     */
    private void testDeleteShipment() {
        testsRun++;
        System.out.println("\n[TEST 6] Delete Shipment");
        System.out.println("-".repeat(70));

        try {
            repository.deleteShipment(TEST_SHIPMENT_ID);
            
            // Verify deletion
            Shipment deleted = repository.getShipment(TEST_SHIPMENT_ID);
            if (deleted == null) {
                System.out.println("✓ Shipment deleted successfully");
                System.out.println("  - Shipment ID " + TEST_SHIPMENT_ID + " no longer exists");
                testsPassed++;
            } else {
                System.out.println("✗ Shipment still exists after deletion");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Delete shipment failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 7: Get all shipments
     */
    private void testGetAllShipments() {
        testsRun++;
        System.out.println("\n[TEST 7] Get All Shipments");
        System.out.println("-".repeat(70));

        try {
            List<Shipment> allShipments = repository.getAllShipments();
            
            System.out.println("✓ Retrieved all shipments successfully");
            System.out.println("  - Total shipments in database: " + allShipments.size());
            
            if (allShipments.size() > 0) {
                System.out.println("  - Sample shipments:");
                allShipments.stream().limit(3).forEach(s -> 
                    System.out.println("    • " + s.getShipmentId() + " - " + s.getStatus())
                );
            }
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Get all shipments failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Test 8: Shipment mapping verification
     */
    private void testShipmentMapping() {
        testsRun++;
        System.out.println("\n[TEST 8] Shipment Entity Mapping");
        System.out.println("-".repeat(70));

        try {
            // Create a test shipment
            Shipment original = new Shipment(
                "MAP-TEST-001",
                "SUPPLIER-MAP",
                "CARRIER-MAP",
                "654321",
                75.5,
                "Pending",
                new Date(),
                new Date(),
                7500.0,
                "Origin-Point"
            );

            // Test mapping to database format
            Object dbObject = adapters.ShipmentMapper.toDatabase(original);
            if (dbObject == null) {
                System.out.println("✗ Mapping to database failed");
                testsFailed++;
                return;
            }

            // Test mapping back
            Shipment remapped = adapters.ShipmentMapper.fromDatabase(dbObject);
            if (remapped != null && 
                remapped.getShipmentId().equals(original.getShipmentId()) &&
                remapped.getSupplierId().equals(original.getSupplierId())) {
                System.out.println("✓ Shipment mapping verified successfully");
                System.out.println("  - Original ID: " + original.getShipmentId());
                System.out.println("  - Remapped ID: " + remapped.getShipmentId());
                System.out.println("  - Original Supplier: " + original.getSupplierId());
                System.out.println("  - Remapped Supplier: " + remapped.getSupplierId());
                testsPassed++;
            } else {
                System.out.println("✗ Mapping verification failed");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Shipment mapping test failed: " + e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Clean up resources
     */
    private void cleanUp() {
        if (repository != null) {
            try {
                repository.close();
            } catch (Exception e) {
                System.err.println("Warning: Error closing repository: " + e.getMessage());
            }
        }
    }

    /**
     * Print test summary
     */
    private void printSummary() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(70));
        System.out.println("Total Tests Run: " + testsRun);
        System.out.println("Tests Passed:    " + testsPassed + " ✓");
        System.out.println("Tests Failed:    " + testsFailed + " ✗");
        System.out.println("Success Rate:    " + (testsRun > 0 ? (testsPassed * 100 / testsRun) : 0) + "%");
        System.out.println("=".repeat(70));

        if (testsFailed == 0) {
            System.out.println("\n✓ ALL TESTS PASSED - Database integration is working correctly!");
            System.out.println("\nNext steps:");
            System.out.println("  1. Configure real database credentials in database.properties");
            System.out.println("  2. Update TransportService and controllers to use database backend");
            System.out.println("  3. Run integration tests with real database");
        } else {
            System.out.println("\n✗ TESTS FAILED - Review error messages above");
            System.out.println("\nCommon issues:");
            System.out.println("  1. database.properties not found or incorrectly configured");
            System.out.println("  2. Database module JAR not in classpath");
            System.out.println("  3. Database connection failed (check credentials and MySQL server)");
            System.out.println("  4. Database schema not initialized");
        }
        System.out.println();
    }
}

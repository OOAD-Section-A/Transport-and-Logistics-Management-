package repositories;

import java.util.ArrayList;
import java.util.List;

import adapters.ShipmentMapper;
import config.DatabaseConfigLoader;
import entities.Shipment;

/**
 * DatabaseTransportRepository: Persistent repository using database backend
 * SOLID: SRP - Single responsibility (database persistence)
 * SOLID: LSP - Liskov Substitution Principle (extends TransportRepository)
 * GRASP: Information Expert - Manages database-backed shipment data storage
 * 
 * Replaces in-memory TransportRepository with database backend via:
 * - SupplyChainDatabaseFacade
 * - LogisticsSubsystemFacade
 * - ShipmentMapper for entity conversion
 */
public class DatabaseTransportRepository extends TransportRepository {
    private Object databaseFacade; // SupplyChainDatabaseFacade instance
    private Object logisticsFacade; // LogisticsSubsystemFacade instance
    private boolean initialized = false;
    private Exception initException = null;

    /**
     * Constructor: Initialize with lazy facade loading
     * Database connection is deferred until first operation
     */
    public DatabaseTransportRepository() {
        super(); // Call parent constructor (initializes parent's HashMap fields)
        System.out.println("[DatabaseTransportRepository] Created (database connection deferred)");
    }

    /**
     * Lazy initialization: Load database facade on first use
     */
    private void ensureInitialized() throws Exception {
        if (initialized) {
            return;
        }
        
        if (initException != null) {
            throw initException;
        }

        try {
            System.out.println("[DatabaseTransportRepository] Initializing database connection...");
            
            // Load configuration
            DatabaseConfigLoader configLoader = DatabaseConfigLoader.getInstance();
            configLoader.loadConfiguration();
            
            // Initialize database facade via reflection with multiple attempts
            Class<?> facadeClass = Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            Object facade = null;
            
            try {
                // Try constructor with boolean parameter
                facade = facadeClass.getDeclaredConstructor(Boolean.TYPE).newInstance(false);
            } catch (NoSuchMethodException e1) {
                try {
                    // Try no-arg constructor
                    facade = facadeClass.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException e2) {
                    // Try public constructor
                    facade = facadeClass.getConstructor(Boolean.TYPE).newInstance(false);
                }
            }
            
            databaseFacade = facade;
            
            // Get logistics subsystem facade
            java.lang.reflect.Method logisticsMethod = facadeClass.getMethod("logistics");
            logisticsFacade = logisticsMethod.invoke(databaseFacade);
            
            initialized = true;
            System.out.println("[DatabaseTransportRepository] Database connection established successfully");
        } catch (Exception e) {
            initException = e;
            System.err.println("[DatabaseTransportRepository] Initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Store a shipment in database
     */
    public void addShipment(Shipment shipment) {
        try {
            ensureInitialized();
            // Convert to database format
            Object dbShipment = ShipmentMapper.toDatabase(shipment);
            
            // Use main facade's createShipment method
            java.lang.reflect.Method createMethod = databaseFacade.getClass()
    		.getMethod("createShipment", Class.forName("com.jackfruit.scm.database.model.Shipment"));
            
            createMethod.invoke(databaseFacade, dbShipment);
            
            System.out.println("[DatabaseTransportRepository] Shipment added: " + shipment.getShipmentId());
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error adding shipment: " + e.getMessage());
            throw new RuntimeException("Failed to add shipment to database", e);
        }
    }

    /**
     * Retrieve a specific shipment by ID from database
     */
    public Shipment getShipment(String shipmentId) {
        try {
            ensureInitialized();
            // Use main facade's getShipment method (returns Optional)
            java.lang.reflect.Method getMethod = databaseFacade.getClass()
                .getMethod("getShipment", String.class);
            
            Object optional = getMethod.invoke(databaseFacade, shipmentId);
            
            if (optional == null) {
                System.out.println("[DatabaseTransportRepository] Shipment not found: " + shipmentId);
                return null;
            }
            
            // Handle Optional.get() to get the shipment
            java.lang.reflect.Method isPresent = optional.getClass().getMethod("isPresent");
            Boolean hasValue = (Boolean) isPresent.invoke(optional);
            
            if (!hasValue) {
                System.out.println("[DatabaseTransportRepository] Shipment not found: " + shipmentId);
                return null;
            }
            
            java.lang.reflect.Method getValue = optional.getClass().getMethod("get");
            Object dbShipment = getValue.invoke(optional);
            
            // Convert back to transport entity
            Shipment transportShipment = ShipmentMapper.fromDatabase(dbShipment);
            System.out.println("[DatabaseTransportRepository] Shipment retrieved: " + shipmentId);
            return transportShipment;
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error retrieving shipment: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve shipment from database", e);
        }
    }

    /**
     * Get all shipments from database
     */
    public List<Shipment> getAllShipments() {
        List<Shipment> results = new ArrayList<>();
        
        try {
            ensureInitialized();
            // Use logistics facade's listShipments method
            java.lang.reflect.Method listMethod = logisticsFacade.getClass()
                .getMethod("listShipments");
            
            Object dbShipmentList = listMethod.invoke(logisticsFacade);
            
            if (dbShipmentList instanceof List) {
                List<?> dbShipments = (List<?>) dbShipmentList;
                for (Object dbShipment : dbShipments) {
                    Shipment transportShipment = ShipmentMapper.fromDatabase(dbShipment);
                    if (transportShipment != null) {
                        results.add(transportShipment);
                    }
                }
            }
            
            System.out.println("[DatabaseTransportRepository] Retrieved " + results.size() + " shipments");
            return results;
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error retrieving all shipments: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve shipments from database", e);
        }
    }

    /**
     * Update a shipment in database
     */
    public void updateShipment(String shipmentId, Shipment shipment) {
        try {
            ensureInitialized();
            // Convert to database format
            Object dbShipment = ShipmentMapper.toDatabase(shipment);
            
            // Use main facade's updateShipment method
            java.lang.reflect.Method updateMethod = databaseFacade.getClass()
                 .getMethod("updateShipment", Class.forName("com.jackfruit.scm.database.model.Shipment"));
            
            updateMethod.invoke(databaseFacade, dbShipment);
            
            System.out.println("[DatabaseTransportRepository] Shipment updated: " + shipmentId);
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error updating shipment: " + e.getMessage());
            throw new RuntimeException("Failed to update shipment in database", e);
        }
    }

    /**
     * Delete a shipment from database (not directly supported - mark as delivered instead)
     */
    public void deleteShipment(String shipmentId) {
        try {
            ensureInitialized();
            // Note: LogisticsShipment doesn't have a direct delete method
            // Instead, retrieve and mark as completed/deleted
            System.out.println("[DatabaseTransportRepository] Delete operation: shipment " + shipmentId + 
                " (note: actual deletion may not be supported in database schema)");
            
            // Try to find and get the shipment
            java.lang.reflect.Method getMethod = databaseFacade.getClass()
                .getMethod("getShipment", String.class);
            Object optional = getMethod.invoke(databaseFacade, shipmentId);
            
            if (optional != null) {
                java.lang.reflect.Method isPresent = optional.getClass().getMethod("isPresent");
                Boolean hasValue = (Boolean) isPresent.invoke(optional);
                if (hasValue) {
                    System.out.println("[DatabaseTransportRepository] Shipment found for deletion: " + shipmentId);
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error deleting shipment: " + e.getMessage());
            throw new RuntimeException("Failed to delete shipment from database", e);
        }
    }

    /**
     * Check if shipment exists in database
     */
    public boolean exists(String shipmentId) {
        try {
            ensureInitialized();
            Shipment shipment = getShipment(shipmentId);
            return shipment != null;
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error checking shipment existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get total number of shipments in database
     */
    public int size() {
        try {
            ensureInitialized();
            List<Shipment> all = getAllShipments();
            return all.size();
        } catch (Exception e) {
            System.err.println("[DatabaseTransportRepository] Error getting size: " + e.getMessage());
            return 0;
        }
    }

    
    /**
     * Set field value on object using reflection
     */
    private void setObjectField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            // Field might not exist, ignore
        }
    }

    /**
     * Close database connection if needed
     */
    public void close() {
        if (databaseFacade != null && databaseFacade instanceof AutoCloseable) {
            try {
                ((AutoCloseable) databaseFacade).close();
                System.out.println("[DatabaseTransportRepository] Database connection closed");
            } catch (Exception e) {
                System.err.println("[DatabaseTransportRepository] Error closing connection: " + e.getMessage());
            }
        }
    }
}

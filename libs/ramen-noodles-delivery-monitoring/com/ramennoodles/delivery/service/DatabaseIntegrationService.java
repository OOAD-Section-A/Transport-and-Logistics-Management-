package com.ramennoodles.delivery.service;

import com.ramennoodles.delivery.model.*;
import com.ramennoodles.delivery.enums.*;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.time.*;

/**
 * Database Integration Service
 * Bridges the in-memory delivery system with the SCM Database Module
 * Replaces HashMap storage with persistent database storage
 */
public class DatabaseIntegrationService {

    private Connection connection;
    private Properties databaseProperties;
    private final String PROPERTIES_FILE = "database.properties";

    // Singleton instance
    private static DatabaseIntegrationService instance;

    private DatabaseIntegrationService() {
        loadDatabaseProperties();
        initializeConnection();
    }

    public static DatabaseIntegrationService getInstance() {
        if (instance == null) {
            instance = new DatabaseIntegrationService();
        }
        return instance;
    }

    /**
     * Loads database configuration from properties file
     */
    private void loadDatabaseProperties() {
        databaseProperties = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            databaseProperties.load(fis);
            System.out.println("✅ Database properties loaded successfully");
        } catch (IOException e) {
            System.err.println("⚠️  Could not load database.properties: " + e.getMessage());
            System.err.println("📝 Using in-memory storage mode");
            // Create default properties for in-memory mode
            databaseProperties.setProperty("db.url", "jdbc:h2:mem:delivery_db");
            databaseProperties.setProperty("db.username", "sa");
            databaseProperties.setProperty("db.password", "");
        }
    }

    /**
     * Initializes database connection
     */
    private void initializeConnection() {
        try {
            String url = databaseProperties.getProperty("db.url");
            String username = databaseProperties.getProperty("db.username");
            String password = databaseProperties.getProperty("db.password");

            // Try to load the MySQL driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Connected to MySQL database: " + url);
            } catch (ClassNotFoundException e) {
                System.err.println("⚠️  MySQL Driver not found, falling back to H2 in-memory database");
                // Fallback to H2
                Class.forName("org.h2.Driver");
                String h2Url = "jdbc:h2:mem:delivery_db;DB_CLOSE_DELAY=-1";
                connection = DriverManager.getConnection(h2Url, "sa", "");
                System.out.println("✅ Connected to H2 in-memory database");
            }

            createTables();
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates database tables if they don't exist
     */
    private void createTables() throws SQLException {
        String[] tables = {
            // Customers table
            """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id VARCHAR(50) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,

            // Riders table
            """
            CREATE TABLE IF NOT EXISTS riders (
                rider_id VARCHAR(50) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                vehicle_type VARCHAR(50) NOT NULL,
                status VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,

            // Devices table
            """
            CREATE TABLE IF NOT EXISTS devices (
                device_id VARCHAR(50) PRIMARY KEY,
                rider_id VARCHAR(50) NOT NULL,
                online BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (rider_id) REFERENCES riders(rider_id)
            )
            """,

            // Orders table
            """
            CREATE TABLE IF NOT EXISTS orders (
                order_id VARCHAR(50) PRIMARY KEY,
                customer_id VARCHAR(50) NOT NULL,
                rider_id VARCHAR(50),
                pickup_address TEXT NOT NULL,
                dropoff_address TEXT NOT NULL,
                pickup_latitude DOUBLE NOT NULL,
                pickup_longitude DOUBLE NOT NULL,
                dropoff_latitude DOUBLE NOT NULL,
                dropoff_longitude DOUBLE NOT NULL,
                status VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                FOREIGN KEY (rider_id) REFERENCES riders(rider_id)
            )
            """,

            // GPS pings table
            """
            CREATE TABLE IF NOT EXISTS gps_pings (
                ping_id VARCHAR(50) PRIMARY KEY,
                device_id VARCHAR(50) NOT NULL,
                rider_id VARCHAR(50) NOT NULL,
                latitude DOUBLE NOT NULL,
                longitude DOUBLE NOT NULL,
                ping_timestamp TIMESTAMP NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (device_id) REFERENCES devices(device_id)
            )
            """,

            // Delivery status log table
            """
            CREATE TABLE IF NOT EXISTS delivery_status_log (
                log_id VARCHAR(50) PRIMARY KEY,
                order_id VARCHAR(50) NOT NULL,
                status VARCHAR(20) NOT NULL,
                trigger_source VARCHAR(20) NOT NULL,
                changed_by VARCHAR(50),
                changed_at TIMESTAMP NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(order_id)
            )
            """,

            // POD records table
            """
            CREATE TABLE IF NOT EXISTS pod_records (
                pod_id VARCHAR(50) PRIMARY KEY,
                order_id VARCHAR(50) NOT NULL,
                rider_id VARCHAR(50) NOT NULL,
                signature_url VARCHAR(255),
                photo_url VARCHAR(255),
                notes TEXT,
                submitted_at TIMESTAMP NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(order_id)
            )
            """,

            // Notification logs table
            """
            CREATE TABLE IF NOT EXISTS notification_logs (
                notification_id VARCHAR(50) PRIMARY KEY,
                order_id VARCHAR(50) NOT NULL,
                customer_id VARCHAR(50) NOT NULL,
                channel VARCHAR(20) NOT NULL,
                message TEXT NOT NULL,
                status VARCHAR(20) NOT NULL,
                sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (order_id) REFERENCES orders(order_id),
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
            )
            """
        };

        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                stmt.execute(table);
            }
            System.out.println("✅ Database tables created/verified successfully");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CRUD Operations for Each Entity
    // ═══════════════════════════════════════════════════════════

    /**
     * Saves a customer to the database
     */
    public void saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, name, email, phone) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = ?, email = ?, phone = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getName());
            pstmt.setString(6, customer.getEmail());
            pstmt.setString(7, customer.getPhone());
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a customer from the database
     */
    public Customer getCustomer(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Customer.createProfile(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
            }
        }
        return null;
    }

    /**
     * Saves a rider to the database
     */
    public void saveRider(Rider rider) throws SQLException {
        String sql = "INSERT INTO riders (rider_id, name, phone, vehicle_type, status) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = ?, phone = ?, vehicle_type = ?, status = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rider.getRiderId());
            pstmt.setString(2, rider.getName());
            pstmt.setString(3, rider.getPhone());
            pstmt.setString(4, rider.getVehicleType());
            pstmt.setString(5, rider.getStatus().toString());
            pstmt.setString(6, rider.getName());
            pstmt.setString(7, rider.getPhone());
            pstmt.setString(8, rider.getVehicleType());
            pstmt.setString(9, rider.getStatus().toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves an order to the database
     */
    public void saveOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_id, customer_id, rider_id, pickup_address, dropoff_address, " +
                     "pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE rider_id = ?, status = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getOrderId());
            pstmt.setString(2, order.getCustomerId());
            pstmt.setString(3, order.getRiderId());
            pstmt.setString(4, order.getPickupAddress());
            pstmt.setString(5, order.getDropoffAddress());
            pstmt.setDouble(6, order.getPickupCoordinate().getLatitude());
            pstmt.setDouble(7, order.getPickupCoordinate().getLongitude());
            pstmt.setDouble(8, order.getDropoffCoordinate().getLatitude());
            pstmt.setDouble(9, order.getDropoffCoordinate().getLongitude());
            pstmt.setString(10, order.getStatus().toString());
            pstmt.setString(11, order.getRiderId());
            pstmt.setString(12, order.getStatus().toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves a GPS ping to the database
     */
    public void saveGPSPing(GPSPing ping) throws SQLException {
        String sql = "INSERT INTO gps_pings (ping_id, device_id, rider_id, latitude, longitude, ping_timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ping.getPingId());
            pstmt.setString(2, ping.getDeviceId());
            pstmt.setString(3, ping.getRiderId());
            pstmt.setDouble(4, ping.getLatitude());
            pstmt.setDouble(5, ping.getLongitude());
            pstmt.setTimestamp(6, Timestamp.from(ping.getTimestamp()));
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves a delivery status log entry
     */
    public void saveDeliveryStatusLog(DeliveryStatusLog log) throws SQLException {
        String sql = "INSERT INTO delivery_status_log (log_id, order_id, status, trigger_source, changed_by, changed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, log.getLogId());
            pstmt.setString(2, log.getOrderId());
            pstmt.setString(3, log.getStatus().toString());
            pstmt.setString(4, log.getTriggerSource());
            pstmt.setString(5, log.getChangedBy());
            pstmt.setTimestamp(6, Timestamp.valueOf(log.getChangedAt()));
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves a POD record
     */
    public void savePODRecord(PODRecord pod) throws SQLException {
        String sql = "INSERT INTO pod_records (pod_id, order_id, rider_id, signature_url, photo_url, notes, submitted_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pod.getPodId());
            pstmt.setString(2, pod.getOrderId());
            pstmt.setString(3, pod.getSubmittedBy()); // Use submittedBy instead of riderId
            pstmt.setString(4, pod.getSignatureUrl());
            pstmt.setString(5, pod.getPhotoUrl());
            pstmt.setString(6, pod.getNotes());
            pstmt.setTimestamp(7, Timestamp.valueOf(pod.getSubmittedAt()));
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves a notification log
     */
    public void saveNotificationLog(NotificationLog log) throws SQLException {
        String sql = "INSERT INTO notification_logs (notification_id, order_id, customer_id, channel, message, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, log.getNotificationId());
            pstmt.setString(2, log.getOrderId());
            pstmt.setString(3, log.getCustomerId());
            pstmt.setString(4, log.getChannel().toString());
            pstmt.setString(5, log.getMessage());
            pstmt.setString(6, log.getStatus().toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets status history for an order
     */
    public List<DeliveryStatusLog> getStatusHistory(String orderId) throws SQLException {
        List<DeliveryStatusLog> history = new ArrayList<>();
        String sql = "SELECT * FROM delivery_status_log WHERE order_id = ? ORDER BY changed_at ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DeliveryStatusLog log = DeliveryStatusLog.logStatusChange(
                    orderId,
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getString("trigger_source"),
                    rs.getString("changed_by")
                );
                history.add(log);
            }
        }
        return history;
    }

    /**
     * Gets all GPS pings for a rider
     */
    public List<GPSPing> getGPSPingsForRider(String riderId) throws SQLException {
        List<GPSPing> pings = new ArrayList<>();
        String sql = "SELECT * FROM gps_pings WHERE rider_id = ? ORDER BY ping_timestamp ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, riderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                GPSPing ping = new GPSPing(
                    rs.getString("ping_id"),
                    rs.getString("device_id"),
                    riderId,
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getTimestamp("ping_timestamp").toInstant()
                );
                pings.add(ping);
            }
        }
        return pings;
    }

    /**
     * Closes database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Gets database connection for advanced operations
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Tests database connectivity
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() &&
                   connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            return false;
        }
    }
}
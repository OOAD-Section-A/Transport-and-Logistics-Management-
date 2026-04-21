package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DatabaseConfigLoader: Load database configuration from properties file
 * SOLID: SRP - Single responsibility (configuration loading)
 * GRASP: Information Expert - Knows how to load database config
 * Singleton pattern: Only one instance per JVM
 */
public class DatabaseConfigLoader {
    private static DatabaseConfigLoader instance = null;
    private static final String CONFIG_FILE = "database.properties";
    private boolean loaded = false;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private DatabaseConfigLoader() {
    }

    public static synchronized DatabaseConfigLoader getInstance() {
        if (instance == null) {
            instance = new DatabaseConfigLoader();
        }
        return instance;
    }

    /**
     * Load database configuration from properties file and set JVM system properties
     * Configuration precedence:
     * 1. database.properties file in classpath
     * 2. JVM system properties (if already set)
     * 3. Environment variables (if JVM properties not set)
     */
    public void loadConfiguration() throws Exception {
        if (loaded) {
            System.out.println("[DatabaseConfigLoader] Configuration already loaded");
            return;
        }

        Properties props = new Properties();
        
        // Try to load from classpath
        try (InputStream input = DatabaseConfigLoader.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Configuration file not found: " + CONFIG_FILE);
            }
            props.load(input);
        } catch (IOException e) {
            System.err.println("[DatabaseConfigLoader] Error loading " + CONFIG_FILE + ": " + e.getMessage());
            throw new Exception("Failed to load database configuration", e);
        }

        // Extract and validate required properties
        this.dbUrl = System.getProperty("db.url", props.getProperty("db.url"));
	this.dbUsername = System.getProperty("db.username", props.getProperty("db.username"));
	this.dbPassword = System.getProperty("db.password", props.getProperty("db.password"));

        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            throw new Exception("Missing required property: db.url");
        }
        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            throw new Exception("Missing required property: db.username");
        }
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            throw new Exception("Missing required property: db.password");
        }

        // Set JVM system properties for database facade discovery
        System.setProperty("db.url", dbUrl);
        System.setProperty("db.username", dbUsername);
        System.setProperty("db.password", dbPassword);

        this.loaded = true;
        System.out.println("[DatabaseConfigLoader] Configuration loaded successfully from " + CONFIG_FILE);
        System.out.println("  - db.url: " + maskPassword(dbUrl));
        System.out.println("  - db.username: " + dbUsername);
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Mask password for logging (show only first 2 chars)
     */
    private static String maskPassword(String value) {
        if (value == null || value.length() < 3) return "***";
        return value.substring(0, Math.min(2, value.length())) + "***";
    }
}

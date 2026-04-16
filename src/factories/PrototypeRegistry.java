package factories;

import entities.Shipment;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry: PrototypeRegistry
 * CREATIONAL PATTERN: Prototype Pattern
 * Stores prototype shipments for cloning
 * SOLID: SRP - Manages prototype templates
 * Allows cloning of existing shipment templates instead of creating from scratch
 */
public class PrototypeRegistry {
    private static PrototypeRegistry instance;
    private Map<String, Shipment> prototypes = new HashMap<>();

    // Singleton pattern for registry
    private PrototypeRegistry() {
    }

    public static PrototypeRegistry getInstance() {
        if (instance == null) {
            instance = new PrototypeRegistry();
        }
        return instance;
    }

    /**
     * Register a prototype shipment template
     */
    public void registerPrototype(String templateName, Shipment prototype) {
        prototypes.put(templateName, prototype);
    }

    /**
     * Clone a registered prototype by name
     * Creates a deep copy of the prototype
     */
    public Shipment clonePrototype(String templateName) {
        if (!prototypes.containsKey(templateName)) {
            throw new IllegalArgumentException("Prototype not found: " + templateName);
        }
        return prototypes.get(templateName).clone();
    }

    /**
     * Get prototype by name (without cloning)
     */
    public Shipment getPrototype(String templateName) {
        return prototypes.get(templateName);
    }

    /**
     * Check if prototype exists
     */
    public boolean hasPrototype(String templateName) {
        return prototypes.containsKey(templateName);
    }

    /**
     * List all available prototype templates
     */
    public java.util.Set<String> getPrototypeNames() {
        return prototypes.keySet();
    }
}

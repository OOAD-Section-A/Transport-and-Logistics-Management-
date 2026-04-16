package flyweight;

import entities.Carrier;
import java.util.HashMap;
import java.util.Map;

/**
 * Flyweight: CarrierFlyweightFactory
 * STRUCTURAL PATTERN: Flyweight Pattern
 * Reduces memory usage by reusing Carrier objects
 * SOLID: SRP - Single responsibility (carrier flyweight management)
 * 
 * Problem: Creating many Carrier objects wastes memory when most are identical
 * Solution: Share common Carrier instances instead of creating duplicates
 */
public class CarrierFlyweightFactory {
    private static CarrierFlyweightFactory instance;
    private Map<String, Carrier> carrierCache = new HashMap<>();

    // Singleton pattern
    private CarrierFlyweightFactory() {
    }

    public static CarrierFlyweightFactory getInstance() {
        if (instance == null) {
            instance = new CarrierFlyweightFactory();
        }
        return instance;
    }

    /**
     * Get or create a carrier flyweight
     * If carrier with given ID exists, return cached instance
     * Otherwise, create new carrier and cache it
     */
    public Carrier getCarrier(String carrierId, String carrierName, String transportMode, double capacity) {
        if (!carrierCache.containsKey(carrierId)) {
            System.out.println("[FLYWEIGHT] Creating new Carrier flyweight: " + carrierId);
            Carrier newCarrier = new Carrier(carrierId, carrierName, transportMode, capacity);
            carrierCache.put(carrierId, newCarrier);
        } else {
            System.out.println("[FLYWEIGHT] Reusing cached Carrier: " + carrierId);
        }
        return carrierCache.get(carrierId);
    }

    /**
     * Get cached carrier by ID only
     */
    public Carrier getCarrier(String carrierId) {
        return carrierCache.get(carrierId);
    }

    /**
     * Check if carrier is cached
     */
    public boolean hasCarrier(String carrierId) {
        return carrierCache.containsKey(carrierId);
    }

    /**
     * Get total number of cached carriers (memory optimization metric)
     */
    public int getCachedCarrierCount() {
        return carrierCache.size();
    }

    /**
     * Clear cache (use with caution)
     */
    public void clearCache() {
        System.out.println("[FLYWEIGHT] Clearing carrier cache");
        carrierCache.clear();
    }
}

package adapters;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import entities.Shipment;

/**
 * ShipmentMapper: Bidirectional mapping between transport.Shipment and database Shipment
 * SOLID: SRP - Single responsibility (entity mapping)
 * GRASP: Adapter - Adapt between two different entity models
 * 
 * Maps:
 * - transport.Shipment (local) ↔ com.jackfruit.scm.database.model.Shipment (database)
 * 
 * Field Mapping:
 *   shipmentId          →  deliveryId
 *   supplierId          →  customerId
 *   destination         →  deliveryAddress
 *   weight              →  (not in DB model)
 *   status              →  deliveryStatus
 *   createdDate         →  createdAt
 *   deliveryDate        →  deliveryDate
 *   cost                →  deliveryCost
 *   origin              →  (not in DB model)
 *   carrierId           →  (not in DB model)
 */
public class ShipmentMapper {

    /**
     * Convert transport.Shipment to database Shipment object
     * Uses reflection to construct proper database Shipment instances
     */
    public static Object toDatabase(Shipment transportShipment) {
        if (transportShipment == null) {
            return null;
        }

        try {
            // Load the database Shipment class
            Class<?> dbShipmentClass = Class.forName("com.jackfruit.scm.database.model.Shipment");
            
            // Create instance using no-arg constructor
            Object dbShipment = dbShipmentClass.getDeclaredConstructor().newInstance();
            
            // Set fields using reflection and setter methods
            setField(dbShipment, "deliveryId", transportShipment.getShipmentId());
            setField(dbShipment, "orderId", transportShipment.getShipmentId());
            setField(dbShipment, "customerId", transportShipment.getSupplierId());
            setField(dbShipment, "deliveryAddress", transportShipment.getDestination() != null ? transportShipment.getDestination() : "Unknown");
            setField(dbShipment, "deliveryStatus", transportShipment.getStatus() != null ? transportShipment.getStatus() : "Pending");
            setField(dbShipment, "deliveryType", "STANDARD");
            setField(dbShipment, "deliveryDate", convertToLocalDateTime(transportShipment.getDeliveryDate()));
            setField(dbShipment, "deliveryCost", BigDecimal.valueOf(transportShipment.getCost()));
            setField(dbShipment, "assignedAgent", null);
            setField(dbShipment, "warehouseId", "WH-DEFAULT");
            setField(dbShipment, "createdAt", convertToLocalDateTime(transportShipment.getCreatedDate()));
            setField(dbShipment, "updatedAt", convertToLocalDateTime(new Date()));
            
            return dbShipment;
        } catch (Exception e) {
            System.err.println("[ShipmentMapper] Error converting to database format: " + e.getMessage());
            throw new RuntimeException("Failed to map shipment to database format", e);
        }
    }

    /**
     * Convert database Shipment back to transport.Shipment
     * Uses reflection to extract fields from database objects
     */
    public static Shipment fromDatabase(Object dbShipment) {
        if (dbShipment == null) {
            return null;
        }

        try {
            Class<?> clazz = dbShipment.getClass();
            
            String shipmentId = (String) getField(dbShipment, clazz, "deliveryId");
            String customerId = (String) getField(dbShipment, clazz, "customerId");
            String deliveryAddress = (String) getField(dbShipment, clazz, "deliveryAddress");
            String deliveryStatus = (String) getField(dbShipment, clazz, "deliveryStatus");
            LocalDateTime deliveryDate = (LocalDateTime) getField(dbShipment, clazz, "deliveryDate");
            BigDecimal deliveryCost = (BigDecimal) getField(dbShipment, clazz, "deliveryCost");
            LocalDateTime createdAt = (LocalDateTime) getField(dbShipment, clazz, "createdAt");
            
            // Return converted shipment
            return new Shipment(
                shipmentId,
                customerId,
                null,                          // carrierId not in database model
                deliveryAddress,
                0.0,                           // weight not in database model
                deliveryStatus,
                convertToDate(createdAt),
                convertToDate(deliveryDate),
                deliveryCost != null ? deliveryCost.doubleValue() : 0.0,
                null                           // origin not in database model
            );
        } catch (Exception e) {
            System.err.println("[ShipmentMapper] Error converting from database: " + e.getMessage());
            throw new RuntimeException("Failed to map shipment from database", e);
        }
    }

    /**
     * Set a field value using setter method (reflection)
     * Tries setter method first (e.g., setDeliveryId for deliveryId)
     */
    private static void setField(Object obj, String fieldName, Object value) {
        try {
            // Build setter name: capitalize first letter and prepend "set"
            String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            
            // Find the right setter by trying different parameter types
            java.lang.reflect.Method[] methods = obj.getClass().getMethods();
            for (java.lang.reflect.Method m : methods) {
                if (m.getName().equals(setterName) && m.getParameterTypes().length == 1) {
                    m.invoke(obj, value);
                    return;
                }
            }
            
            System.out.println("[ShipmentMapper] Warning: Setter " + setterName + " not found");
        } catch (Exception e) {
            System.out.println("[ShipmentMapper] Warning: Could not set field " + fieldName + ": " + e.getMessage());
        }
    }

    /**
     * Get field value from object using reflection
     */
    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            System.out.println("[ShipmentMapper] Warning: Could not get field " + fieldName);
            return null;
        }
    }

    /**
     * Convert java.util.Date to java.time.LocalDateTime
     */
    private static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return LocalDateTime.now();
        }
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    /**
     * Convert java.time.LocalDateTime to java.util.Date
     */
    private static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return new Date();
        }
        return java.util.Date.from(
            localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );
    }
}

package transport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * FacadeInspector: Diagnostic tool to inspect database facade structure
 * Helps discover correct method signatures and types
 */
public class FacadeInspector {
    public static void main(String[] args) {
        try {
            // Load and inspect SupplyChainDatabaseFacade
            Class<?> facadeClass = Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            System.out.println("=== SupplyChainDatabaseFacade ===");
            inspectCreateShipmentMethod(facadeClass);
            
            // Try to create instance and get logistics facade
            Object facade = facadeClass.getDeclaredConstructor().newInstance();
            Method logisticsMethod = facadeClass.getMethod("logistics");
            Object logisticsFacade = logisticsMethod.invoke(facade);
            
            System.out.println("\n=== LogisticsSubsystemFacade ===");
            inspectCreateShipmentMethod(logisticsFacade.getClass());
            
            // Find the Shipment type
            System.out.println("\n=== Finding Shipment Type ===");
            for (Method m : facadeClass.getMethods()) {
                if (m.getName().equals("createShipment")) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length > 0) {
                        Class<?> shipmentType = params[0];
                        System.out.println("Shipment type: " + shipmentType.getCanonicalName());
                        inspectClass(shipmentType);
                    }
                }
            }
            
            // Close facade if AutoCloseable
            if (facade instanceof AutoCloseable) {
                ((AutoCloseable) facade).close();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void inspectCreateShipmentMethod(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("createShipment")) {
                System.out.println("Method found: " + m.getName());
                System.out.println("  Return type: " + m.getReturnType().getSimpleName());
                System.out.println("  Parameters:");
                for (Parameter p : m.getParameters()) {
                    System.out.println("    - " + p.getType().getCanonicalName() + " " + p.getName());
                }
            }
        }
    }
    
    private static void inspectClass(Class<?> clazz) {
        System.out.println("\nClass Details: " + clazz.getCanonicalName());
        System.out.println("Constructors:");
        try {
            for (java.lang.reflect.Constructor<?> c : clazz.getConstructors()) {
                System.out.println("  public " + c.getName() + "(" + getParamTypes(c) + ")");
            }
            for (java.lang.reflect.Constructor<?> c : clazz.getDeclaredConstructors()) {
                if (!java.lang.reflect.Modifier.isPublic(c.getModifiers())) {
                    System.out.println("  " + c.getName() + "(" + getParamTypes(c) + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("  (no public constructors)");
        }
        
        System.out.println("Fields:");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("  " + f.getType().getSimpleName() + " " + f.getName());
        }
        
        System.out.println("Setter Methods:");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().startsWith("set") && m.getParameterTypes().length == 1) {
                System.out.println("  " + m.getName() + "(" + m.getParameterTypes()[0].getSimpleName() + ")");
            }
        }
    }
    
    private static String getParamTypes(java.lang.reflect.Executable e) {
        Class<?>[] params = e.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(params[i].getSimpleName());
        }
        return sb.toString();
    }
}

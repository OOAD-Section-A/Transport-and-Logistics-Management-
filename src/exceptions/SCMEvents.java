package exceptions;

import java.util.Map;

/**
 * SCM Events - Central event emission system
 */
public class SCMEvents {
    public static void emit(Map<Integer, ExceptionSpec> catalog, 
                           SCMExceptionHandler handler,
                           int id,
                           Severity fallback,
                           String detail,
                           String domain,
                           String defaultMessage) {
        ExceptionSpec spec = catalog.getOrDefault(id, 
            new ExceptionSpec("UNKNOWN", fallback, domain, defaultMessage));
        
        if (handler != null) {
            handler.handle("[" + spec.code() + "] " + spec.description() + " - " + detail, spec.severity());
        } else {
            System.err.println("[" + spec.code() + "] " + spec.description() + " - " + detail);
        }
    }
}

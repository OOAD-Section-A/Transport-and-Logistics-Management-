package exceptions;

import java.util.Map;

/**
 * SCM Events - Central event emission helpers
 */
public final class SCMEvents {
    private SCMEvents() {
        // Utility class
    }

    public static void emit(Map<Integer, ExceptionSpec> catalog,
                            SCMExceptionHandler handler,
                            int id,
                            Severity fallback,
                            String detail,
                            String domain,
                            String defaultMessage) {
        if (handler == null) {
            return;
        }

        ExceptionSpec spec = catalog == null ? null : catalog.get(id);
        Severity severity = (spec != null && spec.severity() != null) ? spec.severity() : fallback;
        String base = spec != null ? spec.description() : defaultMessage;
        String message = (detail == null || detail.isBlank()) ? base : base + " | " + detail;

        if (spec == null || id <= 0) {
            handler.handle("[UNREGISTERED] " + message, severity);
        } else {
            handler.handle("[" + id + "] " + spec.code() + " - " + message, severity);
        }
    }

    public static void fire(
            SCMExceptionHandler handler,
            int exceptionId,
            String message,
            Severity severity,
            String[] knownMessages) {
        if (handler == null) {
            return;
        }
        if (exceptionId > 0 && knownMessages != null && exceptionId <= knownMessages.length) {
            handler.handle("[" + exceptionId + "] " + knownMessages[exceptionId - 1] + ": " + message, severity);
        } else {
            handler.handle("[UNREGISTERED] " + message, severity);
        }
    }
}

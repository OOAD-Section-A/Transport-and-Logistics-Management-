package adapters;

import entities.AlertManager;
import entities.AlertManager.*;

/**
 * AlertManager Adapter
 * Integrates AlertManager with Transport Management System
 * LOW COUPLING: Implements standard interfaces
 */
public class AlertManagerAdapter {
    private final AlertManager.Port alertManager;
    
    public AlertManagerAdapter() {
        // Create with mock implementations
        AlertManager.AlertChannel critical = envelope -> 
            System.out.println("  🚨 [CRITICAL] " + envelope.entityId() + ": " + envelope.message());
        
        AlertManager.AlertChannel major = envelope -> 
            System.out.println("  ⚠️ [MAJOR] " + envelope.entityId() + ": " + envelope.message());
        
        AlertManager.AlertChannel warning = envelope -> 
            System.out.println("  ⚡ [WARNING] " + envelope.entityId() + ": " + envelope.message());
        
        this.alertManager = AlertManager.create(critical, major, warning);
    }
    
    public void emitAlert(String entityId, String process, AlertLevel level, 
                         String message, long elapsed, long sla) {
        AlertEnvelope envelope = new AlertEnvelope(entityId, process, level, 
                                                    message, elapsed, sla);
        alertManager.emit(envelope);
    }
    
    public void registerHandler(exceptions.SCMExceptionHandler handler) {
        alertManager.registerHandler(handler);
    }
}

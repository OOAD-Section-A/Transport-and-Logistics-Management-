package entities;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import exceptions.*;

/**
 * Alert Manager
 * PATTERN: Chain of Responsibility
 * Routes alerts through channels based on severity
 */
public final class AlertManager implements IStateWorkflowExceptionSource {
    public interface Port { 
        void emit(AlertEnvelope envelope); 
        void registerHandler(SCMExceptionHandler handler); 
    }
    
    public interface AlertChannel { 
        void send(AlertEnvelope envelope); 
    }
    
    public enum AlertLevel { CRITICAL, MAJOR, WARNING, INFO }
    
    public record AlertEnvelope(String entityId, String processName, AlertLevel level,
                                String message, long elapsedMs, long slaMs) {}
    
    private static final Map<Integer, ExceptionSpec> CATALOG = Map.of(
        211, new ExceptionSpec("DELIVERY_TIMEOUT", Severity.MAJOR,
                "Management", "Delivery exceeded time window."),
        213, new ExceptionSpec("PARTIAL_DELIVERY", Severity.WARNING,
                "Management", "Only partial delivery recorded.")
    );
    
    private static final class Link {
        private final Predicate<AlertEnvelope> matcher;
        private final AlertChannel channel;
        private Link next;
        
        private Link(Predicate<AlertEnvelope> matcher, AlertChannel channel) {
            this.matcher = Objects.requireNonNull(matcher);
            this.channel = Objects.requireNonNull(channel);
        }
        
        private Link then(Link other) { 
            this.next = other; 
            return other; 
        }
        
        private boolean handle(AlertEnvelope e) {
            if (matcher.test(e)) {
                channel.send(e);
                return true;
            }
            return next != null && next.handle(e);
        }
    }
    
    private final Link chainHead;
    private SCMExceptionHandler handler;
    
    public static Port create(AlertChannel critical, AlertChannel major, AlertChannel warning) {
        AlertManager impl = new AlertManager(critical, major, warning);
        return new Port() {
            @Override public void emit(AlertEnvelope e) { impl.emit(e); }
            @Override public void registerHandler(SCMExceptionHandler h) { impl.registerHandler(h); }
        };
    }
    
    private AlertManager(AlertChannel critical, AlertChannel major, AlertChannel warning) {
        Link crit = new Link(e -> e.level() == AlertLevel.CRITICAL, critical);
        Link maj = crit.then(new Link(e -> e.level() == AlertLevel.MAJOR, major));
        maj.then(new Link(e -> e.level() == AlertLevel.WARNING || e.level() == AlertLevel.INFO, warning));
        this.chainHead = crit;
    }
    
    public void emit(AlertEnvelope envelope) {
        try {
            String normalized = envelope.message().toLowerCase(Locale.ROOT);
            
            if (envelope.elapsedMs() > envelope.slaMs() * 2) {
                fireWorkflowTimeout(211, envelope.processName(), envelope.entityId(), envelope.elapsedMs());
                return;
            }
            
            if (envelope.elapsedMs() > envelope.slaMs()) {
                fireSLABreach(213, envelope.processName(), envelope.entityId(), 
                    envelope.slaMs(), envelope.elapsedMs());
                return;
            }
            
            chainHead.handle(envelope);
        } catch (Exception ex) {
            raise(0, Severity.MINOR, "Alert error: " + ex.getMessage());
        }
    }
    
    public void registerHandler(SCMExceptionHandler h) { this.handler = h; }
    
    private void raise(int id, Severity sev, String detail) {
        SCMEvents.emit(CATALOG, handler, id, sev, detail, "Management", detail);
    }
    
    @Override public void fireInvalidEntityState(int id, String type, String eid, String curr, String req) { }
    @Override public void fireWorkflowTimeout(int id, String workflow, String entity, long elapsedMs) { 
        raise(id, Severity.MAJOR, workflow + " timeout"); 
    }
    @Override public void fireExpiredEntity(int id, String type, String entity, String attr) { }
    @Override public void fireSLABreach(int id, String process, String entity, long slaMs, long actualMs) { 
        raise(id, Severity.WARNING, entity + " SLA breach"); 
    }
}

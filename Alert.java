import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public final class Alert implements IStateWorkflowExceptionSource {
    public interface Port { void emit(AlertEnvelope envelope); void registerHandler(SCMExceptionHandler handler); }
    public interface AlertChannel { void send(AlertEnvelope envelope); }
    public enum AlertLevel { CRITICAL, MAJOR, WARNING, INFO }
    public record AlertEnvelope(String entityId, String processName, AlertLevel level,
                                String message, long elapsedMs, long slaMs) {}
    private static final Map<Integer, ExceptionSpec> CATALOG = Map.of(
            206, new ExceptionSpec("CONTRACT_EXPIRED_ALERT", Severity.WARNING,
                    "Multi-level Pricing", "A pricing contract is at or past its expiry date."),
            211, new ExceptionSpec("DELIVERY_TIMEOUT", Severity.MAJOR,
                    "Real-Time Delivery", "Delivery has exceeded its maximum allowed time window."),
            213, new ExceptionSpec("PARTIAL_DELIVERY_RECORDED", Severity.WARNING,
                    "Real-Time Delivery", "Only part of the order was delivered.")
    );
    private static final class Link {
        private final Predicate<AlertEnvelope> matcher;
        private final AlertChannel channel;
        private Link next;
        private Link(Predicate<AlertEnvelope> matcher, AlertChannel channel) {
            this.matcher = Objects.requireNonNull(matcher, "matcher");
            this.channel = Objects.requireNonNull(channel, "channel");
        }
        private Link then(Link other) { this.next = other; return other; }
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
    public static Port create(AlertChannel criticalChannel, AlertChannel majorChannel, AlertChannel warningChannel) {
        Alert impl = new Alert(criticalChannel, majorChannel, warningChannel);
        return new Port() {
            @Override public void emit(AlertEnvelope envelope) { impl.emit(envelope); }
            @Override public void registerHandler(SCMExceptionHandler handler) { impl.registerHandler(handler); }
        };
    }
    private Alert(AlertChannel criticalChannel, AlertChannel majorChannel, AlertChannel warningChannel) {
        Link critical = new Link(e -> e.level() == AlertLevel.CRITICAL, criticalChannel);
        Link major = critical.then(new Link(e -> e.level() == AlertLevel.MAJOR, majorChannel));
        major.then(new Link(e -> e.level() == AlertLevel.WARNING || e.level() == AlertLevel.INFO, warningChannel));
        this.chainHead = critical;
    }
    public void emit(AlertEnvelope envelope) {
        try {
            String normalizedMessage = envelope.message().toLowerCase(Locale.ROOT);
            if (normalizedMessage.contains("contract expired")) {
                fireExpiredEntity(206, "Contract", envelope.entityId(), "contractExpiry");
                return;
            }
            if (envelope.elapsedMs() > envelope.slaMs() * 2) {
                fireWorkflowTimeout(211, envelope.processName(), envelope.entityId(), envelope.elapsedMs());
                return;
            }
            if (envelope.elapsedMs() > envelope.slaMs()) {
                fireSLABreach(213, envelope.processName(), envelope.entityId(), envelope.slaMs(), envelope.elapsedMs());
                return;
            }
            chainHead.handle(envelope);
        } catch (RuntimeException ex) {
            raise(0, Severity.MINOR, "Unregistered exception in Alert: " + ex.getMessage());
        }
    }
    @Override
    public void registerHandler(SCMExceptionHandler handler) {
        this.handler = handler;
    }
    private void raise(int id, Severity fallback, String detail) {
        SCMEvents.emit(CATALOG, handler, id, fallback, detail,
                "Transport and Logistics Management", "Unregistered alert exception.");
    }
    @Override public void fireInvalidEntityState(int exceptionId, String entityType, String entityId, String currentState, String requiredState) { raise(exceptionId, Severity.MAJOR, entityType + " " + entityId + " state=" + currentState + " required=" + requiredState); }
    @Override public void fireWorkflowTimeout(int exceptionId, String workflowName, String entityId, long elapsedMs) { raise(exceptionId, Severity.MAJOR, "workflow=" + workflowName + " entity=" + entityId + " elapsedMs=" + elapsedMs); }
    @Override public void fireExpiredEntity(int exceptionId, String entityType, String entityId, String expiredAttribute) { raise(exceptionId, Severity.WARNING, "entityType=" + entityType + " entity=" + entityId + " expired=" + expiredAttribute); }
    @Override public void fireSLABreach(int exceptionId, String processName, String entityId, long slaMs, long actualMs) { raise(exceptionId, Severity.WARNING, "process=" + processName + " entity=" + entityId + " slaMs=" + slaMs + " actualMs=" + actualMs); }
}

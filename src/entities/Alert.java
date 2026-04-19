package entities;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import exceptions.*;
import com.scm.factory.SCMExceptionFactory;
import com.scm.subsystems.TransportLogisticsSubsystem;

/**
 * Alert Manager
 * PATTERN: Chain of Responsibility
 * Routes alerts through channels based on severity
 */
public final class Alert implements IStateWorkflowExceptionSource {
    public interface Port { void emit(AlertEnvelope envelope); void registerHandler(SCMExceptionHandler handler); }
    public interface AlertChannel { void send(AlertEnvelope envelope); }
    public enum AlertLevel { CRITICAL, MAJOR, WARNING, INFO }
    public record AlertEnvelope(String entityId, String processName, AlertLevel level,
                                String message, long elapsedMs, long slaMs) {}
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
    private final TransportLogisticsSubsystem exceptions = TransportLogisticsSubsystem.INSTANCE;
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
                fireSLABreach(463, envelope.processName(), envelope.entityId(), envelope.slaMs(), envelope.elapsedMs());
                return;
            }
            if (envelope.elapsedMs() > envelope.slaMs() * 2) {
                fireWorkflowTimeout(463, envelope.processName(), envelope.entityId(), envelope.elapsedMs());
                return;
            }
            if (envelope.elapsedMs() > envelope.slaMs()) {
                fireSLABreach(463, envelope.processName(), envelope.entityId(), envelope.slaMs(), envelope.elapsedMs());
                return;
            }
            chainHead.handle(envelope);
        } catch (RuntimeException ex) {
            com.scm.handler.SCMExceptionHandler.INSTANCE.handle(
                SCMExceptionFactory.createUnregistered("Transport and Logistics Management", "Alert failed: " + ex.getMessage())
            );
            return;
        }
    }
    public void registerHandler(SCMExceptionHandler handler) {
    }
    private void raise(long elapsedMs, String entityId) {
        exceptions.onCriticalTransitDelay(entityId, elapsedMs);
    }

    @Override public void fireInvalidEntityState(int exceptionId, String entityType, String entityId, String currentState, String requiredState) { }
    @Override public void fireWorkflowTimeout(int exceptionId, String workflowName, String entityId, long elapsedMs) { raise(elapsedMs, entityId); }
    @Override public void fireExpiredEntity(int exceptionId, String entityType, String entityId, String expiredAttribute) { }
    @Override public void fireSLABreach(int exceptionId, String processName, String entityId, long slaMs, long actualMs) { raise(actualMs - slaMs, entityId); }
}

package exceptions;

/**
 * State Workflow Exception Source
 */
public interface IStateWorkflowExceptionSource {
    void fireInvalidEntityState(int exceptionId, String entityType, String entityId, String currentState, String requiredState);
    void fireWorkflowTimeout(int exceptionId, String workflowName, String entityId, long elapsedMs);
    void fireExpiredEntity(int exceptionId, String entityType, String entityId, String expiredAttribute);
    void fireSLABreach(int exceptionId, String processName, String entityId, long slaMs, long actualMs);
}

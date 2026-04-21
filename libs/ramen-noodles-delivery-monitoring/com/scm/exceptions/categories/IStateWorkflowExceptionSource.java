package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IStateWorkflowExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireInvalidEntityState(int exceptionId, String entityType, String entityId, String currentState, String requiredState);
    void fireWorkflowTimeout(int exceptionId, String workflowName, String entityId, long elapsedMs);
    void fireExpiredEntity(int exceptionId, String entityType, String entityId, String expiredAttribute);
    void fireSLABreach(int exceptionId, String processName, String entityId, long slaMs, long actualMs);
}
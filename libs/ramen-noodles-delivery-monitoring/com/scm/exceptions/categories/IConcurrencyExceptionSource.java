package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IConcurrencyExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireDeadlock(int exceptionId, String entityType, String entityId, String operation);
    void fireRollbackFailed(int exceptionId, String entityType, String entityId);
    void fireConflict(int exceptionId, String entityType, String entityId, String conflictReason);
    void fireDuplicateSubmission(int exceptionId, String entityType, String entityId);
}
package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IDataIntegrityExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireDuplicateRecord(int exceptionId, String entityType, String duplicateKey);
    void fireReferentialViolation(int exceptionId, String childEntity, String parentEntity, String key);
    void fireDataInconsistency(int exceptionId, String entityType, String entityId, String description);
    void fireWriteFailure(int exceptionId, String entityType, String entityId, String operation);
}
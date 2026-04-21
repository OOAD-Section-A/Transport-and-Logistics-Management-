package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IResourceAvailabilityExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireResourceNotFound(int exceptionId, String resourceType, String resourceId);
    void fireResourceExhausted(int exceptionId, String resourceType, String resourceId, int requested, int available);
    void fireResourceBlocked(int exceptionId, String resourceType, String resourceId, String reason);
    void fireCapacityExceeded(int exceptionId, String resourceType, String resourceId, int limit);
}
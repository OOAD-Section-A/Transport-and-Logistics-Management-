package exceptions;

/**
 * Resource Availability Exception Source
 */
public interface IResourceAvailabilityExceptionSource {
    void fireResourceNotFound(int exceptionId, String resourceType, String resourceId);
    void fireResourceExhausted(int exceptionId, String resourceType, String resourceId, int requested, int available);
    void fireResourceBlocked(int exceptionId, String resourceType, String resourceId, String reason);
    void fireCapacityExceeded(int exceptionId, String resourceType, String resourceId, int limit);
}

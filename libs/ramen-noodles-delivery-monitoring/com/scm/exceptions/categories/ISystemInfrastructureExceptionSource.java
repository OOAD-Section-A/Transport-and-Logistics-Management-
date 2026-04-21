package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface ISystemInfrastructureExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void firePlatformFailure(int exceptionId, String component, String operation, String detail);
    void fireProcessingError(int exceptionId, String processName, String entityId, String reason);
    void firePerformanceDegradation(int exceptionId, String component, long thresholdMs, long actualMs);
    void fireRenderOrFormatError(int exceptionId, String component, String format, String reason);
}
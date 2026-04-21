package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IInputValidationExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireInvalidInput(int exceptionId, String fieldName, String receivedValue, String rule);
    void fireInvalidReference(int exceptionId, String refType, String refValue);
    void fireConfigurationError(int exceptionId, String configKey, String reason);
    void fireInvalidStateTransition(int exceptionId, String entityId, String fromState, String toState);
    void fireValidationFailure(int exceptionId, String entityType, String entityId, String reason);
}
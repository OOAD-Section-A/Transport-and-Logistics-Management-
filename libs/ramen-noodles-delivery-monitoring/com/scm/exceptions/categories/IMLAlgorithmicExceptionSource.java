package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IMLAlgorithmicExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireModelFailure(int exceptionId, String modelName, String reason);
    void fireModelDegradation(int exceptionId, String modelName, String metric, double threshold, double actual);
    void fireMissingInputData(int exceptionId, String modelName, String missingDataType, String affectedPeriod);
    void fireAlgorithmicAlert(int exceptionId, String processName, String entityId, String detail);
}
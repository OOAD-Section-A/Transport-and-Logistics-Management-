package exceptions;

/**
 * ML/Algorithmic Exception Source
 */
public interface IMLAlgorithmicExceptionSource {
    void fireModelFailure(int exceptionId, String modelName, String reason);
    void fireModelDegradation(int exceptionId, String modelName, String metric, double threshold, double actual);
    void fireMissingInputData(int exceptionId, String modelName, String missingDataType, String affectedPeriod);
    void fireAlgorithmicAlert(int exceptionId, String processName, String entityId, String detail);
}

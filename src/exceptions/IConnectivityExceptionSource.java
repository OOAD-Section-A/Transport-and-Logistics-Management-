package exceptions;

/**
 * Connectivity Exception Source
 */
public interface IConnectivityExceptionSource {
    void fireConnectionFailed(int exceptionId, String targetSystem, String host);
    void fireTimeout(int exceptionId, String targetSystem, int timeoutMs);
    void fireServiceUnavailable(int exceptionId, String targetSystem, String reason);
    void firePartialConnectivity(int exceptionId, String targetSystem, String degradedCapability);
}

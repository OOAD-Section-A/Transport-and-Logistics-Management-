package com.scm.exceptions.categories;
import com.scm.exceptions.SCMExceptionHandler;
public interface IConnectivityExceptionSource {
    void registerHandler(SCMExceptionHandler handler);
    void fireConnectionFailed(int exceptionId, String targetSystem, String host);
    void fireTimeout(int exceptionId, String targetSystem, int timeoutMs);
    void fireServiceUnavailable(int exceptionId, String targetSystem, String reason);
    void firePartialConnectivity(int exceptionId, String targetSystem, String degradedCapability);
}
package exceptions;

/**
 * Exception Handler Interface for SCM System
 */
public interface SCMExceptionHandler {
    void handle(String message, Severity severity);
}

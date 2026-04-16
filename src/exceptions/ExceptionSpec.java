package exceptions;

/**
 * Exception Specification
 */
public record ExceptionSpec(
    String code,
    Severity severity,
    String domain,
    String description
) {}

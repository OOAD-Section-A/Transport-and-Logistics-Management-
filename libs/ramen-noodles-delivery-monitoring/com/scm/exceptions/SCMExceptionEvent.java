package com.scm.exceptions;
import java.time.Instant;
public class SCMExceptionEvent {
    private final int exceptionId;
    private final String exceptionName;
    private final Severity severity;
    private final String subsystem;
    private final String errorMessage;
    private final Instant timestamp;
    private final String detail;
    public SCMExceptionEvent(int exceptionId, String exceptionName, Severity severity, String subsystem, String errorMessage, String detail) {
        this.exceptionId = exceptionId; this.exceptionName = exceptionName; this.severity = severity;
        this.subsystem = subsystem; this.errorMessage = errorMessage; this.detail = detail; this.timestamp = Instant.now();
    }
    public int getExceptionId() { return exceptionId; }
    public String getExceptionName() { return exceptionName; }
    public Severity getSeverity() { return severity; }
    public String getSubsystem() { return subsystem; }
    public String getErrorMessage() { return errorMessage; }
    public String getDetail() { return detail; }
    public Instant getTimestamp() { return timestamp; }
}
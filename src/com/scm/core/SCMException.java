package com.scm.core;
public class SCMException extends Exception {
    public SCMException(String message) {
        super(message);
    }
    public String getExceptionId() { return "E-MOCK-123"; }
}

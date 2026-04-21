package com.scm.handler;
import com.scm.core.SCMException;

public enum SCMExceptionHandler {
    INSTANCE;
    public void handle(SCMException e) {
        System.out.println("[MOCK SCM LOG] " + e.getMessage());
    }
}

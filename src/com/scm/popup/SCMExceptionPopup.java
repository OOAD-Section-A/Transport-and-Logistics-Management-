package com.scm.popup;
import com.scm.core.SCMException;

public class SCMExceptionPopup {
    private SCMExceptionPopup() {} // They use reflection to instantiate this
    public void show(SCMException e) {
        System.out.println("[MOCK SCM POPUP] " + e.getMessage());
    }
}

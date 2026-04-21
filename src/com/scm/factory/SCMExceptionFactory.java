package com.scm.factory;
import com.scm.core.SCMException;
import com.scm.core.Severity;

public class SCMExceptionFactory {
    public static SCMException create(int id, String name, String subsystem, String msg, Severity sev) {
        return new SCMException("[" + name + "] " + msg);
    }
    public static SCMException createUnregistered(String subsystem, String msg) {
        return new SCMException("[Unregistered] " + msg);
    }
}

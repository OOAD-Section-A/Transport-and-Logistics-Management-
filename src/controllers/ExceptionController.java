package controllers;

import com.scm.core.SCMException;
import com.scm.handler.SCMExceptionHandler;

/**
 * REST Controller for Exception Handling APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class ExceptionController {

    /**
     * POST /exceptions
     * Reports an exception.
     */
    public String reportException(SCMException exception) {
        SCMExceptionHandler.INSTANCE.handle(exception);
        return "Exception ID: " + exception.getExceptionId();
    }
}
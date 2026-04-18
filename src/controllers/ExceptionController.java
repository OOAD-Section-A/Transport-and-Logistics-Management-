package controllers;

import exceptions.SCMException;
import services.TransportService;

/**
 * REST Controller for Exception Handling APIs.
 * Assumes Spring Boot framework for actual REST implementation.
 */
public class ExceptionController {

    private TransportService transportService;

    public ExceptionController(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * POST /exceptions
     * Reports an exception.
     */
    public String reportException(SCMException exception) {
        return transportService.reportException(exception);
    }
}
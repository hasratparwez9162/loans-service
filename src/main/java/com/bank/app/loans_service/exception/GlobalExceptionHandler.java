package com.bank.app.loans_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle all exceptions.
     * @param ex The exception that was thrown.
     * @param request The web request during which the exception was thrown.
     * @return A response entity with an error message and HTTP status code 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

    /**
     * Handle ResourceNotFoundException.
     * @param ex The exception that was thrown.
     * @param request The web request during which the exception was thrown.
     * @return A response entity with an error message and HTTP status code 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("ResourceNotFoundException: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + ex.getMessage());
    }
}
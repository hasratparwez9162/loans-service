package com.bank.app.loans_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        ArrayList st = new ArrayList(List.of(ex.getStackTrace())) ;
        return new ResponseEntity<>("An error occurred: " + ex.getMessage()+ "\n"+ st +"\n" + ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

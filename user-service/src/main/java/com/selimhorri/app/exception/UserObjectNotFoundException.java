package com.selimhorri.app.exception;

public class UserObjectNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public UserObjectNotFoundException(String message) {
        super(message);
    }
    
    public UserObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 
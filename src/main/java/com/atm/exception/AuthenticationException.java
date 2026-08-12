package com.atm.exception;

/**
 * Exception thrown when authentication/login operations fail.
 */
public class AuthenticationException extends ATMException {
    public AuthenticationException(String message) {
        super(message);
    }
}

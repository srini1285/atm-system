package com.atm.exception;

/**
 * Exception thrown when an operation is invalid or cannot be performed.
 */
public class InvalidOperationException extends ATMException {
    public InvalidOperationException(String message) {
        super(message);
    }
}

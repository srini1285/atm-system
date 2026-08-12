package com.atm.exception;

/**
 * Base exception class for ATM-related errors.
 * All ATM exceptions extend this class for unified error handling.
 */
public class ATMException extends Exception {
    public ATMException(String message) {
        super(message);
    }

    public ATMException(String message, Throwable cause) {
        super(message, cause);
    }
}

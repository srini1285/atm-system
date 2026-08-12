package com.atm.exception;

/**
 * Exception thrown when customer is not found in the system.
 */
public class CustomerNotFoundException extends ATMException {
    public CustomerNotFoundException(String customerName) {
        super("Customer '" + customerName + "' not found in the system.");
    }
}

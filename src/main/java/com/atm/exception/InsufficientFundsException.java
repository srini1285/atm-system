package com.atm.exception;

/**
 * Exception thrown when an account has insufficient funds for an operation.
 * Note: This is mainly used for withdraw operations. Transfers can exceed balance (creating debt).
 */
public class InsufficientFundsException extends ATMException {
    private double balance;
    private double requestedAmount;

    public InsufficientFundsException(double requestedAmount, double balance) {
        super(String.format("Insufficient funds. Requested: $%.2f, Available: $%.2f", 
              requestedAmount, balance));
        this.requestedAmount = requestedAmount;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }
}

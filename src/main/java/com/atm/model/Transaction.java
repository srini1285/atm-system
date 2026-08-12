package com.atm.model;

import java.time.LocalDateTime;

/**
 * Represents a transaction record in the ATM system.
 * Tracks deposits, withdrawals, and transfers.
 */
public class Transaction {
    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,
        TRANSFER_IN,
        DEBT_SETTLEMENT
    }

    private final String customerName;
    private final Type type;
    private final long amountCents; // Amount in cents
    private final String relatedCustomer; // For transfers/settlements
    private final LocalDateTime timestamp;
    private final String description;

    /**
     * Creates a new transaction record.
     *
     * @param customerName The customer performing the transaction
     * @param type The type of transaction
     * @param amountCents The amount in cents
     * @param relatedCustomer The other party (for transfers/settlements)
     * @param description Additional description
     */
    public Transaction(String customerName, Type type, long amountCents, 
                      String relatedCustomer, String description) {
        this.customerName = customerName;
        this.type = type;
        this.amountCents = amountCents;
        this.relatedCustomer = relatedCustomer;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getCustomerName() {
        return customerName;
    }

    public Type getType() {
        return type;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public double getAmount() {
        return amountCents / 100.0;
    }

    public String getRelatedCustomer() {
        return relatedCustomer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s $%.2f %s",
                timestamp, customerName, type, getAmount(),
                relatedCustomer != null ? "(" + relatedCustomer + ")" : "");
    }
}

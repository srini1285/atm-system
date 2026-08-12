package com.atm.model;

import java.time.LocalDateTime;

/**
 * Represents a debt obligation between two customers.
 */
public class Debt {
    private final String debtor; // Person who owes
    private final String creditor; // Person owed to
    private long amountCents; // Amount in cents
    private final LocalDateTime createdAt;

    /**
     * Creates a new debt record.
     *
     * @param debtor The customer who owes
     * @param creditor The customer owed to
     * @param amountCents The debt amount in cents
     */
    public Debt(String debtor, String creditor, long amountCents) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amountCents = amountCents;
        this.createdAt = LocalDateTime.now();
    }

    public String getDebtor() {
        return debtor;
    }

    public String getCreditor() {
        return creditor;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public double getAmount() {
        return amountCents / 100.0;
    }

    public void setAmountCents(long cents) {
        this.amountCents = cents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s owes %s $%.2f (since %s)", debtor, creditor, getAmount(), createdAt);
    }
}

package com.atm.model;

import java.util.*;

/**
 * Represents a customer account in the ATM system.
 * Tracks balance, debts owed to others, and debts owed from others.
 */
public class Customer {
    private final String name;
    private long balanceCents; // Balance in cents for precision
    private final Map<String, Long> debtsOwedTo = new HashMap<>(); // Debt to other customers
    private final Map<String, Long> debtsOwedFrom = new HashMap<>(); // Debt from other customers
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Creates a new customer with zero balance.
     *
     * @param name The customer's name
     */
    public Customer(String name) {
        this.name = name;
        this.balanceCents = 0;
    }

    /**
     * Gets the customer's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the available balance in cents.
     */
    public long getBalanceCents() {
        return balanceCents;
    }

    /**
     * Gets the available balance in dollars.
     */
    public double getBalance() {
        return balanceCents / 100.0;
    }

    /**
     * Sets the available balance in cents.
     */
    public void setBalanceCents(long cents) {
        this.balanceCents = cents;
    }

    /**
     * Gets map of debts owed to other customers.
     * Key: customer name, Value: amount in cents
     */
    public Map<String, Long> getDebtsOwedTo() {
        return new HashMap<>(debtsOwedTo);
    }

    /**
     * Gets map of debts owed from other customers.
     * Key: customer name, Value: amount in cents
     */
    public Map<String, Long> getDebtsOwedFrom() {
        return new HashMap<>(debtsOwedFrom);
    }

    /**
     * Checks if this customer has any debts owed to others.
     */
    public boolean hasDebtsOwedTo() {
        return !debtsOwedTo.isEmpty();
    }

    /**
     * Checks if this customer has any debts owed from others.
     */
    public boolean hasDebtsOwedFrom() {
        return !debtsOwedFrom.isEmpty();
    }

    /**
     * Adds a debt owed to another customer.
     *
     * @param creditorName The customer this customer owes to
     * @param amountCents Amount in cents
     */
    public void addDebtOwedTo(String creditorName, long amountCents) {
        debtsOwedTo.put(creditorName, debtsOwedTo.getOrDefault(creditorName, 0L) + amountCents);
    }

    /**
     * Reduces a debt owed to another customer.
     *
     * @param creditorName The customer this customer owes to
     * @param amountCents Amount in cents to reduce
     */
    public void reduceDebtOwedTo(String creditorName, long amountCents) {
        Long currentDebt = debtsOwedTo.getOrDefault(creditorName, 0L);
        if (currentDebt <= amountCents) {
            debtsOwedTo.remove(creditorName);
        } else {
            debtsOwedTo.put(creditorName, currentDebt - amountCents);
        }
    }

    /**
     * Gets total debt owed to all customers in cents.
     */
    public long getTotalDebtOwedCents() {
        return debtsOwedTo.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * Gets total debt owed from all customers in cents.
     */
    public long getTotalDebtOwedFromCents() {
        return debtsOwedFrom.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * Adds a debt owed from another customer.
     *
     * @param debtorName The customer who owes this customer
     * @param amountCents Amount in cents
     */
    public void addDebtOwedFrom(String debtorName, long amountCents) {
        debtsOwedFrom.put(debtorName, debtsOwedFrom.getOrDefault(debtorName, 0L) + amountCents);
    }

    /**
     * Reduces a debt owed from another customer.
     *
     * @param debtorName The customer who owed this customer
     * @param amountCents Amount in cents to reduce
     */
    public void reduceDebtOwedFrom(String debtorName, long amountCents) {
        Long currentDebt = debtsOwedFrom.getOrDefault(debtorName, 0L);
        if (currentDebt <= amountCents) {
            debtsOwedFrom.remove(debtorName);
        } else {
            debtsOwedFrom.put(debtorName, currentDebt - amountCents);
        }
    }

    /**
     * Records a transaction in the customer's history.
     */
    public void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Gets the customer's transaction history.
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", balance=$" + String.format("%.2f", getBalance()) +
                ", debtsOwedTo=" + debtsOwedTo +
                ", debtsOwedFrom=" + debtsOwedFrom +
                '}';
    }
}

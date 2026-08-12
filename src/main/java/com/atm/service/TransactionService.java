package com.atm.service;

import com.atm.model.Transaction;
import java.util.*;

/**
 * Service for handling transaction recording and retrieval.
 * Maintains a log of all transactions in the system.
 */
public class TransactionService {
    private final List<Transaction> allTransactions = new ArrayList<>();

    /**
     * Records a transaction.
     *
     * @param transaction The transaction to record
     */
    public void recordTransaction(Transaction transaction) {
        allTransactions.add(transaction);
    }

    /**
     * Gets all transactions for a specific customer.
     *
     * @param customerName The customer's name
     * @return List of transactions for this customer
     */
    public List<Transaction> getTransactionsByCustomer(String customerName) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (t.getCustomerName().equals(customerName)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Gets all transactions in the system.
     *
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(allTransactions);
    }

    /**
     * Clears all transaction records.
     */
    public void clear() {
        allTransactions.clear();
    }

    /**
     * Gets the count of all transactions.
     *
     * @return Number of transactions
     */
    public int getTransactionCount() {
        return allTransactions.size();
    }
}

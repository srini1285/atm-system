package com.atm.service;

import com.atm.exception.*;
import com.atm.model.Customer;
import com.atm.model.Transaction;
import com.atm.repository.CustomerRepository;
import com.atm.util.CurrencyFormatter;

import java.util.*;

/**
 * Core ATM service that handles all banking operations.
 * Manages customer login/logout, deposits, withdrawals, and transfers.
 */
public class ATMService {
    private final CustomerRepository repository;
    private final TransactionService transactionService;
    private Customer currentCustomer;

    /**
     * Creates a new ATM service with in-memory customer storage.
     */
    public ATMService() {
        this.repository = new CustomerRepository();
        this.transactionService = new TransactionService();
    }

    /**
     * Logs in a customer, creating the account if it doesn't exist.
     *
     * @param customerName The name of the customer to login
     * @return The logged-in customer
     * @throws InvalidOperationException if name is empty or null
     */
    public Customer login(String customerName) throws InvalidOperationException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidOperationException("Customer name cannot be empty.");
        }

        currentCustomer = repository.findOrCreate(customerName);
        return currentCustomer;
    }

    /**
     * Logs out the current customer.
     *
     * @throws AuthenticationException if no customer is logged in
     */
    public void logout() throws AuthenticationException {
        if (currentCustomer == null) {
            throw new AuthenticationException("No customer currently logged in.");
        }
        currentCustomer = null;
    }

    /**
     * Gets the currently logged-in customer.
     *
     * @return The current customer
     * @throws AuthenticationException if no customer is logged in
     */
    public Customer getCurrentCustomer() throws AuthenticationException {
        if (currentCustomer == null) {
            throw new AuthenticationException("You must login first.");
        }
        return currentCustomer;
    }

    /**
     * Checks if a customer is currently logged in.
     */
    public boolean isLoggedIn() {
        return currentCustomer != null;
    }

    /**
     * Deposits funds into the current customer's account.
     * If the customer has outstanding debts, the deposit is applied to settle debts first.
     *
     * @param amountCents The amount to deposit in cents
     * @return The updated balance after deposit
     * @throws AuthenticationException if not logged in
     * @throws InvalidOperationException if amount is invalid
     */
    public long deposit(long amountCents) throws AuthenticationException, InvalidOperationException {
        Customer customer = getCurrentCustomer();

        if (amountCents <= 0) {
            throw new InvalidOperationException("Deposit amount must be positive.");
        }

        long remainingDeposit = amountCents;

        // Settle debts first
        Map<String, Long> debts = new HashMap<>(customer.getDebtsOwedTo());
        for (Map.Entry<String, Long> entry : debts.entrySet()) {
            String creditorName = entry.getKey();
            long debtAmount = entry.getValue();

            long paymentAmount = Math.min(remainingDeposit, debtAmount);
            if (paymentAmount > 0) {
                remainingDeposit -= paymentAmount;
                customer.reduceDebtOwedTo(creditorName, paymentAmount);

                // Update creditor's record
                Customer creditor = repository.findByName(creditorName).orElse(null);
                if (creditor != null) {
                    creditor.reduceDebtOwedFrom(customer.getName(), paymentAmount);
                    creditor.recordTransaction(new Transaction(
                            creditor.getName(),
                            Transaction.Type.DEBT_SETTLEMENT,
                            paymentAmount,
                            customer.getName(),
                            "Received payment from " + customer.getName()
                    ));
                }

                customer.recordTransaction(new Transaction(
                        customer.getName(),
                        Transaction.Type.DEBT_SETTLEMENT,
                        paymentAmount,
                        creditorName,
                        "Paid debt to " + creditorName
                ));
            }
        }

        // Add remaining to balance
        customer.setBalanceCents(customer.getBalanceCents() + remainingDeposit);
        customer.recordTransaction(new Transaction(
                customer.getName(),
                Transaction.Type.DEPOSIT,
                amountCents,
                null,
                "Deposit"
        ));

        repository.save(customer);
        return customer.getBalanceCents();
    }

    /**
     * Withdraws funds from the current customer's account.
     *
     * @param amountCents The amount to withdraw in cents
     * @return The updated balance after withdrawal
     * @throws AuthenticationException if not logged in
     * @throws InvalidOperationException if amount is invalid
     * @throws InsufficientFundsException if account has insufficient funds
     */
    public long withdraw(long amountCents) throws AuthenticationException, InvalidOperationException,
            InsufficientFundsException {
        Customer customer = getCurrentCustomer();

        if (amountCents <= 0) {
            throw new InvalidOperationException("Withdrawal amount must be positive.");
        }

        if (customer.getBalanceCents() < amountCents) {
            throw new InsufficientFundsException(amountCents, customer.getBalanceCents());
        }

        customer.setBalanceCents(customer.getBalanceCents() - amountCents);
        customer.recordTransaction(new Transaction(
                customer.getName(),
                Transaction.Type.WITHDRAWAL,
                amountCents,
                null,
                "Withdrawal"
        ));

        repository.save(customer);
        return customer.getBalanceCents();
    }

    /**
     * Transfers funds from the current customer to a target customer.
     * If the sender doesn't have enough balance, creates a debt obligation.
     *
     * @param targetName The name of the recipient
     * @param amountCents The amount to transfer in cents
     * @return The updated balance of the sender after transfer
     * @throws AuthenticationException if not logged in
     * @throws InvalidOperationException if operation is invalid (e.g., transfer to self, invalid amount)
     */
    public long transfer(String targetName, long amountCents) throws AuthenticationException,
            InvalidOperationException {
        Customer sender = getCurrentCustomer();

        if (targetName == null || targetName.trim().isEmpty()) {
            throw new InvalidOperationException("Target customer name cannot be empty.");
        }

        if (amountCents <= 0) {
            throw new InvalidOperationException("Transfer amount must be positive.");
        }

        if (sender.getName().equalsIgnoreCase(targetName)) {
            throw new InvalidOperationException("Cannot transfer to yourself.");
        }

        Customer recipient = repository.findOrCreate(targetName);

        // Determine how much can be transferred from balance
        long transferFromBalance = Math.min(amountCents, sender.getBalanceCents());
        long debtCreated = amountCents - transferFromBalance;

        // Transfer from balance
        if (transferFromBalance > 0) {
            sender.setBalanceCents(sender.getBalanceCents() - transferFromBalance);
            recipient.setBalanceCents(recipient.getBalanceCents() + transferFromBalance);
        }

        // Create debt if necessary
        if (debtCreated > 0) {
            sender.addDebtOwedTo(targetName, debtCreated);
            recipient.addDebtOwedFrom(sender.getName(), debtCreated);
        }

        // Record transactions
        sender.recordTransaction(new Transaction(
                sender.getName(),
                Transaction.Type.TRANSFER_OUT,
                amountCents,
                targetName,
                "Transfer to " + targetName
        ));

        recipient.recordTransaction(new Transaction(
                recipient.getName(),
                Transaction.Type.TRANSFER_IN,
                transferFromBalance,
                sender.getName(),
                "Transfer from " + sender.getName()
        ));

        repository.save(sender);
        repository.save(recipient);

        return sender.getBalanceCents();
    }

    /**
     * Gets the current customer's balance information.
     *
     * @return Formatted balance information
     * @throws AuthenticationException if not logged in
     */
    public String getBalanceInfo() throws AuthenticationException {
        Customer customer = getCurrentCustomer();
        StringBuilder sb = new StringBuilder();
        sb.append("Your balance is ").append(CurrencyFormatter.formatCents(customer.getBalanceCents()));

        if (customer.hasDebtsOwedTo()) {
            sb.append("\n");
            Map<String, Long> debts = customer.getDebtsOwedTo();
            for (String creditor : debts.keySet()) {
                long debt = debts.get(creditor);
                sb.append("Owed ").append(CurrencyFormatter.formatCents(debt))
                        .append(" to ").append(creditor).append("\n");
            }
            sb.setLength(sb.length() - 1); // Remove trailing newline
        }

        if (customer.hasDebtsOwedFrom()) {
            if (customer.hasDebtsOwedTo()) {
                sb.append("\n");
            } else {
                sb.append("\n");
            }
            Map<String, Long> debts = customer.getDebtsOwedFrom();
            for (String debtor : debts.keySet()) {
                long debt = debts.get(debtor);
                sb.append("Owed ").append(CurrencyFormatter.formatCents(debt))
                        .append(" from ").append(debtor).append("\n");
            }
            sb.setLength(sb.length() - 1); // Remove trailing newline
        }

        return sb.toString();
    }
}

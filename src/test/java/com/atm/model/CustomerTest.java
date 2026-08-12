package com.atm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Customer model class.
 */
public class CustomerTest {
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer("Alice");
    }

    @Test
    public void testCustomerCreation() {
        assertEquals("Alice", customer.getName());
        assertEquals(0, customer.getBalanceCents());
        assertEquals(0.0, customer.getBalance());
        assertFalse(customer.hasDebtsOwedTo());
        assertFalse(customer.hasDebtsOwedFrom());
    }

    @Test
    public void testSetBalance() {
        customer.setBalanceCents(10000); // $100
        assertEquals(10000, customer.getBalanceCents());
        assertEquals(100.0, customer.getBalance());
    }

    @Test
    public void testAddDebtOwedTo() {
        customer.addDebtOwedTo("Bob", 5000); // $50
        assertTrue(customer.hasDebtsOwedTo());
        assertEquals(5000, customer.getDebtsOwedTo().get("Bob"));
    }

    @Test
    public void testAddMultipleDebtsOwedTo() {
        customer.addDebtOwedTo("Bob", 5000);
        customer.addDebtOwedTo("Charlie", 3000);
        customer.addDebtOwedTo("Bob", 2000);

        assertEquals(7000, customer.getDebtsOwedTo().get("Bob")); // Accumulated
        assertEquals(3000, customer.getDebtsOwedTo().get("Charlie"));
        assertEquals(10000, customer.getTotalDebtOwedCents());
    }

    @Test
    public void testReduceDebtOwedTo() {
        customer.addDebtOwedTo("Bob", 5000);
        customer.reduceDebtOwedTo("Bob", 2000);

        assertEquals(3000, customer.getDebtsOwedTo().get("Bob"));
    }

    @Test
    public void testReduceDebtOwedToCompletely() {
        customer.addDebtOwedTo("Bob", 5000);
        customer.reduceDebtOwedTo("Bob", 5000);

        assertFalse(customer.hasDebtsOwedTo());
        assertNull(customer.getDebtsOwedTo().get("Bob"));
    }

    @Test
    public void testReduceDebtOwedToMoreThanAmount() {
        customer.addDebtOwedTo("Bob", 5000);
        customer.reduceDebtOwedTo("Bob", 10000); // Try to reduce more than debt

        assertFalse(customer.hasDebtsOwedTo());
        assertNull(customer.getDebtsOwedTo().get("Bob"));
    }

    @Test
    public void testAddDebtOwedFrom() {
        customer.addDebtOwedFrom("Bob", 5000);
        assertTrue(customer.hasDebtsOwedFrom());
        assertEquals(5000, customer.getDebtsOwedFrom().get("Bob"));
    }

    @Test
    public void testRecordTransaction() {
        Transaction transaction = new Transaction(
                "Alice",
                Transaction.Type.DEPOSIT,
                10000,
                null,
                "Test deposit"
        );
        customer.recordTransaction(transaction);

        assertEquals(1, customer.getTransactions().size());
        assertEquals(transaction, customer.getTransactions().get(0));
    }
}

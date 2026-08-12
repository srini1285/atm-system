package com.atm.service;

import com.atm.exception.*;
import com.atm.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ATMService class.
 */
public class ATMServiceTest {
    private ATMService atmService;

    @BeforeEach
    public void setUp() {
        atmService = new ATMService();
    }

    @Test
    public void testLogin() throws Exception {
        Customer customer = atmService.login("Alice");
        assertEquals("Alice", customer.getName());
        assertEquals(0, customer.getBalanceCents());
        assertTrue(atmService.isLoggedIn());
    }

    @Test
    public void testLoginWithEmptyName() {
        assertThrows(InvalidOperationException.class, () -> atmService.login(""));
    }

    @Test
    public void testLoginWithNullName() {
        assertThrows(InvalidOperationException.class, () -> atmService.login(null));
    }

    @Test
    public void testLogout() throws Exception {
        atmService.login("Alice");
        atmService.logout();
        assertFalse(atmService.isLoggedIn());
    }

    @Test
    public void testLogoutWithoutLogin() {
        assertThrows(AuthenticationException.class, () -> atmService.logout());
    }

    @Test
    public void testDeposit() throws Exception {
        atmService.login("Alice");
        long newBalance = atmService.deposit(10000); // $100
        assertEquals(10000, newBalance);
    }

    @Test
    public void testDepositWithoutLogin() {
        assertThrows(AuthenticationException.class, () -> atmService.deposit(10000));
    }

    @Test
    public void testDepositNegativeAmount() throws Exception {
        atmService.login("Alice");
        assertThrows(InvalidOperationException.class, () -> atmService.deposit(-1000));
    }

    @Test
    public void testDepositZeroAmount() throws Exception {
        atmService.login("Alice");
        assertThrows(InvalidOperationException.class, () -> atmService.deposit(0));
    }

    @Test
    public void testWithdraw() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000); // $100
        long newBalance = atmService.withdraw(5000); // $50
        assertEquals(5000, newBalance);
    }

    @Test
    public void testWithdrawWithoutLogin() {
        assertThrows(AuthenticationException.class, () -> atmService.withdraw(5000));
    }

    @Test
    public void testWithdrawInsufficientFunds() throws Exception {
        atmService.login("Alice");
        atmService.deposit(5000); // $50
        assertThrows(InsufficientFundsException.class, () -> atmService.withdraw(10000)); // $100
    }

    @Test
    public void testWithdrawNegativeAmount() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000);
        assertThrows(InvalidOperationException.class, () -> atmService.withdraw(-1000));
    }

    @Test
    public void testTransfer() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000); // $100
        long newBalance = atmService.transfer("Bob", 5000); // $50
        assertEquals(5000, newBalance);
    }

    @Test
    public void testTransferWithoutLogin() {
        assertThrows(AuthenticationException.class, () -> atmService.transfer("Bob", 5000));
    }

    @Test
    public void testTransferToSelf() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000);
        assertThrows(InvalidOperationException.class, () -> atmService.transfer("Alice", 5000));
    }

    @Test
    public void testTransferNegativeAmount() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000);
        assertThrows(InvalidOperationException.class, () -> atmService.transfer("Bob", -1000));
    }

    @Test
    public void testTransferMoreThanBalance() throws Exception {
        atmService.login("Alice");
        atmService.deposit(5000); // $50
        long newBalance = atmService.transfer("Bob", 10000); // $100
        assertEquals(0, newBalance);

        // Verify debt was created
        Customer alice = atmService.getCurrentCustomer();
        assertEquals(5000, alice.getDebtsOwedTo().get("Bob"));
    }

    @Test
    public void testDepositSettlesDebt() throws Exception {
        // Alice transfers to Bob, creating debt
        atmService.login("Alice");
        atmService.deposit(5000); // $50
        atmService.transfer("Bob", 10000); // $100
        assertEquals(5000, atmService.getCurrentCustomer().getDebtsOwedTo().get("Bob"));

        // Alice deposits to settle debt
        long newBalance = atmService.deposit(3000); // $30
        assertEquals(0, newBalance); // All goes to settle debt
        assertEquals(2000, atmService.getCurrentCustomer().getDebtsOwedTo().get("Bob")); // Remaining debt
    }

    @Test
    public void testMultipleTransfersAndDebtSettlement() throws Exception {
        atmService.login("Bob");
        atmService.deposit(8000); // $80
        atmService.transfer("Alice", 5000); // Transfer $50
        atmService.transfer("Alice", 10000); // Transfer $100 (overdraft)
        atmService.deposit(3000); // Deposit $30

        Customer bob = atmService.getCurrentCustomer();
        assertEquals(0, bob.getBalanceCents()); // All went to settle debt
        assertEquals(4000, bob.getDebtsOwedTo().get("Alice")); // $40 remaining debt
    }

    @Test
    public void testBalanceInfoWithDebts() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000); // $100
        atmService.transfer("Bob", 15000); // $150 (creates $50 debt)

        String info = atmService.getBalanceInfo();
        assertTrue(info.contains("$5000") || info.contains("5000"));
        assertTrue(info.contains("Owed"));
        assertTrue(info.contains("Bob"));
    }

    @Test
    public void testGetCurrentCustomerWithoutLogin() {
        assertThrows(AuthenticationException.class, () -> atmService.getCurrentCustomer());
    }

    @Test
    public void testPersistenceAcrossLoginLogout() throws Exception {
        atmService.login("Alice");
        atmService.deposit(10000);
        atmService.logout();

        // Login again
        atmService.login("Alice");
        Customer alice = atmService.getCurrentCustomer();
        assertEquals(10000, alice.getBalanceCents());
    }
}

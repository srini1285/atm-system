package com.atm.ui;

import com.atm.exception.*;
import com.atm.service.ATMService;
import com.atm.util.CurrencyFormatter;

import java.util.Scanner;

/**
 * Command-line interface for the ATM system.
 * Handles user input and displays output to the console.
 */
public class CLIInterface {
    private final ATMService atmService;
    private final Scanner scanner;
    private boolean running;

    /**
     * Creates a new CLI interface.
     *
     * @param atmService The ATM service to use
     */
    public CLIInterface(ATMService atmService) {
        this.atmService = atmService;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Starts the CLI interface and processes user commands.
     */
    public void start() {
        printWelcome();
        printHelp();

        while (running) {
            System.out.print("\nATM> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            processCommand(input);
        }

        printGoodbye();
        scanner.close();
    }

    /**
     * Processes a user command.
     *
     * @param input The command input
     */
    private void processCommand(String input) {
        String[] parts = input.split("\\s+", 3);
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "login":
                    if (parts.length < 2) {
                        System.out.println("Error: login requires a customer name.");
                        break;
                    }
                    handleLogin(parts[1]);
                    break;

                case "logout":
                    handleLogout();
                    break;

                case "deposit":
                    if (parts.length < 2) {
                        System.out.println("Error: deposit requires an amount.");
                        break;
                    }
                    handleDeposit(parts[1]);
                    break;

                case "withdraw":
                    if (parts.length < 2) {
                        System.out.println("Error: withdraw requires an amount.");
                        break;
                    }
                    handleWithdraw(parts[1]);
                    break;

                case "transfer":
                    if (parts.length < 3) {
                        System.out.println("Error: transfer requires a target name and amount.");
                        break;
                    }
                    handleTransfer(parts[1], parts[2]);
                    break;

                case "help":
                    printHelp();
                    break;

                case "exit":
                case "quit":
                    running = false;
                    break;

                default:
                    System.out.println("Error: Unknown command '" + command + "'. Type 'help' for available commands.");
                    break;
            }
        } catch (ATMException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount. Please enter a valid number.");
        }
    }

    /**
     * Handles the login command.
     */
    private void handleLogin(String customerName) throws ATMException {
        atmService.login(customerName);
        System.out.println("Hello, " + customerName + "!");
        System.out.println(atmService.getBalanceInfo());
    }

    /**
     * Handles the logout command.
     */
    private void handleLogout() throws ATMException {
        String name = atmService.getCurrentCustomer().getName();
        atmService.logout();
        System.out.println("Goodbye, " + name + "!");
    }

    /**
     * Handles the deposit command.
     */
    private void handleDeposit(String amountStr) throws ATMException {
        double amount = Double.parseDouble(amountStr);
        long amountCents = CurrencyFormatter.dollarsTocents(amount);

        long newBalance = atmService.deposit(amountCents);
        System.out.println(atmService.getBalanceInfo());
    }

    /**
     * Handles the withdraw command.
     */
    private void handleWithdraw(String amountStr) throws ATMException {
        double amount = Double.parseDouble(amountStr);
        long amountCents = CurrencyFormatter.dollarsTocents(amount);

        long newBalance = atmService.withdraw(amountCents);
        System.out.println(atmService.getBalanceInfo());
    }

    /**
     * Handles the transfer command.
     */
    private void handleTransfer(String targetName, String amountStr) throws ATMException {
        double amount = Double.parseDouble(amountStr);
        long amountCents = CurrencyFormatter.dollarsTocents(amount);

        long newBalance = atmService.transfer(targetName, amountCents);
        System.out.println("Transferred " + CurrencyFormatter.formatCents(amountCents) + " to " + targetName);
        System.out.println(atmService.getBalanceInfo());
    }

    /**
     * Prints the welcome message.
     */
    private void printWelcome() {
        System.out.println("\n====================================");
        System.out.println("    Welcome to ATM System v1.0");
        System.out.println("====================================\n");
        System.out.println("Type 'help' for available commands or 'exit' to quit.\n");
    }

    /**
     * Prints the goodbye message.
     */
    private void printGoodbye() {
        System.out.println("\nThank you for using ATM System. Goodbye!");
    }

    /**
     * Prints the help message with available commands.
     */
    private void printHelp() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("login [name]           - Login as customer (creates account if new)");
        System.out.println("logout                 - Logout from current account");
        System.out.println("deposit [amount]       - Deposit funds to your account");
        System.out.println("withdraw [amount]      - Withdraw funds from your account");
        System.out.println("transfer [name] [amt]  - Transfer funds to another customer");
        System.out.println("help                   - Show this help message");
        System.out.println("exit                   - Exit the application");
        System.out.println("------------------------\n");
    }
}

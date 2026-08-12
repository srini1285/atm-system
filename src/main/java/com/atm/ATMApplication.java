package com.atm;

import com.atm.service.ATMService;
import com.atm.ui.CLIInterface;

/**
 * Main entry point for the ATM application.
 * Initializes the ATM service and starts the CLI interface.
 */
public class ATMApplication {
    public static void main(String[] args) {
        // Create ATM service with in-memory storage
        ATMService atmService = new ATMService();

        // Create and start CLI interface
        CLIInterface cli = new CLIInterface(atmService);
        cli.start();
    }
}

package com.atm.util;

/**
 * Utility class for formatting currency values for display.
 */
public class CurrencyFormatter {
    /**
     * Formats cents to a dollar string.
     *
     * @param cents The amount in cents
     * @return Formatted string like "$100.50"
     */
    public static String formatCents(long cents) {
        double dollars = cents / 100.0;
        return String.format("$%.2f", dollars);
    }

    /**
     * Formats dollars to a dollar string.
     *
     * @param dollars The amount in dollars
     * @return Formatted string like "$100.50"
     */
    public static String formatDollars(double dollars) {
        return String.format("$%.2f", dollars);
    }

    /**
     * Converts dollars to cents.
     *
     * @param dollars The amount in dollars
     * @return Amount in cents
     */
    public static long dollarsTocents(double dollars) {
        return Math.round(dollars * 100);
    }

    /**
     * Converts cents to dollars.
     *
     * @param cents The amount in cents
     * @return Amount in dollars
     */
    public static double centsToDollars(long cents) {
        return cents / 100.0;
    }
}

package com.example.raybank.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting Malaysian Ringgit (RM) currency.
 */
public class CurrencyFormatter {
    private static final Locale MALAYSIA_LOCALE = new Locale.Builder().setLanguage("ms").setRegion("MY").build();

    /**
     * Format amount as Malaysian Ringgit (RM).
     * 
     * @param amount The amount to format
     * @return Formatted string like "RM1,234.56"
     */
    public static String formatRM(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(MALAYSIA_LOCALE);
        // Replace MYR with RM
        String formatted = currencyFormat.format(amount);
        return formatted.replace("MYR", "RM");
    }

    /**
     * Format amount as Malaysian Ringgit without currency symbol.
     * 
     * @param amount The amount to format
     * @return Formatted string like "1,234.56"
     */
    public static String formatAmount(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(MALAYSIA_LOCALE);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(amount);
    }
}

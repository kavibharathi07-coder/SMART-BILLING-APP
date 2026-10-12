
package com.supermarket.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class ConsoleUtil {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[94m";
    public static final String PURPLE = "\u001B[95m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private ConsoleUtil() {}

    public static void separator() {
        System.out.println(CYAN + "------------------------------------------------------------" + RESET);
    }

    public static String money(double amount) {
        return CURRENCY.format(amount);
    }

    public static void header(String title) {
        String line = "+------------------------------------------------------------+";
        String bottom = "+------------------------------------------------------------+";
        System.out.println(CYAN + line + RESET);
        System.out.println(BLUE + "| " + RESET + BOLD + String.format("%-58s", title) + RESET + BLUE + " |" + RESET);
        System.out.println(PURPLE + "| " + RESET + String.format("%-58s", "SmartMart Console Suite") + PURPLE + " |" + RESET);
        System.out.println(BLUE + "| " + RESET + String.format("%-58s", "----------------------------------------------------------") + BLUE + " |" + RESET);
        System.out.println(PURPLE + bottom + RESET);
    }

    public static void printHeader(String title) {
        header(title);
    }

    public static void success(String message) {
        box("SUCCESS", message, GREEN);
    }

    public static void error(String message) {
        box("ERROR", message, RED);
    }

    public static void warning(String message) {
        box("NOTICE", message, YELLOW);
    }

    public static void box(String title, String message, String color) {
        String border = "+------------------------------------------------------------+";
        System.out.println(color + border + RESET);
        System.out.println(color + "| " + BOLD + String.format("%-58s", title) + color + "|" + RESET);
        System.out.println(color + "+------------------------------------------------------------+" + RESET);
        System.out.println(color + "| " + RESET + String.format("%-58s", truncate(message, 58)) + color + "|" + RESET);
        System.out.println(color + "+------------------------------------------------------------+" + RESET);
    }

    public static void panelTop(String title) {
        System.out.println(PURPLE + "+------------------------------------------------------------+" + RESET);
        panelLine(title, CYAN + BOLD);
        panelLine("SmartMart Console Suite", BLUE);
        panelLine("----------------------------------------------------------", PURPLE);
    }

    public static void panelLine(String text, String color) {
        System.out.println(color + "| " + RESET + String.format("%-58s", truncate(text, 58)) + color + " |" + RESET);
    }

    public static void panelDivider() {
        System.out.println(PURPLE + "|------------------------------------------------------------|" + RESET);
    }

    public static void panelMenu(int number, String label) {
        String color = number % 2 == 0 ? BLUE : CYAN;
        panelLine(String.format("%2d. %s", number, label), color);
    }

    public static void panelBottom() {
        System.out.println(PURPLE + "+------------------------------------------------------------+" + RESET);
    }

    private static String truncate(String value, int length) {
        if (value == null) return "";
        return value.length() <= length ? value : value.substring(0, length - 1) + "…";
    }

    public static void pause() {
        System.out.println();
        System.out.print("Press Enter to continue...");
        InputUtil.readLine("");
    }
}

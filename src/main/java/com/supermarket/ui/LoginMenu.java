package com.supermarket.ui;

import com.supermarket.enums.Role;
import com.supermarket.model.AppUser;
import com.supermarket.service.AuthService;
import com.supermarket.util.InputUtil;
import com.supermarket.util.ConsoleUtil;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class LoginMenu {

    private final AuthService authService;

    // Standard ANSI Escape Codes for Cross-Platform Styling
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    public LoginMenu() {
        this.authService = new AuthService();
        // Force the console to use UTF-8 to prevent '?' character glitches in PowerShell
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Fallback silently if UTF-8 isn't supported
        }
    }

    public void show() {
        while (true) {
            System.out.println();
            ConsoleUtil.header("SMART BILLING APP");
            System.out.println(ConsoleUtil.CYAN + "  1. " + ConsoleUtil.RESET + "Staff Login");
            System.out.println(ConsoleUtil.BLUE + "  2. " + ConsoleUtil.RESET + "General User");
            System.out.println(ConsoleUtil.PURPLE + "  3. " + ConsoleUtil.RESET + "Exit");

            String choice = InputUtil.readRequired(CYAN + "> " + RESET + BOLD + "Enter choice: " + RESET);

            switch (choice) {
                case "1":
                    staffLogin();
                    break;

                case "2":
                    new GeneralUserMenu().show();
                    break;

                case "3":
                    System.out.println();
                    ConsoleUtil.success("Thank you for using Smart Billing App.");
                    return;

                default:
                    printErrorBox("INVALID CHOICE", "Please try again.");
            }
        }
    }

    private void staffLogin() {
        System.out.println();
        ConsoleUtil.header("STAFF LOGIN");

        // 1. Prompt for Username inside the left border
        String username = InputUtil.readRequired(ConsoleUtil.CYAN + "Username: " + ConsoleUtil.RESET);

        // 2. Prompt for Password inside the left border
        String password = InputUtil.readPassword(ConsoleUtil.CYAN + "Password: " + ConsoleUtil.RESET);
        
        ConsoleUtil.separator();

        try {
            AppUser user = authService.login(username, password);

            if (user == null) {
                printErrorBox("LOGIN FAILED", "Invalid username or password.");
                return;
            }

            if (!user.isActive()) {
                printErrorBox("LOGIN FAILED", "Your account is inactive.");
                return;
            }

            // Success Header Layout
            System.out.println();
            ConsoleUtil.success("Login successful. Welcome, " + user.getFullName() + "!");

            // User Profile Alignment
            System.out.printf("  %-12s : %s%n", "User ID", user.getUserId());
            System.out.printf("  %-12s : %s%n", "Username", user.getUsername());
            System.out.printf("  %-12s : %s%n", "Name", user.getFullName());
            System.out.printf("  %-12s : %s%s%s%n", "Role", ConsoleUtil.YELLOW, user.getRole(), ConsoleUtil.RESET);
            System.out.printf("  %-12s : %s[ACTIVE]%s%n", "Status", ConsoleUtil.GREEN, ConsoleUtil.RESET);
            ConsoleUtil.separator();
            System.out.println();

            if (user.getRole() == Role.ADMIN) {
                System.out.println(CYAN + ">>> Opening Admin Menu..." + RESET);
                new AdminMenu(user).show();
            } else if (user.getRole() == Role.BILLER) {
                System.out.println(CYAN + ">>> Opening Biller Menu..." + RESET);
                new BillerMenu(user).show();
            } else if (user.getRole() == Role.INVENTORY) {
                System.out.println(CYAN + ">>> Opening Inventory Menu..." + RESET);
                new InventoryMenu(user).show();
            } else {
                printErrorBox("ROLE ERROR", "Unknown user role.");
            }

        } catch (Exception e) {
            printErrorBox("SYSTEM ERROR", "Error: " + e.getMessage());
        }
    }

    private void printErrorBox(String title, String message) {
        ConsoleUtil.error(title + ": " + message);
    }
}

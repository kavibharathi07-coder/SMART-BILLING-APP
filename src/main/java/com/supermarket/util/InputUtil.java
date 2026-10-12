
package com.supermarket.util;

import java.util.Scanner;

public final class InputUtil {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputUtil() {}

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);

            if (!value.isEmpty()) {
                return value;
            }

            ConsoleUtil.error("Input cannot be empty.");
        }
    }

    public static String readPassword(String prompt) {
        return readRequired(prompt);
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readRequired(prompt));
            } catch (NumberFormatException e) {
                ConsoleUtil.error("Enter a valid whole number.");
            }
        }
    }

    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);

            if (value > 0) {
                return value;
            }

            ConsoleUtil.error("Enter a number greater than zero.");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                double value = Double.parseDouble(readRequired(prompt));

                if (Double.isFinite(value)) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Show the validation message below.
            }

            ConsoleUtil.error("Enter a valid number.");
        }
    }

    public static double readNonNegativeDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);

            if (value >= 0) {
                return value;
            }

            ConsoleUtil.error("Value cannot be negative.");
        }
    }
}

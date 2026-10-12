package com.supermarket.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern PHONE =
            Pattern.compile(
                    "^[0-9]{10}$"
            );

    private static final Pattern EMAIL =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
            );

    private ValidationUtil() {
    }

    public static boolean isValidPhone(
            String phone
    ) {

        return phone == null
                || phone.isBlank()
                || PHONE.matcher(phone).matches();
    }

    public static boolean isValidEmail(
            String email
    ) {

        return email == null
                || email.isBlank()
                || EMAIL.matcher(email).matches();
    }
}
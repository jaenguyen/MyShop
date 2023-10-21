package com.ic.myshop.validator;

import java.util.regex.Pattern;

public class AuthValidator {

    public static boolean checkEmail(String email) {
        if (isNone(email)) {
            return false;
        }
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    public static boolean checkPhone(String phone) {
        if (isNone(phone)) {
            return false;
        }
        return Pattern.compile(PHONE_PATTER).matcher(phone).matches();
    }

    public static boolean checkPassword(String password, String password2) {
        if (isNone(password) || isNone(password2)) {
            return false;
        }
        return password.equals(password2);
    }

    public static boolean isNone (String input) {
        if (input.isEmpty() || input == null) {
            return true;
        }
        return false;
    }

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_PATTER = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
}

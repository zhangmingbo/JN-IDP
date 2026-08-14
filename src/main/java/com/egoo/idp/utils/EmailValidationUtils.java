package com.egoo.idp.utils;

import java.util.regex.Pattern;

public class EmailValidationUtils {

    // 常用的邮箱正则表达式
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * 验证邮箱地址，不满足格式返回空字符串
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "";
        }

        String trimmedEmail = email.trim();
        if (EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return trimmedEmail;
        }

        return "";
    }

}

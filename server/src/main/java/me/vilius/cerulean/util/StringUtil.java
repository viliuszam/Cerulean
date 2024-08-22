package me.vilius.cerulean.util;

public class StringUtil {
    public static String censorUsername(String username) {
        if (username == null || username.length() < 3) {
            return username;
        }
        return username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1);
    }
}

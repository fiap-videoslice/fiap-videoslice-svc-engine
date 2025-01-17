package com.example.fiap.videoslice.domain.utils;

public class StringUtils {
    private StringUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
}

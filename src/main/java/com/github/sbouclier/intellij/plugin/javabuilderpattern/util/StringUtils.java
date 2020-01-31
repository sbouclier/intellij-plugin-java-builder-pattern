package com.github.sbouclier.intellij.plugin.javabuilderpattern.util;

/**
 * String utility class
 */
public class StringUtils {

    public static String firstUppercaseLetter(String text) {
        return new StringBuilder()
                .append(text.substring(0, 1).toUpperCase())
                .append(text.substring(1))
                .toString();
    }
}

package com.example.keuangan.util;

import org.slf4j.Logger;

public class LogUtil {

    private LogUtil() {
        // prevent instantiation
    }

    public static void info(Logger log, String message, Object... args) {
        log.info(format(message), args);
    }

    public static void warn(Logger log, String message, Object... args) {
        log.warn(format(message), args);
    }

    public static void error(Logger log, String message, Object... args) {
        log.error(format(message), args);
    }

    public static void error(Logger log, String message, Throwable throwable, Object... args) {
        log.error(format(message), args, throwable);
    }

    private static String format(String message) {
        return "[KEUANGAN] " + message;
    }
}

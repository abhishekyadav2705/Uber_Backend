package com.backend.abhishek.uber.advices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    private static final AppLogger INSTANCE = new AppLogger();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
    }

    public static AppLogger getInstance() {
        return INSTANCE;
    }

    public void logInfo(String message) {
        log("INFO", message);
    }

    public void logWarning(String message) {
        log("WARN", message);
    }

    public void logError(String message) {
        log("ERROR", message);
    }

    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String threadName = Thread.currentThread().getName();
        String classMethod = getCallingClassAndMethod();

        System.out.printf("[%s] [%s] [Thread: %s] [%s] %s%n",
                timestamp, level, threadName, classMethod, message);
    }

    private String getCallingClassAndMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Index 3 or 4 usually gives the direct caller (skip getStackTrace, log, and wrapper method)
        for (int i = 3; i < stackTrace.length; i++) {
            if (!stackTrace[i].getClassName().equals(AppLogger.class.getName())) {
                return stackTrace[i].getClassName().substring(stackTrace[i].getClassName().lastIndexOf(".") + 1)
                        + "." + stackTrace[i].getMethodName() + "()";
            }
        }
        return "Unknown";
    }
}



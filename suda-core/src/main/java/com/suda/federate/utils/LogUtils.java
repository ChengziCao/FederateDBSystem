package com.suda.federate.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    /**
     * -1:none,  0: console, 1: file
     */
    public static int STDOUT = 0;
    public static boolean DEBUG = true;

    public static void debug(String msg) {
        if (!DEBUG) return;
        if (STDOUT == 1)
            LOGGER.info(findCaller() + " : " + msg);
        else
            System.out.println(findCaller() + " : " + msg);
    }

    public static void info(String msg) {
        if (STDOUT == 1)
            LOGGER.info(findCaller() + " : " + msg);
        else
            System.out.println(findCaller() + " : " + msg);
    }


    public static void error(String msg) {
        if (STDOUT == 1)
            LOGGER.error(findCaller() + " : " + msg);
        else
            System.out.println(findCaller() + " : " + msg);
    }

    private static String findCaller() {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = null;
        String logClassName = LogUtils.class.getName();
        int i = 0;
        for (int len = callStack.length; i < len; i++) {
            if (logClassName.equals(callStack[i].getClassName())) {
                break;
            }
        }
        caller = callStack[i + 2];
        return caller.toString();
    }
}
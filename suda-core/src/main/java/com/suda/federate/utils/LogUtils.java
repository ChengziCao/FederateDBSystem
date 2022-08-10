package com.suda.federate.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    public static int STDOUT = 0;

    public static void info(String content) {
        if (STDOUT == 0) {
            System.out.println(content);
        } else if (STDOUT == 1) {
            LOGGER.info(content);
        }
    }

    public static void error(String content) {
        if (STDOUT == 0) {
            System.out.println(content);
        } else if (STDOUT == 1) {
            LOGGER.error(content);
        }
    }
}

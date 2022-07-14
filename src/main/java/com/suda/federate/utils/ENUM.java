package com.suda.federate.utils;

public class ENUM {
    public enum FD_DATABASE {MYSQL, POSTGRESQL}

    public enum FD_DATA_TYPE {POINT,INT,FLOAT,STRING}

    public enum FD_FUNCTION {KNN, DISTANCE}

    /**
     * check string equal with enum object. Insensitive to upper case or lower case.
     */
    public static <T> boolean equals(String string, T object) {
        return string.equalsIgnoreCase(object.toString());
    }
}

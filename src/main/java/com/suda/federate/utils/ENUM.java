package com.suda.federate.utils;

public class ENUM {
    public enum DATABASE {MYSQL, POSTGRESQL}

    public enum DATA_TYPE {POINT, LINESTRING, INT, DOUBLE, STRING, POLYGON}

    public enum FUNCTION {KNN, DISTANCE, RKNN, CONTAINS}


    /**
     * check string equal with enum object. Insensitive to upper case or lower case.
     */
    public static <T> boolean equals(String string, T object) {
        String objString = object.toString();
        if (string.toLowerCase().startsWith("fd_"))
            objString = "fd_" + objString;
        return string.equalsIgnoreCase(objString);
    }
}

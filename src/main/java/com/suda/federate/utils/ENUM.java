package com.suda.federate.utils;

public class ENUM {
    public enum DATABASE {MYSQL, POSTGRESQL}

    public enum DATA_TYPE {POINT, LINESTRING, INT, DOUBLE, STRING, POLYGON}

    public enum FUNCTION {KNN, RANGE_COUNT, RANGE_QUERY}

    public static FUNCTION str2FUNCTION(String str) {
        if (str.equalsIgnoreCase("RANGE_COUNT") || str.equalsIgnoreCase("RangeCount"))
            return FUNCTION.RANGE_COUNT;
        else if (str.equalsIgnoreCase("RANGE_QUERY") || str.equalsIgnoreCase("RangeQuery"))
            return FUNCTION.RANGE_QUERY;
        else if (str.equalsIgnoreCase("KNN"))
            return FUNCTION.KNN;
        else
            return null;
//        FUNCTION.valueOf()
    }


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

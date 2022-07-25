package com.suda.federate.utils;

public class ENUM {
    public enum DATABASE {MYSQL, POSTGRESQL}

    public enum DATA_TYPE {POINT, LINESTRING, INT, DOUBLE, STRING, POLYGON}

    public enum FUNCTION {KNN, RANGE_COUNT, RANGE_QUERY}

    public static DATABASE str2DATABASE(String str) {
        if (str.equalsIgnoreCase("mysql"))
            return DATABASE.MYSQL;
        else if (str.equalsIgnoreCase("postgis") || str.equalsIgnoreCase("POSTGRESQL"))
            return DATABASE.POSTGRESQL;
        else
            return null;
    }

    public static FUNCTION str2FUNCTION(String str) {
        if (str.equalsIgnoreCase("RANGE_COUNT") || str.equalsIgnoreCase("RangeCount"))
            return FUNCTION.RANGE_COUNT;
        else if (str.equalsIgnoreCase("RANGE_QUERY") || str.equalsIgnoreCase("RangeQuery"))
            return FUNCTION.RANGE_QUERY;
        else if (str.equalsIgnoreCase("KNN"))
            return FUNCTION.KNN;
        else
            return null;
    }

    public static DATA_TYPE str2DATATYPE(String str) {
        if (str.equalsIgnoreCase("int") || str.equalsIgnoreCase("fd_int"))
            return DATA_TYPE.INT;
        else if (str.equalsIgnoreCase("double") || str.equalsIgnoreCase("fd_double"))
            return DATA_TYPE.DOUBLE;
        else if (str.equalsIgnoreCase("point") || str.equalsIgnoreCase("fd_point"))
            return DATA_TYPE.POINT;
        else
            return null;
    }
}

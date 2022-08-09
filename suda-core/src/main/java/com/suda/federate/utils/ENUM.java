package com.suda.federate.utils;

import com.suda.federate.rpc.FederateService;

public class ENUM {
    public enum DATABASE {MYSQL, POSTGRESQL}

    public enum DATA_TYPE {POINT, LINESTRING, INT, DOUBLE, STRING, POLYGON}



    public static DATABASE str2DATABASE(String str) {
        if (str.equalsIgnoreCase("mysql"))
            return DATABASE.MYSQL;
        else if (str.equalsIgnoreCase("postgis") || str.equalsIgnoreCase("POSTGRESQL"))
            return DATABASE.POSTGRESQL;
        else
            return null;
    }

    public static FederateService.SQLExpression.Function str2FUNCTION(String str) {
        if (str.equalsIgnoreCase("RANGE_COUNT") || str.equalsIgnoreCase("RangeCount"))
            return FederateService.SQLExpression.Function.RANGE_COUNT;
        else if (str.equalsIgnoreCase("RANGE_QUERY") || str.equalsIgnoreCase("RangeQuery"))
            return FederateService.SQLExpression.Function.RANGE_QUERY;
        else if (str.equalsIgnoreCase("KNN"))
            return FederateService.SQLExpression.Function.KNN;
        else if (str.equalsIgnoreCase("PolygonRangeQuery") || str.equalsIgnoreCase("Polygon_Range_Query")) {
            return FederateService.SQLExpression.Function.POLYGON_RANGE_QUERY;
        } else
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

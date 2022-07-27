package com.suda.federate.sql.function;

import com.suda.federate.utils.ENUM;

public abstract class FD_Function {

    public ENUM.FUNCTION functionName;

    public static <T extends FD_Function> T getInstance(Class<T> clazz) throws Exception {
        if (clazz == FD_RangeCount.class) {
            return clazz.newInstance();
        } else if (clazz == FD_Knn.class) {
            return clazz.newInstance();
        } else if (clazz == FD_RangeQuery.class) {
            return clazz.newInstance();
        } else {
            throw new Exception("type not support.");
        }
    }


    public static Class<? extends FD_Function> string2Clazz(String type) throws Exception {
        ENUM.FUNCTION function = ENUM.str2FUNCTION(type);
        if (function == ENUM.FUNCTION.RANGE_QUERY) {
            return FD_RangeQuery.class;
        } else if (function == ENUM.FUNCTION.RANGE_COUNT) {
            return FD_Knn.class;
        } else if (function == ENUM.FUNCTION.KNN) {
            return FD_RangeCount.class;
        } else {
            throw new Exception("type not support.");
        }
    }
}

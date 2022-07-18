package com.suda.federate.sql.function;

import com.suda.federate.sql.expression.FD_Type;
import com.suda.federate.utils.ENUM;

import java.util.Arrays;
import java.util.List;

public abstract class FD_Function implements FD_Type {

    public static List<String> supportFunctionList = Arrays.asList("FD_Distance", "FD_Knn", "FD_Rknn");

    public String name;

    public FD_Function(String name) {
        this.name = name;
    }

    public static <T extends FD_Function> T getInstance(Class<T> clazz) throws Exception {
        if (clazz == FD_Distance.class) {
            return clazz.newInstance();
        } else if (clazz == FD_Knn.class) {
            return clazz.newInstance();
        } else if (clazz == FD_Rknn.class) {
            return clazz.newInstance();
        } else {
            throw new Exception("type not support.");
        }
    }


    public static Class<? extends FD_Function> string2Clazz(String type) throws Exception {

        if (ENUM.equals(type, ENUM.FUNCTION.DISTANCE)) {
            return FD_Distance.class;
        } else if (ENUM.equals(type, ENUM.FUNCTION.KNN)) {
            return FD_Knn.class;
        } else if (ENUM.equals(type, ENUM.FUNCTION.RKNN)) {
            return FD_Rknn.class;
        } else {
            throw new Exception("type not support.");
        }
    }

}

package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;
//import javafx.util.Pair;
import org.apache.calcite.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FD_Variable<valueType> {
    public final ENUM.DATA_TYPE dataType;
    public valueType value;

    public FD_Variable(ENUM.DATA_TYPE dataType, valueType value) {
        this.dataType = dataType;
        this.value = value;
    }

    /**
     * string 2 FD_Variable Object
     *
     * @param value string value
     * @param clazz FD_Variable Type
     * @return Instance which is the subclass of FD_Variable
     */
    public static <T> T getInstance(String value, Class<T> clazz) throws Exception {
        if (clazz == Integer.class || clazz == Double.class || clazz == Float.class) {
            return clazz.getConstructor(String.class).newInstance(value);
        } else if (clazz == FD_Point.class) {
            String[] strArray = value.split(" ");
            return clazz.getConstructor(Float.class, Float.class).newInstance(Float.parseFloat(strArray[0]), Float.parseFloat(strArray[1]));
        } else if (clazz == FD_LineString.class || clazz == FD_Polygon.class) {
            List<FD_Point> points = Arrays.stream((value.split(",")))
                    .map(x -> x.trim().split(" "))
                    .map(x -> new Pair<>(Float.valueOf(x[0]), Float.valueOf(x[1])))
                    .map(x -> new FD_Point(x.getKey(), x.getValue()))
                    .collect(Collectors.toList());
            return clazz.getConstructor(String.class, List.class).newInstance(points);
        } else {
            throw new Exception("type not support.");
        }
    }

    /**
     * string 2 class
     *
     * @param type string of FD_Variable type
     * @return class which is the subclass of FD_Variable
     * @throws Exception
     */
    public static Class<?> string2Clazz(String type) throws Exception {
        ENUM.DATA_TYPE dataType = ENUM.str2DATATYPE(type);
        if (dataType == ENUM.DATA_TYPE.POINT) {
            return FD_Point.class;
        } else if (dataType == ENUM.DATA_TYPE.INT) {
            return Integer.class;
        } else if (dataType == ENUM.DATA_TYPE.LINESTRING) {
            return FD_LineString.class;
        } else if (dataType == ENUM.DATA_TYPE.DOUBLE) {
            return Double.class;
        } else if (dataType == ENUM.DATA_TYPE.POLYGON) {
            return FD_Polygon.class;
        } else {
            throw new Exception("type not support.");
        }
    }

}

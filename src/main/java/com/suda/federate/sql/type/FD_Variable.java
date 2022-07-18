package com.suda.federate.sql.type;

import com.suda.federate.sql.expression.FD_Type;
import com.suda.federate.utils.ENUM;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FD_Variable<valueType> implements FD_Type {
    public final ENUM.DATA_TYPE dataType;
    public String name;
    public valueType value;

    public FD_Variable(ENUM.DATA_TYPE dataType, String name, valueType value) {
        this.dataType = dataType;
        this.name = name;
        this.value = value;
    }

    /**
     * string 2 FD_Variable Object
     *
     * @param name  obj name
     * @param value string value
     * @param clazz FD_Variable Type
     * @return Instance which is the subclass of FD_Variable
     */
    public static <T extends FD_Variable> T getInstance(String name, String value, Class<T> clazz) throws Exception {
        if (clazz == FD_Int.class) {
            return clazz.getConstructor(String.class, Integer.class).newInstance(name, Integer.valueOf(value));
        } else if (clazz == FD_Double.class) {
            return clazz.getConstructor(String.class, Double.class).newInstance(name, Double.valueOf(value));
        } else if (clazz == FD_Point.class) {
            String[] strArray = value.split(" ");
            return clazz.getConstructor(String.class, Float.class, Float.class).newInstance(name, Float.parseFloat(strArray[0]), Float.parseFloat(strArray[1]));
        } else if (clazz == FD_LineString.class) {
            List<FD_Point> points = Arrays.stream((value.split(",")))
                    .map(x -> x.trim().split(" "))
                    .map(x -> new Pair<>(Float.valueOf(x[0]), Float.valueOf(x[1])))
                    .map(x -> new FD_Point("_", x.getKey(), x.getValue()))
                    .collect(Collectors.toList());
            return clazz.getConstructor(String.class, List.class).newInstance(name, points);
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
    public static Class<? extends FD_Variable> string2Clazz(String type) throws Exception {
        if (ENUM.equals(type, ENUM.DATA_TYPE.POINT)) {
            return FD_Point.class;
        } else if (ENUM.equals(type, ENUM.DATA_TYPE.INT)) {
            return FD_Int.class;
        } else if (ENUM.equals(type, ENUM.DATA_TYPE.LINESTRING)) {
            return FD_LineString.class;
        } else if (ENUM.equals(type, ENUM.DATA_TYPE.DOUBLE)) {
            return FD_Double.class;
        } else {
            throw new Exception("type not support.");
        }
    }

    public static List<FD_Variable> results2FDVariable(List<ResultSet> resultSets, Class<?> clazz) throws Exception {
        // List<T> variables = new ArrayList<>();
        List<FD_Variable> variables = new ArrayList<>();

        for (ResultSet rs : resultSets) {
            // 首个元素不会跳过，可理解为带头指针的链表
            while (rs.next()) {
                variables.add(FD_Variable.getInstance(
                        rs.getObject("id", Integer.class).toString(),
                        rs.getObject("dis", Double.class).toString(),
                        FD_Double.class
                ));
            }
        }
        return variables;
    }

    @Override
    public String toString() {
        return "FD_Variable{" +
                dataType +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

}

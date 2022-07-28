package com.suda.federate.sql.function;

import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM;

import java.util.List;

public class FD_RangeCount extends FD_Function {

    public FD_Point point;
    public Double radius;

//    public FD_RangeCount(SQLExpression expression) {
//        point = (FD_Point) expression.variables.get(0);
//        radius = Double.parseDouble(expression.variables.get(1).toString());
//        functionName = ENUM.FUNCTION.RANGE_COUNT;
//    }
    public FD_RangeCount(List<String> variables) {
        String s = variables.get(0);
        point = new FD_Point(111.0f,112.0f);//TODO
        radius = Double.parseDouble(variables.get(1).toString());
        functionName = ENUM.FUNCTION.RANGE_COUNT;
    }
}

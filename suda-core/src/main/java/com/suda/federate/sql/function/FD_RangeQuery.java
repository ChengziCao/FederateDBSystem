package com.suda.federate.sql.function;

import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM;

public class FD_RangeQuery extends FD_Function {
    public FD_Point point;
    public Double radius;

//    public FD_RangeQuery(SQLExpression expression) {
//        point = (FD_Point) expression.variables.get(0);
//        radius = Double.parseDouble(expression.variables.get(1).toString());
//        functionName = ENUM.FUNCTION.RANGE_QUERY;
//    }
}

package com.suda.federate.sql.function;

import com.suda.federate.sql.expression.SQLExpression;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM;

public class FD_RangeCount extends FD_Function {

    public FD_Point point;
    public Double radius;

    public FD_RangeCount(SQLExpression expression) {
        point = (FD_Point) expression.variables.get(0);
        radius = Double.parseDouble(expression.variables.get(1).value.toString());
        functionName = ENUM.FUNCTION.RANGE_COUNT;
    }
}

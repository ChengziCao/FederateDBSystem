package com.suda.federate.sql.function;

import com.suda.federate.sql.common.SQLExpression;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM;

public class FD_Knn extends FD_Function {
    public FD_Point point;
    public Integer k;

    public FD_Knn(SQLExpression expression) {
        functionName = ENUM.FUNCTION.KNN;
        point = (FD_Point) expression.variables.get(0);
        k = Integer.parseInt(expression.variables.get(1).toString());
    }
}

package com.suda.federate.sql.translator;

import com.suda.federate.sql.expression.SQLExpression;
import com.suda.federate.sql.type.FD_Variable;

import java.util.List;

public interface SQLTranslator {

    /***
     * translate query.json to native SQL supported by target database.
     * @return native SQL supported by target database.
     */
    String translate(SQLExpression expression);
}

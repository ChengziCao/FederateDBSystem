package com.suda.federate.sql.translator;

import com.suda.federate.sql.type.FD_Variable;

import java.util.List;

public interface SQLTranslator {

    /***
     * translate query.json to native SQL supported by target database.
     * @param originalSql original sql of query.json.
     * @param variables variables of originalSql.
     * @return native SQL supported by target database.
     */
    String translate(String originalSql, List<FD_Variable> variables);
}

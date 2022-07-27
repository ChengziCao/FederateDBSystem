package com.suda.federate.sql.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.ENUM.FUNCTION;
import com.suda.federate.utils.FederateUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SQLExpression {

    public FUNCTION function;
    public List<Object> variables = new ArrayList<>();

    public SQLExpression() {

    }


    /**
     * parse query.json, return original sql and params list
     *
     * @param queryFile the path of query.json.
     * @return String  sql list，List<FD_Variable> params list of every sql
     * @throws IOException
     */
    public static List<SQLExpression> parseSQLExpression(String queryFile) throws Exception {
        String queryPath = FederateUtils.getRealPath(queryFile);

        List<SQLExpression> sqlExpressionList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(queryPath)));
        // 处理query.json多个查询
        if (jsonString.charAt(0) == '{') jsonString = '[' + jsonString + ']';
        JSONArray queryJsonArray = JSONArray.parseArray(jsonString);
        // 处理一条SQL语句
        for (int i = 0; i < queryJsonArray.size(); i++) {
//            List<FD_Variable> variableList = new ArrayList<>();
            SQLExpression expression = new SQLExpression();
            JSONObject queryJson = queryJsonArray.getJSONObject(i);
            expression.function = ENUM.str2FUNCTION(queryJson.getString("function"));
            // 保存 params
            for (Object varObj : queryJson.getJSONArray("params")) {
                JSONObject var = (JSONObject) varObj;
                expression.variables.add(FD_Variable.getInstance(
                        var.getString("value"),
                        FD_Variable.string2Clazz(var.getString("type"))));
            }
            sqlExpressionList.add(expression);
        }
        return sqlExpressionList;
    }

}

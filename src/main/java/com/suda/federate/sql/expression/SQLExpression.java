package com.suda.federate.sql.expression;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.sql.function.FD_Function;
import com.suda.federate.sql.type.FD_Variable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLExpression {
    // select metrics from table_name where filter order by _ limit _;
    public String type;
    public List<String> columns;
    public List<String> filters;
    public String tableName;
    public String order;
    public Integer limit;

    public List<FD_Variable> variables;
    public List<FD_Function> functions;


    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append(type).append(" ").append(String.join(", ", columns)).append(" from ").append(tableName);
        if (filters.size() > 0)
            sb.append(" where ").append(String.join(", ", filters));
        if (order != null) {
            sb.append(" order by ").append(order);
        }
        if (limit == null) {
            // 默认 limit 100
            limit = 100;
        }
        sb.append(" limit ").append(limit);
        return sb.toString();
    }

    public SQLExpression(String type, List<String> columns, List<String> filters, List<FD_Variable> variables, List<FD_Function> functions, String tableName, String order, Integer limit) throws Exception {
        this.type = type;
        this.columns = columns;
        this.filters = filters;
        this.tableName = tableName;
        this.order = order;
        this.limit = limit;
        this.variables = variables;
        this.functions = functions;
    }


    /**
     * parse query.json, return original sql and params list
     *
     * @param queryPath the path of query.json.
     * @return String  sql list，List<FD_Variable> params list of every sql
     * @throws IOException
     */
    public static List<SQLExpression> generateSQLExpression(String queryPath) throws Exception {
        List<SQLExpression> sqlExpressionList = new ArrayList<>();

        String jsonString = new String(Files.readAllBytes(Paths.get(queryPath)));
        // 处理query.json多个查询
        if (jsonString.charAt(0) == '{') jsonString = '[' + jsonString + ']';
        JSONArray queryJsonArray = JSONArray.parseArray(jsonString);
        // 处理一条SQL语句
        for (int i = 0; i < queryJsonArray.size(); i++) {
            List<FD_Variable> variableList = new ArrayList<>();
            List<FD_Function> functionList = new ArrayList<>();

            JSONObject queryJson = queryJsonArray.getJSONObject(i);

            // 保存 variables
            JSONArray variablesJsonArray = queryJson.getJSONArray("variables");
            for (Object varObj : variablesJsonArray) {
                JSONObject var = (JSONObject) varObj;
                variableList.add(FD_Variable.getInstance(var.getString("name"), var.getString("value"), FD_Variable.string2Clazz(var.getString("type"))));
            }
            JSONArray columnArray = queryJson.getJSONArray("columns");
            JSONArray filterArray = queryJson.getJSONArray("filter");

            List<String> collections = columnArray.toList(String.class);
            collections.addAll(filterArray.toList(String.class));
            // 保存 function
            for (String col : collections) {
                for (String var : FD_Function.supportFunctionList) {
                    if (col.toLowerCase().contains(var.toLowerCase())) {
                        functionList.add(FD_Function.getInstance(FD_Function.string2Clazz(var)));
                    }
                }
            }

            SQLExpression expression = new SQLExpression((String) queryJson.getOrDefault("type", "select"), columnArray.toList(String.class), filterArray.toList(String.class), variableList, functionList, queryJson.getString("table"), (String) queryJson.getOrDefault("order", null), (Integer) queryJson.getOrDefault("limit", null));
            sqlExpressionList.add(expression);
        }
        return sqlExpressionList;
    }
}

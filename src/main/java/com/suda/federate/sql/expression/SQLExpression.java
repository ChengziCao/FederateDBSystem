package com.suda.federate.sql.expression;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.ENUM.DATABASE;
import com.suda.federate.utils.ENUM.FUNCTION;
import com.suda.federate.utils.FederateUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SQLExpression {

    // select metrics from table_name where filter order by _ limit _;
    public FUNCTION function;
    public List<FD_Variable> variables = new ArrayList<>();

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
                        "_",
                        var.getString("value"),
                        FD_Variable.string2Clazz(var.getString("type"))));
            }
            sqlExpressionList.add(expression);
        }
        return sqlExpressionList;
    }


    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeQuerySQL(FD_Point point, Double radius, DATABASE dbType) {
        String template;
        if (DATABASE.POSTGRESQL == dbType) {
            template = "select st_astext(location) as loc from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location ) < %f order by st_distance( st_geographyfromtext('POINT(%f %f)'), location) limit 10000;";
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, radius, point.value.x, point.value.y);
    }

    /**
     * select RangeCounting (point, radius) from table;
     *
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeCountingSQL(FD_Point point, Double radius, DATABASE dbType) {
        String template;
        if (dbType == DATABASE.POSTGRESQL) {
            template = "select count(1) from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location) < %f;";
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, radius);
    }

    public static String generateKnnRadiusQuerySQL(FD_Point point, Integer k, DATABASE dbType) {
        String template;
        if (dbType == DATABASE.POSTGRESQL) {
            template = "select ST_Distance(ST_GeographyFromText('POINT(%f %f)'), ST_GeographyFromText(ST_AsText(location))) as dis from osm_sh order by dis limit 1 offset %d;";
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, k - 1);
    }
}

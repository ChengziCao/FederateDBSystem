package com.suda.federate.application;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.sql.function.SpatialFunctions;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.sql.type.Point;
import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.rpc.Client;
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    // 0: console, 1: file
//    private static int STDOUT = 0;

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
            SQLExpression.Builder expression = SQLExpression.newBuilder();
            JSONObject queryJson = queryJsonArray.getJSONObject(i);
            expression.setFunction(queryJson.getString("function"));
            // 保存 params
            for (Object varObj : queryJson.getJSONArray("params")) {
                JSONObject var = (JSONObject) varObj;
                String type=var.getString("type");
                String value=var.getString("value");
                if ("point".equals(type)){
                    com.suda.federate.sql.type.Point point = (com.suda.federate.sql.type.Point) SpatialFunctions.GeomFromTextWithoutBracket(value);
                    expression.setPoint(FederateCommon.Point.newBuilder()
                            .setLatitude(point.getY())
                            .setLongitude(point.getX()).build());
                }else{
                    expression.setLiteral(Double.parseDouble(value));
                }

            }
            sqlExpressionList.add(expression.build());
        }
        return sqlExpressionList;
    }
    public static void federateKnn(SQLExpression expression,Client client){
        //#TODO 需要多个client
        client.knnRadiusQuery(expression);
    }
    public static void main(String[] args) throws SQLException {
        Client client = new Client(8887,"127.0.0.1");

        try {

            String queryFile = "query.json";

            // TODO 解析 query.json，获取原始 SQL
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
                // TODO: query
                if (expression.getFunction().equals("RangeCount")){
                    client.rangeCount(expression);}
                if(expression.getFunction().equals("RangeQuery")){
                    client.rangeQuery(expression);}
                if(expression.getFunction().equals("Knn")){
                    federateKnn(expression,client);
                }

                System.out.println("===========================================================================");
            }

        } catch (Exception e) {
//            e.printStackTrace();
            try {
                client.shutDown();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            LOGGER.error(buildErrorMessage(e));
        }
    }

    // System.out.println(content) 不显示行号，找不到是哪 print 的
//    public static void print(String content) {
//        if (STDOUT == 0) {
//            System.out.println(content);
//        } else if (STDOUT == 1) {
//            LOGGER.info(content);
//        }
//    }


    public static String getStackTraceString(Throwable ex) {//(Exception ex) {
        StackTraceElement[] traceElements = ex.getStackTrace();

        StringBuilder traceBuilder = new StringBuilder();

        if (traceElements != null && traceElements.length > 0) {
            for (StackTraceElement traceElement : traceElements) {
                traceBuilder.append("\t").append(traceElement.toString());
                traceBuilder.append("\n");
            }
        }
        return traceBuilder.toString();
    }

    //构造异常堆栈信息
    public static String buildErrorMessage(Exception ex) {

        String result;
        String stackTrace = getStackTraceString(ex);
        String exceptionType = ex.toString();
        String exceptionMessage = ex.getMessage();

        result = String.format("%s : %s \r\n %s", exceptionType, exceptionMessage, stackTrace);

        return result;
    }
}
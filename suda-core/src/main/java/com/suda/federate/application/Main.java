package com.suda.federate.application;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.query.SpatialFunctions;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.suda.federate.query.FederateQuery.*;

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
            expression.setTable(queryJson.getString("table"));
            // 保存 params
            for (Object varObj : queryJson.getJSONArray("params")) {
                JSONObject var = (JSONObject) varObj;
                String type = var.getString("type");
                String value = var.getString("value");
                if ("point".equals(type)) {
                    expression.setPoint(SpatialFunctions.PointFromText(value));
                } else {
                    expression.setLiteral(Double.parseDouble(value));
                }
            }
            sqlExpressionList.add(expression.build());
        }
        return sqlExpressionList;
    }

    public static List<FederateService.PolygonRequest> parsePolygonRequest(String queryFile) throws Exception {
        String queryPath = FederateUtils.getRealPath(queryFile);

        List<FederateService.PolygonRequest> polygonRequestList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(queryPath)));
        // 处理query.json多个查询
        if (jsonString.charAt(0) == '{') jsonString = '[' + jsonString + ']';
        JSONArray queryJsonArray = JSONArray.parseArray(jsonString);
        // 处理一条SQL语句
        for (int i = 0; i < queryJsonArray.size(); i++) {
//            List<FD_Variable> variableList = new ArrayList<>();
            FederateService.PolygonRequest.Builder expression = FederateService.PolygonRequest.newBuilder();
            JSONObject queryJson = queryJsonArray.getJSONObject(i);
            expression.setFunction(queryJson.getString("function"));
            expression.setTable(queryJson.getString("table"));
            // 保存 params
            for (Object varObj : queryJson.getJSONArray("params")) {
                JSONObject var = (JSONObject) varObj;
                String type = var.getString("type");
                String value = var.getString("value");
                if ("polygon".equals(type)) {//TODO 简化
                    FederateCommon.Polygon polygon = SpatialFunctions.PolygonFromText(value);
                    expression.setPolygon(polygon);
                }
            }
            polygonRequestList.add(expression.build());
        }
        return polygonRequestList;
    }


    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {

        //----------------test 1 Polygon
        try {

            String queryFile = "query2.json";
            List<FederateService.PolygonRequest> polygonRequests = parsePolygonRequest(queryFile);

            for (FederateService.PolygonRequest polygonRequest : polygonRequests) {
                System.out.printf("====================== NO.%d query statement =============================%n", polygonRequests.indexOf(polygonRequest));
                federatePolygonRangeQuery(polygonRequest);
                federatePrivacyPolygonRangeQuery(polygonRequest.toBuilder().setUuid(UUID.randomUUID().toString()).build());
                System.out.println("===========================================================================");
            }

        } catch (Exception e) {
            LOGGER.error(buildErrorMessage(e));
        }

//        try {
//
//            String queryFile = "query.json";
//            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);
//
//            for (SQLExpression expression : sqlExpressions) {
//                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
//
//                if (expression.getFunction().equals("RangeCount")){
////                    int x= federateRangeCount(expression);
////                    System.out.println("count sum "+x);
//
//                }
//                if(expression.getFunction().equals("RangeQuery")){
////                    federateRangeQuery(expression);
////                    federatePrivacyRangeQuery(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
//
//                }
//                if(expression.getFunction().equals("Knn")){
//                    federateKnn(expression);
//                    System.out.println("===========================================================================");
//
//                    federatePrivacyKnn(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
//                }
//
//                System.out.println("===========================================================================");
//            }
//
//        } catch (Exception e) {
////
//            LOGGER.error(buildErrorMessage(e));
//        }
    }



    // System.out.println(content) 不显示行号，找不到是哪 print 的
//    public static void print(String content) {
//        if (STDOUT == 0) {
//            System.out.println(content);
//        } else if (STDOUT == 1) {
//            LOGGER.info(content);
//        }
//    }
    //TODO 用完清理垃圾

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
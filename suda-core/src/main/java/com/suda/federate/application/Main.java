package com.suda.federate.application;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.config.DbConfig;
import com.suda.federate.config.ModelConfig;
import com.suda.federate.driver.FederateDriver;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.sql.common.SQLGenerator;
import com.suda.federate.sql.enumerator.StreamingIterator;
import com.suda.federate.sql.function.SpatialFunctions;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.FederateUtils;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

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
            expression.setTable(queryJson.getString("table"));
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
    public static ModelConfig modelConfigInitialization(String configFile) throws IOException, SQLException, ClassNotFoundException {
        String configPath = FederateUtils.getRealPath(configFile);
        List<DbConfig> configList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(configPath)));
        // 可能有多个数据源，写成 json array 格式
        ModelConfig obj = JSONObject.parseObject(jsonString, ModelConfig.class);
        return obj;
    }
    public static void federateKnn(SQLExpression expression,Client client){
        //#TODO 需要多个client
        client.knnRadiusQuery(expression);
    }
    public static Integer federateRangeCount(SQLExpression expression,Map<String,Map<String,String>> tableMap,List<FederateDBClient> federateDBClients ){
        ExecutorService executorService = Executors.newFixedThreadPool(federateDBClients.size());
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Integer> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    String endpoint= federateDBClient.getEndpoint();
                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);
                    SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();

                    try{
                        int count= federateDBClient.rangeCount(expression);
                        System.out.println(endpoint+" 服务器返回信息："+ count);
                        iterator.add(count);
                    }catch (StatusRuntimeException e){
                        System.out.println("RPC调用失败："+e.getMessage());
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }
        try {
            List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
            for (Future<Boolean> status : statusList) {
                if (!status.get()) {
                    LOGGER.error("error in fedSpatialPublicQuery");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        int count =0;
        while(iterator.hasNext()){
            int c= iterator.next();
            count+=c;
            System.out.println();
        }
        return count;



    }

    public static void federateRangeQuery(SQLExpression expression,Map<String,Map<String,String>> tableMap,List<FederateDBClient> federateDBClients ){
        ExecutorService executorService = Executors.newFixedThreadPool(federateDBClients.size());
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<FederateService.SQLReplyList> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    String endpoint= federateDBClient.getEndpoint();
                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);
                    SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

                    try{
                        FederateService.SQLReplyList  replylist = federateDBClient.rangeQuery(expression);
                        System.out.println(endpoint+" 服务器返回信息："+ replylist.getMessageList());
                        iterator.add(replylist);
                    }catch (StatusRuntimeException e){
                        System.out.println("RPC调用失败："+e.getMessage());
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }
        try {
            List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
            for (Future<Boolean> status : statusList) {
                if (!status.get()) {
                    LOGGER.error("error in fedSpatialPublicQuery");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
    public static StreamingIterator<Double> federateKnnRadiusQuery(SQLExpression expression,Map<String,Map<String,String>> tableMap,List<FederateDBClient> federateDBClients ){
        ExecutorService executorService = Executors.newFixedThreadPool(federateDBClients.size());
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Double> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    String endpoint= federateDBClient.getEndpoint();
                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);
                    SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

                    try{
                        Double radius= federateDBClient.knnRadiusQuery(expression);//TODO 精度
                        System.out.println(endpoint+" 服务器返回信息：radius "+ radius);
                        iterator.add(radius);
                    }catch (StatusRuntimeException e){
                        System.out.println("RPC调用失败："+e.getMessage());
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }
        try {
            List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
            for (Future<Boolean> status : statusList) {
                if (!status.get()) {
                    LOGGER.error("error in fedSpatialPublicQuery");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return iterator;

    }
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        List<FederateDBClient> federateDBClients = new ArrayList<>();
        Map<String,Map<String,String>> tableMap = new HashMap<>();
        String modelFile= "model.json";
        ModelConfig modelConfig = modelConfigInitialization(modelFile);
        ModelConfig.Schemas schema = modelConfig.getSchemas().get(0);
        List<ModelConfig.Tables>  tables = schema.getTables();
        for (ModelConfig.Tables table : tables) {
            String federateTableName = table.getName();
            ModelConfig.Operand operand = table.getOperand();
            List<ModelConfig.Feds> siloTables = operand.getFeds();
            Map<String,String> oneTableMap = new HashMap<>();
            for (ModelConfig.Feds feds : siloTables) {
                String endpoint = feds.getEndpoint();
                String siloTableName=feds.getName();
                String es[] =endpoint.split(":");
                String ip =es[0];
                int port = Integer.parseInt(es[1]);
                oneTableMap.put(endpoint,siloTableName);
                //TODO .put(feds)
                federateDBClients.add(new FederateDBClient(ip,port));
            }
            tableMap.put(federateTableName,oneTableMap);
        }
        try {

            String queryFile = "query.json";

            // TODO 解析 query.json，获取原始 SQL
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
                // TODO: query
                if (expression.getFunction().equals("RangeCount")){
                    int x= federateRangeCount(expression,tableMap,federateDBClients);
                    System.out.println("count sum"+x);
                }
                if(expression.getFunction().equals("RangeQuery")){
                    federateRangeQuery(expression,tableMap,federateDBClients);
                }
                if(expression.getFunction().equals("Knn")){
                    federateKnn(expression,tableMap,federateDBClients);
                }

                System.out.println("===========================================================================");
            }

        } catch (Exception e) {
//
            LOGGER.error(buildErrorMessage(e));
        }
//        Client client = new Client(8887,"127.0.0.1");
//        try {
//
//            String queryFile = "query.json";
//
//            // TODO 解析 query.json，获取原始 SQL
//            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);
//
//            for (SQLExpression expression : sqlExpressions) {
//                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
//                // TODO: query
//                if (expression.getFunction().equals("RangeCount")){
//                    client.rangeCount(expression);}
//                if(expression.getFunction().equals("RangeQuery")){
//                    client.rangeQuery(expression);}
//                if(expression.getFunction().equals("Knn")){
//                    federateKnn(expression,client);
//                }
//
//                System.out.println("===========================================================================");
//            }
//
//        } catch (Exception e) {
////            e.printStackTrace();
//            try {
//                client.shutDown();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//            LOGGER.error(buildErrorMessage(e));
//        }
    }

    private static void federateKnn(SQLExpression expression, Map<String, Map<String, String>> tableMap, List<FederateDBClient> federateDBClients) {
        StreamingIterator<Double> radiusIterator=federateKnnRadiusQuery(expression,tableMap,federateDBClients);
        double minRadius = Double.MAX_VALUE;
        while (radiusIterator.hasNext()){
            double r= radiusIterator.next();
            minRadius = r < minRadius ? r : minRadius;
        }
        int k =(int)expression.getLiteral();//TODO 精确度
        double l = 0.0, u = minRadius, e = 1e-3;
        double threshold = minRadius;
        while (u - l >= e) {
            threshold = (l + u) / 2;

            SQLExpression queryExpression = expression.toBuilder().setLiteral(threshold).build();

            int count =federateRangeCount(
                    queryExpression,tableMap,federateDBClients
            );
            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
            if (count > k) {
                u = threshold;
            } else if (count < k) {
                l = threshold;
            } else {
                federateRangeQuery(expression.toBuilder().setLiteral(threshold).build(),tableMap,federateDBClients);
                return;
            }
        }
        System.out.println("out of loop! approximate query: ");
        federateRangeQuery(expression.toBuilder().setLiteral(minRadius).build(),tableMap,federateDBClients);
        return;
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
package com.suda.federate.application;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.config.DbConfig;
import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.sql.enumerator.StreamingIterator;
import com.suda.federate.sql.function.SpatialFunctions;
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

import static com.suda.federate.security.sha.SecretSum.computeS;
import static com.suda.federate.security.sha.SecretSum.lag;

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



    public static Integer federateRangeCount(SQLExpression expression){
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<FederateService.SQLReply> iterator = new StreamingIterator<>(federateDBClients.size());


        for (Map.Entry<String,FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint= federateDBClient.getEndpoint();
                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);

                    try{

                        FederateService.SQLReply reply= federateDBClient.rangeCount(expression.toBuilder()
                                .setT(t).setId(endpoint2Id.get(endpoint))
                                .addAllIdList(idList).setTable(siloTableName).build());
                        System.out.println(endpoint+" 服务器返回信息："+ reply.getMessage());
                        iterator.add(reply);
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
        //TODO beautify
        int count =0;
        int i=0;
        int[][] fakeLocalSumList =new int[t][];
        while(iterator.hasNext()){
            FederateService.SQLReply reply = iterator.next();
            count+=reply.getMessage();
            if(i<t){
                fakeLocalSumList[i]=reply.getFakeLocalSumList().stream().mapToInt(Integer::intValue).toArray();
            }
            i+=1;
            System.out.println();
        }
        int[] tempS =computeS(fakeLocalSumList);
        int[] s = Arrays.copyOfRange(tempS, 1, t+1);
        int secureSum = lag(idList.stream().mapToInt(Integer::intValue).toArray(), s, 0);
        System.out.println("secure count "+secureSum);
        return count;



    }

    public static void federateRangeQuery(SQLExpression expression){
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<FederateService.SQLReplyList> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String,FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint= federateDBClient.getEndpoint();
                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);
                    SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

                    try{
                        FederateService.SQLReplyList  replylist = federateDBClient.rangeQuery(expression);
//                        System.out.println(endpoint+" 服务器返回信息："+ replylist.getMessageList());
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
        List<FederateCommon.Point> result = new ArrayList<>();
        while(iterator.hasNext()){
            result.addAll(iterator.next().getMessageList());
        }
        for(FederateCommon.Point r : result){
            System.out.println(r);
        }
        System.out.println("public range query count:"+ result.size());

    }
    public static void federatePrivacyRangeQuery(SQLExpression expression){
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());
        String uuid = expression.getUuid();
        for (Map.Entry<String,FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint= federateDBClient.getEndpoint();

                    try{
                        boolean  reply = federateDBClient.privacyRangeQuery(expression);
                        System.out.println(endpoint+" 服务器返回信息：privacyRangeQuery "+ reply);
                        iterator.add(reply);
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



        FederateService.UnionRequest.Builder unionRequest = FederateService.UnionRequest.newBuilder();
        List<String> endpoints = new ArrayList<>(endpoint2Id.keySet());
        int index=-1;//TODO hardcode
        String leader =endpoints.get(index+1);
        unionRequest.setLoop(1).setIndex(index).setUuid(uuid).addAllEndpoints(endpoints);

        FederateDBClient leaderFederateDBClient =federateDBClients.get(leader);
        FederateService.UnionResponse unionResponse=leaderFederateDBClient.privacyUnion(unionRequest.build());

        System.out.println(unionResponse.getPointList());
        System.out.println(unionResponse.getPointCount());

    }
    public static StreamingIterator<Double> federateKnnRadiusQuery(SQLExpression expression ){

        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Double> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String,FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
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
    public static Map<String,FederateDBClient> federateDBClients = new HashMap<>();
    public static final Map<String,Map<String,String>> tableMap = new HashMap<>();
    public static final ExecutorService executorService;
    public static final List<Integer> idList;//secure sum id list
    public static final Map<String,Integer> endpoint2Id= new HashMap<>();//secure sum id
    public static final Integer t;//secure sum t
    static {
        String modelFile= "model.json";
        ModelConfig modelConfig = null;
        try {
            modelConfig = modelConfigInitialization(modelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if (federateDBClients.containsKey(endpoint)){
                    continue;
                }
                federateDBClients.put(endpoint,new FederateDBClient(ip,port));
            }
            tableMap.put(federateTableName,oneTableMap);
        }
        executorService= Executors.newFixedThreadPool(federateDBClients.size());
        idList= new ArrayList<Integer>(){{for(int i=0;i<federateDBClients.size();i++){add(i+1);}}};
        t=federateDBClients.size();
        int i = 0;
        for (String endpoint : federateDBClients.keySet()) {
            endpoint2Id.put(endpoint,idList.get(i));//or put idList[i];
            i+=1;
        }
    }
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {

        try {

            String queryFile = "query.json";
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));

                if (expression.getFunction().equals("RangeCount")){
//                    int x= federateRangeCount(expression);
//                    System.out.println("count sum "+x);

                }
                if(expression.getFunction().equals("RangeQuery")){
//                    federateRangeQuery(expression);
//                    federatePrivacyRangeQuery(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());

                }
                if(expression.getFunction().equals("Knn")){
                    federateKnn(expression);
                    System.out.println("===========================================================================");

                    federatePrivacyKnn(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
                }

                System.out.println("===========================================================================");
            }

        } catch (Exception e) {
//
            LOGGER.error(buildErrorMessage(e));
        }
    }

    private static void federateKnn(SQLExpression expression) {
        StreamingIterator<Double> radiusIterator=federateKnnRadiusQuery(expression);
        double minRadius = Double.MAX_VALUE;
        while (radiusIterator.hasNext()){
            double r= radiusIterator.next();
            minRadius = r < minRadius ? r : minRadius;
        }
        int k =(int)expression.getLiteral();//TODO 精确度
        double l = 0.0, u = minRadius, e = 1e-3;
        double threshold = minRadius;
        while (u - l >= e) {//TODO 改为并发？！
            threshold = (l + u) / 2;

            SQLExpression queryExpression = expression.toBuilder().setLiteral(threshold).build();

            int count =federateRangeCount(
                    queryExpression);
            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
            if (count > k) {
                u = threshold;
            } else if (count < k) {
                l = threshold;
            } else {
                federateRangeQuery(expression.toBuilder().setLiteral(threshold).build());
                return;
            }
        }
        System.out.println("out of loop! approximate query: ");
        federateRangeQuery(expression.toBuilder().setLiteral(minRadius).build());
        return;
    }
    private static void federatePrivacyKnn(SQLExpression expression) {
        StreamingIterator<Double> radiusIterator=federateKnnRadiusQuery(expression);
        double minRadius = Double.MAX_VALUE;
        while (radiusIterator.hasNext()){
            double r= radiusIterator.next();
            minRadius = r < minRadius ? r : minRadius;
        }
        int k =(int)expression.getLiteral();//TODO 精确度
        double l = 0.0, u = minRadius, e = 1e-3;
        double threshold = minRadius;
        while (u - l >= e) {//TODO 改为并发？！
            threshold = (l + u) / 2;

            SQLExpression queryExpression = expression.toBuilder().setLiteral(threshold).build();

            int count =federateRangeCount(queryExpression);//TODO secure
            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
            if (count > k) {
                u = threshold;
            } else if (count < k) {
                l = threshold;
            } else {
                federatePrivacyRangeQuery(expression.toBuilder().setLiteral(threshold).build());
                return;
            }
        }
        System.out.println("out of loop! approximate query: ");
        federatePrivacyRangeQuery(expression.toBuilder().setLiteral(minRadius).build());
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
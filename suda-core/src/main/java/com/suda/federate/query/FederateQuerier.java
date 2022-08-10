package com.suda.federate.query;

import com.suda.federate.application.FederateDBClient;
import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.FederateUtils;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static com.suda.federate.security.sha.SecretSum.computeS;
import static com.suda.federate.security.sha.SecretSum.lag;

public class FederateQuerier {
    private final Logger LOGGER = LoggerFactory.getLogger(FederateQuerier.class);
    private Map<String, FederateDBClient> federateDBClients = new HashMap<>();
    private final Map<String, Map<String, String>> tableMap = new HashMap<>();
    private final ExecutorService executorService;
    private final Map<String, Integer> endpoint2Id = new HashMap<>();//secure sum id
    private final List<Integer> idList;//secure sum id list
    private final Integer t;//secure sum t

    public FederateQuerier(String modelFile) throws IOException {
        ModelConfig modelConfig = FederateUtils.parseModelConfig(modelFile);
        ModelConfig.Schemas schema = modelConfig.getSchemas().get(0);
        List<ModelConfig.Tables> tables = schema.getTables();
        for (ModelConfig.Tables table : tables) {
            String federateTableName = table.getName();
            ModelConfig.Operand operand = table.getOperand();
            List<ModelConfig.Feds> siloTables = operand.getFeds();

            Map<String, String> oneTableMap = new HashMap<>();
            for (ModelConfig.Feds feds : siloTables) {
                String endpoint = feds.getEndpoint();
                String siloTableName = feds.getName();
                String[] es = endpoint.split(":");
                String ip = es[0];
                int port = Integer.parseInt(es[1]);
                oneTableMap.put(endpoint, siloTableName);
                //TODO .put(feds)
                federateDBClients.putIfAbsent(endpoint, new FederateDBClient(ip, port));
            }
            tableMap.put(federateTableName, oneTableMap);
        }
        executorService = Executors.newFixedThreadPool(federateDBClients.size());
        idList = new ArrayList<Integer>() {{
            for (int i = 0; i < federateDBClients.size(); i++) {
                add(i + 1);
            }
        }};
        t = federateDBClients.size();
        int i = 0;
        for (String endpoint : federateDBClients.keySet()) {
            endpoint2Id.put(endpoint, idList.get(i));//or put idList[i];
            i += 1;
        }
    }

    /**
     * federate query packaging
     *
     * @param expression
     * @param privacyFlag
     */
    public void query(FederateService.SQLExpression expression, boolean privacyFlag) throws ExecutionException, InterruptedException {
        if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_COUNT)) {
            federateRangeCount(expression);
//                    FederateQuery.federateRangeQuery(expression);
        } else if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_QUERY)) {
            federateRangeQuery(expression);
//                    federatePrivacyRangeQuery(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
        } else if (expression.getFunction().equals(FederateService.SQLExpression.Function.KNN)) {
//                    federateKnn(expression);
//                    federatePrivacyKnn(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
        } else if (expression.getFunction().equals(FederateService.SQLExpression.Function.POLYGON_RANGE_QUERY)) {
            federatePolygonRangeQuery(expression);
//                    federatePrivacyPolygonRangeQuery(expression.toBuilder().setUuid(UUID.randomUUID().toString()).build());
        } else {
            return;
        }
    }

    public void federateRangeQuery(FederateService.SQLExpression expression) throws InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<FederateService.SQLReplyList> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    FederateService.SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能
                    FederateService.SQLReplyList replyList = federateDBClient.rangeQuery(queryExpression);
                    System.out.println(endpoint + " 服务器返回信息：" + replyList.getMessageList());
                    iterator.add(replyList);
                    return true;
                } catch (Exception e) {
                    // LOGGER.error("RPC调用失败：" + "error in fedSpatialPublicQuery" + e.getMessage());
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
        while (iterator.hasNext()) {
            result.addAll(iterator.next().getMessageList());
        }
        for (FederateCommon.Point r : result) {
            System.out.println(r);
        }
        System.out.println("public range query count:" + result.size());
    }

    public void federatePrivacyRangeQuery(FederateService.SQLExpression expression) {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());
        String uuid = expression.getUuid();
        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    try {
                        boolean reply = federateDBClient.privacyRangeQuery(expression);
                        System.out.println(endpoint + " 服务器返回信息：privacyRangeQuery " + reply);
                        iterator.add(reply);
                    } catch (StatusRuntimeException e) {
                        System.out.println("RPC调用失败：" + e.getMessage());
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
        int index = -1;//TODO hardcode
        String leader = endpoints.get(index + 1);
        unionRequest.setLoop(1).setIndex(index).setUuid(uuid).addAllEndpoints(endpoints);

        FederateDBClient leaderFederateDBClient = federateDBClients.get(leader);
        FederateService.UnionResponse unionResponse = leaderFederateDBClient.privacyUnion(unionRequest.build());

        System.out.println(unionResponse.getPointList());
        System.out.println(unionResponse.getPointCount());
        //清理各silo垃圾
        clearCache(uuid);

    }

    public void federatePolygonRangeQuery(FederateService.SQLExpression expression) throws ExecutionException, InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<FederateService.SQLReplyList> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    FederateService.SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

                    try {
                        FederateService.SQLReplyList replylist = federateDBClient.polygonRangeQuery(queryExpression);
//                        System.out.println(endpoint+" 服务器返回信息："+ replylist.getMessageList());
                        iterator.add(replylist);
                    } catch (StatusRuntimeException e) {
                        System.out.println("RPC调用失败：" + e.getMessage());
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
        while (iterator.hasNext()) {
            result.addAll(iterator.next().getMessageList());
        }
        for (FederateCommon.Point r : result) {
            System.out.println(r);
        }
        System.out.println("public polygon range query count:" + result.size());

    }

    public void federatePrivacyPolygonRangeQuery(FederateService.SQLExpression expression) {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());
        String uuid = expression.getUuid();
        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    FederateService.SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能
                    try {
                        boolean status = federateDBClient.privacyPolygonRangeQuery(queryExpression);
                        System.out.println(endpoint + " 服务器返回信息：" + status);
                        iterator.add(status);
                    } catch (StatusRuntimeException e) {
                        System.out.println("RPC调用失败：" + e.getMessage());
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
        int index = -1;//TODO hardcode
        String leader = endpoints.get(index + 1);
        unionRequest.setLoop(1).setIndex(index).setUuid(uuid).addAllEndpoints(endpoints);

        FederateDBClient leaderFederateDBClient = federateDBClients.get(leader);
        FederateService.UnionResponse unionResponse = leaderFederateDBClient.privacyUnion(unionRequest.build());

        System.out.println(unionResponse.getPointList());
        System.out.println(unionResponse.getPointCount());
        //清理各silo垃圾
        clearCache(uuid);

    }

    private StreamingIterator<Double> federateKnnRadiusQuery(FederateService.SQLExpression expression) throws InterruptedException, ExecutionException {

        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Double> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    FederateService.SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

                    try {
                        Double radius = federateDBClient.knnRadiusQuery(expression);//TODO 精度
                        System.out.println(endpoint + " 服务器返回信息：radius " + radius);
                        iterator.add(radius);
                    } catch (StatusRuntimeException e) {
                        System.out.println("RPC调用失败：" + e.getMessage());
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

    public Integer federateRangeCount(FederateService.SQLExpression expression) throws InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<FederateService.SQLReply> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    try {

                        FederateService.SQLReply reply = federateDBClient.rangeCount(expression.toBuilder()
                                .setT(t).setId(endpoint2Id.get(endpoint))
                                .addAllIdList(idList).setTable(siloTableName).build());
                        System.out.println(endpoint + " 服务器返回信息：" + reply.getMessage());
                        iterator.add(reply);
                    } catch (StatusRuntimeException e) {
                        System.out.println("RPC调用失败：" + e.getMessage());
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
        int count = 0;
        int i = 0;
        List<List<Integer>> fakeLocalSumList = new ArrayList<>();
        while (iterator.hasNext()) {
            FederateService.SQLReply reply = iterator.next();
            count += reply.getMessage();
            // 有可能返回超过t个？
            if (i < t) {
                fakeLocalSumList.add(reply.getFakeLocalSumList());
            }
            i += 1;
        }
        List<Integer> S = computeS(fakeLocalSumList);
        int secureSum = lag(idList, S, 0);
        System.out.println("secure count: " + secureSum);
        return count;

    }

    public void clearCache(String uuid) {
        for (FederateDBClient client : federateDBClients.values()) {
            client.clearCache(uuid);
        }
    }


}

package com.suda.federate.query;

import com.suda.federate.application.FederateDBClient;
import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.FederateUtils;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static com.suda.federate.security.sha.SecretSum.computeS;
import static com.suda.federate.security.sha.SecretSum.lag;

public class FederateQuery {
    public static final Logger LOGGER = LoggerFactory.getLogger(FederateQuery.class);

    public static Map<String, FederateDBClient> federateDBClients = new HashMap<>();
    public static final Map<String, Map<String, String>> tableMap = new HashMap<>();
    public static final ExecutorService executorService;
    public static final Map<String, Integer> endpoint2Id = new HashMap<>();//secure sum id
    public static final List<Integer> idList;//secure sum id list

    public static final Integer t;//secure sum t

    static {
        String modelFile = "model.json";
        ModelConfig modelConfig = null;
        try {
            modelConfig = FederateUtils.modelConfigInitialization(modelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                String es[] = endpoint.split(":");
                String ip = es[0];
                int port = Integer.parseInt(es[1]);
                oneTableMap.put(endpoint, siloTableName);
                //TODO .put(feds)
                if (federateDBClients.containsKey(endpoint)) {
                    continue;
                }
                federateDBClients.put(endpoint, new FederateDBClient(ip, port));
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

    public static void federateRangeQuery(FederateService.SQLExpression expression) {
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
                        FederateService.SQLReplyList replylist = federateDBClient.rangeQuery(queryExpression);
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
        System.out.println("public range query count:" + result.size());

    }

    public static void federatePrivacyRangeQuery(FederateService.SQLExpression expression) {
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

    public static void federatePolygonRangeQuery(FederateService.PolygonRequest polygonRequest) {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<FederateService.SQLReplyList> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(polygonRequest.getTable()).get(endpoint);
                    FederateService.PolygonRequest queryExpression = polygonRequest.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能

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

    public static void federatePrivacyPolygonRangeQuery(FederateService.PolygonRequest polygonRequest) {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());
        String uuid = polygonRequest.getUuid();
        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(polygonRequest.getTable()).get(endpoint);
                    FederateService.PolygonRequest queryExpression = polygonRequest.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能
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

    public static StreamingIterator<Double> federateKnnRadiusQuery(FederateService.SQLExpression expression) {

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


    public static Integer federateRangeCount(FederateService.SQLExpression expression) {
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
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
        int[][] fakeLocalSumList = new int[t][];
        while (iterator.hasNext()) {
            FederateService.SQLReply reply = iterator.next();
            count += reply.getMessage();
            if (i < t) {
                fakeLocalSumList[i] = reply.getFakeLocalSumList().stream().mapToInt(Integer::intValue).toArray();
            }
            i += 1;
            System.out.println();
        }
        int[] tempS = computeS(fakeLocalSumList);
        int[] s = Arrays.copyOfRange(tempS, 1, t + 1);
        int secureSum = lag(idList.stream().mapToInt(Integer::intValue).toArray(), s, 0);
        System.out.println("secure count " + secureSum);
        return count;


    }

    public static void clearCache(String uuid) {
        for (FederateDBClient client : federateDBClients.values()) {
            client.clearCache(uuid);
        }
    }


}

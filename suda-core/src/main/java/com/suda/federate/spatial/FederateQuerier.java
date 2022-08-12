package com.suda.federate.spatial;

import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FederateQuerier {

    /**
     * key : endpoints
     * value: DBClient
     */
    public Map<String, FederateDBClient> federateDBClients = new HashMap<>();
    private final Map<String, Map<String, String>> tableMap = new HashMap<>();
    private final ExecutorService executorService;
    private final Map<String, Integer> endpoint2Id = new HashMap<>();//secure sum id
    private final List<Integer> idList;//secure sum id list
    private final Integer t;//secure sum t

    public FederateQuerier(String modelFile) throws IOException {
        /**
         * model.json 解析
         */
        ModelConfig modelConfig = FederateUtils.parseModelConfig(modelFile);
        ModelConfig.Schemas schema = modelConfig.getSchemas().get(0);
        for (ModelConfig.Tables table : schema.getTables()) {
            Map<String, String> oneSiloMap = new HashMap<>();
            for (ModelConfig.Feds feds : table.getFeds()) {
                oneSiloMap.put(feds.getEndpoint(), feds.getSiloTableName());
                //TODO .put(feds)
                federateDBClients.putIfAbsent(feds.getEndpoint(), new FederateDBClient(feds.getIp(), feds.getPort()));
            }
            tableMap.put(table.getName(), oneSiloMap);
        }
        executorService = Executors.newFixedThreadPool(federateDBClients.size());

        /**
         * 参数初始化
         */
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
     * public query
     *
     * @param expression
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public boolean fedSpatialPublicQuery(FederateService.SQLExpression expression) throws InterruptedException, ExecutionException {
        StreamingIterator<FederateService.SQLReply> iterator = new StreamingIterator<>(federateDBClients.size());
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    FederateService.SQLReply reply = federateDBClient.fedSpatialQuery(expression, siloTableName);
                    System.out.println(endpoint + " 服务器返回信息：" + reply.getNum());
                    iterator.add(reply);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }
        // TODO 如果某个 data silos 阻塞，全局都会阻塞
        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);

        checkStatus(statusList, Future.class);

        // 数字
        if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_COUNT)) {
            int count = 0;
            while (iterator.hasNext())
                count += iterator.next().getNum();
            System.out.println("count: " + count);
        }

        // Point List
        else {
            List<FederateCommon.Point> result = new ArrayList<>();
            while (iterator.hasNext())
                result.addAll(iterator.next().getPointList());
            System.out.println("public range query count:" + result.size());
            System.out.println(FederateUtils.flatPointList(result));
        }
        return true;
    }

    /**
     * privacy radius known query
     *
     * @param expression
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public boolean fedSpatialPrivacyQuery(FederateService.SQLExpression expression) throws InterruptedException, ExecutionException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());

        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
            tasks.add(() -> {
                try {
                    FederateDBClient federateDBClient = entry.getValue();
                    String endpoint = federateDBClient.getEndpoint();
                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
                    Boolean status = federateDBClient.fedSpatialPrivacyQuery(expression.toBuilder()
                            .setT(t).setId(endpoint2Id.get(endpoint))
                            .addAllIdList(idList).setTable(siloTableName).build());
                    iterator.add(status);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }

        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
        checkStatus(statusList, Future.class);

        // 数字
        if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_COUNT)) {

            int leaderIndex = 0;
            List<String> endpoints = new ArrayList<>(endpoint2Id.keySet());
            FederateService.SummationRequest.Builder requestBuilder = FederateService.SummationRequest.newBuilder();

            requestBuilder.setIndex(leaderIndex).addAllIdList(idList).setSiloSize(t).setUuid(expression.getUuid()).addAllEndpoints(endpoints);

            FederateDBClient leaderFederateDBClient = federateDBClients.get(endpoints.get(leaderIndex));

            FederateService.SummationResponse summationResponse = leaderFederateDBClient.privacySummation(requestBuilder.setResponse(FederateService.SummationResponse.newBuilder().build()).build());

            System.out.println("secure count: " + summationResponse.getCount());
            //System.out.println("secure list: " + FederateUtils.flatPointList(unionResponse.getPointList()));
            //清理各silo垃圾
            clearCache(expression.getUuid());
            return true;
            //int count = 0;
            //int i = 0;
            //List<List<Integer>> fakeLocalSumList = new ArrayList<>();
            //while (iterator.hasNext()) {
            //    FederateService.SQLReply reply = iterator.next();
            //    count += reply.getNum();
            //    // 有可能返回超过t个？
            //    if (i < t) {
            //        fakeLocalSumList.add(reply.getFakeLocalSumList());
            //    }
            //    i += 1;
            //}
            //List<Integer> S = computeS(fakeLocalSumList);
            //int secureSum = lag(idList, S, 0);
            //System.out.println("secure count: " + secureSum);
        } else {
            FederateService.UnionRequest.Builder unionRequest = FederateService.UnionRequest.newBuilder();
            List<String> endpoints = new ArrayList<>(endpoint2Id.keySet());
            int index = -1;//TODO hardcode
            String leader = endpoints.get(index + 1);

            String uuid = expression.getUuid();
            unionRequest.setLoop(1).setIndex(index).setUuid(uuid).addAllEndpoints(endpoints);
            FederateDBClient leaderFederateDBClient = federateDBClients.get(leader);
            FederateService.UnionResponse unionResponse = leaderFederateDBClient.privacyUnion(unionRequest.build());
            System.out.println("secure count: " + unionResponse.getPointCount());
            System.out.println("secure list: " + FederateUtils.flatPointList(unionResponse.getPointList()));
            //清理各silo垃圾
            clearCache(uuid);
            return true;
        }
    }


    private void privacyKnn() {

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
                    LogUtils.error("error in fedSpatialPublicQuery");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return iterator;

    }

    private void checkStatus(List<?> statusList, Class clazz) {
        boolean status = true;
        //if(statusList.get(0))
        if (clazz == Boolean.class) {
            status = statusList.stream().allMatch(x -> (Boolean) x);
        } else if (clazz == Future.class) {
            status = statusList.stream().allMatch(x -> {
                try {
                    return ((Future<Boolean>) x).get();
                } catch (Exception e) {
                    return false;
                }
            });
        }
        if (!status)
            throw new RuntimeException("error in fedSpatial Query");
    }


    private void clearCache(String uuid) {
        for (FederateDBClient client : federateDBClients.values()) {
            client.clearCache(uuid);
        }
    }
}

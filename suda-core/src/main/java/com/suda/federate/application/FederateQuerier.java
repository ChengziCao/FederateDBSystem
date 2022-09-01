//package com.suda.federate.application;
//
//import com.suda.federate.config.ModelConfig;
//import com.suda.federate.rpc.FederateCommon;
//import com.suda.federate.rpc.FederateService;
//import com.suda.federate.silo.FederateDBClient;
//import com.suda.federate.utils.FederateUtils;
//import com.suda.federate.utils.LogUtils;
//import com.suda.federate.utils.StreamingIterator;
//import javafx.util.Pair;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.List;
//import java.util.concurrent.*;
//
//import static com.suda.federate.utils.ENUM.str2FUNCTION;
//
//public class FederateQuerier {
//
//    /**
//     * key : endpoints
//     * value: DBClient
//     */
//    private final Map<String, FederateDBClient> federateDBClients = new HashMap<>();
//    private final Map<String, Map<String, String>> tableMap = new HashMap<>();
//    private final ExecutorService executorService;
//    private final Map<String, Integer> endpoint2Id = new HashMap<>();//secure sum id
//    private final List<Integer> idList;//secure sum id list
//    private final Integer t;//secure sum t
//
//
//    public FederateQuerier(String modelFile) throws IOException {
//        /**
//         * model.json 解析
//         */
//        ModelConfig modelConfig = FederateUtils.parseModelConfig(modelFile);
//        ModelConfig.Schemas schema = modelConfig.getSchemas().get(0);
//        for (ModelConfig.Tables table : schema.getTables()) {
//            Map<String, String> oneSiloMap = new HashMap<>();
//            for (ModelConfig.Feds feds : table.getFeds()) {
//                oneSiloMap.put(feds.getEndpoint(), feds.getSiloTableName());
//                //TODO .put(feds)
//                federateDBClients.putIfAbsent(feds.getEndpoint(), new FederateDBClient(feds.getIp(), feds.getPort()));
//            }
//            tableMap.put(table.getName(), oneSiloMap);
//        }
//        executorService = Executors.newFixedThreadPool(federateDBClients.size());
//
//        /**
//         * 参数初始化
//         */
//        idList = new ArrayList<Integer>() {{
//            for (int i = 0; i < federateDBClients.size(); i++) {
//                add(i + 1);
//            }
//        }};
//        t = federateDBClients.size();
//        int i = 0;
//        for (String endpoint : federateDBClients.keySet()) {
//            endpoint2Id.put(endpoint, idList.get(i));//or put idList[i];
//            i += 1;
//        }
//    }
//
//    /**
//     * public query
//     *
//     * @param expression
//     * @throws InterruptedException
//     * @throws ExecutionException
//     */
//    public Object fedSpatialPublicQuery(FederateService.SQLExpression expression) throws Exception {
//
//        StreamingIterator<FederateService.SQLReply> iterator = new StreamingIterator<>(federateDBClients.size());
//        List<Callable<Boolean>> tasks = new ArrayList<>();
//        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
//            tasks.add(() -> {
//                try {
//                    FederateDBClient federateDBClient = entry.getValue();
//                    String endpoint = federateDBClient.getEndpoint();
//                    String siloTableName = tableMap.get(expression.getTable()).get(endpoint);
//                    FederateService.SQLReply reply = federateDBClient.fedSpatialQuery(expression, siloTableName);
//                    // System.out.println(endpoint + " 服务器返回信息：" + reply.getIntegerNumber());
//                    iterator.add(reply);
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    iterator.finish();
//                }
//            });
//        }
//        // TODO 如果某个 data silos 阻塞，全局都会阻塞
//        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
//
//        checkStatus(statusList, Future.class);
//
//        // 数字
//        if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_COUNT)) {
//            int count = 0;
//            while (iterator.hasNext())
//                count += iterator.next().getIntegerNumber();
//
//            LogUtils.debug("count: " + count);
//            return count;
//        }
//
//        // Point List
//        else {
//            List<FederateCommon.Point> result = new ArrayList<>();
//            while (iterator.hasNext()) {
//                result.addAll(iterator.next().getPointList());
//            }
//            LogUtils.debug("public range query count:" + result.size());
//            LogUtils.debug(FederateUtils.flatPointList(result));
//            return result;
//        }
//    }
//
//    /**
//     * privacy radius known query
//     *
//     * @param expression
//     * @throws InterruptedException
//     * @throws ExecutionException
//     */
//    public Object fedPrivacyRadiusKnowQuery(FederateService.SQLExpression expression) throws Exception {
//
//        List<Callable<Boolean>> tasks = new ArrayList<>();
//        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());
//
//        for (Map.Entry<String, FederateDBClient> entry : federateDBClients.entrySet()) {
//            tasks.add(() -> {
//                try {
//                    FederateDBClient federateDBClient = entry.getValue();
//
//                    FederateService.Status status;
//                    switch (expression.getFunction()) {
//                        case RANGE_COUNT:
//                            status = federateDBClient.privacyRangeCount(expression);
//                            break;
//                        case RANGE_QUERY:
//                            status = federateDBClient.privacyRangeQuery(expression);
//                            break;
//                        case POLYGON_RANGE_QUERY:
//                            status = federateDBClient.privacyPolygonRangeQuery(expression);
//                            break;
//                        default:
//                            status = null;
//                    }
//
//                    iterator.add(status.getMsg().equals("ok"));
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    iterator.finish();
//                }
//            });
//        }
//
//        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
//        checkStatus(statusList, Future.class);
//
//        // 数字
//        if (expression.getFunction().equals(FederateService.SQLExpression.Function.RANGE_COUNT)) {
//
//            int leaderIndex = 0;
//            List<String> endpoints = new ArrayList<>(endpoint2Id.keySet());
//            FederateService.SummationRequest.Builder requestBuilder = FederateService.SummationRequest.newBuilder();
//
//            requestBuilder.setIndex(leaderIndex).addAllIdList(idList).setSiloSize(t).setUuid(expression.getUuid()).addAllEndpoints(endpoints);
//
//            FederateDBClient leaderFederateDBClient = federateDBClients.get(endpoints.get(leaderIndex));
//
//            FederateService.SummationResponse summationResponse = leaderFederateDBClient.privacySummation(requestBuilder.setResponse(FederateService.SummationResponse.newBuilder().build()).build());
//
//            LogUtils.debug("secure count: " + summationResponse.getCount());
//            //System.out.println("secure list: " + FederateUtils.flatPointList(unionResponse.getPointList()));
//            //清理各silo垃圾
//            clearCache(expression.getUuid());
//            return summationResponse.getCount();
//
//        }
//        // 集合
//        else {
//            FederateService.UnionRequest.Builder unionRequest = FederateService.UnionRequest.newBuilder();
//            List<String> endpoints = new ArrayList<>(endpoint2Id.keySet());
//            int index = -1;//TODO hardcode
//            String leader = endpoints.get(index + 1);
//            String uuid = expression.getUuid();
//            unionRequest.setLoop(1).setIndex(index).setUuid(uuid).addAllEndpoints(endpoints);
//            FederateDBClient leaderFederateDBClient = federateDBClients.get(leader);
//            FederateService.UnionResponse unionResponse = leaderFederateDBClient.privacyUnion(unionRequest.build());
//            // LogUtils.debug("secure count: " + unionResponse.getPointCount());
//            LogUtils.debug("secure list: " + FederateUtils.flatPointList(unionResponse.getPointList()));
//            //清理各silo垃圾
//            clearCache(uuid);
//            return unionRequest.getPointList();
//        }
//    }
//
//    public List<FederateCommon.Point> federatePrivacyKnn(FederateService.SQLExpression expression) throws Exception {
//        double minRadius = Double.MAX_VALUE;
//        for (FederateDBClient client : federateDBClients.values()) {
//            Double r = client.knnRadiusQuery(expression);
//            minRadius = r < minRadius ? r : minRadius;
//        }
//        int k = expression.getIntegerNumber();
//        double l = 0.0, u = minRadius, e = 1e-3;
//        double threshold = minRadius;
//        while (u - l >= e) {
//            threshold = (l + u) / 2;
//            FederateService.SQLExpression rangeCountExpression = expression.toBuilder()
//                    .setFunction(str2FUNCTION("RANGE_COUNT")).setDoubleNumber(threshold).build();
//            int count = (int) fedPrivacyRadiusKnowQuery(rangeCountExpression);
//
//            if (count > k) {
//                u = threshold;
//            } else if (count < k) {
//                l = threshold;
//            } else {
//                break;
//            }
//        }
//        FederateService.SQLExpression rangeQueryExpression = expression.toBuilder()
//                .setFunction(str2FUNCTION("RANGE_QUERY")).setDoubleNumber(threshold).build();
//        List<FederateCommon.Point> pointList =
//                (List<FederateCommon.Point>) fedPrivacyRadiusKnowQuery(rangeQueryExpression);
//        return pointList;
//    }
//
//
//
//
//
//
//
//    private void clearCache(String uuid) {
//        for (FederateDBClient client : federateDBClients.values()) {
//            client.clearCache(uuid);
//        }
//    }
//}

package com.suda.federate.spatial;

import com.suda.federate.config.ModelConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
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



//    /**
//     *
//     * @param queryPoint
//     * @param S: 参考点集合
//     * @return: query point 与 参考点集合S各个点的垂直平分线
//     */
//    public static HashMap<FederateCommon.Point, Pair<Object, Double>> computeBisector(FederateCommon.Point queryPoint, List<FederateCommon.Point> S) {
//        HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict = new HashMap<>();
//
//        //MyPoint q = new MyPoint(queryPoint.x(), queryPoint.y());
//
//        Object k, b;
//
//        for(FederateCommon.Point s : S) {
//            if (doubleEqual(queryPoint.getLongitude(), s.getLongitude())) {  //垂直平分线平行于y轴
//                k = null;
//                b = (queryPoint.getLatitude() + s.getLatitude()) / 2;
//            } else if (doubleEqual(queryPoint.getLatitude(), s.getLatitude())) { //垂直平分线平行于x轴
//                k = 0;
//                b = (queryPoint.getLongitude() + s.getLongitude()) / 2;
//            } else {
//                Pair<Double, Double> middlePoint = new Pair<>((s.getLatitude()  + queryPoint.getLatitude()) /2, (s.getLongitude() + queryPoint.getLongitude()) / 2);
//                k = -1 / ((queryPoint.getLongitude() - s.getLongitude()) / (queryPoint.getLatitude()  - s.getLatitude()));
//                b = middlePoint.getSecond() - (double)k * middlePoint.getFirst();
//            }
//            bisectorDict.put(s, new Pair<>(k, (double)b));
//        }
//        return bisectorDict;
//    }
//    /**
//     *
//     * @param bisectorDict: 字典：<s: 垂直平分线>, s in S
//     * @return: 字典：<垂直平分线交点, <p1, p2, 0>>, 其中p1和p2分别是垂直平分线l1和l2对应的参考点，这里的0是为了计算每个交点level值初始化
//     */
//    public static HashMap<FederateCommon.Point, Triple<FederateCommon.Point,FederateCommon.Point, Integer>> computeIntersections(HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict) {
//        HashMap<FederateCommon.Point, Triple<FederateCommon.Point,FederateCommon.Point, Integer>> intersectionSetDict = new HashMap<>();
//        for (FederateCommon.Point p1 : bisectorDict.keySet()) {
//            for (FederateCommon.Point p2 : bisectorDict.keySet()) {
//                if (p1.equals(p2)) {
//                    continue;
//                } else {
//                    Pair<Object, Object> intersectionPointTmp = computeIntersection(bisectorDict.get(p1), bisectorDict.get(p2));
//                    if (intersectionPointTmp.equals(new Pair<>(null, null))) {
//                        continue;
//                    } else {
//                        FederateCommon.Point intersectionPoint = FederateCommon.Point.newBuilder()
//                                .setLatitude((double)intersectionPointTmp.getFirst())
//                                        .setLongitude((double)intersectionPointTmp.getSecond())
//                                                .build();
//                        intersectionSetDict.put(intersectionPoint, Triple.of(p1, p2,0));
//                    }
//                }
//            }
//        }
//        return intersectionSetDict;
//    }
//    /**
//     *
//     * @param l1: y = k1*x + b1
//     * @param l2: y = k2*x + b2
//     * @return: l1和l2的交点
//     */
//    public static Pair<Object, Object> computeIntersection(Pair<Object, Double> l1, Pair<Object, Double> l2) {
//        if (l1.getFirst() == null) {
//            if (l2.getFirst() == null) {
//                return new Pair<>(null, null);
//            } else if ((double) l2.getFirst() == 0.0) {
//                return new Pair<>(l1.getSecond(), l2.getSecond());
//            } else {
//                double y = (double) l2.getFirst() * l1.getSecond() + l2.getSecond();
//                return new Pair<>(l1.getSecond(), y);
//            }
//        } else if (l2.getFirst() == null) {
//            if (l1.getFirst() == null) {
//                return new Pair<>(null, null);
//            } else if ((double) l1.getFirst() == 0.0) {
//                return new Pair<>(l2.getSecond(), l1.getSecond());
//            } else {
//                double y = (double) l1.getFirst() * l2.getSecond() + l1.getSecond();
//                return new Pair<>(l2.getSecond(), y);
//            }
//        } else if (l2.getFirst().equals(l1.getFirst())) {
//            return new Pair<>(null, null);
//        } else {
//            double x = (l1.getSecond() - l2.getSecond()) / ((double) l2.getFirst() - (double) l1.getFirst());
//            double y = ((double) l2.getFirst() * l1.getSecond() - (double) l1.getFirst() * l2.getSecond()) / ((double) l2.getFirst() - (double) l1.getFirst());
//            return new Pair<>(x, y);
//        }
//    }
//    public static boolean doubleEqual(double a, double b) {
//        return (a - b > -0.000001) && (a - b) < 0.000001;
//    }
//    //TODO
//    private void federatePublicRkNN(FederateService.SQLExpression expression){
//
//        //step1 ------ 联邦KNN查询，注意，查询KNN时k+1 -> setLiteral(expression.getLiteral()+1)
//
//        List<FederateCommon.Point> S = federateKnn(expression.toBuilder().setLiteral(expression.getLiteral()+1));
//        for (FederateCommon.Point p : S) {
//            System.out.println(p);
//        }
//
//        //step2 ------ 就搁着计算
//        // 对q和S里每一个点 求垂直平分线
//        FederateCommon.Point queryPoint = expression.getPoint();
//        HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict = computeBisector(queryPoint, S);
//        // 计算所有垂直平分线的交点 : HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict = computeIntersections(bisectorDict);
//        // 计算各个交点的level值
//        HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict = computeLevel(computeIntersections(bisectorDict), queryPoint, S);
//        // 筛选交点中level<k的点
//        List<Point>res = legalIntersections(intersectionSetDict, K);
//        // 建立凸包
//        List<Point> CH = convexHull(res);
//
//        //step3 ------ 发CH到 silo 完成RkNN
//        expression=expression.toBuilder().clearPolygon().setPolygon(FederateCommon.Polygon.newBuilder().addAllPoint(CH).build()).build();
//        publicPolygonRangeQuery(expression);
//    }

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

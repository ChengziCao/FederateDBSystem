package com.suda.federate;

import com.suda.federate.config.DbConfig;
import com.suda.federate.utils.SQLGenerator;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.silo.FederateDBService;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.suda.federate.security.sha.SecretSum.setSummation;

public class FederatePostgresqlService extends FederateDBService {

    private DatabaseMetaData metaData;
    private Connection conn;

    FederatePostgresqlService(DbConfig config) {
        try {
            init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(DbConfig config) throws ClassNotFoundException, SQLException {
        Class.forName(config.getDriver());
        conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }


    @Override
    public void rangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Integer result = 0;
        try {
            result = localRangeCount(request.getPoint(), request.getTable(), request.getLiteral());
            FederateService.SQLReply reply = setSummation(request, result);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //        @Override
//        public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
//            Integer currIndex = request.getIndex();
//            Integer currLoop = request.getLoop();
//            if (federateClientMap == null || federateClientMap.isEmpty()) {
//                initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
//            }
//            List<Callable<FederateService.UnionResponse>> task1 = new ArrayList<>();
//
//
//            if (currIndex ==-1 ) {//第一次循环开始
//                currIndex=0;
//                isLeader = true;
//            }
//
//            int nextIndexOfEndPoint = currIndex + 1;
//            int newLoop = currLoop;
//            if (nextIndexOfEndPoint == request.getEndpointsCount()) {
//                nextIndexOfEndPoint = 0;//再来一圈
//                newLoop = currLoop + 1;
//            }
//
//            int finalNextIndexOfEndPoint = nextIndexOfEndPoint;
//            int finalNewLoop = newLoop;
//            System.out.printf("recev %d,%d, next: %d,%d \n",currLoop,currIndex, finalNewLoop, finalNextIndexOfEndPoint);
//
//            if (isLeader && finalNewLoop == 2 && finalNextIndexOfEndPoint == 1) {//2圈结束
//                SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//
//                List<FederateCommon.Point> points = request.getPointList();
//                Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                for (FederateCommon.Point point : points) {
//                    resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                }
//                resPairs.removeAll(siloCache.getObfSet());
//                resPairs.addAll(siloCache.getLocalSet());
//                List<FederateCommon.Point> points2 = new ArrayList<>();
//                for (Pair<Double, Double> point : resPairs) {
//                    points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                            .setLatitude(point.getRight()).build());
//                }
//                FederateService.UnionResponse finalResult = FederateService.UnionResponse.newBuilder()
//                        .setLoop(finalNewLoop)
//                        .setIndex(finalNextIndexOfEndPoint)
//                        .addAllEndpoints(request.getEndpointsList())
//                        .setUuid(request.getUuid())
//                        .addAllPoint(points2)
//                        .build();
//                responseObserver.onNext(finalResult);//TODO clean buffer
//                responseObserver.onCompleted();
//                System.out.println(isLeader);
////                System.out.println("Send final res " + finalResult);
//                System.out.println("Send final res size" + finalResult.getPointCount());
//
//                return;//return 是灵魂
//            }
//
//            String nextEndpoint = request.getEndpoints(finalNextIndexOfEndPoint);
//            FederateDBClient nextFederateDBClient = getClient(nextEndpoint);
//            SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//            FederateService.UnionRequest.Builder nexRequest = request.toBuilder()
//                    .setLoop(finalNewLoop);
//            FederateService.UnionResponse unionResponse=null;
//
//            if (finalNewLoop == 0 || (finalNewLoop == 1 && finalNextIndexOfEndPoint == 0)) {
//                System.out.printf("loop 1, add random %d,%d\n", finalNewLoop, finalNextIndexOfEndPoint);
//
//                Set<Pair<Double, Double>> pairs = siloCache.getObfSet();
//                List<FederateCommon.Point> points = new ArrayList<>();
//                for (Pair<Double, Double> point : pairs) {
//                    points.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                            .setLatitude(point.getRight()).build());
//                }
//                nexRequest.setIndex(finalNextIndexOfEndPoint)
//                        .addAllPoint(points);
//                unionResponse= nextFederateDBClient.privacyUnion(nexRequest.build());
//            }
//            if (finalNewLoop == 1 || (finalNewLoop == 2 && finalNextIndexOfEndPoint == 0)) {
//                System.out.printf("loop 2, remove random %d,%d\n", finalNewLoop, finalNextIndexOfEndPoint);
//
//                List<FederateCommon.Point> points = request.getPointList();
//                Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                for (FederateCommon.Point point : points) {
//                    resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                }
//                resPairs.removeAll(siloCache.getObfSet());
//                resPairs.addAll(siloCache.getLocalSet());
//                List<FederateCommon.Point> points2 = new ArrayList<>();
//                for (Pair<Double, Double> point : resPairs) {
//                    points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft()).setLatitude(point.getRight()).build());
//                }
//                nexRequest.setIndex(finalNextIndexOfEndPoint)
//                        .addAllPoint(points2);
//            }
//
//            System.out.println("task res " + unionResponse);
//            if (unionResponse == null) {
//                System.out.println("task res null");
//            }else{
//                System.out.println("task res " + unionResponse.getPointCount());
//            }
//            responseObserver.onNext(unionResponse);
//            responseObserver.onCompleted();
//
//        }

//
//        @Override
//        public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
//            Integer currIndex = request.getIndex();
//            Integer currLoop = request.getLoop();
//            if (federateClientMap == null || federateClientMap.isEmpty()) {
//                initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
//            }
//            List<Callable<FederateService.UnionResponse>> task1 = new ArrayList<>();
//
//
//            if (currIndex == 0 && currLoop == 0) {//第一次循环开始
//                isLeader = true;
//            }
//
//            int nextIndexOfEndPoint = currIndex + 1;
//            int newLoop = currLoop;
//            if (nextIndexOfEndPoint == request.getEndpointsCount()) {
//                nextIndexOfEndPoint = 0;//再来一圈
//                newLoop = currLoop + 1;
//            }
//
//            int finalNextIndexOfEndPoint = nextIndexOfEndPoint;
//            int finalNewLoop = newLoop;
//            System.out.printf("recev %d,%d, next: %d,%d \n",currLoop,currIndex, finalNewLoop, finalNextIndexOfEndPoint);
//
//            if (isLeader && finalNewLoop == 2 && finalNextIndexOfEndPoint == 1) {//2圈结束
//                SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//
//                List<FederateCommon.Point> points = request.getPointList();
//                Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                for (FederateCommon.Point point : points) {
//                    resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                }
//                resPairs.removeAll(siloCache.getObfSet());
//                resPairs.addAll(siloCache.getLocalSet());
//                List<FederateCommon.Point> points2 = new ArrayList<>();
//                for (Pair<Double, Double> point : resPairs) {
//                    points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                            .setLatitude(point.getRight()).build());
//                }
//                FederateService.UnionResponse finalResult = FederateService.UnionResponse.newBuilder()
//                        .setLoop(finalNewLoop)
//                        .setIndex(finalNextIndexOfEndPoint)
//                        .addAllEndpoints(request.getEndpointsList())
//                        .setUuid(request.getUuid())
//                        .addAllPoint(points2)
//                        .build();
//                responseObserver.onNext(finalResult);//TODO clean buffer
//                responseObserver.onCompleted();
//                System.out.println(isLeader);
////                System.out.println("Send final res " + finalResult);
//                System.out.println("Send final res size" + finalResult.getPointCount());
//
//                return;//return 是灵魂
//            }
//
//            String nextEndpoint = request.getEndpoints(finalNextIndexOfEndPoint);
//            FederateDBClient nextFederateDBClient = getClient(nextEndpoint);
//            SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//            FederateService.UnionRequest.Builder nexRequest = request.toBuilder()
//                    .setLoop(finalNewLoop);
//            FederateService.UnionResponse unionResponse=null;
//
//            if (finalNewLoop == 0 || (finalNewLoop == 1 && finalNextIndexOfEndPoint == 0)) {
//                System.out.printf("loop 1, add random %d,%d\n", finalNewLoop, finalNextIndexOfEndPoint);
//
//                Set<Pair<Double, Double>> pairs = siloCache.getObfSet();
//                List<FederateCommon.Point> points = new ArrayList<>();
//                for (Pair<Double, Double> point : pairs) {
//                    points.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                            .setLatitude(point.getRight()).build());
//                }
//                nexRequest.setIndex(finalNextIndexOfEndPoint)
//                        .addAllPoint(points);
//                unionResponse= nextFederateDBClient.privacyUnion(nexRequest.build());
//            }
//            if (finalNewLoop == 1 || (finalNewLoop == 2 && finalNextIndexOfEndPoint == 0)) {
//                System.out.printf("loop 2, remove random %d,%d\n", finalNewLoop, finalNextIndexOfEndPoint);
//
//                List<FederateCommon.Point> points = request.getPointList();
//                Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                for (FederateCommon.Point point : points) {
//                    resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                }
//                resPairs.removeAll(siloCache.getObfSet());
//                resPairs.addAll(siloCache.getLocalSet());
//                List<FederateCommon.Point> points2 = new ArrayList<>();
//                for (Pair<Double, Double> point : resPairs) {
//                    points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft()).setLatitude(point.getRight()).build());
//                }
//                nexRequest.setIndex(finalNextIndexOfEndPoint)
//                        .addAllPoint(points2);
//            }
//
//            System.out.println("task res " + unionResponse);
//            if (unionResponse == null) {
//                System.out.println("task res null");
//            }else{
//                System.out.println("task res " + unionResponse.getPointCount());
//            }
//            responseObserver.onNext(unionResponse);
//            responseObserver.onCompleted();
//
//        }

//        public void privacyUnion3(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
//            Integer currIndex = request.getIndex();
//            Integer currLoop = request.getLoop();
//            if (federateClientMap == null || federateClientMap.isEmpty()) {
//                initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
//            }
//            List<Callable<FederateService.UnionResponse>> task1 = new ArrayList<>();
//
//
//            if (currIndex == 0 && currLoop == 0) {//第一次循环开始
//                isLeader = true;
//            }
//
//            int nextIndexOfEndPoint = currIndex + 1;
//            int newLoop = currLoop;
//            if (nextIndexOfEndPoint == request.getEndpointsCount()) {
//                nextIndexOfEndPoint = 0;//再来一圈
//                newLoop = currLoop + 1;
//            }
//
//            int finalNextIndexOfEndPoint = nextIndexOfEndPoint;
//            int finalNewLoop = newLoop;
//            if (finalNewLoop == 2 && finalNextIndexOfEndPoint == 1) {//2圈结束
//                SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//
//                List<FederateCommon.Point> points = request.getPointList();
//                Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                for (FederateCommon.Point point : points) {
//                    resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                }
//                resPairs.removeAll(siloCache.getObfSet());
//                resPairs.addAll(siloCache.getLocalSet());
//                List<FederateCommon.Point> points2 = new ArrayList<>();
//                for (Pair<Double, Double> point : resPairs) {
//                    points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                            .setLatitude(point.getRight()).build());
//                }
//                FederateService.UnionResponse finalResult = FederateService.UnionResponse.newBuilder()
//                        .setLoop(finalNewLoop)
//                        .setIndex(finalNextIndexOfEndPoint)
//                        .addAllEndpoints(request.getEndpointsList())
//                        .setUuid(request.getUuid())
//                        .addAllPoint(points2)
//                        .build();
//                responseObserver.onNext(finalResult);//TODO clean buffer
//                responseObserver.onCompleted();
//                System.out.println("Send final res " + finalResult);
//                System.out.println("Send final res size" + finalResult.getPointCount());
//
//                return;//return 是灵魂
//            }
//            task1.add(() -> {
//                String nextEndpoint = request.getEndpoints(finalNextIndexOfEndPoint);
//                FederateDBClient nextFederateDBClient = getClient(nextEndpoint);
//                SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//                FederateService.UnionRequest.Builder nexRequest = request.toBuilder()
//                        .setLoop(finalNewLoop);
//                if (finalNewLoop == 0 || (finalNewLoop == 1 && finalNextIndexOfEndPoint == 0)) {
//                    System.out.printf("loop 1, add random %d,%d", finalNewLoop, finalNextIndexOfEndPoint);
//
//                    Set<Pair<Double, Double>> pairs = siloCache.getObfSet();
//                    List<FederateCommon.Point> points = new ArrayList<>();
//                    for (Pair<Double, Double> point : pairs) {
//                        points.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                                .setLatitude(point.getRight()).build());
//                    }
//                    nexRequest.setIndex(finalNextIndexOfEndPoint)
//                            .addAllPoint(points);
//
//                } else if (finalNewLoop == 1 || (finalNewLoop == 2 && finalNextIndexOfEndPoint == 0)) {
//                    System.out.printf("loop 2, remove random %d,%d", finalNewLoop, finalNextIndexOfEndPoint);
//
//                    List<FederateCommon.Point> points = request.getPointList();
//                    Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//                    for (FederateCommon.Point point : points) {
//                        resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//                    }
//                    resPairs.removeAll(siloCache.getObfSet());
//                    resPairs.addAll(siloCache.getLocalSet());
//                    List<FederateCommon.Point> points2 = new ArrayList<>();
//                    for (Pair<Double, Double> point : resPairs) {
//                        points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft()).setLatitude(point.getRight()).build());
//                    }
//                    nexRequest.setIndex(finalNextIndexOfEndPoint)
//                            .addAllPoint(points2);
//                }
//                FederateService.UnionResponse unionResponse = nextFederateDBClient.privacyUnion(nexRequest.build());
//
//                return unionResponse;
//            });
//            FederateService.UnionResponse recUnionResponse = null;
//            try {
//                List<Future<FederateService.UnionResponse>> alphaList = executorService.invokeAll(task1);
//                for (Future<FederateService.UnionResponse> falpha : alphaList) {
//                    recUnionResponse = falpha.get();
//                    System.out.println("task res " + recUnionResponse);
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//            responseObserver.onNext(recUnionResponse);
//            responseObserver.onCompleted();
//
//
//        }


    @Override
    public void privacyRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = localRangeQuery(request.getPoint(), request.getLiteral(), FederateCommon.Point.class);
            List<Pair<Double, Double>> resPairs = new ArrayList<>();
            for (FederateCommon.Point point : res) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
            SiloCache siloCache = new SiloCache(resPairs);
            buffer.set(request.getUuid(), siloCache);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);// 表示查成功了，不返回具体结果
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReplyList> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReplyList.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = localRangeQuery(request.getPoint(), request.getLiteral(), FederateCommon.Point.class);
            replyList = FederateService.SQLReplyList.newBuilder()
                    .addAllMessage(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void privacyPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = localPolygonRangeQuery(request.getPolygon(), FederateCommon.Point.class);
            List<Pair<Double, Double>> resPairs = new ArrayList<>();
            for (FederateCommon.Point point : res) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
            SiloCache siloCache = new SiloCache(resPairs);
            buffer.set(request.getUuid(), siloCache);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);// 表示查成功了，不返回具体结果
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void knnRadiusQuery(FederateService.SQLExpression request, StreamObserver<FederateService.KnnRadiusQueryResponse> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Double result = 0.0;
        try {
            Double k = request.getLiteral();
            int kk = k.intValue();
            result = localKnnRadiusQuery(request.getPoint(), request.getTable(), kk);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //构造返回
        FederateService.KnnRadiusQueryResponse reply = FederateService.KnnRadiusQueryResponse.newBuilder().setRadius(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void polygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReplyList> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReplyList.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = localPolygonRangeQuery(request.getPolygon(), FederateCommon.Point.class);
            replyList = FederateService.SQLReplyList.newBuilder()
                    .addAllMessage(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    private <T> List<T> localPolygonRangeQuery(FederateCommon.Polygon polygon, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果

        // 生成 SQL
        String sql = SQLGenerator.generatePolygonRangeQuerySQL(polygon);
//        LOGGER.info(String.format("\n%s Target SQL: ", "PostGis") + sql);
        // 执行 SQL
        List<T> pointList = executeSql(sql, resultClass);

//        LOGGER.info(String.format("\n%s PolyonRangeQuery Result:", "PostGis") + pointList.toString());

        return pointList;
    }

    /**
     * query: select RangeCounting (P, radius) from table_name;
     * result: Integer，The number of points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    public Integer localRangeCount(FederateCommon.Point point, String tableName, Double radius) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // 读取参数
        // TODO: plaintext query

        // 生成目标 SQL
        String sql = SQLGenerator.generateRangeCountingSQL(point, tableName, radius);
//        LOGGER.info(String.format("\n%s Target SQL: ", "postgresql") + sql);
        // 执行 SQL
        Integer ans = executeSql(sql, Integer.class, false);

//        LOGGER.info(String.format("\n%s RangeCount Result: ", "postgresql") + ans);

        // TODO: secure summation
        return ans;
    }

    public Double localKnnRadiusQuery(FederateCommon.Point point, String tableName, Integer k) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//knn主函数
        Double minRadius = Double.MAX_VALUE;
        // 初始化查询半径
        String sql = SQLGenerator.generateKnnRadiusQuerySQL(point, tableName, k);
        Double ans = executeSql(sql, Double.class, false);
        return ans;
    }


    /**
     * query: select RangeQuery (P, radius) from table_name;
     * result: List<point>，points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    private <T> List<T> localRangeQuery(FederateCommon.Point point, Double radius, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果
        // 生成 SQL
        String sql = SQLGenerator.generateRangeQuerySQL(point, radius);
//        LOGGER.info(String.format("\n%s Target SQL: ", "postgresql") + sql);
        // 执行 SQL
        List<T> pointList = executeSql(sql, resultClass);
//        LOGGER.info(String.format("\n%s RangeQuery Result:", "postgresql") + pointList.toString());

        return pointList;
    }


    public <T> List<T> executeSql(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        List<T> resultList = new ArrayList<>();
//            while (resultSet.next()) {
//                T t = resultSet2Object(resultSet, clazz);
//                resultList.add(t);
//            }
        return null;
    }

    public <T> List<T> executeSql(String sql, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        return resultSet2List(resultSet, resultClass);
    }

    public <T> T executeSql(String sql, Class<T> resultClass, Boolean listFlag) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (listFlag) {
            // to do something
            return null;
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            return resultSet2Object(resultSet, resultClass);
        }
    }
}
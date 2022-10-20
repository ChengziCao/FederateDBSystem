package com.suda.federate;

import com.suda.federate.config.DbConfig;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.SQLExecutor;
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
import java.util.Locale;

public class FederatePostgresqlService extends FederateDBService {

    private DatabaseMetaData metaData;
    private SQLExecutor executor;

    FederatePostgresqlService(DbConfig config) {
        try {
            Class.forName(config.getDriver());
            Connection conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
            executor = new SQLExecutor(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void publicRangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        try {
            int result = executor.localRangeCount(request.getPoint(), request.getTable(), request.getDoubleNumber());
            FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setIntegerNumber(result).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void publicRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = executor.localRangeQuery(request.getPoint(), request.getTable(), request.getDoubleNumber(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder()
                    .setIntegerNumber(res.size()).addAllPoint(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void publicKNN(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = executor.localKnnQuery(request.getPoint(), request.getTable(), request.getIntegerNumber(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder()
                    .setIntegerNumber(res.size()).addAllPoint(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void publicPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = executor.localPolygonRangeQuery(request.getPolygon(), request.getTable(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder()
                    .setIntegerNumber(res.size()).addAllPoint(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void privacyRangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            Integer result = executor.localRangeCount(request.getPoint(), request.getTable(), request.getDoubleNumber());
            //FederateService.SQLReply reply = setSummation(request, result);
//            LogUtils.debug(request.getUuid());
            buffer.set(request.getUuid(), result);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void privacyRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = executor.localRangeQuery(request.getPoint(), request.getTable(), request.getDoubleNumber(), FederateCommon.Point.class);
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
    public void privacyPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = executor.localPolygonRangeQuery(request.getPolygon(), request.getTable(), FederateCommon.Point.class);
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
    public void knnRadiusQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Double result = 0.0;
        try {
            int k = request.getIntegerNumber();
            result = executor.localKnnRadiusQuery(request.getPoint(), request.getTable(), k);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();
        }
        //构造返回
        FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setDoubleNumber(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
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

}
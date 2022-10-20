package com.suda.federate.silo;

import com.google.protobuf.Empty;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.utils.ConcurrentBuffer;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.suda.federate.security.sha.SecretSum.*;

/***
 * 实现 service.proto 中定义的接口
 */
public abstract class FederateDBService extends FederateGrpc.FederateImplBase {

    public static ConcurrentBuffer buffer = new ConcurrentBuffer();

    public static FederateService.UnionResponse UnionRequest2UnionResponse(FederateService.UnionRequest request) {
        return FederateService.UnionResponse.newBuilder()
                .addAllPoint(request.getPointList())
                .setLoop(request.getLoop())//!!!!
                .setIndex(request.getIndex())//!!!!
                .addAllEndpoints(request.getEndpointsList())
                .setUuid(request.getUuid())
                .build();
    }

    public static FederateService.UnionRequest UnionResponse2UnionRequest(FederateService.UnionResponse response) {
        return FederateService.UnionRequest.newBuilder()
                .addAllPoint(response.getPointList())
                .setLoop(response.getLoop())//!!!!
                .setIndex(response.getIndex())//!!!!
                .addAllEndpoints(response.getEndpointsList())
                .setUuid(response.getUuid())
                .build();

    }

    @Override
    public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {

        FederateDBClient client = new FederateDBClient(request.getEndpoints(1));
//        LogUtils.debug(client.getEndpoint());
        request = personalAdd(request);
        FederateService.UnionResponse response = client.localUnion(request.toBuilder().setLoop(1).setIndex(0).build());
//        System.out.println(response);
        LogUtils.debug("response of add " + response.getPointCount());
        request = personalDel(UnionResponse2UnionRequest(response));
        // 可以 random Endpoints 更加safe
        response = client.localUnion(request.toBuilder().setLoop(0).setIndex(0).build());
        LogUtils.debug("response of add " + response.getPointCount());
//        localUnion(request.toBuilder().setLoop(0).build(),responseObserver);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void localUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {

        Integer currIndex = request.getIndex();
        Integer currLoop = request.getLoop();

        int finalNextIndexOfEndPoint = currIndex + 1;

        int endpointIdx = finalNextIndexOfEndPoint % request.getEndpointsCount();
        String nextEndpoint = request.getEndpoints(endpointIdx);

        FederateDBClient nextFederateDBClient = new FederateDBClient(nextEndpoint);
//        LogUtils.debug(nextFederateDBClient.getEndpoint());
        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//        LogUtils.debug("request " + request.getPointCount());

        Set<Pair<Double, Double>> obfPairs = siloCache.getObfSet();
        Set<Pair<Double, Double>> localSet = siloCache.getLocalSet();

        FederateService.UnionResponse.Builder unionResponse = FederateService.UnionResponse.newBuilder();
        List<FederateCommon.Point> points;
        if (currLoop == 1) {//currLoop==1 add
            points = new ArrayList<>();
            for (Pair<Double, Double> pp : obfPairs) {
                FederateCommon.Point px = FederateCommon.Point.newBuilder().setLongitude(pp.getLeft()).setLatitude(pp.getRight()).build();
                points.add(px);
            }

        } else {//currLoop==0 del
            points = request.getPointList();
            Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
            for (FederateCommon.Point point : points) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
//            LogUtils.debug(String.format("before remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
            resPairs.removeAll(obfPairs);
//            LogUtils.debug(String.format("after remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
            resPairs.addAll(localSet);
//            LogUtils.debug(String.format("after remove and add silo %d from res %d\n", localSet.size(), resPairs.size()));

            List<FederateCommon.Point> points2 = new ArrayList<>();
            for (Pair<Double, Double> point : resPairs) {
                points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
                        .setLatitude(point.getRight()).build());
            }
            points = points2;
        }
//        unionResponse.addAllPoint(points);

        FederateService.UnionResponse resultResponse = UnionRequest2UnionResponse(request).toBuilder()
                .clearPoint().addAllPoint(points).setIndex(finalNextIndexOfEndPoint).build();
        if (finalNextIndexOfEndPoint + 1 == request.getEndpointsCount()) {
            responseObserver.onNext(resultResponse);
            responseObserver.onCompleted();
        } else {
            FederateService.UnionResponse resultResponse2 = nextFederateDBClient.localUnion(UnionResponse2UnionRequest(resultResponse));
//            if (resultResponse2 == null) {
//                System.out.println("task add resultResponse null");
//            } else {
//                System.out.println("task add resultResponse " + resultResponse2.getPointCount());
//            }
            responseObserver.onNext(resultResponse2);
            responseObserver.onCompleted();
        }
    }


    public FederateService.UnionRequest personalAdd(FederateService.UnionRequest request) {
        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//        LogUtils.debug("request " + request.getPointCount());

        Set<Pair<Double, Double>> obfPairs = siloCache.getObfSet();
        List<FederateCommon.Point> points = new ArrayList<>();
        for (Pair<Double, Double> pp : obfPairs) {
            FederateCommon.Point px = FederateCommon.Point.newBuilder().setLongitude(pp.getLeft()).setLatitude(pp.getRight()).build();
            points.add(px);
        }
//        LogUtils.debug("add local size " + points.size());
        return request.toBuilder().addAllPoint(points).build();
    }

    public FederateService.UnionRequest personalDel(FederateService.UnionRequest request) {

        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//        System.out.println("request " + request.getPointCount());

        Set<Pair<Double, Double>> obfPairs = siloCache.getObfSet();
        Set<Pair<Double, Double>> localSet = siloCache.getLocalSet();

        FederateService.UnionResponse.Builder unionResponse = FederateService.UnionResponse.newBuilder();
        List<FederateCommon.Point> points = request.getPointList();
        Set<Pair<Double, Double>> resPairs = new TreeSet<>();
        for (FederateCommon.Point point : points) {
            resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
        }
//        LogUtils.debug(String.format("before remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
        resPairs.removeAll(obfPairs);
//        LogUtils.debug(String.format("after remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
        resPairs.addAll(localSet);
//        LogUtils.debug(String.format("after remove and add silo %d from res %d\n", localSet.size(), resPairs.size()));

        List<FederateCommon.Point> points2 = new ArrayList<>();
        for (Pair<Double, Double> point : resPairs) {
            points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
                    .setLatitude(point.getRight()).build());
        }
        points = points2;
//        System.out.println("remove&add local, size " + points.size());
        return request.toBuilder().addAllPoint(points).build();
    }

    @Override
    public void localSummation(FederateService.SummationRequest request, StreamObserver<FederateService.SummationResponse> responseObserver) {
        FederateService.SummationResponse response = request.getResponse();
        Integer ans = (Integer) buffer.get(request.getUuid());
        // LogUtils.debug(request.getSiloSize() + "," + ans + "," + request.getPublicKeyList());
        List<Integer> cryptPloy = localEncrypt(request.getSiloSize(), ans, request.getPublicKeyList());
        // LogUtils.debug(cryptPloy.toString());
        response = response.toBuilder().addFakeLocalSum(FederateService.SummationResponse.FakeLocalSum.newBuilder().addAllNum(cryptPloy).build()).build();
        // LogUtils.debug(response.getFakeLocalSumList().toString());
        int nextIndex = request.getNowIndex() + 1;
        // LogUtils.debug(Integer.toString(nextIndex));
        if (nextIndex <= request.getEndIndex()) {
            FederateDBClient nextClient = new FederateDBClient(request.getEndpoints(nextIndex));
            // LogUtils.debug(nextClient.toString());
            response = nextClient.localSummation(request.toBuilder().setResponse(response).setNowIndex(nextIndex).build());
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void clearCache(FederateService.CacheID request, StreamObserver<Empty> observer) {
        String uuid = request.getUuid();
        buffer.remove(uuid);
        observer.onNext(Empty.newBuilder().build());
        observer.onCompleted();
    }

    //TODO 使用 cache 计算 knn ，优化knn中while循环的rangeCount
//    private double bufferCount(FederateService.PrivacyCountRequest request) {
//        Object o = buffer.get(request.getCacheUuid());
//        if (o instanceof DistanceDataSet) {
//            DistanceDataSet distanceDataSet = (DistanceDataSet) o;
//            int count = distanceDataSet.getRangeCount(request.getRadius());
//            LOG.debug("in privacy knn local radius: {} , count: {} ", request.getRadius(), count);
//            return count;
//        } else {
//            AggCache aggCache = (AggCache) o;
//            double res = aggCache.getColumn(request.getColumnId());
//            LOG.info("in privacy aggregate the result is {}", res);
//            return res;
//        }
//    }



}

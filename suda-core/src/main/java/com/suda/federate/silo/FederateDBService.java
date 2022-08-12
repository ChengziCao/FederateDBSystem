package com.suda.federate.silo;

import com.google.protobuf.Empty;
import com.suda.federate.security.sha.SecretSum;
import com.suda.federate.spatial.FederateDBClient;
import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.dp.Laplace;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.spatial.FederateQuerier;
import com.suda.federate.utils.ConcurrentBuffer;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.suda.federate.security.sha.SecretSum.*;

// 定义一个实现服务接口的类
public abstract class FederateDBService extends FederateGrpc.FederateImplBase {

    public Map<String, FederateDBClient> federateClientMap = null;
    //public  List<String> endpoints = null;
    public int THREAD_POOL_SIZE = 0;
    public ExecutorService executorService = null;
    public Laplace lp = null;
    public ConcurrentBuffer buffer = new ConcurrentBuffer();


    public void initClients(List<String> endpoints) {
        federateClientMap = new TreeMap<>();
        for (String endpoint : endpoints) {
            federateClientMap.put(endpoint, new FederateDBClient(endpoint));
        }
        THREAD_POOL_SIZE = endpoints.size();
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
    }

    public FederateDBClient getClient(String endpoint) {
        if (federateClientMap.containsKey(endpoint)) {
            return federateClientMap.get(endpoint);
        }
        return null;
    }

    //public FederateDBClient getClient(String endpoint,boolean used) {//没啥用
    //    return new FederateDBClient(endpoint);
    //}
    //
    //public FederateDBClient getClient(Integer endpointId){
    //    String endpoint = endpoints.get(endpointId);
    //    if (federateClientMap.containsKey(endpoint)){
    //        return federateClientMap.get(endpoint);
    //    }
    //    return null;
    //}
//    @Override
//    public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
//        Integer currIndex = request.getIndex();
//        Integer currLoop = request.getLoop();
//        if (federateClientMap == null || federateClientMap.isEmpty()) {
//            initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
//            System.out.println("init client ok");
//        }
//        List<Callable<FederateService.UnionResponse>> task1 = new ArrayList<>();
//        if(currIndex==-1){
//            currIndex=0;
//            System.out.println("isLeader");
//        }
//        boolean add= currIndex<request.getEndpointsCount();
//        boolean del =(currIndex>=request.getEndpointsCount()) && (currIndex+1 <2*request.getEndpointsCount());
//        int finalNextIndexOfEndPoint=currIndex+1;
//        int finalNewLoop = currLoop;
//
//        int endpointIdx=finalNextIndexOfEndPoint%request.getEndpointsCount();
//        String nextEndpoint = request.getEndpoints(endpointIdx);
//
//        FederateDBClient nextFederateDBClient = getClient(nextEndpoint);
//        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
//        FederateService.UnionRequest.Builder nexRequest = request.toBuilder()
//                .setLoop(finalNewLoop);
//        System.out.println("request "+nexRequest.getPointCount());
//        if(request.getLoop()==1 && add){
//            System.out.printf("loop 1, add random %d,%d \n", finalNewLoop, finalNextIndexOfEndPoint);
//            Set<Pair<Double, Double>> pairs = siloCache.getObfSet();
//            List<FederateCommon.Point> points = new ArrayList<>();
//            for (Pair<Double, Double> point : pairs) {
//                points.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                        .setLatitude(point.getRight()).build());
//            }
//
//
//            nexRequest.setIndex(finalNextIndexOfEndPoint)
//                    .addAllPoint(points);
//            //56- 28 true 28 random
//            System.out.printf("after add silo %d from res %d\n\n",pairs.size(),nexRequest.getPointCount());
//
//            FederateService.UnionResponse unionResponse = nextFederateDBClient.privacyUnion(nexRequest.build());
//
//            if (unionResponse == null) {
//                System.out.println("task add res null");
//            }else{
//                System.out.println("task add res " + unionResponse.getPointCount());
//            }
//            responseObserver.onNext(unionResponse);
//            responseObserver.onCompleted();
////                add
//        }else if(request.getLoop()==1 && del){
//            //remove
//            System.out.printf("loop 2, remove random %d,%d \n", finalNewLoop, finalNextIndexOfEndPoint);
//
//            List<FederateCommon.Point> points = request.getPointList();
//            Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//            for (FederateCommon.Point point : points) {
//                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//            }
//            System.out.printf("before remove silo %d from res %d\n",siloCache.getObfSet().size(),resPairs.size());
//            resPairs.removeAll(siloCache.getObfSet());
//            System.out.printf("after remove silo %d from res %d\n",siloCache.getObfSet().size(),resPairs.size());
//            resPairs.addAll(siloCache.getLocalSet());
//            System.out.printf("after remove and add silo %d from res %d\n",siloCache.getLocalSet().size(),resPairs.size());
//
//            List<FederateCommon.Point> points2 = new ArrayList<>();
//            for (Pair<Double, Double> point : resPairs) {
//                points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft()).setLatitude(point.getRight()).build());
//            }
//
//            nexRequest.setIndex(finalNextIndexOfEndPoint)
//                    .addAllPoint(points2);
//            FederateService.UnionResponse unionResponse = nextFederateDBClient.privacyUnion(nexRequest.build());
//
//            if (unionResponse == null) {
//                System.out.println("task del res null");
//            }else{
//                System.out.println("task del res " + unionResponse.getPointCount());
//            }
//            responseObserver.onNext(unionResponse);
//            responseObserver.onCompleted();
//        }else{//结束
//            System.out.println("loop结束");
//
//            List<FederateCommon.Point> points = request.getPointList();
//            Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//            for (FederateCommon.Point point : points) {
//                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//            }
//            System.out.printf("before remove silo %d from res %d\n",siloCache.getObfSet().size(),resPairs.size());
//            resPairs.removeAll(siloCache.getObfSet());
//            System.out.printf("after remove silo %d from res %d\n",siloCache.getObfSet().size(),resPairs.size());
//            resPairs.addAll(siloCache.getLocalSet());
//            System.out.printf("after remove and add silo %d from res %d\n",siloCache.getLocalSet().size(),resPairs.size());
//
//            List<FederateCommon.Point> points2 = new ArrayList<>();
//            for (Pair<Double, Double> point : resPairs) {
//                points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                        .setLatitude(point.getRight()).build());
//            }
//            FederateService.UnionResponse finalResult = FederateService.UnionResponse.newBuilder()
//                    .setLoop(0)
//                    .setIndex(finalNextIndexOfEndPoint)
//                    .addAllEndpoints(request.getEndpointsList())
//                    .setUuid(request.getUuid())
//                    .addAllPoint(points2)
//                    .build();
//            responseObserver.onNext(finalResult);//TODO clean buffer
//            responseObserver.onCompleted();
//
////                System.out.println("Send final res " + finalResult);
//            System.out.println("Send final res size" + finalResult.getPointCount());
//
//            return;//return 是灵魂
//        }
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
    public void localUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
        if (federateClientMap == null || federateClientMap.isEmpty()) {
            initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
            LogUtils.debug("init client ok");
        }
        Integer currIndex = request.getIndex();
        Integer currLoop = request.getLoop();

        int finalNextIndexOfEndPoint = currIndex + 1;

        int endpointIdx = finalNextIndexOfEndPoint % request.getEndpointsCount();
        String nextEndpoint = request.getEndpoints(endpointIdx);

        FederateDBClient nextFederateDBClient = getClient(nextEndpoint);

        LogUtils.debug(nextFederateDBClient.getEndpoint());

        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
        LogUtils.debug("request " + request.getPointCount());

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
            LogUtils.debug(String.format("before remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
            resPairs.removeAll(obfPairs);
            LogUtils.debug(String.format("after remove silo %d from res %d\n", obfPairs.size(), resPairs.size()));
            resPairs.addAll(localSet);
            LogUtils.debug(String.format("after remove and add silo %d from res %d\n", localSet.size(), resPairs.size()));

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
            if (resultResponse2 == null) {
                System.out.println("task add resultResponse null");
            } else {
                System.out.println("task add resultResponse " + resultResponse2.getPointCount());
            }
            responseObserver.onNext(resultResponse2);
            responseObserver.onCompleted();
        }
    }


    public FederateService.UnionRequest personalAdd(FederateService.UnionRequest request) {

        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
        LogUtils.debug("request " + request.getPointCount());

        Set<Pair<Double, Double>> obfPairs = siloCache.getObfSet();
        List<FederateCommon.Point> points = new ArrayList<>();
        for (Pair<Double, Double> pp : obfPairs) {
            FederateCommon.Point px = FederateCommon.Point.newBuilder().setLongitude(pp.getLeft()).setLatitude(pp.getRight()).build();
            points.add(px);
        }
        LogUtils.debug("add local size " + points.size());
        return request.toBuilder().addAllPoint(points).build();
    }

    public FederateService.UnionRequest personalDel(FederateService.UnionRequest request) {

        SiloCache siloCache = (SiloCache) buffer.get(request.getUuid());
        System.out.println("request " + request.getPointCount());

        Set<Pair<Double, Double>> obfPairs = siloCache.getObfSet();
        Set<Pair<Double, Double>> localSet = siloCache.getLocalSet();

        FederateService.UnionResponse.Builder unionResponse = FederateService.UnionResponse.newBuilder();
        List<FederateCommon.Point> points = request.getPointList();
        Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
        for (FederateCommon.Point point : points) {
            resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
        }
        System.out.printf("before remove silo %d from res %d\n", obfPairs.size(), resPairs.size());
        resPairs.removeAll(obfPairs);
        System.out.printf("after remove silo %d from res %d\n", obfPairs.size(), resPairs.size());
        resPairs.addAll(localSet);
        System.out.printf("after remove and add silo %d from res %d\n", localSet.size(), resPairs.size());

        List<FederateCommon.Point> points2 = new ArrayList<>();
        for (Pair<Double, Double> point : resPairs) {
            points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
                    .setLatitude(point.getRight()).build());
        }
        points = points2;
        System.out.println("remove&add local, size " + points.size());
        return request.toBuilder().addAllPoint(points).build();
    }

    @Override
    public void privacySummation(FederateService.SummationRequest request, StreamObserver<FederateService.SummationResponse> responseObserver) {
        if (federateClientMap == null || federateClientMap.isEmpty()) {
            initClients(request.getEndpointsList());
            LogUtils.debug("init client ok");
        }
        LogUtils.debug("privacy summation start.");
        FederateDBClient leaderClient = federateClientMap.get(request.getEndpoints(request.getIndex()));
        FederateService.SummationResponse response = leaderClient.localSummation(request);
        List<List<Integer>> fakeLocalSumList = response.getFakeLocalSumList().stream().map(x -> x.getNumList()).collect(Collectors.toList());
        LogUtils.debug(fakeLocalSumList.toString());
        List<Integer> S = computeS(fakeLocalSumList);
        LogUtils.debug(S.toString());
        int secureSum = lag(request.getIdListList(), S, 0);
        LogUtils.debug("privacy summation finished.");
        responseObserver.onNext(response.toBuilder().setCount(secureSum).build());
        responseObserver.onCompleted();
    }

    @Override
    public void localSummation(FederateService.SummationRequest request, StreamObserver<FederateService.SummationResponse> responseObserver) {
        FederateService.SummationResponse response = request.getResponse();

        Integer ans = (Integer) buffer.get(request.getUuid());
        LogUtils.debug(request.getSiloSize() + "," + ans + "," + request.getIdListList());
        List<Integer> cryptPloy = localClient(request.getSiloSize(), ans, request.getIdListList());
        LogUtils.debug(cryptPloy.toString());

        response = response.toBuilder().addFakeLocalSum(FederateService.SummationResponse.FakeLocalSum.newBuilder().addAllNum(cryptPloy).build()).build();
        //response.toBuilder().addFakeLocal
        LogUtils.debug(response.getFakeLocalSum(request.getIndex()).toString());

        int nextIndex = request.getIndex() + 1;
        if (nextIndex < request.getEndpointsCount()) {
            FederateDBClient nextClient = federateClientMap.get(request.getEndpoints(nextIndex));
            response = nextClient.localSummation(request.toBuilder().setResponse(response).setIndex(nextIndex).build());
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void privacyUnion(FederateService.UnionRequest request, StreamObserver<FederateService.UnionResponse> responseObserver) {
        if (federateClientMap == null || federateClientMap.isEmpty()) {
            initClients(request.getEndpointsList());//索引endpoint都初始化，一劳永逸（如果loop顺序不变，建议只初始化nextnexFederateDBClient）
            LogUtils.debug("init client ok");
        }
        FederateDBClient client = getClient(request.getEndpoints(1));
        LogUtils.debug(client.getEndpoint());
        request = personalAdd(request);

        FederateService.UnionResponse response = client.localUnion(request.toBuilder().setLoop(1).setIndex(0).build());
//        System.out.println("response of add "+response.getPointList());
        LogUtils.debug("response of add " + response.getPointCount());

        request = personalDel(UnionResponse2UnionRequest(response));
        // 可以 random Endpoints 更加safe
        response = client.localUnion(request.toBuilder().setLoop(0).setIndex(0).build());
//        System.out.println("response of add "+response.getPointList());
        LogUtils.debug("response of add " + response.getPointCount());

//        localUnion(request.toBuilder().setLoop(0).build(),responseObserver);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

//        List<FederateCommon.Point> points =request.getPointCount()==0 ? new ArrayList<>(): request.getPointList();
//        if(add || lastAdd){
//            for (Pair<Double, Double> pp : obfPairs) {
//                FederateCommon.Point px=FederateCommon.Point.newBuilder().setLongitude(pp.getLeft()).setLatitude(pp.getRight()).build();
//                points.add(px);
//            }
//        }else{
//
//            Set<Pair<Double, Double>> resPairs = new TreeSet<Pair<Double, Double>>();
//            for (FederateCommon.Point point : points) {
//                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
//            }
//            System.out.printf("before remove silo %d from res %d\n",obfPairs.size(),resPairs.size());
//            resPairs.removeAll(obfPairs);
//            System.out.printf("after remove silo %d from res %d\n",obfPairs.size(),resPairs.size());
//            resPairs.addAll(localSet);
//            System.out.printf("after remove and add silo %d from res %d\n",localSet.size(),resPairs.size());
//
//            List<FederateCommon.Point> points2 = new ArrayList<>();
//            for (Pair<Double, Double> point : resPairs) {
//                points2.add(FederateCommon.Point.newBuilder().setLongitude(point.getLeft())
//                        .setLatitude(point.getRight()).build());
//            }
//            points=points2;
//        }
//        if(lastAdd){
//            finalNewLoop=0;
//            finalNextIndexOfEndPoint=0;
//        }
//        if (!isLeader && (lastAdd || lastDel)) {
//            FederateService.UnionResponse response = FederateService.UnionResponse.newBuilder()
//                    .addAllPoint(points)
//                    .setLoop(finalNewLoop)//!!!!
//                    .setIndex(finalNextIndexOfEndPoint)//!!!!
//                    .addAllEndpoints(request.getEndpointsList())
//                    .setUuid(request.getUuid())
//                    .build();
//            responseObserver.onNext(response);
//            responseObserver.onCompleted();
//        }else{
//            FederateService.UnionRequest resultRequest=FederateService.UnionRequest.newBuilder()
//                    .addAllPoint(points)
//                    .setLoop(finalNewLoop)//!!!!
//                    .setIndex(finalNextIndexOfEndPoint)//!!!!
//                    .addAllEndpoints(request.getEndpointsList())
//                    .setUuid(request.getUuid())
//                    .build();
//            FederateService.UnionResponse resultResponse = nextFederateDBClient.privacyUnion(resultRequest);
//            if (resultResponse == null) {
//                System.out.println("task add resultResponse null");
//            }else{
//                System.out.println("task add resultResponse " + resultResponse.getPointCount());
//            }
//            responseObserver.onNext(resultResponse);
//            responseObserver.onCompleted();
//
//        }
//        System.out.printf("currloop %d, currIndex %d, add %b, lastAdd %b, del %b, lastDel %b \n",currLoop,currIndex,add,lastAdd,del,lastDel);


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


    public static <T> List<T> resultSet2List(ResultSet resultSet, Class<T> clazz) throws
            SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T t = resultSet2Object(resultSet, clazz);
            resultList.add(t);
        }
        return resultList;
    }

    public static <T> T resultSet2Object(ResultSet resultSet, Class<T> clazz) throws
            SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (resultSet.isBeforeFirst()) {
            // 跳过头指针
            resultSet.next();
        }
        if (clazz == Integer.class || clazz == Double.class || clazz == String.class) {
            return clazz.getConstructor(String.class).newInstance(resultSet.getObject(1).toString());
        } else if (clazz == FederateCommon.Point.class) {
            String content = resultSet.getObject(1).toString();

            List<Float> temp = FederateUtils.parseNumFromString(content, Float.class);
            FederateCommon.Point point = FederateCommon.Point.newBuilder()
                    .setLongitude(temp.get(0)).setLatitude(temp.get(1)).build();//TODO check 顺序
            return (T) point;
        } else if (clazz == HashMap.class) {
            Map<String, Object> mmap = new HashMap<>();
            int count = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= count; i++) {
                mmap.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
            }
            return clazz.getConstructor(Map.class).newInstance(mmap);
        } else {
            return null;
        }
    }

}

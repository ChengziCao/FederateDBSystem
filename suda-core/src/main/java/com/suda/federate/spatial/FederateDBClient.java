package com.suda.federate.spatial;

import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//一个silo/db 对应一个FederateDBClient，
public final class FederateDBClient {//被edSpatialClient调用 List<String> endpoints = tableClients.keySet().stream().map(FederateDBClient::getEndpoint).collect(Collectors.toList());
    private final FederateGrpc.FederateBlockingStub blockingStub; //非阻塞 AbstractBlockingStub
    //    private final FederateGrpc.FederateStub asyncStub; // extends AbstractAsyncStub 没用到？
    private String endpoint;

    public FederateDBClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().maxInboundMessageSize(1024 * 1024 * 80));
        this.endpoint = String.format("%s:%d", host, port);
    }

    public FederateDBClient(String endpoint) {
        this(ManagedChannelBuilder.forTarget(endpoint).usePlaintext().maxInboundMessageSize(1024 * 1024 * 80));
        this.endpoint = endpoint;
    }

    public FederateDBClient(ManagedChannelBuilder<?> channelBuilder) {
        this(channelBuilder.build());
    }

    public FederateDBClient(Channel channel) {
        blockingStub = FederateGrpc.newBlockingStub(channel);
//        asyncStub = FederateGrpc.newStub(channel);
    }

    //public void FederateRangeCount() {
    //    try {
    //        FederateService.SQLReply response = blockingStub.getResult(null);
    //    } catch (StatusRuntimeException e) {
    //        LogUtils.error(String.format("RPC failed in dp range count: %s", e.getStatus()));
    //    }
    //}

    public FederateService.SQLReply fedSpatialQuery(FederateService.SQLExpression expression, String siloTableName) {
        FederateService.SQLReply response;
        try {
            expression = expression.toBuilder().setTable(siloTableName).build();
            switch (expression.getFunction()) {
                case RANGE_COUNT:
                    response = blockingStub.publicRangeCount(expression);
                    break;
                case RANGE_QUERY:
                    response = blockingStub.publicRangeQuery(expression);
                    break;
                case KNN:
                    response = null;
                    break;
                case POLYGON_RANGE_QUERY:
                    response = blockingStub.publicPolygonRangeQuery(expression);
                    break;
                default:
                    response = null;
            }
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
            return null;
        }
        return response;
    }

    public Boolean fedSpatialPrivacyQuery(FederateService.SQLExpression expression) {
        FederateService.Status status = null;
        try {
            switch (expression.getFunction()) {
                case RANGE_COUNT:
                    status = blockingStub.privacyRangeCount(expression);
                    break;
                case RANGE_QUERY:
                    status = blockingStub.privacyRangeQuery(expression);
                    break;
                case KNN:
                    status = null;
                    break;
                case POLYGON_RANGE_QUERY:
                    status = blockingStub.privacyPolygonRangeQuery(expression);
                    break;
                default:
                    status = null;
            }
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
            return null;
        }
        return status.getMsg().equals("ok");
    }
//    public static StreamingIterator<Double> federateKnnRadiusQuery(FederateService.SQLExpression expression ){
//
//        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
//        StreamingIterator<Double> iterator = new StreamingIterator<>(federateDBClients.size());
//
//        for (Map.Entry<String,FederateDBClient> entry : federateDBClients.entrySet()) {
//            tasks.add(() -> {
//                try {
//                    FederateDBClient federateDBClient = entry.getValue();
//                    String endpoint= federateDBClient.getEndpoint();
//                    String siloTableName =tableMap.get(expression.getTable()).get(endpoint);
//                    SQLExpression queryExpression = expression.toBuilder().setTable(siloTableName).build();//TODO 添加更多功能
//
//                    try{
//                        Double radius= federateDBClient.knnRadiusQuery(expression);//TODO 精度
//                        System.out.println(endpoint+" 服务器返回信息：radius "+ radius);
//                        iterator.add(radius);
//                    }catch (StatusRuntimeException e){
//                        System.out.println("RPC调用失败："+e.getMessage());
//                        return false;
//                    }
//
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    iterator.finish();
//                }
//            });
//        }
//        try {
//            List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
//            for (Future<Boolean> status : statusList) {
//                if (!status.get()) {
//                    LOGGER.error("error in fedSpatialPublicQuery");
//                }
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        return iterator;
//
//    }
//    private static void federateKnn(FederateService.SQLExpression expression) {
//        StreamingIterator<Double> radiusIterator=federateKnnRadiusQuery(expression);
//        double minRadius = Double.MAX_VALUE;
//        while (radiusIterator.hasNext()){
//            double r= radiusIterator.next();
//            minRadius = r < minRadius ? r : minRadius;
//        }
//        int k =(int)expression.getLiteral();//TODO 精确度
//        double l = 0.0, u = minRadius, e = 1e-3;
//        double threshold = minRadius;
//        while (u - l >= e) {//TODO 改为并发？！
//            threshold = (l + u) / 2;
//
//            FederateService.SQLExpression queryExpression = expression.toBuilder().setLiteral(threshold).build();
//
//            int count =federateRangeCount(
//                    queryExpression);
//            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
//            if (count > k) {
//                u = threshold;
//            } else if (count < k) {
//                l = threshold;
//            } else {
//                federateRangeQuery(expression.toBuilder().setLiteral(threshold).build());
//                return;
//            }
//        }
//        System.out.println("out of loop! approximate query: ");
//        federateRangeQuery(expression.toBuilder().setLiteral(minRadius).build());
//        return;
//    }
//    private static void federatePrivacyKnn(SQLExpression expression) {
//        StreamingIterator<Double> radiusIterator=federateKnnRadiusQuery(expression);
//        double minRadius = Double.MAX_VALUE;
//        while (radiusIterator.hasNext()){
//            double r= radiusIterator.next();
//            minRadius = r < minRadius ? r : minRadius;
//        }
//        int k =(int)expression.getLiteral();//TODO 精确度
//        double l = 0.0, u = minRadius, e = 1e-3;
//        double threshold = minRadius;
//        while (u - l >= e) {//TODO 改为并发？！
//            threshold = (l + u) / 2;
//
//            SQLExpression queryExpression = expression.toBuilder().setLiteral(threshold).build();
//
//            int count =federateRangeCount(queryExpression);//TODO secure
//            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
//            if (count > k) {
//                u = threshold;
//            } else if (count < k) {
//                l = threshold;
//            } else {
//                federatePrivacyRangeQuery(expression.toBuilder().setLiteral(threshold).build());
//                return;
//            }
//        }
//        System.out.println("out of loop! approximate query: ");
//        federatePrivacyRangeQuery(expression.toBuilder().setLiteral(minRadius).build());
//        return;
//    }

    public FederateService.SummationResponse privacySummation(FederateService.SummationRequest request) {
        try {
            FederateService.SummationResponse response = blockingStub.privacySummation(request);
            return response;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public FederateService.SummationResponse localSummation(FederateService.SummationRequest request) {
        try {
            FederateService.SummationResponse response = blockingStub.localSummation(request);
            return response;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public FederateService.UnionResponse privacyUnion(FederateService.UnionRequest unionRequest) {
        try {
            FederateService.UnionResponse unionResponse = blockingStub.privacyUnion(unionRequest);
            return unionResponse;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public FederateService.UnionResponse localUnion(FederateService.UnionRequest unionRequest) {
        try {
            FederateService.UnionResponse unionResponse = blockingStub.localUnion(unionRequest);
            return unionResponse;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public Double knnRadiusQuery(FederateService.SQLExpression expression) {
        FederateService.KnnRadiusQueryResponse response;
        try {
            response = blockingStub.knnRadiusQuery(expression);
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
            return 0.0;
        }
        System.out.println("服务器返回信息：" + response.getRadius());
        return response.getRadius();

    }


    public String getEndpoint() {
        return endpoint;
    }

    //    public boolean addClient(String endpoint) {
//        FederateService.AddClientRequest request = FederateService.AddClientRequest.newBuilder().setEndpoint(endpoint).build();
//        FederateService.GeneralResponse response;
//        try {
//            response = blockingStub.addClient(request);
//        } catch (StatusRuntimeException e) {
//            LOG.error("RPC failed in add client: {}", e.getStatus());
//            return false;
//        }
//        if (response.getStatus().getCode() != FederateService.Code.kOk) {
//            LOG.error("add client {} failed", endpoint);
//            return false;
//        } else {
//            LOG.debug("add client {} ok", endpoint);
//            return true;
//        }
//    }
    @Override
    public String toString() {
        return String.format("DBClient[%s]", endpoint);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FederateDBClient)) {
            return false;
        }
        return endpoint.equals(((FederateDBClient) obj).endpoint);
    }

    public void clearCache(String uuid) {
        try {
            blockingStub.clearCache(FederateService.CacheID.newBuilder().setUuid(uuid).build());
        } catch (StatusRuntimeException e) {
            LogUtils.error("RPC failed in clear cache: " + e.getStatus());
        }
    }
}
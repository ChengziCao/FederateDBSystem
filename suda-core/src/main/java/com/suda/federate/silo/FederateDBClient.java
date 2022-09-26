package com.suda.federate.silo;

import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.LogUtils;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/***
 * 一个 silo/db 对应一个FederateDBClient，调用 FederateDBService
 */
public final class FederateDBClient {
    private final FederateGrpc.FederateBlockingStub blockingStub; //非阻塞 AbstractBlockingStub
    //    private final FederateGrpc.FederateStub asyncStub; // extends AbstractAsyncStub 没用到？
    private String endpoint;
    private Integer siloId;

    public FederateDBClient(String host, int port,Integer siloId) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().maxInboundMessageSize(1024 * 1024 * 80));
        this.endpoint = String.format("%s:%d", host, port);
        this.siloId = siloId;
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

    public FederateService.SQLReply fedSpatialQuery(FederateService.SQLExpression expression) {
        FederateService.SQLReply response;
        try {
//            expression = expression.toBuilder().setTable(siloTableName).build();
            switch (expression.getFunction()) {
                case RANGE_COUNT:
                    response = blockingStub.publicRangeCount(expression);
                    break;
                case RANGE_QUERY:
                    response = blockingStub.publicRangeQuery(expression);
                    break;
                case KNN:
                    response = blockingStub.publicKNN(expression);
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

    public FederateService.Status privacyRangeCount(FederateService.SQLExpression request) {
        try {
            FederateService.Status status = blockingStub.privacyRangeCount(request);
            return status;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public FederateService.Status privacyPolygonRangeQuery(FederateService.SQLExpression request) {
        try {
            FederateService.Status status = blockingStub.privacyPolygonRangeQuery(request);
            return status;
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
        }
        return null;
    }

    public FederateService.Status privacyRangeQuery(FederateService.SQLExpression request) {
        try {
            FederateService.Status status = blockingStub.privacyRangeQuery(request);
            return status;
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
        FederateService.SQLReply response;
        try {
            response = blockingStub.knnRadiusQuery(expression);
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败：" + e.getMessage());
            return 0.0;
        }
        LogUtils.debug("knn radius query:" + response.getDoubleNumber());
        // System.out.println("服务器返回信息：" + response.getDoubleNumber());
        return response.getDoubleNumber();
    }


    public String getEndpoint() {
        return endpoint;
    }

    public Integer getSiloId() {
        return siloId;
    }

    public void setSiloId(Integer siloId) {
        this.siloId = siloId;
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
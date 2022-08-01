package com.suda.federate.application;

import com.suda.federate.rpc.FederateService;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.suda.federate.rpc.FederateGrpc;
//一个silo/db 对应一个FederateDBClient，
public final class FederateDBClient {//被edSpatialClient调用 List<String> endpoints = tableClients.keySet().stream().map(FederateDBClient::getEndpoint).collect(Collectors.toList());
    private static final Logger LOG = LogManager.getLogger(FederateDBClient.class);

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
    public void FederateRangeCount(){
        try {
            FederateService.SQLReply esponse = blockingStub.getResult(null);
        } catch (StatusRuntimeException e) {
            LOG.error("RPC failed in dp range count: {}", e.getStatus());
        }
    }

    //客户端方法
    public FederateService.SQLReply rangeCount(FederateService.SQLExpression expression){

        FederateService.SQLReply response;
        try{
            response = blockingStub.rangeCount(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return null;
        }
        return response;
    }
    public FederateService.UnionResponse privacyUnion(FederateService.UnionRequest unionRequest){
        try {
            FederateService.UnionResponse unionResponse= blockingStub.privacyUnion(unionRequest);
            return unionResponse;
        }catch (StatusRuntimeException e){
        System.out.println("RPC调用失败："+e.getMessage());
        }
        return null;
    }
    public FederateService.UnionResponse localUnion(FederateService.UnionRequest unionRequest){
        try {
            FederateService.UnionResponse unionResponse= blockingStub.localUnion(unionRequest);
            return unionResponse;
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
        }
        return null;
    }
    public boolean privacyRangeQuery(FederateService.SQLExpression expression){
        try {
            FederateService.Status status= blockingStub.privacyRangeQuery(expression);
            return status.getMsg().equals("ok");
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
        }
        return false;
    }
    //客户端方法
    public FederateService.SQLReplyList rangeQuery(FederateService.SQLExpression expression){

        FederateService.SQLReplyList response;
        try{
            response = blockingStub.rangeQuery(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return FederateService.SQLReplyList.newBuilder().build();
        }
        return response;
    }
    public Double knnRadiusQuery(FederateService.SQLExpression expression){
        FederateService.KnnRadiusQueryResponse response;
        try{
            response = blockingStub.knnRadiusQuery(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return 0.0;
        }
        System.out.println("服务器返回信息："+response.getRadius());
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
            return;
        } catch (StatusRuntimeException e) {
            LOG.error("RPC failed in clear cache: {}", e.getStatus());
            return;
        }
    }
}
package com.suda.federate.rpc;

import com.suda.federate.rpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client {

    //一个gRPC信道
    private final ManagedChannel channel;


    private final FederateGrpc.FederateBlockingStub blockingStub;//阻塞/同步 存根

    //初始化信道和存根
    public Client(int port, String host){
        this(ManagedChannelBuilder.forAddress(host,port).usePlaintext());
    }

    private Client(ManagedChannelBuilder<?> channelBuilder){
        channel = channelBuilder.build();
        blockingStub = FederateGrpc.newBlockingStub(channel);
    }

    public void shutDown()throws InterruptedException{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    //客户端方法
    public void rangeCount(FederateService.SQLExpression expression){

        FederateService.SQLReply response;
        try{
            response = blockingStub.rangeCount(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return;
        }

        System.out.println("服务器返回信息："+response.getMessage());
    }
    //客户端方法
    public void rangeQuery(FederateService.SQLExpression expression){

        FederateService.SQLReplyList response;
        try{
            response = blockingStub.rangeQuery(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return;
        }

        System.out.println("服务器返回信息：");
        response.getMessageList().forEach((s) ->{
            System.out.println(s);
        });
    }
    public void knnRadiusQuery(FederateService.SQLExpression expression){
        FederateService.SQLReply response;
        try{
            response = blockingStub.knnRadiusQuery(expression);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return;
        }
        System.out.println("服务器返回信息："+response.getMessage());

    }

    public static void main(String[] args)throws Exception {
        Client client = new Client(8887,"127.0.0.1");
        try {
            for (int i=0;i<5;i++){
//                client.greet();
                continue;
            }
        }finally {
            client.shutDown();
        }

    }

}


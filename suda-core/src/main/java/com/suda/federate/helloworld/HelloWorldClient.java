package com.suda.federate.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import com.suda.federate.helloworld.*;
import java.util.concurrent.TimeUnit;

public class HelloWorldClient {

    //一个gRPC信道
    private final ManagedChannel channel;


    private final GreeterGrpc.GreeterBlockingStub blockingStub;//阻塞/同步 存根

    //初始化信道和存根
    public HelloWorldClient(int port, String host){
        this(ManagedChannelBuilder.forAddress(host,port).usePlaintext());
    }

    private HelloWorldClient(ManagedChannelBuilder<?> channelBuilder){
        channel = channelBuilder.build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutDown()throws InterruptedException{
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    //客户端方法
    public void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
        }catch (StatusRuntimeException e){
            System.out.println("RPC调用失败："+e.getMessage());
            return;
        }

        System.out.println("服务器返回信息："+response.getMessage());
    }

    public static void main(String[] args)throws Exception {
        HelloWorldClient client = new HelloWorldClient(8887,"127.0.0.1");
        try {
            for (int i=0;i<5;i++){
                client.greet("world:"+i);
            }
        }finally {
            client.shutDown();
        }

    }

}


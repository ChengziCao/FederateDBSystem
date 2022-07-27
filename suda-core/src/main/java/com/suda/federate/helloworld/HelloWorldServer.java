package com.suda.federate.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class HelloWorldServer {


    private int port = 8887;
    private Server server;

    /**
     * 启动服务
     * @throws Exception
     */
    private void start()throws Exception{
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build().start();
        System.out.println("service start ....");
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloWorldServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop(){
        if (server != null){
            server.shutdown();
        }
    }
    // block 一直到程序退出
    private void blockUntilShutDown()throws InterruptedException{
        if (server != null){
            server.awaitTermination();
        }
    }

    // 定义一个实现服务接口的类
    private class GreeterImpl extends GreeterGrpc.GreeterImplBase{

        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver){
            System.out.println("收到的信息："+req.getName());

            //这里可以放置具体业务处理代码 start
            //这里可以放置具体业务处理代码 end

            //构造返回
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello: " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args)throws Exception {
        HelloWorldServer server  = new HelloWorldServer();
        server.start();
        server.blockUntilShutDown();
    }
}


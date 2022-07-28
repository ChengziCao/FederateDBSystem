//package com.suda.federate.rpc;
//
//
//import io.grpc.ServerBuilder;
//import io.grpc.stub.StreamObserver;
//
//public class Server {
//
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    private int port = 8887;
//    private io.grpc.Server server;
//
//    /**
//     * 启动服务
//     * @throws Exception
//     */
//    protected void start()throws Exception{
//        server = ServerBuilder.forPort(port)
//                .addService(new GreeterImpl())
//                .build().start();
//        System.out.println("service start ....");
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                System.err.println("*** shutting down gRPC server since JVM is shutting down");
//                Server.this.stop();
//                System.err.println("*** server shut down");
//            }
//        });
//    }
//
//    private void stop(){
//        if (server != null){
//            server.shutdown();
//        }
//    }
//    // block 一直到程序退出
//    protected void blockUntilShutDown()throws InterruptedException{
//        if (server != null){
//            server.awaitTermination();
//        }
//    }
//
//
//}
//

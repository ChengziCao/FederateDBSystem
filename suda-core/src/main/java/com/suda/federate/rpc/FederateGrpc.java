package com.suda.federate.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.31.1)",
    comments = "Source: service.proto")
public final class FederateGrpc {

  private FederateGrpc() {}

  public static final String SERVICE_NAME = "federate.Federate";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.AddClientRequest,
      com.suda.federate.rpc.FederateService.GeneralResponse> getAddClientMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddClient",
      requestType = com.suda.federate.rpc.FederateService.AddClientRequest.class,
      responseType = com.suda.federate.rpc.FederateService.GeneralResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.AddClientRequest,
      com.suda.federate.rpc.FederateService.GeneralResponse> getAddClientMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.AddClientRequest, com.suda.federate.rpc.FederateService.GeneralResponse> getAddClientMethod;
    if ((getAddClientMethod = FederateGrpc.getAddClientMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getAddClientMethod = FederateGrpc.getAddClientMethod) == null) {
          FederateGrpc.getAddClientMethod = getAddClientMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.AddClientRequest, com.suda.federate.rpc.FederateService.GeneralResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddClient"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.AddClientRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.GeneralResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("AddClient"))
              .build();
        }
      }
    }
    return getAddClientMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getGetResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetResult",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getGetResultMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getGetResultMethod;
    if ((getGetResultMethod = FederateGrpc.getGetResultMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getGetResultMethod = FederateGrpc.getGetResultMethod) == null) {
          FederateGrpc.getGetResultMethod = getGetResultMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("GetResult"))
              .build();
        }
      }
    }
    return getGetResultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getRangeCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RangeCount",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getRangeCountMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getRangeCountMethod;
    if ((getRangeCountMethod = FederateGrpc.getRangeCountMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getRangeCountMethod = FederateGrpc.getRangeCountMethod) == null) {
          FederateGrpc.getRangeCountMethod = getRangeCountMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RangeCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("RangeCount"))
              .build();
        }
      }
    }
    return getRangeCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReplyList> getRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RangeQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReplyList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReplyList> getRangeQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReplyList> getRangeQueryMethod;
    if ((getRangeQueryMethod = FederateGrpc.getRangeQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getRangeQueryMethod = FederateGrpc.getRangeQueryMethod) == null) {
          FederateGrpc.getRangeQueryMethod = getRangeQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReplyList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RangeQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReplyList.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("RangeQuery"))
              .build();
        }
      }
    }
    return getRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.Status> getPrivacyRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "privacyRangeQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.Status> getPrivacyRangeQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status> getPrivacyRangeQueryMethod;
    if ((getPrivacyRangeQueryMethod = FederateGrpc.getPrivacyRangeQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPrivacyRangeQueryMethod = FederateGrpc.getPrivacyRangeQueryMethod) == null) {
          FederateGrpc.getPrivacyRangeQueryMethod = getPrivacyRangeQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "privacyRangeQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.Status.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("privacyRangeQuery"))
              .build();
        }
      }
    }
    return getPrivacyRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getKnnRadiusQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "KnnRadiusQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getKnnRadiusQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getKnnRadiusQueryMethod;
    if ((getKnnRadiusQueryMethod = FederateGrpc.getKnnRadiusQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getKnnRadiusQueryMethod = FederateGrpc.getKnnRadiusQueryMethod) == null) {
          FederateGrpc.getKnnRadiusQueryMethod = getKnnRadiusQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "KnnRadiusQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("KnnRadiusQuery"))
              .build();
        }
      }
    }
    return getKnnRadiusQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest,
      com.suda.federate.rpc.FederateService.UnionResponse> getPrivacyUnionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "privacyUnion",
      requestType = com.suda.federate.rpc.FederateService.UnionRequest.class,
      responseType = com.suda.federate.rpc.FederateService.UnionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest,
      com.suda.federate.rpc.FederateService.UnionResponse> getPrivacyUnionMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest, com.suda.federate.rpc.FederateService.UnionResponse> getPrivacyUnionMethod;
    if ((getPrivacyUnionMethod = FederateGrpc.getPrivacyUnionMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPrivacyUnionMethod = FederateGrpc.getPrivacyUnionMethod) == null) {
          FederateGrpc.getPrivacyUnionMethod = getPrivacyUnionMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.UnionRequest, com.suda.federate.rpc.FederateService.UnionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "privacyUnion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.UnionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.UnionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("privacyUnion"))
              .build();
        }
      }
    }
    return getPrivacyUnionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FederateStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FederateStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FederateStub>() {
        @java.lang.Override
        public FederateStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FederateStub(channel, callOptions);
        }
      };
    return FederateStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FederateBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FederateBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FederateBlockingStub>() {
        @java.lang.Override
        public FederateBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FederateBlockingStub(channel, callOptions);
        }
      };
    return FederateBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FederateFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FederateFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FederateFutureStub>() {
        @java.lang.Override
        public FederateFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FederateFutureStub(channel, callOptions);
        }
      };
    return FederateFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class FederateImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public void addClient(com.suda.federate.rpc.FederateService.AddClientRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.GeneralResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAddClientMethod(), responseObserver);
    }

    /**
     */
    public void getResult(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetResultMethod(), responseObserver);
    }

    /**
     */
    public void rangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getRangeCountMethod(), responseObserver);
    }

    /**
     */
    public void rangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReplyList> responseObserver) {
      asyncUnimplementedUnaryCall(getRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void privacyRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getKnnRadiusQueryMethod(), responseObserver);
    }

    /**
     */
    public void privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyUnionMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAddClientMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.AddClientRequest,
                com.suda.federate.rpc.FederateService.GeneralResponse>(
                  this, METHODID_ADD_CLIENT)))
          .addMethod(
            getGetResultMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_GET_RESULT)))
          .addMethod(
            getRangeCountMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_RANGE_COUNT)))
          .addMethod(
            getRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReplyList>(
                  this, METHODID_RANGE_QUERY)))
          .addMethod(
            getPrivacyRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.Status>(
                  this, METHODID_PRIVACY_RANGE_QUERY)))
          .addMethod(
            getKnnRadiusQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_KNN_RADIUS_QUERY)))
          .addMethod(
            getPrivacyUnionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.UnionRequest,
                com.suda.federate.rpc.FederateService.UnionResponse>(
                  this, METHODID_PRIVACY_UNION)))
          .build();
    }
  }

  /**
   */
  public static final class FederateStub extends io.grpc.stub.AbstractAsyncStub<FederateStub> {
    private FederateStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederateStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FederateStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public void addClient(com.suda.federate.rpc.FederateService.AddClientRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.GeneralResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAddClientMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getResult(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetResultMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void rangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRangeCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void rangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReplyList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRangeQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void privacyRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacyRangeQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getKnnRadiusQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacyUnionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class FederateBlockingStub extends io.grpc.stub.AbstractBlockingStub<FederateBlockingStub> {
    private FederateBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederateBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FederateBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public com.suda.federate.rpc.FederateService.GeneralResponse addClient(com.suda.federate.rpc.FederateService.AddClientRequest request) {
      return blockingUnaryCall(
          getChannel(), getAddClientMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReply getResult(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getGetResultMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReply rangeCount(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getRangeCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReplyList rangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.Status privacyRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReply knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getKnnRadiusQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.UnionResponse privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyUnionMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class FederateFutureStub extends io.grpc.stub.AbstractFutureStub<FederateFutureStub> {
    private FederateFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FederateFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FederateFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends a greeting
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.GeneralResponse> addClient(
        com.suda.federate.rpc.FederateService.AddClientRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAddClientMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> getResult(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getGetResultMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> rangeCount(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getRangeCountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReplyList> rangeQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.Status> privacyRangeQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacyRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> knnRadiusQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getKnnRadiusQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.UnionResponse> privacyUnion(
        com.suda.federate.rpc.FederateService.UnionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacyUnionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_CLIENT = 0;
  private static final int METHODID_GET_RESULT = 1;
  private static final int METHODID_RANGE_COUNT = 2;
  private static final int METHODID_RANGE_QUERY = 3;
  private static final int METHODID_PRIVACY_RANGE_QUERY = 4;
  private static final int METHODID_KNN_RADIUS_QUERY = 5;
  private static final int METHODID_PRIVACY_UNION = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FederateImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FederateImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_CLIENT:
          serviceImpl.addClient((com.suda.federate.rpc.FederateService.AddClientRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.GeneralResponse>) responseObserver);
          break;
        case METHODID_GET_RESULT:
          serviceImpl.getResult((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_RANGE_COUNT:
          serviceImpl.rangeCount((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_RANGE_QUERY:
          serviceImpl.rangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReplyList>) responseObserver);
          break;
        case METHODID_PRIVACY_RANGE_QUERY:
          serviceImpl.privacyRangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status>) responseObserver);
          break;
        case METHODID_KNN_RADIUS_QUERY:
          serviceImpl.knnRadiusQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_PRIVACY_UNION:
          serviceImpl.privacyUnion((com.suda.federate.rpc.FederateService.UnionRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class FederateBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FederateBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.suda.federate.rpc.FederateService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Federate");
    }
  }

  private static final class FederateFileDescriptorSupplier
      extends FederateBaseDescriptorSupplier {
    FederateFileDescriptorSupplier() {}
  }

  private static final class FederateMethodDescriptorSupplier
      extends FederateBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FederateMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FederateGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FederateFileDescriptorSupplier())
              .addMethod(getAddClientMethod())
              .addMethod(getGetResultMethod())
              .addMethod(getRangeCountMethod())
              .addMethod(getRangeQueryMethod())
              .addMethod(getPrivacyRangeQueryMethod())
              .addMethod(getKnnRadiusQueryMethod())
              .addMethod(getPrivacyUnionMethod())
              .build();
        }
      }
    }
    return result;
  }
}

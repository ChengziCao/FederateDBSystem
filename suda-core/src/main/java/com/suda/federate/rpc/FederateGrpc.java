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
      com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "publicRangeCount",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeCountMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeCountMethod;
    if ((getPublicRangeCountMethod = FederateGrpc.getPublicRangeCountMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPublicRangeCountMethod = FederateGrpc.getPublicRangeCountMethod) == null) {
          FederateGrpc.getPublicRangeCountMethod = getPublicRangeCountMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "publicRangeCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("publicRangeCount"))
              .build();
        }
      }
    }
    return getPublicRangeCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "publicRangeQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getPublicRangeQueryMethod;
    if ((getPublicRangeQueryMethod = FederateGrpc.getPublicRangeQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPublicRangeQueryMethod = FederateGrpc.getPublicRangeQueryMethod) == null) {
          FederateGrpc.getPublicRangeQueryMethod = getPublicRangeQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "publicRangeQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("publicRangeQuery"))
              .build();
        }
      }
    }
    return getPublicRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getPublicPolygonRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "publicPolygonRangeQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.SQLReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.SQLReply> getPublicPolygonRangeQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply> getPublicPolygonRangeQueryMethod;
    if ((getPublicPolygonRangeQueryMethod = FederateGrpc.getPublicPolygonRangeQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPublicPolygonRangeQueryMethod = FederateGrpc.getPublicPolygonRangeQueryMethod) == null) {
          FederateGrpc.getPublicPolygonRangeQueryMethod = getPublicPolygonRangeQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.SQLReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "publicPolygonRangeQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLReply.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("publicPolygonRangeQuery"))
              .build();
        }
      }
    }
    return getPublicPolygonRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.Status> getPrivacyRangeCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "privacyRangeCount",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.Status> getPrivacyRangeCountMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status> getPrivacyRangeCountMethod;
    if ((getPrivacyRangeCountMethod = FederateGrpc.getPrivacyRangeCountMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPrivacyRangeCountMethod = FederateGrpc.getPrivacyRangeCountMethod) == null) {
          FederateGrpc.getPrivacyRangeCountMethod = getPrivacyRangeCountMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "privacyRangeCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.Status.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("privacyRangeCount"))
              .build();
        }
      }
    }
    return getPrivacyRangeCountMethod;
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
      com.suda.federate.rpc.FederateService.Status> getPrivacyPolygonRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "privacyPolygonRangeQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.Status> getPrivacyPolygonRangeQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status> getPrivacyPolygonRangeQueryMethod;
    if ((getPrivacyPolygonRangeQueryMethod = FederateGrpc.getPrivacyPolygonRangeQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPrivacyPolygonRangeQueryMethod = FederateGrpc.getPrivacyPolygonRangeQueryMethod) == null) {
          FederateGrpc.getPrivacyPolygonRangeQueryMethod = getPrivacyPolygonRangeQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "privacyPolygonRangeQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.Status.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("privacyPolygonRangeQuery"))
              .build();
        }
      }
    }
    return getPrivacyPolygonRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> getKnnRadiusQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "KnnRadiusQuery",
      requestType = com.suda.federate.rpc.FederateService.SQLExpression.class,
      responseType = com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression,
      com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> getKnnRadiusQueryMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> getKnnRadiusQueryMethod;
    if ((getKnnRadiusQueryMethod = FederateGrpc.getKnnRadiusQueryMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getKnnRadiusQueryMethod = FederateGrpc.getKnnRadiusQueryMethod) == null) {
          FederateGrpc.getKnnRadiusQueryMethod = getKnnRadiusQueryMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SQLExpression, com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "KnnRadiusQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SQLExpression.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("KnnRadiusQuery"))
              .build();
        }
      }
    }
    return getKnnRadiusQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest,
      com.suda.federate.rpc.FederateService.SummationResponse> getPrivacySummationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "privacySummation",
      requestType = com.suda.federate.rpc.FederateService.SummationRequest.class,
      responseType = com.suda.federate.rpc.FederateService.SummationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest,
      com.suda.federate.rpc.FederateService.SummationResponse> getPrivacySummationMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest, com.suda.federate.rpc.FederateService.SummationResponse> getPrivacySummationMethod;
    if ((getPrivacySummationMethod = FederateGrpc.getPrivacySummationMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getPrivacySummationMethod = FederateGrpc.getPrivacySummationMethod) == null) {
          FederateGrpc.getPrivacySummationMethod = getPrivacySummationMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SummationRequest, com.suda.federate.rpc.FederateService.SummationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "privacySummation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SummationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SummationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("privacySummation"))
              .build();
        }
      }
    }
    return getPrivacySummationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest,
      com.suda.federate.rpc.FederateService.SummationResponse> getLocalSummationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "localSummation",
      requestType = com.suda.federate.rpc.FederateService.SummationRequest.class,
      responseType = com.suda.federate.rpc.FederateService.SummationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest,
      com.suda.federate.rpc.FederateService.SummationResponse> getLocalSummationMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.SummationRequest, com.suda.federate.rpc.FederateService.SummationResponse> getLocalSummationMethod;
    if ((getLocalSummationMethod = FederateGrpc.getLocalSummationMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getLocalSummationMethod = FederateGrpc.getLocalSummationMethod) == null) {
          FederateGrpc.getLocalSummationMethod = getLocalSummationMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.SummationRequest, com.suda.federate.rpc.FederateService.SummationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "localSummation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SummationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.SummationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("localSummation"))
              .build();
        }
      }
    }
    return getLocalSummationMethod;
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

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest,
      com.suda.federate.rpc.FederateService.UnionResponse> getLocalUnionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "localUnion",
      requestType = com.suda.federate.rpc.FederateService.UnionRequest.class,
      responseType = com.suda.federate.rpc.FederateService.UnionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest,
      com.suda.federate.rpc.FederateService.UnionResponse> getLocalUnionMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.UnionRequest, com.suda.federate.rpc.FederateService.UnionResponse> getLocalUnionMethod;
    if ((getLocalUnionMethod = FederateGrpc.getLocalUnionMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getLocalUnionMethod = FederateGrpc.getLocalUnionMethod) == null) {
          FederateGrpc.getLocalUnionMethod = getLocalUnionMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.UnionRequest, com.suda.federate.rpc.FederateService.UnionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "localUnion"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.UnionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.UnionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("localUnion"))
              .build();
        }
      }
    }
    return getLocalUnionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.CacheID,
      com.google.protobuf.Empty> getClearCacheMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ClearCache",
      requestType = com.suda.federate.rpc.FederateService.CacheID.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.CacheID,
      com.google.protobuf.Empty> getClearCacheMethod() {
    io.grpc.MethodDescriptor<com.suda.federate.rpc.FederateService.CacheID, com.google.protobuf.Empty> getClearCacheMethod;
    if ((getClearCacheMethod = FederateGrpc.getClearCacheMethod) == null) {
      synchronized (FederateGrpc.class) {
        if ((getClearCacheMethod = FederateGrpc.getClearCacheMethod) == null) {
          FederateGrpc.getClearCacheMethod = getClearCacheMethod =
              io.grpc.MethodDescriptor.<com.suda.federate.rpc.FederateService.CacheID, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ClearCache"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.suda.federate.rpc.FederateService.CacheID.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new FederateMethodDescriptorSupplier("ClearCache"))
              .build();
        }
      }
    }
    return getClearCacheMethod;
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
     * 打招呼，打通了说明add成功了，更稳妥，还没做
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
    public void publicRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getPublicRangeCountMethod(), responseObserver);
    }

    /**
     */
    public void publicRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getPublicRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void publicPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnimplementedUnaryCall(getPublicPolygonRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void privacyRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyRangeCountMethod(), responseObserver);
    }

    /**
     */
    public void privacyRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void privacyPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyPolygonRangeQueryMethod(), responseObserver);
    }

    /**
     */
    public void knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getKnnRadiusQueryMethod(), responseObserver);
    }

    /**
     */
    public void privacySummation(com.suda.federate.rpc.FederateService.SummationRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacySummationMethod(), responseObserver);
    }

    /**
     */
    public void localSummation(com.suda.federate.rpc.FederateService.SummationRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLocalSummationMethod(), responseObserver);
    }

    /**
     */
    public void privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPrivacyUnionMethod(), responseObserver);
    }

    /**
     */
    public void localUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLocalUnionMethod(), responseObserver);
    }

    /**
     */
    public void clearCache(com.suda.federate.rpc.FederateService.CacheID request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getClearCacheMethod(), responseObserver);
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
            getPublicRangeCountMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_PUBLIC_RANGE_COUNT)))
          .addMethod(
            getPublicRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_PUBLIC_RANGE_QUERY)))
          .addMethod(
            getPublicPolygonRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.SQLReply>(
                  this, METHODID_PUBLIC_POLYGON_RANGE_QUERY)))
          .addMethod(
            getPrivacyRangeCountMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.Status>(
                  this, METHODID_PRIVACY_RANGE_COUNT)))
          .addMethod(
            getPrivacyRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.Status>(
                  this, METHODID_PRIVACY_RANGE_QUERY)))
          .addMethod(
            getPrivacyPolygonRangeQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.Status>(
                  this, METHODID_PRIVACY_POLYGON_RANGE_QUERY)))
          .addMethod(
            getKnnRadiusQueryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SQLExpression,
                com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse>(
                  this, METHODID_KNN_RADIUS_QUERY)))
          .addMethod(
            getPrivacySummationMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SummationRequest,
                com.suda.federate.rpc.FederateService.SummationResponse>(
                  this, METHODID_PRIVACY_SUMMATION)))
          .addMethod(
            getLocalSummationMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.SummationRequest,
                com.suda.federate.rpc.FederateService.SummationResponse>(
                  this, METHODID_LOCAL_SUMMATION)))
          .addMethod(
            getPrivacyUnionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.UnionRequest,
                com.suda.federate.rpc.FederateService.UnionResponse>(
                  this, METHODID_PRIVACY_UNION)))
          .addMethod(
            getLocalUnionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.UnionRequest,
                com.suda.federate.rpc.FederateService.UnionResponse>(
                  this, METHODID_LOCAL_UNION)))
          .addMethod(
            getClearCacheMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.suda.federate.rpc.FederateService.CacheID,
                com.google.protobuf.Empty>(
                  this, METHODID_CLEAR_CACHE)))
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
     * 打招呼，打通了说明add成功了，更稳妥，还没做
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
    public void publicRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublicRangeCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publicRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublicRangeQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void publicPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublicPolygonRangeQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void privacyRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacyRangeCountMethod(), getCallOptions()), request, responseObserver);
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
    public void privacyPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacyPolygonRangeQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getKnnRadiusQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void privacySummation(com.suda.federate.rpc.FederateService.SummationRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacySummationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void localSummation(com.suda.federate.rpc.FederateService.SummationRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLocalSummationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrivacyUnionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void localUnion(com.suda.federate.rpc.FederateService.UnionRequest request,
        io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLocalUnionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clearCache(com.suda.federate.rpc.FederateService.CacheID request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getClearCacheMethod(), getCallOptions()), request, responseObserver);
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
     * 打招呼，打通了说明add成功了，更稳妥，还没做
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
    public com.suda.federate.rpc.FederateService.SQLReply publicRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPublicRangeCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReply publicRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPublicRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SQLReply publicPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPublicPolygonRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.Status privacyRangeCount(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyRangeCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.Status privacyRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.Status privacyPolygonRangeQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyPolygonRangeQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse knnRadiusQuery(com.suda.federate.rpc.FederateService.SQLExpression request) {
      return blockingUnaryCall(
          getChannel(), getKnnRadiusQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SummationResponse privacySummation(com.suda.federate.rpc.FederateService.SummationRequest request) {
      return blockingUnaryCall(
          getChannel(), getPrivacySummationMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.SummationResponse localSummation(com.suda.federate.rpc.FederateService.SummationRequest request) {
      return blockingUnaryCall(
          getChannel(), getLocalSummationMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.UnionResponse privacyUnion(com.suda.federate.rpc.FederateService.UnionRequest request) {
      return blockingUnaryCall(
          getChannel(), getPrivacyUnionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.suda.federate.rpc.FederateService.UnionResponse localUnion(com.suda.federate.rpc.FederateService.UnionRequest request) {
      return blockingUnaryCall(
          getChannel(), getLocalUnionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty clearCache(com.suda.federate.rpc.FederateService.CacheID request) {
      return blockingUnaryCall(
          getChannel(), getClearCacheMethod(), getCallOptions(), request);
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
     * 打招呼，打通了说明add成功了，更稳妥，还没做
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
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> publicRangeCount(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPublicRangeCountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> publicRangeQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPublicRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SQLReply> publicPolygonRangeQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPublicPolygonRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.Status> privacyRangeCount(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacyRangeCountMethod(), getCallOptions()), request);
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
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.Status> privacyPolygonRangeQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacyPolygonRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse> knnRadiusQuery(
        com.suda.federate.rpc.FederateService.SQLExpression request) {
      return futureUnaryCall(
          getChannel().newCall(getKnnRadiusQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SummationResponse> privacySummation(
        com.suda.federate.rpc.FederateService.SummationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacySummationMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.SummationResponse> localSummation(
        com.suda.federate.rpc.FederateService.SummationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLocalSummationMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.UnionResponse> privacyUnion(
        com.suda.federate.rpc.FederateService.UnionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPrivacyUnionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.suda.federate.rpc.FederateService.UnionResponse> localUnion(
        com.suda.federate.rpc.FederateService.UnionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLocalUnionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> clearCache(
        com.suda.federate.rpc.FederateService.CacheID request) {
      return futureUnaryCall(
          getChannel().newCall(getClearCacheMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_CLIENT = 0;
  private static final int METHODID_GET_RESULT = 1;
  private static final int METHODID_PUBLIC_RANGE_COUNT = 2;
  private static final int METHODID_PUBLIC_RANGE_QUERY = 3;
  private static final int METHODID_PUBLIC_POLYGON_RANGE_QUERY = 4;
  private static final int METHODID_PRIVACY_RANGE_COUNT = 5;
  private static final int METHODID_PRIVACY_RANGE_QUERY = 6;
  private static final int METHODID_PRIVACY_POLYGON_RANGE_QUERY = 7;
  private static final int METHODID_KNN_RADIUS_QUERY = 8;
  private static final int METHODID_PRIVACY_SUMMATION = 9;
  private static final int METHODID_LOCAL_SUMMATION = 10;
  private static final int METHODID_PRIVACY_UNION = 11;
  private static final int METHODID_LOCAL_UNION = 12;
  private static final int METHODID_CLEAR_CACHE = 13;

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
        case METHODID_PUBLIC_RANGE_COUNT:
          serviceImpl.publicRangeCount((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_PUBLIC_RANGE_QUERY:
          serviceImpl.publicRangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_PUBLIC_POLYGON_RANGE_QUERY:
          serviceImpl.publicPolygonRangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SQLReply>) responseObserver);
          break;
        case METHODID_PRIVACY_RANGE_COUNT:
          serviceImpl.privacyRangeCount((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status>) responseObserver);
          break;
        case METHODID_PRIVACY_RANGE_QUERY:
          serviceImpl.privacyRangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status>) responseObserver);
          break;
        case METHODID_PRIVACY_POLYGON_RANGE_QUERY:
          serviceImpl.privacyPolygonRangeQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.Status>) responseObserver);
          break;
        case METHODID_KNN_RADIUS_QUERY:
          serviceImpl.knnRadiusQuery((com.suda.federate.rpc.FederateService.SQLExpression) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.KnnRadiusQueryResponse>) responseObserver);
          break;
        case METHODID_PRIVACY_SUMMATION:
          serviceImpl.privacySummation((com.suda.federate.rpc.FederateService.SummationRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse>) responseObserver);
          break;
        case METHODID_LOCAL_SUMMATION:
          serviceImpl.localSummation((com.suda.federate.rpc.FederateService.SummationRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.SummationResponse>) responseObserver);
          break;
        case METHODID_PRIVACY_UNION:
          serviceImpl.privacyUnion((com.suda.federate.rpc.FederateService.UnionRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse>) responseObserver);
          break;
        case METHODID_LOCAL_UNION:
          serviceImpl.localUnion((com.suda.federate.rpc.FederateService.UnionRequest) request,
              (io.grpc.stub.StreamObserver<com.suda.federate.rpc.FederateService.UnionResponse>) responseObserver);
          break;
        case METHODID_CLEAR_CACHE:
          serviceImpl.clearCache((com.suda.federate.rpc.FederateService.CacheID) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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
              .addMethod(getPublicRangeCountMethod())
              .addMethod(getPublicRangeQueryMethod())
              .addMethod(getPublicPolygonRangeQueryMethod())
              .addMethod(getPrivacyRangeCountMethod())
              .addMethod(getPrivacyRangeQueryMethod())
              .addMethod(getPrivacyPolygonRangeQueryMethod())
              .addMethod(getKnnRadiusQueryMethod())
              .addMethod(getPrivacySummationMethod())
              .addMethod(getLocalSummationMethod())
              .addMethod(getPrivacyUnionMethod())
              .addMethod(getLocalUnionMethod())
              .addMethod(getClearCacheMethod())
              .build();
        }
      }
    }
    return result;
  }
}

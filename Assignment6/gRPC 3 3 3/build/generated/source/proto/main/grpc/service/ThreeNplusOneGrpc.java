package service;

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
    value = "by gRPC proto compiler (version 1.33.1)",
    comments = "Source: services/ThreeNplusOne.proto")
public final class ThreeNplusOneGrpc {

  private ThreeNplusOneGrpc() {}

  public static final String SERVICE_NAME = "services.ThreeNplusOne";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<service.ThreeReq,
      service.ThreeRes> getGetResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getResult",
      requestType = service.ThreeReq.class,
      responseType = service.ThreeRes.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<service.ThreeReq,
      service.ThreeRes> getGetResultMethod() {
    io.grpc.MethodDescriptor<service.ThreeReq, service.ThreeRes> getGetResultMethod;
    if ((getGetResultMethod = ThreeNplusOneGrpc.getGetResultMethod) == null) {
      synchronized (ThreeNplusOneGrpc.class) {
        if ((getGetResultMethod = ThreeNplusOneGrpc.getGetResultMethod) == null) {
          ThreeNplusOneGrpc.getGetResultMethod = getGetResultMethod =
              io.grpc.MethodDescriptor.<service.ThreeReq, service.ThreeRes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  service.ThreeReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  service.ThreeRes.getDefaultInstance()))
              .setSchemaDescriptor(new ThreeNplusOneMethodDescriptorSupplier("getResult"))
              .build();
        }
      }
    }
    return getGetResultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<service.ThreeReq,
      service.ThreePathRes> getGetPathMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getPath",
      requestType = service.ThreeReq.class,
      responseType = service.ThreePathRes.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<service.ThreeReq,
      service.ThreePathRes> getGetPathMethod() {
    io.grpc.MethodDescriptor<service.ThreeReq, service.ThreePathRes> getGetPathMethod;
    if ((getGetPathMethod = ThreeNplusOneGrpc.getGetPathMethod) == null) {
      synchronized (ThreeNplusOneGrpc.class) {
        if ((getGetPathMethod = ThreeNplusOneGrpc.getGetPathMethod) == null) {
          ThreeNplusOneGrpc.getGetPathMethod = getGetPathMethod =
              io.grpc.MethodDescriptor.<service.ThreeReq, service.ThreePathRes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getPath"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  service.ThreeReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  service.ThreePathRes.getDefaultInstance()))
              .setSchemaDescriptor(new ThreeNplusOneMethodDescriptorSupplier("getPath"))
              .build();
        }
      }
    }
    return getGetPathMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ThreeNplusOneStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneStub>() {
        @java.lang.Override
        public ThreeNplusOneStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ThreeNplusOneStub(channel, callOptions);
        }
      };
    return ThreeNplusOneStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ThreeNplusOneBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneBlockingStub>() {
        @java.lang.Override
        public ThreeNplusOneBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ThreeNplusOneBlockingStub(channel, callOptions);
        }
      };
    return ThreeNplusOneBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ThreeNplusOneFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ThreeNplusOneFutureStub>() {
        @java.lang.Override
        public ThreeNplusOneFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ThreeNplusOneFutureStub(channel, callOptions);
        }
      };
    return ThreeNplusOneFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ThreeNplusOneImplBase implements io.grpc.BindableService {

    /**
     */
    public void getResult(service.ThreeReq request,
        io.grpc.stub.StreamObserver<service.ThreeRes> responseObserver) {
      asyncUnimplementedUnaryCall(getGetResultMethod(), responseObserver);
    }

    /**
     */
    public void getPath(service.ThreeReq request,
        io.grpc.stub.StreamObserver<service.ThreePathRes> responseObserver) {
      asyncUnimplementedUnaryCall(getGetPathMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetResultMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                service.ThreeReq,
                service.ThreeRes>(
                  this, METHODID_GET_RESULT)))
          .addMethod(
            getGetPathMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                service.ThreeReq,
                service.ThreePathRes>(
                  this, METHODID_GET_PATH)))
          .build();
    }
  }

  /**
   */
  public static final class ThreeNplusOneStub extends io.grpc.stub.AbstractAsyncStub<ThreeNplusOneStub> {
    private ThreeNplusOneStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ThreeNplusOneStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ThreeNplusOneStub(channel, callOptions);
    }

    /**
     */
    public void getResult(service.ThreeReq request,
        io.grpc.stub.StreamObserver<service.ThreeRes> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetResultMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getPath(service.ThreeReq request,
        io.grpc.stub.StreamObserver<service.ThreePathRes> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetPathMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ThreeNplusOneBlockingStub extends io.grpc.stub.AbstractBlockingStub<ThreeNplusOneBlockingStub> {
    private ThreeNplusOneBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ThreeNplusOneBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ThreeNplusOneBlockingStub(channel, callOptions);
    }

    /**
     */
    public service.ThreeRes getResult(service.ThreeReq request) {
      return blockingUnaryCall(
          getChannel(), getGetResultMethod(), getCallOptions(), request);
    }

    /**
     */
    public service.ThreePathRes getPath(service.ThreeReq request) {
      return blockingUnaryCall(
          getChannel(), getGetPathMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ThreeNplusOneFutureStub extends io.grpc.stub.AbstractFutureStub<ThreeNplusOneFutureStub> {
    private ThreeNplusOneFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ThreeNplusOneFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ThreeNplusOneFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<service.ThreeRes> getResult(
        service.ThreeReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetResultMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<service.ThreePathRes> getPath(
        service.ThreeReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetPathMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_RESULT = 0;
  private static final int METHODID_GET_PATH = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ThreeNplusOneImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ThreeNplusOneImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_RESULT:
          serviceImpl.getResult((service.ThreeReq) request,
              (io.grpc.stub.StreamObserver<service.ThreeRes>) responseObserver);
          break;
        case METHODID_GET_PATH:
          serviceImpl.getPath((service.ThreeReq) request,
              (io.grpc.stub.StreamObserver<service.ThreePathRes>) responseObserver);
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

  private static abstract class ThreeNplusOneBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ThreeNplusOneBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return service.ThreeNplusOneProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ThreeNplusOne");
    }
  }

  private static final class ThreeNplusOneFileDescriptorSupplier
      extends ThreeNplusOneBaseDescriptorSupplier {
    ThreeNplusOneFileDescriptorSupplier() {}
  }

  private static final class ThreeNplusOneMethodDescriptorSupplier
      extends ThreeNplusOneBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ThreeNplusOneMethodDescriptorSupplier(String methodName) {
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
      synchronized (ThreeNplusOneGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ThreeNplusOneFileDescriptorSupplier())
              .addMethod(getGetResultMethod())
              .addMethod(getGetPathMethod())
              .build();
        }
      }
    }
    return result;
  }
}

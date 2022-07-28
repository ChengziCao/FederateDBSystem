package com.suda.federate.silo;

import com.suda.federate.application.FederateDBClient;
import com.suda.federate.config.FedSpatialConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.dp.Laplace;
import com.suda.federate.sql.function.FD_RangeCount;
import com.suda.federate.utils.ENUM;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 定义一个实现服务接口的类
public abstract class FederateDBService extends FederateGrpc.FederateImplBase {
    private static final Logger LOG = LogManager.getLogger(FederateDBService.class);
    protected final Map<String, FederateDBClient> federateClientMap;
    protected final Lock clientLock;

    protected Random random;
    private final int THREAD_POOL_SIZE;
    private final ExecutorService executorService;
    private final Laplace lp;

    FederateDBService(int threadNum) {
        federateClientMap = new TreeMap<>();
        clientLock = new ReentrantLock();
        random = new Random();
        THREAD_POOL_SIZE = threadNum;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.lp = new Laplace(FedSpatialConfig.EPS_DP, FedSpatialConfig.SD_DP);
    }

//        @Override
//        public void getResult(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
//            System.out.println("收到的信息："+request.getFunction());
//
//            //这里可以放置具体业务处理代码 start
//            //这里可以放置具体业务处理代码 end
//            List<String> variables = null;
//            if (request.getFunction().equals(ENUM.FUNCTION.RANGE_COUNT.name())){
//                FD_RangeCount rangeCounting = new FD_RangeCount(variables);
////                Integer result = localRangeCount(rangeCounting.point, rangeCounting.radius);
//            }
//            //构造返回
//            FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setMessage(0.0).build();
//            responseObserver.onNext(reply);
//            responseObserver.onCompleted();
//        }

        @Override
        public void addClient(FederateService.AddClientRequest request, StreamObserver<FederateService.GeneralResponse> responseObserver) {
            super.addClient(request, responseObserver);
        }



}

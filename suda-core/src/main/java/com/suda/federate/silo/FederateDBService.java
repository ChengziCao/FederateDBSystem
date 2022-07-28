package com.suda.federate.silo;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateGrpc;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.sql.function.FD_RangeCount;
import com.suda.federate.utils.ENUM;
import io.grpc.stub.StreamObserver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// 定义一个实现服务接口的类
public class FederateDBService extends FederateGrpc.FederateImplBase{


        @Override
        public void getResult(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
            System.out.println("收到的信息："+request.getFunction());

            //这里可以放置具体业务处理代码 start
            //这里可以放置具体业务处理代码 end
            List<String> variables = null;
            if (request.getFunction().equals(ENUM.FUNCTION.RANGE_COUNT.name())){
                FD_RangeCount rangeCounting = new FD_RangeCount(variables);
//                Integer result = localRangeCount(rangeCounting.point, rangeCounting.radius);
            }
            //构造返回
            FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setMessage(0.0).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void addClient(FederateService.AddClientRequest request, StreamObserver<FederateService.GeneralResponse> responseObserver) {
            super.addClient(request, responseObserver);
        }



}

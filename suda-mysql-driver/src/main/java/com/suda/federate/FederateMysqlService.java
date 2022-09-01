package com.suda.federate;

import com.suda.federate.config.DbConfig;
import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.security.sha.SiloCache;
import com.suda.federate.silo.FederateDBService;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.SQLExecutor;
import com.suda.federate.utils.SQLGenerator;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FederateMysqlService extends FederateDBService {

    private DatabaseMetaData metaData;
    private SQLExecutor executor;

    FederateMysqlService(DbConfig config) {
        try {
            Class.forName(config.getDriver());
            Connection conn = DriverManager.getConnection(config.getUrl(),
                    config.getUser(), config.getPassword());
            executor = new SQLExecutor(conn, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void publicRangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Integer result = 0;
        try {
            result = executor.localRangeCount(request.getPoint(),
                    request.getTable(), request.getDoubleNumber());
            //构造返回
            FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setIntegerNumber(result).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publicKNN(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> res = executor.localKnnQuery(request.getPoint(), request.getTable(), request.getIntegerNumber(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder()
                    .setIntegerNumber(res.size()).addAllPoint(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void privacyRangeCount(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            Integer result = executor.localRangeCount(request.getPoint(), request.getTable(), request.getDoubleNumber());

            buffer.set(request.getUuid(), result);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void privacyRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = executor.localRangeQuery(request.getPoint(), request.getTable(), request.getDoubleNumber(), FederateCommon.Point.class);
            List<Pair<Double, Double>> resPairs = new ArrayList<>();
            for (FederateCommon.Point point : res) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
            LogUtils.info(resPairs.toString());
            SiloCache siloCache = new SiloCache(resPairs);
            buffer.set(request.getUuid(), siloCache);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);// 表示查成功了，不返回具体结果
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // @Override
    // public void privacyKNN(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) throws Exception {
    //     super.privacyKNN(request, responseObserver);
    // }

    @Override
    public void publicRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> pointList = executor.localRangeQuery(request.getPoint(), request.getTable(), request.getDoubleNumber(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder().setIntegerNumber(pointList.size()).addAllPoint(pointList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void knnRadiusQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        Double result = 0.0;
        try {
            int k = request.getIntegerNumber();
            result = executor.localKnnRadiusQuery(request.getPoint(), request.getTable(), k);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        //构造返回
        FederateService.SQLReply reply = FederateService.SQLReply.newBuilder().setDoubleNumber(result).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void publicPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.SQLReply> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.SQLReply.Builder replyList = null;
        try {
            List<FederateCommon.Point> pointList = executor.localPolygonRangeQuery(request.getPolygon(), request.getTable(), FederateCommon.Point.class);
            replyList = FederateService.SQLReply.newBuilder().setIntegerNumber(pointList.size()).addAllPoint(pointList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //构造返回
        assert replyList != null;
        responseObserver.onNext(replyList.build());
        responseObserver.onCompleted();
    }

    @Override
    public void privacyPolygonRangeQuery(FederateService.SQLExpression request, StreamObserver<FederateService.Status> responseObserver) {
        System.out.println("收到的信息：" + request.getFunction());
        FederateService.Status status;
        try {
            List<FederateCommon.Point> res = executor.localPolygonRangeQuery(request.getPolygon(), request.getTable(), FederateCommon.Point.class);
            List<Pair<Double, Double>> resPairs = new ArrayList<>();
            for (FederateCommon.Point point : res) {
                resPairs.add(Pair.of(point.getLongitude(), point.getLatitude()));
            }
            SiloCache siloCache = new SiloCache(resPairs);
            buffer.set(request.getUuid(), siloCache);
            status = FederateService.Status.newBuilder().setCode(FederateService.Code.kOk).setMsg("ok").build();
            responseObserver.onNext(status);// 表示查成功了，不返回具体结果
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 重写 resultSet2Object
     * Mysql geometry 字段中 srid 4326 经纬度是反过来的
     *
     * @throws SQLException
     */

    @Override
    public <T> T resultSet2Object(ResultSet resultSet, Class<T> clazz) throws
            SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (resultSet.isBeforeFirst()) {
            // 跳过头指针
            resultSet.next();
        }
        if (clazz == Integer.class || clazz == Double.class || clazz == String.class) {
            return clazz.getConstructor(String.class).newInstance(resultSet.getObject(1).toString());
        } else if (clazz == FederateCommon.Point.class) {
            String content = resultSet.getObject(1).toString();
            List<Float> temp = FederateUtils.parseNumFromString(content, Float.class);
            FederateCommon.Point point = FederateCommon.Point.newBuilder()
                    .setLongitude(temp.get(1)).setLatitude(temp.get(0)).build(); // 翻转顺序
            return (T) point;
        } else if (clazz == HashMap.class) {
            Map<String, Object> mmap = new HashMap<>();
            int count = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= count; i++) {
                mmap.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
            }
            return clazz.getConstructor(Map.class).newInstance(mmap);
        } else {
            return null;
        }
    }


}
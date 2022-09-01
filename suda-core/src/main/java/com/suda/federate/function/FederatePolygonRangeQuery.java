package com.suda.federate.function;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FederatePolygonRangeQuery extends FederateQuery {
    public static List<FederateCommon.Point> publicQuery(FederateService.SQLExpression expression) throws Exception {
        List<FederateService.SQLReply> replyList = fedSpatialPublicQuery(expression);
        List<FederateCommon.Point> pointList = publicUnion(replyList);
        LogUtils.debug("public polygon range query count:" + pointList.size() + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    public static List<FederateCommon.Point> publicQuery(String tableName, List<FederateCommon.Point> pointList) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setPolygon(FederateCommon.Polygon.newBuilder().addAllPoint(pointList).build())
                .setFunction(FederateService.SQLExpression.Function.POLYGON_RANGE_QUERY)
                .setTable(tableName)
                .build();
        return publicQuery(expression);
    }


    public static List<FederateCommon.Point> privacyQuery(FederateService.SQLExpression expression) throws InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    FederateService.Status status = federateDBClient.privacyPolygonRangeQuery(expression);
                    iterator.add(status.getMsg().equals("ok"));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    iterator.finish();
                }
            });
        }

        List<Future<Boolean>> statusList = executorService.invokeAll(tasks);
        checkStatus(statusList, Future.class);

        List<FederateCommon.Point> pointList = privacyUnion(expression);
        LogUtils.debug("secure list: " + FederateUtils.flatPointList(pointList));

        return pointList;
    }

    public static List<FederateCommon.Point> privacyQuery(String tableName, List<FederateCommon.Point> pointList,String uuid) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setPolygon(FederateCommon.Polygon.newBuilder().addAllPoint(pointList).build())
                .setFunction(FederateService.SQLExpression.Function.POLYGON_RANGE_QUERY)
                .setTable(tableName)
                .setUuid(uuid)
                .build();
        return privacyQuery(expression);
    }
}

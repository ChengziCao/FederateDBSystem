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

public class FederateRangeQuery extends FederateQuery {

    /**
     * query: select RangeQuery (P, radius) from table_name
     * @param expression query expression
     * @return List<Point>，points whose distance from P < radius in table_name.
     */
    public static List<FederateCommon.Point> publicQuery(FederateService.SQLExpression expression) throws Exception {
        List<FederateService.SQLReply> replyList = fedSpatialPublicQuery(expression);
        List<FederateCommon.Point> pointList = publicUnion(replyList);
        LogUtils.debug("public range query count: " + pointList.size() + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    /**
     * query: select RangeQuery (P, radius) from table_name
     * @param tableName tableName target table name
     * @param radius range count query radius
     * @param point query location point
     * @return List<Point>，points whose distance from P < radius in table_name.
     */
    public static List<FederateCommon.Point> publicQuery(String tableName, Double radius, FederateCommon.Point point) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setDoubleNumber(radius)
                .setFunction(FederateService.SQLExpression.Function.RANGE_QUERY)
                .setTable(tableName)
                .setPoint(point)
                .build();
        return publicQuery(expression);
    }

    /**
     * query: select RangeQuery (P, radius) from table_name
     * @param expression query expression
     * @return List<Point>，points whose distance from P < radius in table_name.
     */
    public static List<FederateCommon.Point> privacyQuery(FederateService.SQLExpression expression) throws InterruptedException {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    FederateService.Status status = federateDBClient.privacyRangeQuery(expression);
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
        LogUtils.debug("privacy range query count: " + pointList.size() + "\n" + FederateUtils.flatPointList(pointList));

        return pointList;
    }

    /**
     * query: select RangeQuery (P, radius) from table_name
     * @param tableName tableName target table name
     * @param radius range count query radius
     * @param point query location point
     * @param uuid identify this query
     * @return List<Point>，points whose distance from P < radius in table_name.
     */
    public static List<FederateCommon.Point> privacyQuery(String tableName, Double radius, FederateCommon.Point point, String uuid) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setDoubleNumber(radius)
                .setFunction(FederateService.SQLExpression.Function.RANGE_QUERY)
                .setTable(tableName)
                .setPoint(point)
                .setUuid(uuid)
                .build();
        return privacyQuery(expression);
    }
}

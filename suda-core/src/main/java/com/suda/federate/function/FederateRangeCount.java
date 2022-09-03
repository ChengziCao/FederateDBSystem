package com.suda.federate.function;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.StreamingIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FederateRangeCount extends FederateQuery {


    /**
     * query: select RangeCounting (P, radius) from table_name;
     * @param expression query expression (point, radius)
     * @return Integer，The number of points whose distance from P < radius in. table_name.
     */
    public static Integer publicQuery(FederateService.SQLExpression expression) throws Exception {
        List<FederateService.SQLReply> replyList = fedSpatialPublicQuery(expression);
        Integer count = publicSummation(replyList);
        LogUtils.debug("public range count:" + count);
        return count;
    }


    /**
     * query: select RangeCounting (P, radius) from table_name;
     * @param tableName tableName target table name
     * @param radius range count query radius
     * @param point query location point
     * @return Integer，The number of points whose distance from P < radius in. table_name.
     */
    public static Integer publicQuery(String tableName, Double radius, FederateCommon.Point point) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setDoubleNumber(radius)
                .setFunction(FederateService.SQLExpression.Function.RANGE_COUNT)
                .setTable(tableName)
                .setPoint(point)
                .build();
        return publicQuery(expression);
    }

    /**
     * query: select RangeCounting (P, radius) from table_name;
     * @param expression query expression (point, radius)
     * @return Integer，The number of points whose distance from P < radius in. table_name.
     */
    public static Integer privacyQuery(FederateService.SQLExpression expression) throws InterruptedException {

        List<Callable<Boolean>> tasks = new ArrayList<>();
        StreamingIterator<Boolean> iterator = new StreamingIterator<>(federateDBClients.size());

        for (FederateDBClient federateDBClient : federateDBClients) {
            tasks.add(() -> {
                try {
                    FederateService.Status status = federateDBClient.privacyRangeCount(expression);
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

        Integer count = privacySummation(expression);
        LogUtils.debug("secure count: " + count);
        return count;
    }

    /**
     * query: select RangeCounting (P, radius) from table_name;
     * @param tableName tableName target table name
     * @param radius range count query radius
     * @param point query location point
     * @param uuid identify this query
     * @return Integer，The number of points whose distance from P < radius in. table_name.
     */
    public static Integer privacyQuery(String tableName, Double radius, FederateCommon.Point point, String uuid) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setDoubleNumber(radius)
                .setFunction(FederateService.SQLExpression.Function.RANGE_COUNT)
                .setTable(tableName)
                .setUuid(uuid)
                .setPoint(point)
                .build();
        return privacyQuery(expression);
    }
}

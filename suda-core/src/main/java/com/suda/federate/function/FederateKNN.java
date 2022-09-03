package com.suda.federate.function;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.suda.federate.utils.ENUM.str2FUNCTION;

public class FederateKNN extends FederateQuery {

    /**
     * query: select publicKnn (P, K) from table_name;
     * @param expression query expression
     * @return List<Point>, The K nearest neighbors of point P in table_name.
     */
    public static List<FederateCommon.Point> publicQuery(FederateService.SQLExpression expression) throws Exception {

        List<FederateService.SQLReply> replyList = fedSpatialPublicQuery(expression);

        List<FederateCommon.Point> pointList = publicUnion(replyList);

        LogUtils.debug("public knn query count:" + pointList.size() + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    /**
     * query: select public Knn (P, K) from table_name;
     * @param tableName target table name
     * @param k "K" nearest neighbors
     * @param point query location point
     * @return List<Point>, The K nearest neighbors of point P in table_name.
     */
    public static List<FederateCommon.Point> publicQuery(String tableName, Integer k, FederateCommon.Point point) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setIntegerNumber(k)
                .setFunction(FederateService.SQLExpression.Function.KNN)
                .setTable(tableName)
                .setPoint(point)
                .build();

        return publicQuery(expression);
    }

    /**
     * query: select privacy Knn (P, K) from table_name;
     * @param expression query expression
     * @return List<Point>, The K nearest neighbors of point P in table_name.
     */
    public static List<FederateCommon.Point> privacyQuery(FederateService.SQLExpression expression) throws Exception {

        double minRadius = Double.MAX_VALUE;
        for (FederateDBClient client : federateDBClients) {
            Double r = client.knnRadiusQuery(expression);
            minRadius = r < minRadius ? r : minRadius;
        }
        int k = expression.getIntegerNumber();
        double l = 0.0, u = minRadius, e = 1e-3;
        double threshold = minRadius;
        while (u - l >= e) {
            threshold = (l + u) / 2;
            int count = FederateRangeCount.privacyQuery(expression.getTable(), threshold, expression.getPoint(), expression.getUuid());
            if (count > k) {
                u = threshold;
            } else if (count < k) {
                l = threshold;
            } else {
                break;
            }
        }

        List<FederateCommon.Point> pointList =
                FederateRangeQuery.privacyQuery(expression.getTable(), threshold, expression.getPoint(), expression.getUuid());
        return pointList;
    }

    /**
     * query: select privacy Knn (P, K) from table_name;
     * @param tableName target table name
     * @param k "K" nearest neighbors
     * @param point query location point
     * @param uuid identify this query
     * @return List<Point>, The K nearest neighbors of point P in table_name.
     */
    public static List<FederateCommon.Point> privacyQuery(String tableName, Integer k, FederateCommon.Point point, String uuid) throws Exception {
        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
                .setIntegerNumber(k)
                .setFunction(FederateService.SQLExpression.Function.KNN)
                .setTable(tableName)
                .setPoint(point)
                .setUuid(uuid)
                .build();
        return privacyQuery(expression);
    }
}

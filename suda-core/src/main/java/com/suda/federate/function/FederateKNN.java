package com.suda.federate.function;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.FederateUtils;
import com.suda.federate.utils.LogUtils;
import com.suda.federate.utils.SpatialFunctions;

import java.util.*;
import java.util.stream.Collectors;

import static com.suda.federate.utils.ENUM.str2FUNCTION;

public class FederateKNN extends FederateQuery {

    /**
     * query: select publicKnn (P, K) from table_name;
     *
     * @param expression query expression
     * @return List<Point>, The K nearest neighbors of point P in table_name.
     */
    public static List<FederateCommon.Point> publicQuery(FederateService.SQLExpression expression) throws Exception {

        List<FederateService.SQLReply> replyList = fedSpatialPublicQuery(expression);

        List<FederateCommon.Point> pointList = publicUnion(replyList);
        FederateCommon.Point queryPoint = expression.getPoint();
        class PointPair implements Comparable<PointPair> {
            final FederateCommon.Point p1;
            final FederateCommon.Point p2;
            final double distance;

            public PointPair(FederateCommon.Point p1, FederateCommon.Point p2) {
                this.p1 = p1;
                this.p2 = p2;
                distance = SpatialFunctions.dis(p1, p2);
            }

            @Override
            public int compareTo(PointPair o) {
                if (this.distance < o.distance)
                    return -1;
                else if (this.distance > o.distance)
                    return 1;
                else return 0;
            }

            @Override
            public String toString() {
                return "PointPair{" +
                        "p1=" + p1 +
                        ", p2=" + p2 +
                        ", distance=" + distance +
                        '}';
            }
        }
        List<PointPair> pointPairList = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            pointPairList.add(new PointPair(queryPoint, pointList.get(i)));
        }

        Collections.sort(pointPairList);

        pointPairList = pointPairList.subList(0, expression.getIntegerNumber());
        pointList = pointPairList.stream().map(x -> x.p2).collect(Collectors.toList());
        LogUtils.debug("public knn query count:" + pointList.size() + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    /**
     * query: select public Knn (P, K) from table_name;
     *
     * @param tableName target table name
     * @param k         "K" nearest neighbors
     * @param point     query location point
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
     *
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
     *
     * @param tableName target table name
     * @param k         "K" nearest neighbors
     * @param point     query location point
     * @param uuid      identify this query
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

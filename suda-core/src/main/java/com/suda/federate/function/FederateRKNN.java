package com.suda.federate.function;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.rpc.FederateService;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.suda.federate.utils.LogUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;

public class FederateRKNN {
    public static List<FederateCommon.Point> publicQuery(FederateService.SQLExpression expression) throws Exception {
        String tableName = expression.getTable();
        int k = expression.getIntegerNumber();
        FederateCommon.Point queryPoint = expression.getPoint();

        //step1 ------ 联邦KNN查询，注意，查询KNN时k+1 -> setLiteral(expression.getLiteral()+1)

        List<FederateCommon.Point> S = FederateKNN.publicQuery(tableName, k + 1, queryPoint);

        //step2 ------ 就搁着计算
        // 对q和S里每一个点 求垂直平分线
        HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict = computeBisector(queryPoint, S);
        // 计算所有垂直平分线的交点 : HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict = computeIntersections(bisectorDict);
        // 计算各个交点的level值
        HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> intersectionSetDict =
                computeLevel(computeIntersections(bisectorDict), queryPoint, S);
        // 筛选交点中level<k的点
        List<FederateCommon.Point> res = legalIntersections(intersectionSetDict, k);

        // 建立凸包
        List<FederateCommon.Point> CH = convexHull(res);
        LogUtils.debug(CH.toString());
        //step3 ------ 发CH到 silo 完成RkNN
//        expression = expression.toBuilder().clearPolygon().setPolygon(FederateCommon.Polygon.newBuilder().addAllPoint(CH).build()).build();
        return FederatePolygonRangeQuery.publicQuery(tableName, CH);
    }


    public static List<FederateCommon.Point> privacyQuery(FederateService.SQLExpression expression) throws Exception {
        String tableName = expression.getTable();
        int k = expression.getIntegerNumber();
        FederateCommon.Point queryPoint = expression.getPoint();

        //step1 ------ 联邦KNN查询，注意，查询KNN时k+1 -> setLiteral(expression.getLiteral()+1)
        List<FederateCommon.Point> S = FederateKNN.privacyQuery(tableName, k + 1, queryPoint, expression.getUuid());
        for (FederateCommon.Point p : S) {
            System.out.println(p);
        }

        //step2 ------ 就搁着计算
        // 对q和S里每一个点 求垂直平分线
        HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict = computeBisector(queryPoint, S);
        // 计算所有垂直平分线的交点 : HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict = computeIntersections(bisectorDict);
        // 计算各个交点的level值
        HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> intersectionSetDict = computeLevel(computeIntersections(bisectorDict), queryPoint, S);
        // 筛选交点中level<k的点
        List<FederateCommon.Point> res = legalIntersections(intersectionSetDict, k);
        // 建立凸包
        List<FederateCommon.Point> CH = convexHull(res);

        //step3 ------ 发CH到 silo 完成RkNN
        expression = expression.toBuilder().clearPolygon().setPolygon(FederateCommon.Polygon.newBuilder().addAllPoint(CH).build()).build();
        return FederatePolygonRangeQuery.privacyQuery(expression);
    }

    /**
     * @param queryPoint
     * @param S:         参考点集合
     * @return: query point 与 参考点集合S各个点的垂直平分线
     */
    public static HashMap<FederateCommon.Point, Pair<Object, Double>> computeBisector(FederateCommon.Point queryPoint, List<FederateCommon.Point> S) {
        HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict = new HashMap<>();

        //MyPoint q = new MyPoint(queryPoint.x(), queryPoint.y());

        Object k, b;

        for (FederateCommon.Point s : S) {
            if (doubleEqual(queryPoint.getLongitude(), s.getLongitude())) {  //垂直平分线平行于y轴
                k = null;
                b = (queryPoint.getLatitude() + s.getLatitude()) / 2;
            } else if (doubleEqual(queryPoint.getLatitude(), s.getLatitude())) { //垂直平分线平行于x轴
                k = 0;
                b = (queryPoint.getLongitude() + s.getLongitude()) / 2;
            } else {
                Pair<Double, Double> middlePoint = new Pair<>((s.getLatitude() + queryPoint.getLatitude()) / 2, (s.getLongitude() + queryPoint.getLongitude()) / 2);
                k = -1 / ((queryPoint.getLongitude() - s.getLongitude()) / (queryPoint.getLatitude() - s.getLatitude()));
                b = middlePoint.getSecond() - (double) k * middlePoint.getFirst();
            }
            bisectorDict.put(s, new Pair<>(k, (double) b));
        }
        return bisectorDict;
    }

    /**
     * @param bisectorDict: 字典：<s: 垂直平分线>, s in S
     * @return: 字典：<垂直平分线交点, <p1, p2, 0>>, 其中p1和p2分别是垂直平分线l1和l2对应的参考点，这里的0是为了计算每个交点level值初始化
     */
    public static HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> computeIntersections(HashMap<FederateCommon.Point, Pair<Object, Double>> bisectorDict) {
        HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> intersectionSetDict = new HashMap<>();
        for (FederateCommon.Point p1 : bisectorDict.keySet()) {
            for (FederateCommon.Point p2 : bisectorDict.keySet()) {
                if (!equals(p1, p2)) {
                    Pair<Object, Object> intersectionPointTmp = computeIntersection(bisectorDict.get(p1), bisectorDict.get(p2));
                    if (intersectionPointTmp.equals(new Pair<>(null, null))) {
                        continue;
                    } else {
                        FederateCommon.Point intersectionPoint = FederateCommon.Point.newBuilder()
                                .setLatitude((double) intersectionPointTmp.getFirst())
                                .setLongitude((double) intersectionPointTmp.getSecond())
                                .build();
                        intersectionSetDict.put(intersectionPoint, Triple.of(p1, p2, 0));
                    }
                }
            }
        }
        return intersectionSetDict;
    }

    public static HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> computeLevel(
            HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> intersectionSetDict,
            FederateCommon.Point queryPoint,
            List<FederateCommon.Point> S) {
        for (FederateCommon.Point p : intersectionSetDict.keySet()) {   //p是垂直平分线的交点,这条垂直平分线由p1和p2求得
            FederateCommon.Point p1 = intersectionSetDict.get(p).getLeft();
            FederateCommon.Point p2 = intersectionSetDict.get(p).getMiddle();
            int level = intersectionSetDict.get(p).getRight();

            for (FederateCommon.Point s : S) {
                //交点p就是s和q的垂直平分线上的点
                if (!equals(p1, s) && !equals(p2, s)) {
                    if (dis(p, s) < dis(p, queryPoint)) { //点在非q半平面，level值+1
                        intersectionSetDict.put(p, Triple.of(p1, p2, level + 1));
                    }
                }
            }
        }
        return intersectionSetDict;
    }

    /**
     * @param l1: y = k1*x + b1
     * @param l2: y = k2*x + b2
     * @return: l1和l2的交点
     */
    public static Pair<Object, Object> computeIntersection(Pair<Object, Double> l1, Pair<Object, Double> l2) {
        if (l1.getFirst() == null) {
            if (l2.getFirst() == null) {
                return new Pair<>(null, null);
            } else if ((double) l2.getFirst() == 0.0) {
                return new Pair<>(l1.getSecond(), l2.getSecond());
            } else {
                double y = (double) l2.getFirst() * l1.getSecond() + l2.getSecond();
                return new Pair<>(l1.getSecond(), y);
            }
        } else if (l2.getFirst() == null) {
            if (l1.getFirst() == null) {
                return new Pair<>(null, null);
            } else if ((double) l1.getFirst() == 0.0) {
                return new Pair<>(l2.getSecond(), l1.getSecond());
            } else {
                double y = (double) l1.getFirst() * l2.getSecond() + l1.getSecond();
                return new Pair<>(l2.getSecond(), y);
            }
        } else if (l2.getFirst().equals(l1.getFirst())) {
            return new Pair<>(null, null);
        } else {
            double x = (l1.getSecond() - l2.getSecond()) / ((double) l2.getFirst() - (double) l1.getFirst());
            double y = ((double) l2.getFirst() * l1.getSecond() - (double) l1.getFirst() * l2.getSecond()) / ((double) l2.getFirst() - (double) l1.getFirst());
            return new Pair<>(x, y);
        }
    }

    public static List<FederateCommon.Point> legalIntersections(HashMap<FederateCommon.Point, Triple<FederateCommon.Point, FederateCommon.Point, Integer>> intersectionSetDict, int k) {
        List<FederateCommon.Point> res = new ArrayList<>();
        for (FederateCommon.Point p : intersectionSetDict.keySet()) {
            if (intersectionSetDict.get(p).getRight() < k) {
                res.add(p);
            }
        }
        return res;
    }


    public static double dis(FederateCommon.Point p, FederateCommon.Point q) {
        return Math.sqrt(Math.pow(p.getLatitude() - q.getLatitude(), 2) + Math.pow(p.getLongitude() - q.getLongitude(), 2));
    }

    public static boolean equals(FederateCommon.Point p, FederateCommon.Point q) {
        return p.getLatitude() == q.getLatitude() && p.getLongitude() == q.getLongitude();
    }

    public static boolean doubleEqual(double a, double b) {
        return (a - b > -0.000001) && (a - b) < 0.000001;
    }


    public static double calculateBearingToPoint(double currentBearing, int currentX, int currentY,
                                                 int targetX, int targetY) {
        //计算从根部点到目标点的向量的横坐标
        double x = targetX - currentX;
        //同上，计算向量的纵坐标
        double y = targetY - currentY;
        //调用Math类下的atan2方法，计算向量所要偏转的正角度
        double degree = 90 - currentBearing - Math.toDegrees(Math.atan2(y, x));
        //如果角度为负，则转为正角
        if (degree < 0) {
            degree += 360;
        }
        return degree;
    }

    public static List<FederateCommon.Point> convexHull(List<FederateCommon.Point> points) {
        //判断点的总数是否小于3，小于3则不能构成多边形
        if (points.size() < 3) {
            return points;
        }
        //定义新的Set集合，其中不会有重复元素，符合我们的要求
        List<FederateCommon.Point> set = new ArrayList<>();
        FederateCommon.Point xmin = FederateCommon.Point.newBuilder().setLongitude(Double.MAX_VALUE).setLatitude(Double.MAX_VALUE).build();
        //运用for-each遍历的方式，在所有点中寻找最左的点
        for (FederateCommon.Point item : points) {
            if (item.getLongitude() < xmin.getLongitude() || (item.getLongitude() == xmin.getLongitude() && item.getLatitude() < xmin.getLatitude())) {
                xmin = item;
            }
        }
        //设最左的点为初始起点
        FederateCommon.Point nowPoint = xmin, tempPoint = xmin;
        //初始化指向角度为0
        double nowAngle = 0, minAngle = 360, tempAngle;
        double distance;
        double maxdistance = 0;
        //无差别地遍历所有的点
        do {
            set.add(tempPoint);
            //  遍历全部点，寻找下一个在凸包上的点
            for (FederateCommon.Point item : points) {
                //当某一点不在点集之中或者该点为起始点
                if ((!set.contains(item) || item == xmin)) {
                    //调用判断calculateBearingToPoint方法计算所需要偏转的角度
                    tempAngle = calculateBearingToPoint(nowAngle, (int) nowPoint.getLongitude(), (int) nowPoint.getLatitude(), (int) item.getLongitude(), (int) item.getLatitude());
                    //计算目标点与所在点之间的距离
                    distance = (item.getLongitude() - nowPoint.getLongitude()) * (item.getLongitude() - nowPoint.getLongitude()) + (item.getLatitude() - nowPoint.getLatitude()) * (item.getLatitude() - nowPoint.getLatitude());
                    /*如果某一点的偏转角比之前所找到的最小角度还要小
                      则该角度成为了最小偏转角
                      多个点在同一方向上时取距离所在点最远的目标点*/
                    if (tempAngle < minAngle || (doubleEqual(tempAngle, minAngle) && (distance > maxdistance))) {
                        minAngle = tempAngle;
                        tempPoint = item;
                        maxdistance = distance;
                    }
                }
            }
            //遍历完所有点后，初始化判断指标，从刚刚找到的目标点再次出发，重复上述步骤
            nowAngle = minAngle;
            minAngle = 360;
            nowPoint = tempPoint;
        } while (nowPoint != xmin);  // 当下一个点为第一个点时找到了凸包上的全部点，退出循环
        return set;
    }


    public static Rectangle polygon2MinRect(List<FederateCommon.Point> Hull) {
        double minX = Integer.MIN_VALUE, minY = Integer.MIN_VALUE, maxX = Integer.MAX_VALUE, maxY = Integer.MAX_VALUE;
        for (FederateCommon.Point p : Hull) {
            minX = Math.min(minX, p.getLongitude());
            minY = Math.min(minY, p.getLatitude());
            maxX = Math.max(maxX, p.getLongitude());
            maxY = Math.max(maxY, p.getLatitude());
        }
        return rectangle(minX, minY, maxX, maxY);
    }

//    public static List<FederateCommon.Point> publicQuery(String tableName, FederateCommon.Point point, Integer k) throws Exception {
//        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
//                .setIntegerNumber(k)
//                .setFunction(FederateService.SQLExpression.Function.RKNN)
//                .setTable(tableName)
//                .setPoint(point)
//                .build();
//
//        return publicQuery(expression);
//    }
//
//    public static List<FederateCommon.Point> privacyQuery(String tableName, FederateCommon.Point point, Integer k) throws Exception {
//        FederateService.SQLExpression expression = FederateService.SQLExpression.newBuilder()
//                .setIntegerNumber(k)
//                .setFunction(FederateService.SQLExpression.Function.RKNN)
//                .setTable(tableName)
//                .setPoint(point)
//                .build();
//        return privacyQuery(expression);
//    }
}

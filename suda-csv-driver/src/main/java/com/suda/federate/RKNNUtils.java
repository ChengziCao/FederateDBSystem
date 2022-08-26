package com.suda.federate;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.suda.federate.rpc.FederateCommon;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;

import java.util.*;

import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;

public class RKNNUtils {
    public static boolean doubleEqual(double a, double b) {
        return (a - b > -0.000001) && (a - b) < 0.000001;
    }

    public static double dis(Point p, Point q) {
        return Math.sqrt(Math.pow(p.x()-q.x(), 2) + Math.pow(p.y() - q.y(), 2));
    }

    /**
     *
     * @param queryPoint
     * @param S: 参考点集合
     * @return: query point 与 参考点集合S各个点的垂直平分线
     */
    public static HashMap<Point, Pair<Object, Double>> computeBisector(Point queryPoint, Set<Pair<String, Point>> S) {
        HashMap<Point, Pair<Object, Double>> bisectorDict = new HashMap<>();

        //MyPoint q = new MyPoint(queryPoint.x(), queryPoint.y());

        Object k, b;

        for(Pair<String, Point> s : S) {
            if (doubleEqual(queryPoint.y(), s.getSecond().y())) {  //垂直平分线平行于y轴
                k = null;
                b = (queryPoint.x() + s.getSecond().x()) / 2;
            } else if (doubleEqual(queryPoint.x(), s.getSecond().x())) { //垂直平分线平行于x轴
                k = 0;
                b = (queryPoint.y() + s.getSecond().y()) / 2;
            } else {
                Pair<Double, Double> middlePoint = new Pair<>((s.getSecond().x() + queryPoint.x()) /2, (s.getSecond().y() + queryPoint.y()) / 2);
                k = -1 / ((queryPoint.y() - s.getSecond().y()) / (queryPoint.x() - s.getSecond().x()));
                b = middlePoint.getSecond() - (double)k * middlePoint.getFirst();
            }
            bisectorDict.put(s.getSecond(), new Pair<>(k, (double)b));
        }
        return bisectorDict;
    }


//    public HashMap<MyPoint, Pair<Object, Double>> computeBisector(Point queryPoint, Set<MyPoint> S) {
//        HashMap<MyPoint, Pair<Object, Double>> bisectorDict = new HashMap<>();
//
//        MyPoint q = new MyPoint(queryPoint.x(), queryPoint.y());
//
//        Object k, b;
//
//        for(MyPoint s : S) {
//            if (q.getY() == s.getY()) {  //垂直平分线平行于y轴
//                k = null;
//                b = (q.getX() + s.getX()) / 2;
//            } else if (q.getX() == s.getX()) { //垂直平分线平行于x轴
//                k = 0;
//                b = (q.getY() + s.getY()) / 2;
//            } else {
//                Pair<Double, Double> middlePoint = new Pair<>((s.getX() + q.getX()) /2, (s.getY() + q.getY()) / 2);
//                k = -1 / ((q.getY() - s.getY()) / (q.getX() - s.getX()));
//                b = middlePoint.getSecond() - (double)k * middlePoint.getFirst();
//            }
//            bisectorDict.put(s, new Pair<>(k, (double)b));
//        }
//        return bisectorDict;
//    }

    /**
     *
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

    /**
     *
     * @param bisectorDict: 字典：<s: 垂直平分线>, s in S
     * @return: 字典：<垂直平分线交点, <p1, p2, 0>>, 其中p1和p2分别是垂直平分线l1和l2对应的参考点，这里的0是为了计算每个交点level值初始化
     */
    public static HashMap<Point, Triple<Point, Point, Integer>> computeIntersections(HashMap<Point, Pair<Object, Double>> bisectorDict) {
        HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict = new HashMap<>();
        for (Point p1 : bisectorDict.keySet()) {
            for (Point p2 : bisectorDict.keySet()) {
                if (p1.equals(p2)) {
                    continue;
                } else {
                    Pair<Object, Object> intersectionPointTmp = computeIntersection(bisectorDict.get(p1), bisectorDict.get(p2));
                    if (intersectionPointTmp.equals(new Pair<>(null, null))) {
                        continue;
                    } else {
                        Point intersectionPoint = point((double)intersectionPointTmp.getFirst(), (double)intersectionPointTmp.getSecond());
                        intersectionSetDict.put(intersectionPoint, Triple.of(p1, p2,0));
                    }
                }
            }
        }
        return intersectionSetDict;
    }

//    public HashMap<MyPoint, Triple<MyPoint, MyPoint, Integer>> computeIntersections(HashMap<MyPoint, Pair<Object, Double>> bisectorDict) {
//        HashMap<MyPoint, Triple<MyPoint, MyPoint, Integer>> intersectionSetDict = new HashMap<>();
//        for (MyPoint p1 : bisectorDict.keySet()) {
//            for (MyPoint p2 : bisectorDict.keySet()) {
//                if (p1.equals(p2)) {
//                    continue;
//                } else {
//                    Pair<Object, Object> intersectionPointTmp = computeIntersection(bisectorDict.get(p1), bisectorDict.get(p2));
//                    if (intersectionPointTmp.equals(new Pair<>(null, null))) {
//                        continue;
//                    } else {
//                        MyPoint intersectionPoint = new MyPoint((double)intersectionPointTmp.getFirst(), (double)intersectionPointTmp.getSecond());
//                        intersectionSetDict.put(intersectionPoint, Triple.of(p1, p2,0));
//                    }
//                }
//            }
//        }
//        return intersectionSetDict;
//    }

//    public double dis(MyPoint p, MyPoint q) {
//        return Math.sqrt(Math.pow(p.getX()-q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
//    }

    public static HashMap<Point, Triple<Point, Point, Integer>> computeLevel(HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict, Point queryPoint,
                                                                             Set<Pair<String, Point>> S) {
        for (Point p : intersectionSetDict.keySet()) {   //p是垂直平分线的交点,这条垂直平分线由p1和p2求得
            Point p1 = intersectionSetDict.get(p).getLeft();
            Point p2 = intersectionSetDict.get(p).getMiddle();
            int level = intersectionSetDict.get(p).getRight();

            for (Pair<String, Point> s : S) {
                if (p1.equals(s.getSecond()) || p2.equals(s.getSecond())) {   //交点p就是s和q的垂直平分线上的点
                    continue;
                } else {
                    if (dis(p, s.getSecond()) < dis(p, queryPoint)) { //点在非q半平面，level值+1
                        intersectionSetDict.put(p, Triple.of(p1, p2, level + 1));
                    }
                }
            }
        }
        return intersectionSetDict;
    }

//    public HashMap<MyPoint, Triple<MyPoint, MyPoint, Integer>> computeLevel(HashMap<MyPoint, Triple<MyPoint, MyPoint, Integer>> intersectionSetDict, Point queryPoint, Set<MyPoint> S) {
//        for (MyPoint p : intersectionSetDict.keySet()) {   //p是垂直平分线的交点,这条垂直平分线由p1和p2求得
//            MyPoint p1 = intersectionSetDict.get(p).getLeft();
//            MyPoint p2 = intersectionSetDict.get(p).getMiddle();
//            int level = intersectionSetDict.get(p).getRight();
//
//            for (MyPoint s : S) {
//                if (p1.equals(s) || p2.equals(s)) {   //交点p就是s和q的垂直平分线上的点
//                    continue;
//                } else {
//                    if (dis(p, s) < dis(p, new MyPoint(queryPoint.x(), queryPoint.y()))) { //点在非q半平面，level值+1
//                        intersectionSetDict.put(p, Triple.of(p1, p2, level + 1));
//                    }
//                }
//            }
//        }
//        return intersectionSetDict;
//    }

    public static List<Point> legalIntersections(HashMap<Point, Triple<Point, Point, Integer>> intersectionSetDict, int k) {
        List<Point> res = new ArrayList<>();
        for (Point p : intersectionSetDict.keySet()) {
            if (intersectionSetDict.get(p).getRight() < k) {
                res.add(p);
            }
        }
        return res;
    }

//    public List<MyPoint> legalIntersections(HashMap<MyPoint, Triple<MyPoint, MyPoint, Integer>> intersectionSetDict, int k) {
//        List<MyPoint> res = new ArrayList<>();
//        for (MyPoint p : intersectionSetDict.keySet()) {
//            if (intersectionSetDict.get(p).getRight() < k) {
//                res.add(p);
//            }
//        }
//        return res;
//    }



    /**
     *
     * @param point: <经度，纬度>
     * @param ps
     * @return
     */
    public static boolean isPointInPoly (Point point, List<Point> ps) {
        double ALon = point.x() , ALat = point.y();
        int iSum, iCount, iIndex;
        double dLon1 = 0, dLon2 = 0, dLat1 = 0, dLat2 = 0, dLon;
        if (ps.size() < 3) {
            return false;
        }
        iSum = 0;
        iCount = ps.size();
        for (iIndex = 0; iIndex<iCount;iIndex++) {
            if (iIndex == iCount - 1) {
                dLon1 = ps.get(iIndex).x();
                dLat1 = ps.get(iIndex).y();
                dLon2 = ps.get(0).x();
                dLat2 = ps.get(0).y();
            } else {
                dLon1 = ps.get(iIndex).x();
                dLat1 = ps.get(iIndex).y();
                dLon2 = ps.get(iIndex + 1).x();
                dLat2 = ps.get(iIndex + 1).y();
            }
            // 以下语句判断A点是否在边的两端点的水平平行线之间，在则可能有交点，开始判断交点是否在左射线上
            if (((ALat >= dLat1) && (ALat < dLat2)) || ((ALat >= dLat2) && (ALat < dLat1))) {
                if (Math.abs(dLat1 - dLat2) > 0) {
                    //得到 A点向左射线与边的交点的x坐标：
                    dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - ALat) ) / (dLat1 - dLat2);
                    // 如果交点在A点左侧（说明是做射线与 边的交点），则射线与边的全部交点数加一：
                    if (dLon < ALon) {
                        iSum++;
                    }
                }
            }
        }
        return (iSum % 2) != 0;
    }
    public static List<Point> search(RTree<String, Point> tree, Rectangle rect) {
        Set<Pair<String, Point>> result = new HashSet<>();
        Iterable<Entry<String, Point>> searchRes = tree.search(rect).toBlocking().toIterable();
        return new ArrayList<Point>(){{
            for(Entry<String, Point> entry : searchRes) {
                add(entry.geometry());
            }
        }};
    }

    public static List<Point> pointInRegion(List<Point> pointSet, List<Point> region) {
        List<Point> res = new ArrayList<>();
        for (Point p : pointSet) {
            if (isPointInPoly(p, region)) {
                res.add(p);
            }
        }
        return res;
    }
    public static FederateCommon.Point david2FPoint(Point point){
        return FederateCommon.Point.newBuilder().setLatitude(point.x()).setLongitude(point.y()).build();
    }
    public static Point fPoint2David(FederateCommon.Point point){
        return Geometries.point(point.getLatitude(),point.getLongitude());
    }
    public static List<Point> fPoint2DavidList(List<FederateCommon.Point> pointList){
        return new ArrayList<Point>(){{
            for (FederateCommon.Point point : pointList) {
                add(fPoint2David(point));
            }
        }};
    }
    public static List<FederateCommon.Point> david2FPointList(List<Point> pointList){
        return new ArrayList<FederateCommon.Point>(){{
            for (Point point : pointList) {
                add(david2FPoint(point));
            }
        }};
    }

    public static boolean isPointLegal(Point p, Point queryPoint, RTree<String,Point> tree, int K) {
//        double dis = p.distance(queryPoint);
        Integer count=tree.search(p, p.distance(queryPoint)).count().toBlocking().single();
        return  count < K;
    }

    public static List<Point> legalPoint(List<Point> candidatePoint, Point queryPoint, RTree<String,Point> tree, int K) {
        List<Point> legalPointSet = new ArrayList<>();
        for (Point p : candidatePoint) {
            if (isPointLegal(p,queryPoint, tree, K)) {
                legalPointSet.add(p);
            }
        }
        return legalPointSet;
    }
    public static Rectangle polygon2MinRect(List<Point> Hull) {
        double minX = Integer.MIN_VALUE, minY = Integer.MIN_VALUE, maxX = Integer.MAX_VALUE, maxY = Integer.MAX_VALUE;
        for (Point p : Hull) {
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        return rectangle(minX, minY, maxX, maxY);
    }



//    public static boolean isPointInPoly (MyPoint point, MyPoint[] ps) {
//        double ALon = point.getX() , ALat = point.getY();
//        int iSum, iCount, iIndex;
//        double dLon1 = 0, dLon2 = 0, dLat1 = 0, dLat2 = 0, dLon;
//        if (ps.length < 3) {
//            return false;
//        }
//        iSum = 0;
//        iCount = ps.length;
//        for (iIndex = 0; iIndex<iCount;iIndex++) {
//            if (iIndex == iCount - 1) {
//                dLon1 = ps[iIndex].getX();
//                dLat1 = ps[iIndex].getY();
//                dLon2 = ps[0].getX();
//                dLat2 = ps[0].getY();
//            } else {
//                dLon1 = ps[iIndex].getX();
//                dLat1 = ps[iIndex].getY();
//                dLon2 = ps[iIndex + 1].getX();
//                dLat2 = ps[iIndex + 1].getY();
//            }
//            // 以下语句判断A点是否在边的两端点的水平平行线之间，在则可能有交点，开始判断交点是否在左射线上
//            if (((ALat >= dLat1) && (ALat < dLat2)) || ((ALat >= dLat2) && (ALat < dLat1))) {
//                if (Math.abs(dLat1 - dLat2) > 0) {
//                    //得到 A点向左射线与边的交点的x坐标：
//                    dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - ALat) ) / (dLat1 - dLat2);
//                    // 如果交点在A点左侧（说明是做射线与 边的交点），则射线与边的全部交点数加一：
//                    if (dLon < ALon) {
//                        iSum++;
//                    }
//                }
//            }
//        }
//        return (iSum % 2) != 0;
//    }
}

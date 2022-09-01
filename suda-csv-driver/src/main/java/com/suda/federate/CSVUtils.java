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

public class CSVUtils {

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
}

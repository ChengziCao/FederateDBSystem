package com.suda.federate.utils;

import com.suda.federate.rpc.FederateCommon;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpatialFunctions {
    private SpatialFunctions() {
    }


    public static double dis(FederateCommon.Point p, FederateCommon.Point q) {
        double longitude1 = p.getLongitude(), latitude1 = p.getLatitude(), longitude2 = q.getLongitude(), latitude2 = q.getLatitude();
        double Lat1 = rad(latitude1); // 纬度
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;// 两点纬度之差
        double b = rad(longitude1) - rad(longitude2); // 经度之差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));// 计算两点距离的公式
        s = s * 6378137.0;// 弧长乘地球半径（半径为米）
        s = Math.round(s * 10000d) / 10000d;// 精确距离的数值
        return s;
    }

    public static double rad(double d) {
        return d * Math.PI / 180.00; // 角度转换成弧度
    }

    /**
     * 31.253359 121.45611 --> Point
     *
     * @param value
     * @return
     */
    public static FederateCommon.Point PointFromText(String value) {
        String[] strArray = value.trim().split(" ");
        return FederateCommon.Point.newBuilder().setLongitude(Double.parseDouble(strArray[0])).setLatitude(Double.parseDouble(strArray[1])).build();
    }

    /**
     * 31 121, 31 122, 31.5 122, 31 121 --> Polygon
     *
     * @return
     */
    public static FederateCommon.Polygon PolygonFromText(String value) {

        List<FederateCommon.Point> points = Arrays.stream(value.split(","))
                .map(SpatialFunctions::PointFromText)
                .collect(Collectors.toList());
        return FederateCommon.Polygon.newBuilder().addAllPoint(points).build();
        // return FederateCommon.Point.newBuilder().setLongitude(Double.parseDouble(strArray[0])).setLatitude(Double.parseDouble(strArray[1])).build();
    }

}

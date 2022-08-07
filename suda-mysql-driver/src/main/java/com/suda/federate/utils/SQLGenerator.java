package com.suda.federate.utils;

import com.suda.federate.rpc.FederateCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLGenerator {
    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param point
     * @param radius
     * @return
     */
    public static String generateRangeQuerySQL(FederateCommon.Point point, Double radius) {
        String template = "select ST_AsText(location) from osm_sh where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) <= %f limit 10000";
        return String.format(template, point.getLatitude(), point.getLongitude(), radius);
    }

    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param polygon
     * @return
     */
    public static String generatePolygonRangeQuerySQL(FederateCommon.Polygon polygon) {//TODO debug
        String template;
        String polygonString = polygon.getPointList().stream().map(x -> x.getLatitude() + " " + x.getLongitude()).collect(Collectors.joining(","));
        template = "select ST_AsText(location) from osm_sh where ST_Contains(ST_GeomFromText('POLYGON((%s))',st_srid(location)),location) limit 10000";
        return String.format(template, polygonString);
    }


    /**
     * select RangeCounting (point, radius) from table;
     * 维度y  Latitude
     * 经度x Longitude
     *
     * @param point
     * @param radius
     * @return
     */
    public static String generateRangeCountingSQL(FederateCommon.Point point, String tableName, Double radius) {
        String template = "select count(1) from %s where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) <= %f";
        // MySQL point类型 先维度后经度
        return String.format(template, tableName, point.getLatitude(), point.getLongitude(), radius);
    }

    public static String generateKnnRadiusQuerySQL(FederateCommon.Point point, String tableName, Integer k) {
        String template = "select ST_Distance(ST_GeomFromText('POINT(%f %f)',4326), location) as dis from %s order by dis limit 1 offset %d;";
        return String.format(template, point.getLatitude(), point.getLongitude(), tableName, k - 1);

    }
}

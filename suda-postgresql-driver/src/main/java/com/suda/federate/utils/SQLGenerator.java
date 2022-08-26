package com.suda.federate.utils;

import com.suda.federate.rpc.FederateCommon;

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
    public static String generateRangeQuerySQL(FederateCommon.Point point,String tableName, Double radius) {
        //WARNING must set <= instead of <
        String template = "select st_astext(location) from %s where st_distance( st_geographyfromtext('POINT(%f %f)'), location ) <= %f limit 10000;";
        return String.format(template, tableName,point.getLongitude(), point.getLatitude(), radius);
    }

    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param polygon
     * @return
     */
    public static String generatePolygonRangeQuerySQL(FederateCommon.Polygon polygon,String tableName) {//TODO debug
        String template;
        String polygonString = polygon.getPointList().stream().map(x -> x.getLongitude() + " " + x.getLatitude()).collect(Collectors.joining(","));
        template = "select st_astext(location) from %s where ST_Contains(GeomFromText('POLYGON((%s))',st_srid(location)),location) limit 10000;";
        return String.format(template,tableName, polygonString);
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
        String template = "select count(1) from %s where st_distance( st_geographyfromtext('POINT(%f %f)'), location) <= %f;";
        return String.format(template, tableName, point.getLongitude(), point.getLatitude(), radius);
    }

    public static String generateKnnRadiusQuerySQL(FederateCommon.Point point, String tableName, Integer k) {
        String template = "select ST_Distance(ST_GeographyFromText('POINT(%f %f)'), location) as dis from %s order by dis limit 1 offset %d;";
        return String.format(template, point.getLongitude(), point.getLatitude(), tableName, k - 1);
    }
}

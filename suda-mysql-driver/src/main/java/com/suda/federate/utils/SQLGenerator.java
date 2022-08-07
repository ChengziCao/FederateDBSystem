package com.suda.federate.utils;

import com.suda.federate.rpc.FederateCommon;

import java.util.ArrayList;
import java.util.List;

public class SQLGenerator {
    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeQuerySQL(FederateCommon.Point point, Double radius, ENUM.DATABASE dbType) {
        String template;
        if (ENUM.DATABASE.POSTGRESQL == dbType) {
            template = "select st_astext(location) from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location ) <= %f limit 10000;";
            return String.format(template,  point.getLongitude(), point.getLatitude(), radius);
        } else if (ENUM.DATABASE.MYSQL == dbType) {
            template = "select ST_AsText(location) from osm_sh where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) <= %f limit 10000";
            return String.format(template, point.getLatitude(), point.getLongitude(), radius);
        } else {
            template = "";
            return null;
        }
    }
    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param polygon
     * @param dbType
     * @return
     */
    public static String generatePolygonRangeQuerySQL(List<String> polygon, ENUM.DATABASE dbType) {//TODO debug
        String template;


        if (ENUM.DATABASE.POSTGRESQL == dbType) {
            String polygonString = String.join(",",polygon);
            template = "select st_astext(location) from osm_sh where ST_Contains(GeomFromText('POLYGON((%s))',st_srid(location)),location) limit 10000;";
            return String.format(template,  polygonString);
        } else if (ENUM.DATABASE.MYSQL == dbType) {
            List<String> tmp= new ArrayList<>();
            for(String s:polygon){
                String ss[]=s.split(" ");
                tmp.add(String.format("%s %s",ss[1],ss[0]));//翻转
            }
            String polygonString =String.join(",",tmp);
            template = "select ST_AsText(location) from osm_sh where ST_Contains(ST_GeomFromText('POLYGON((%s))',st_srid(location)),location) limit 10000";
            return String.format(template, polygonString);
        } else {
            template = "";
            return null;
        }
    }


    /**
     * select RangeCounting (point, radius) from table;
     *维度y  Latitude
     * 经度x Longitude
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeCountingSQL(FederateCommon.Point point,String tableName,Double radius, ENUM.DATABASE dbType) {
        String template;
        if (dbType == ENUM.DATABASE.POSTGRESQL) {
            template = "select count(1) from %s where st_distance( st_geographyfromtext('POINT(%f %f)'), location) <= %f;";
            return String.format(template,tableName ,point.getLongitude(), point.getLatitude(), radius);
        } else if (ENUM.DATABASE.MYSQL == dbType) {
            template = "select count(1) from %s where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) <= %f";
            // MySQL point类型 先维度后经度
            return String.format(template,tableName, point.getLatitude(), point.getLongitude(), radius);
        } else {
            template = "";
            return template;
        }
    }

    public static String  generateKnnRadiusQuerySQL(FederateCommon.Point point,String tableName, Integer k, ENUM.DATABASE dbType) {
        String template;
        if (dbType == ENUM.DATABASE.POSTGRESQL) {
            template = "select ST_Distance(ST_GeographyFromText('POINT(%f %f)'), location) as dis from %s order by dis limit 1 offset %d;";
            return String.format(template,point.getLongitude(), point.getLatitude(),tableName, k - 1);
        } else if (dbType == ENUM.DATABASE.MYSQL) {
            template = "select ST_Distance(ST_GeomFromText('POINT(%f %f)',4326), location) as dis from %s order by dis limit 1 offset %d;";
            return String.format(template,point.getLatitude(), point.getLongitude(),tableName, k - 1);
        } else {
            template = "";
            return template;
        }

    }
}

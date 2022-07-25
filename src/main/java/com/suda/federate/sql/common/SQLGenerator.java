package com.suda.federate.sql.common;

import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM;

public class SQLGenerator {
    /**
     * queryL select RangeQuery (point, radius) from table;
     *
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeQuerySQL(FD_Point point, Double radius, ENUM.DATABASE dbType) {
        String template;
        if (ENUM.DATABASE.POSTGRESQL == dbType) {
            template = "select st_astext(location) from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location ) < %f order by st_distance( st_geographyfromtext('POINT(%f %f)'), location) limit 10000;";
            return String.format(template, point.value.x, point.value.y, radius, point.value.x, point.value.y);
        } else if (ENUM.DATABASE.MYSQL == dbType) {
            template = "select ST_AsText(location) from osm_sh where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) < %f limit 10000";
            return String.format(template, point.value.y, point.value.x, radius);
        } else {
            template = "";
            return null;
        }
    }

    /**
     * select RangeCounting (point, radius) from table;
     *
     * @param point
     * @param radius
     * @param dbType
     * @return
     */
    public static String generateRangeCountingSQL(FD_Point point, Double radius, ENUM.DATABASE dbType) {
        String template;
        if (dbType == ENUM.DATABASE.POSTGRESQL) {
            template = "select count(1) from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location) < %f;";
            return String.format(template, point.value.x, point.value.y, radius);
        } else if (ENUM.DATABASE.MYSQL == dbType) {
            template = "select count(1) from osm_sh where ST_Distance(ST_GeomFromText('POINT(%f %f)',4326),location) < %f";
            // MySQL point类型 先维度后经度
            return String.format(template, point.value.y, point.value.x, radius);
        } else {
            template = "";
            return template;
        }
    }

    public static String generateKnnRadiusQuerySQL(FD_Point point, Integer k, ENUM.DATABASE dbType) {
        String template;
        if (dbType == ENUM.DATABASE.POSTGRESQL) {
            template = "select ST_Distance(ST_GeographyFromText('POINT(%f %f)'), location) as dis from osm_sh order by dis limit 1 offset %d;";
            return String.format(template, point.value.x, point.value.y, k - 1);
        } else if (dbType == ENUM.DATABASE.MYSQL) {
            template = "select ST_Distance(ST_GeomFromText('POINT(%f %f)',4326), location) as dis from osm_sh order by dis limit 1 offset %d;";
            return String.format(template, point.value.y, point.value.x, k - 1);
        } else {
            template = "";
            return template;
        }

    }
}

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
            template = "select st_astext(location) as loc from osm_sh where st_distance( st_geographyfromtext('POINT(%f %f)'), location ) < %f order by st_distance( st_geographyfromtext('POINT(%f %f)'), location) limit 10000;";
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, radius, point.value.x, point.value.y);
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
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, radius);
    }

    public static String generateKnnRadiusQuerySQL(FD_Point point, Integer k, ENUM.DATABASE dbType) {
        String template;
        if (dbType == ENUM.DATABASE.POSTGRESQL) {
            template = "select ST_Distance(ST_GeographyFromText('POINT(%f %f)'), ST_GeographyFromText(ST_AsText(location))) as dis from osm_sh order by dis limit 1 offset %d;";
        } else {
            template = "";
        }
        return String.format(template, point.value.x, point.value.y, k - 1);
    }
}

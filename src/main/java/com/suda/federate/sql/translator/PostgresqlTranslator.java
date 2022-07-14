package com.suda.federate.sql.translator;

import com.suda.federate.sql.type.FD_Variable;

import java.util.List;

public class PostgresqlTranslator implements SQLTranslator {

    // original sql: select F.id, DF_distance($P, F.location) as dis from nyc_homicides_copy where DF_distance($P, F.location) < 10 order by dis;
    // $P=(583571 4506714)
    // target sql: select id, ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) as dis from nyc_homicides_copy where ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) < 1000 order by dis;;

    @Override
    public String translate(String originalSql, List<FD_Variable> variables) {
        return "select id, ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) as dis from nyc_homicides_copy where ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) < 1000 order by dis";
    }


}

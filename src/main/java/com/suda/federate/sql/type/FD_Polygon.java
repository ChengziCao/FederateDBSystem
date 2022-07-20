package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

import java.util.List;

public class FD_Polygon extends FD_Variable<List<FD_Point>> {


    public FD_Polygon(String name, List<FD_Point> value) {
        super(ENUM.DATA_TYPE.POLYGON, name, value);
    }

    @Override
    public String translate2PostgresqlFormat() {

        // st_geomfromtext('POLYGON((121.43 31.20, 121.46 31.20, 121.46 31.23, 121.43 31.20))', st_srid(location)
        return "ST_GeomFromText('POLYGON" + "((" + value.stream()
                .map(x -> x.value.x + " " + x.value.y)
                .reduce((x, y) -> x + ", " + y).orElse("") + "))',st_srid(location))";
    }
}

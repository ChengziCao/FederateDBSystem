package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

import java.util.List;

public class FD_LineString extends FD_Variable<List<FD_Point>> {

    public FD_LineString(String name, List<FD_Point> value) {
        super(ENUM.DATA_TYPE.LINESTRING, name, value);
    }

    @Override
    public String translate2PostgresqlFormat() {
        return "ST_GeomFromText('LINESTRING" + "(" + value.stream()
                .map(x -> x.value.x + " " + x.value.y)
                .reduce((x, y) -> x + ", " + y).orElse("") + ")',st_srid(location))";
    }
}

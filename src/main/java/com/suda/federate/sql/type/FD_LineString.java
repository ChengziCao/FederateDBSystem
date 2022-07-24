package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

import java.util.List;

public class FD_LineString extends FD_Variable<List<FD_Point>> {

    public FD_LineString(String name, List<FD_Point> value) {
        super(ENUM.DATA_TYPE.LINESTRING, name, value);
    }


}

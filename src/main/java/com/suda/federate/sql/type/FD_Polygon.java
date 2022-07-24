package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

import java.util.List;

public class FD_Polygon extends FD_Variable<List<FD_Point>> {


    public FD_Polygon(String name, List<FD_Point> value) {
        super(ENUM.DATA_TYPE.POLYGON, name, value);
    }


}

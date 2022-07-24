package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

public class FD_Double extends FD_Variable<Double> {


    public FD_Double(String name, Double value) {
        super(ENUM.DATA_TYPE.DOUBLE, name, value);
    }


}

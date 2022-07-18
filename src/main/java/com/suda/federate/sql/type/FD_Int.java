package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

public class FD_Int extends FD_Variable<Integer> {

    public FD_Int(String name, Integer value) {
        super(ENUM.DATA_TYPE.INT, name, value);
    }

    @Override
    public String translate2PostgresqlFormat() {
        return value.toString();
    }
}

package com.suda.federate.sql.function;

public class FD_Distance extends FD_Function {

    public FD_Distance() {
        super("FD_Distance");
    }

    // from: DF_distance(x,x)
    // to: ST_distance(x,x)
    @Override
    public String translate2PostgresqlFormat() {
        return "ST_distance";
    }
}

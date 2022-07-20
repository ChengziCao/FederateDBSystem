package com.suda.federate.sql.function;

public class FD_Contains extends FD_Function {

    public FD_Contains() {
        super("FD_Contains");
    }

    @Override
    public String translate2PostgresqlFormat() {
        return "st_contains";
    }
}

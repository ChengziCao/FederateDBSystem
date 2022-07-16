package com.suda.federate.sql.type;

import com.suda.federate.utils.ENUM;

public abstract class FD_Variable<valueType> {
    // public int id;
    public final ENUM.FD_DATA_TYPE dataType;
    public String name;
    public valueType value;

    public FD_Variable(ENUM.FD_DATA_TYPE dataType, String name, valueType value) {
        this.dataType = dataType;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "FD_Variable{" +
                "dataType=" + dataType +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}

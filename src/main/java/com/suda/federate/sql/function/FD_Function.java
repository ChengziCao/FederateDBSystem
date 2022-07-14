package com.suda.federate.sql.function;

import com.alibaba.fastjson.JSONArray;
import com.suda.federate.utils.ENUM;

import java.util.List;

public interface FD_Function {
    String functionName = null;


    String translation(JSONArray functionParams, ENUM.FD_DATABASE database);
}

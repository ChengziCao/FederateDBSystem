package com.suda.federate.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suda.federate.driver.FederateDBDriver;
import com.suda.federate.driver.FederateDBFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguratorUtils {



    public static List<String> parseQueryJson(String queryPath, String type) throws IOException {
        List<String> sqlList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(queryPath)));
        // 可能有多个配置，写成 json array 格式
        JSONArray jsonArray = JSON.parseArray(jsonString);

        for (Object x : jsonArray) {
            JSONObject json = (JSONObject) (x);
            // TODO sql translate
            sqlList.add((String) json.get("query"));
        }
        return sqlList;
    }
}

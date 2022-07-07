package com.suda.federate.config;

import com.alibaba.fastjson.JSONObject;

public class DriverConfig {
    public String name;
    public String url;
    public String driver;
    public String user;
    public String password;
    public String type;


    public static DriverConfig json2DriverConfig(JSONObject json) {
        DriverConfig config = new DriverConfig();
        try {
            Class.forName(json.getString("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        config.name = json.getString("name");
        config.driver = json.getString("driver");
        config.user = json.getString("user");
        config.password = json.getString("password");
        config.url = json.getString("url");
        config.type = json.getString("type");
        return config;
    }
}

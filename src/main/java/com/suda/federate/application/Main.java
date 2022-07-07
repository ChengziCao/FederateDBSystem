package com.suda.federate.application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suda.federate.config.DriverConfig;
import com.suda.federate.driver.FederateDBDriver;
import com.suda.federate.driver.PostgresqlDriver;
import com.suda.federate.utils.FederateUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            FederateDBDriver postgresqlDriver = new PostgresqlDriver();
            // TODO 读取配置文件
            String jsonString = new String(Files.readAllBytes(Paths.get(FederateUtils.resourcePath("dataSource.json"))));
            JSONArray jsonArray = JSON.parseArray(jsonString);

            List<Connection> connectionList = new ArrayList<>();
            for (Object x : jsonArray) {
                connectionList.add(postgresqlDriver.getConnection(DriverConfig.json2DriverConfig((JSONObject) (x))));
            }

            // TODO 接收用户从 CLI 输入的 SQL 查询

            // https://www.cnblogs.com/iken/articles/4461146.html
            String rawSql = "SELECT count(*) FROM nyc_homicides WHERE ST_DWithin( geom, ST_GeomFromText('POINT(583571 4506714)',26918), 1000);";
            // TODO SQL 优化、分解

            // TODO SQL 执行
            String optimizedSql = rawSql;

            List<ResultSet> resultSets = new ArrayList<>();
            for (Connection conn : connectionList) {
                resultSets.add(postgresqlDriver.executeSql(conn, optimizedSql));
            }

            // TODO 结果聚合
            for (ResultSet rs : resultSets) {
                FederateUtils.printResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

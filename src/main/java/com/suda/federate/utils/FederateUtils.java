package com.suda.federate.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suda.federate.driver.FederateDBDriver;
import com.suda.federate.driver.FederateDBFactory;
import com.suda.federate.sql.type.FD_Double;
import com.suda.federate.sql.type.FD_Int;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.sql.type.FD_Variable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

public class FederateUtils {

    public static Map<String, List<Connection>> parseConfigJson(String configPath) throws IOException, SQLException, ClassNotFoundException {
        Map<String, List<Connection>> connectionMap = new HashMap<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(configPath)));
        // 可能有多个数据源，写成 json array 格式
        JSONArray jsonArray = JSON.parseArray(jsonString);

        for (Object x : jsonArray) {
            JSONObject json = (JSONObject) (x);
            String dbName = (String) json.get("type");
            // 动态加载 driver，不加载 jar 包无法正常运行
            Class.forName(json.getString("driver"));
            // 获取 driver 实例
            FederateDBDriver driver = FederateDBFactory.getDriverInstance(dbName);
            if (connectionMap.containsKey(dbName)) {
                // 获取连接
                connectionMap.get(dbName).add(driver.getConnection(json));
            } else {
                List<Connection> temp = new ArrayList<>();
                temp.add(driver.getConnection(json));
                connectionMap.put(dbName, temp);
            }
        }
        return connectionMap;
    }


    /**
     * parse query.json, return original sql and params list
     *
     * @param queryPath the path of query.json.
     * @return String  sql list，List<FD_Variable> params list of every sql
     * @throws IOException
     */
    public static Object[] parseQueryJson(String queryPath) throws IOException {
        String originalSql;
        List<FD_Variable> variableList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(queryPath)));
        JSONObject queryJson = JSON.parseObject(jsonString);
        // 保存 query
        originalSql = (String) queryJson.get("query");
        // 保存 variables
        JSONArray variables = queryJson.getJSONArray("variables");
        for (Object varObj : variables) {
            JSONObject var = (JSONObject) varObj;
            String varType = var.getString("type");
            if (ENUM.equals(varType, ENUM.FD_DATA_TYPE.POINT)) {
                String[] strArray = var.getString("value").split(" |,");
                variableList.add(new FD_Point(var.getString("name"), Float.parseFloat(strArray[0]), Float.parseFloat(strArray[1])));
            } else if (ENUM.equals(varType, ENUM.FD_DATA_TYPE.INT)) {
                variableList.add(new FD_Int(var.getString("name"), var.getIntValue("value")));
            }
        }

        return new Object[]{originalSql, variableList};
    }

    public static Map<String, Object> printResultSet(ResultSet rs) throws SQLException {
        int count = rs.getMetaData().getColumnCount();
        Map<String, Object> hm = new HashMap<>();
        while (rs.next()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= count; i++) {
                String label = rs.getMetaData().getColumnLabel(i);
                Object val = rs.getObject(i);
                String value = "null";
                if (val != null) value = val.toString();
                sb.append(label).append(":").append(value);
                hm.put(label, value);
                if (i != count) sb.append(" , ");
            }
            System.out.println(sb);
        }
        return hm;
    }


    /**
     * debug mode，get the resources path.
     */
    public static String getResourcePath(String fileName) throws UnsupportedEncodingException {
        String path = Objects.requireNonNull(FederateUtils.class.getResource("/" + fileName)).getPath();
        // 中文路径
        String decodePath = URLDecoder.decode(path, "UTF-8");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // windows 需要移除 路径中第一个 /
            return decodePath.substring(1);
        } else {
            return path;
        }
    }

    /**
     * package mode, get the jar path.
     *
     * @param fileName
     * @return
     */
    public static String getJarPath(String fileName) throws UnsupportedEncodingException {
        String path = FederateUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // 中文路径
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // 手动替换 / 为 \
            decodedPath = decodedPath.replaceAll("/", Matcher.quoteReplacement(File.separator)).substring(1);
        }
        // File.separator 是/（斜杠）与\（反斜杠），Windows下是\（反斜杠），Linux下是/（斜杠）。
        int lastIndex = decodedPath.lastIndexOf(File.separator) + 1;
        return decodedPath.substring(0, lastIndex) + fileName;
    }


    public static List<FD_Variable> results2FDType(List<ResultSet> resultSets, Class<?> clazz) throws SQLException {
        // List<T> variables = new ArrayList<>();
        List<FD_Variable> variables = new ArrayList<>();

        for (ResultSet rs : resultSets) {
            // 首个元素不会跳过，可理解为带头指针的链表
            while (rs.next()) {
                // System.out.println(rs.getObject(1) + " " + rs.getObject(2));
                if (clazz == FD_Double.class) {
                    variables.add(new FD_Double(rs.getObject("id", Integer.class).toString(), rs.getObject("dis", Double.class)));
                }
            }
        }
        return variables;
    }
}

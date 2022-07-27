package com.suda.federate.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.config.DbConfig;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FederateUtils {

    public static List<DbConfig> configInitialization(String configFile) throws IOException, SQLException, ClassNotFoundException {
        String configPath = FederateUtils.getRealPath(configFile);
        List<DbConfig> configList = new ArrayList<>();
        String jsonString = new String(Files.readAllBytes(Paths.get(configPath)));
        // 可能有多个数据源，写成 json array 格式
        JSONArray jsonArray = JSON.parseArray(jsonString);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            configList.add(jsonObject.to(DbConfig.class));
        }
        return configList;
    }

    public static String getOneResult(ResultSet rs) throws SQLException {
        int count = rs.getMetaData().getColumnCount();
        rs.next();
        String res = null;

        Object val = rs.getObject(1);
        if (val != null) {
            res = val.toString();
        }

        return res;
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
     * parse num (float or integer) from string
     * content: "POINT(121.4593425 31.2326237)" --> List [121.4593425, 31.2326237]
     *
     * @param content original string
     * @param clazz   integer or float or double
     */
    public static <T> List<T> parseNumFromString(String content, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        String pattern = "\\d*[.]\\d*";
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(content);
        while (m.find()) {
            list.add(clazz.getConstructor(String.class).newInstance(m.group()));
        }
        return list;
    }


    /**
     * According to the operating mode, get the resources path or jar path.
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getRealPath(String fileName) throws UnsupportedEncodingException {
        String env = Objects.requireNonNull(FederateUtils.class.getResource("")).getProtocol();
        if (env.equals("file")) {
            // IDE 中运行
            return FederateUtils.getResourcePath(fileName);
        } else if (env.equals("jar")) {
            // jar 包运行
            return FederateUtils.getJarPath(fileName);
        } else {
            System.exit(-1);
            return "";
        }
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
}

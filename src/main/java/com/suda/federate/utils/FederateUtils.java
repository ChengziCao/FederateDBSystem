package com.suda.federate.utils;

import org.apache.calcite.avatica.util.Sources;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FederateUtils {
    public static final StringBuilder buf = new StringBuilder();

    public static Map<String, Object> printResultSet(ResultSet rs) throws SQLException {
        int count = rs.getMetaData().getColumnCount();
        Map<String, Object> hm = new HashMap<>();
        while (rs.next()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= count; i++) {
                String label = rs.getMetaData().getColumnLabel(i);
                Object val = rs.getObject(i);
                String value = "null";
                if (val != null)
                    value = val.toString();
                sb.append(label).append(":").append(value);
                hm.put(label, value);
                if (i != count)
                    sb.append(" , ");
            }
            System.out.println(sb);
        }
        return hm;
    }


    /**
     * 获取 resource 目录下的文件
     *
     * @param file 文件名
     * @return
     */
    public static String resourcePath(String file) {
        return Sources.of(Objects.requireNonNull(FederateUtils.class.getResource("/" + file))).file().getAbsolutePath();
    }
}

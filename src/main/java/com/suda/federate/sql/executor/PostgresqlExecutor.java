package com.suda.federate.sql.executor;

import com.suda.federate.driver.PostgresqlDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PostgresqlExecutor implements SQLExecutor<ResultSet> {

    public ResultSet executeSql(Connection conn, String sql) throws SQLException {
        ResultSet rs = PostgresqlDriver.getInstance().executeSql(conn, sql);
        return rs;
    }

    public Map<String, ResultSet> executeSqlBatch(Map<String, Connection> connectionMap, String sql) throws SQLException {
        Map<String, ResultSet> resultSetMap = new HashMap<>();

        for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
            String soilName = entry.getKey();
            Connection conn = entry.getValue();
            ResultSet rs = PostgresqlDriver.getInstance().executeSql(conn, sql);
            resultSetMap.put(soilName, rs);
        }
        return resultSetMap;
    }

}

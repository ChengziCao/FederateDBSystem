package com.suda.federate.sql.executor;

import com.suda.federate.driver.FederateDBFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostgresqlExecutor implements SQLExecutor<ResultSet> {

    public List<ResultSet> executeSql(Map<String, List<Connection>> connectionMap, String sql) throws SQLException {
        List<ResultSet> resultSets = new ArrayList<>();
        for (Map.Entry<String, List<Connection>> entry : connectionMap.entrySet()) {
            String dbName = entry.getKey();
            List<Connection> connectionList = entry.getValue();
            for (Connection conn : connectionList) {
                ResultSet rs = FederateDBFactory.getDriverInstance(dbName).executeSql(conn, sql);
                resultSets.add(rs);
            }
        }
        return resultSets;
    }
}

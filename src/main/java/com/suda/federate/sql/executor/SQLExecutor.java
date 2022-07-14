package com.suda.federate.sql.executor;

import com.suda.federate.driver.FederateDBFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SQLExecutor<resultType> {
    /***
     * sql executor
     * @param connectionMap key: db_type, value: all connection of this db
     * @param sql sql waiting for execution
     * @return
     * @throws SQLException
     */
    List<resultType> executeSql(Map<String, List<Connection>> connectionMap, String sql) throws SQLException;
}

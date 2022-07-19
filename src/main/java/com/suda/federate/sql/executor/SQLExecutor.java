package com.suda.federate.sql.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface SQLExecutor<resultType> {
    /***
     * sql executor
     * @param connection connection
     * @param sql sql waiting for execution
     * @return
     * @throws SQLException
     */
    resultType executeSql(Connection connection, String sql) throws SQLException;


    /***
     * batch sql executor
     * @param connectionMap key: db_type:db_name, value: all connection of this db
     * @param sql sql waiting for execution
     * @return
     * @throws SQLException
     */
    Map<String, resultType> executeSqlBatch(Map<String, Connection> connectionMap, String sql) throws SQLException;
}

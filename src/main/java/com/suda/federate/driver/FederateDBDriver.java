package com.suda.federate.driver;

import com.alibaba.fastjson2.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FederateDBDriver {

    Connection getConnection(JSONObject json) throws SQLException;

    ResultSet executeSql(Connection conn, String sql) throws SQLException;

    ResultSet executeSql(Connection conn, String sql, String[] args) throws SQLException;

    void closeConnection() throws SQLException;
}

package com.suda.federate.driver;

import com.alibaba.fastjson.JSONObject;
import com.suda.federate.utils.ConfiguratorUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FederateDBDriver {

    Connection getConnection(JSONObject json) throws SQLException;

    ResultSet executeSql(Connection conn, String sql) throws SQLException;

    ResultSet executeSql(Connection conn, String sql, String[] args) throws SQLException;

    void closeConnection() throws SQLException;
}

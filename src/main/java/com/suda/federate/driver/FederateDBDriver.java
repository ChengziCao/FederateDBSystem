package com.suda.federate.driver;

import com.suda.federate.config.DriverConfig;

import java.sql.*;

public abstract class FederateDBDriver {
    public ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public ResultSet executeSql(Connection conn, String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public ResultSet executeSql(Connection conn, String sql, String[] args) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        // pstmt
        for (int i = 0; i < args.length; i++) {
            pstmt.setString(i, args[i]);
        }
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    public abstract Connection getConnection(DriverConfig config) throws SQLException;

}

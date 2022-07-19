package com.suda.federate.driver;


import com.alibaba.fastjson2.JSONObject;

import java.sql.*;

public class PostgresqlDriver implements FederateDBDriver {
    // connection 不需要每次都建立连接
    protected ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    // 单例模式，一种数据库 全局只需要一个 driver 即可
    private static PostgresqlDriver postgresqlDriver = null;

    PostgresqlDriver() {
    }

    public static PostgresqlDriver getInstance() {
        return postgresqlDriver == null ? postgresqlDriver = new PostgresqlDriver() : postgresqlDriver;
    }

    @Override
    public ResultSet executeSql(Connection conn, String sql) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    @Override
    public ResultSet executeSql(Connection conn, String sql, String[] args) throws SQLException {
        PreparedStatement pstmt;
        pstmt = conn.prepareStatement(sql);
        // pstmt
        for (int i = 0; i < args.length; i++) {
            pstmt.setString(i, args[i]);
        }
        return pstmt.executeQuery();
    }

    @Override
    public Connection getConnection(JSONObject json) throws SQLException {
        if (threadLocal.get() != null) {
            return threadLocal.get();
        } else {
            Connection connection;
            String name = json.getString("name");
            String driver = json.getString("driver");
            String user = json.getString("user");
            String password = json.getString("password");
            String url = json.getString("url");
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        if (threadLocal.get() != null) {
            threadLocal.get().close();
        }
    }
}

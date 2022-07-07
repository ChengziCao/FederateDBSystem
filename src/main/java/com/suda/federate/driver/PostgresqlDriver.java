package com.suda.federate.driver;


import com.suda.federate.config.DriverConfig;

import java.sql.*;
import java.util.Properties;

public class PostgresqlDriver extends FederateDBDriver{

    public Connection getConnection(DriverConfig config) throws SQLException {
        Connection conn = DriverManager.getConnection(config.url, config.user, config.password);
        return conn;
    }
}

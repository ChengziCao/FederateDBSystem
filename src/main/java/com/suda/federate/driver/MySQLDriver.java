package com.suda.federate.driver;

import com.suda.federate.config.DbConfig;
import com.suda.federate.utils.ENUM;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

public class MySQLDriver extends FederateDriver {

    protected Connection conn;

    public MySQLDriver(DbConfig config) throws SQLException, ClassNotFoundException {
        databaseType = ENUM.DATABASE.MYSQL;
        Class.forName(config.getDriver());
        conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    @Override
    public <T> List<T> executeSql(String sql, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        return resultSet2List(resultSet, resultClass);
    }

    @Override
    public <T> T executeSql(String sql, Class<T> resultClass, Boolean listFlag) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (listFlag) {
            // to do something
            return null;
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            return resultSet2Object(resultSet, resultClass);
        }
    }
}

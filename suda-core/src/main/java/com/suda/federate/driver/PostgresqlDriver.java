package com.suda.federate.driver;


import com.suda.federate.config.DbConfig;
import com.suda.federate.utils.ENUM;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

public class PostgresqlDriver extends FederateDriver {

    protected Connection conn;

    public PostgresqlDriver(DbConfig config) throws SQLException, ClassNotFoundException {
        databaseType = ENUM.DATABASE.POSTGRESQL;
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


//    @Override
//    public Map<String,ResultSet> executeSqlBatch(String sql) throws SQLException {
//        Map<String, ResultSet> resultSetMap = new HashMap<>();
//
//        for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
//            String soilName = entry.getKey();
//            Connection conn = entry.getValue();
//            ResultSet rs = PostgresqlDriver.getInstance().executeSql(conn, sql);
//            resultSetMap.put(soilName, rs);
//        }
//        return resultSetMap;
//    }

//    @Override
//    public ResultSet executeSql(String sql, String[] args) throws SQLException {
//        PreparedStatement pstmt;
//        pstmt = conn.prepareStatement(sql);
//        // pstmt
//        for (int i = 0; i < args.length; i++) {
//            pstmt.setString(i, args[i]);
//        }
//        return pstmt.executeQuery();
//    }


    protected void finalize() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }


}

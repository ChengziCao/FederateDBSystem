package com.suda.federate.utils;

import com.suda.federate.rpc.FederateCommon;
import com.suda.federate.silo.FederateDBService;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLExecutor {
    private FederateDBService service;
    private Connection conn;

    public SQLExecutor(Connection conn, FederateDBService service) {
        this.service = service;
        this.conn = conn;
    }

    /**
     * query: select RangeCounting (P, radius) from table_name;
     * result: Integer，The number of points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    public Integer localRangeCount(FederateCommon.Point point, String tableName,
                                   Double radius) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 生成目标 SQL
        String sql = SQLGenerator.generateRangeCountingSQL(point, tableName, radius);
        LogUtils.debug(String.format("%s Target SQL: ", "Mysql") + "\n" + sql);
        // 执行 SQL
        Integer ans = executeSql(sql, Integer.class, false);
        LogUtils.debug(String.format("%s RangeCount Result: ", "Mysql")+ "\n"  + ans);
        return ans;
    }


    public Double localKnnRadiusQuery(FederateCommon.Point point, String tableName, Integer k) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//knn主函数
        Double minRadius = Double.MAX_VALUE;
        // 初始化查询半径
        String sql = SQLGenerator.generateKnnRadiusQuerySQL(point, tableName, k);
        Double ans = executeSql(sql, Double.class, false);
        return ans;
    }


    /**
     * query: select RangeQuery (P, radius) from table_name;
     * result: List<point>，points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    public <T> List<T> localRangeQuery(FederateCommon.Point point, String tableName, Double radius, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果
        // 生成 SQL
        String sql = SQLGenerator.generateRangeQuerySQL(point, tableName, radius);
        LogUtils.debug(String.format("%s Target SQL: ", "Mysql") + "\n" + sql);
        // 执行 SQL
        List<T> pointList = executeSql(sql, resultClass);
        LogUtils.debug(String.format("%s RangeQuery Count: %d", "Mysql", pointList.size()) + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    public <T> List<T> localPolygonRangeQuery(FederateCommon.Polygon polygon, String tableName, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果
        // 生成 SQL
        String sql = SQLGenerator.generatePolygonRangeQuerySQL(polygon, tableName);
        LogUtils.debug(String.format("%s Target SQL: ", "Mysql") + "\n" + sql);
        // 执行 SQL
        List<T> pointList = executeSql(sql, resultClass);
        LogUtils.debug(String.format("%s PolyonRangeQuery Count: %d", "Mysql", pointList.size()) + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }

    public <T> List<T> localKnnQuery(FederateCommon.Point point, String tableName, Integer k, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sql = SQLGenerator.generateKNNSQL(point, tableName, k);
        LogUtils.debug(String.format("%s Target SQL: ", "Mysql")+ "\n"  + sql);
        // 执行 SQL
        List<T> pointList = executeSql(sql, resultClass);
        LogUtils.debug(String.format("%s Knn Query Count: %d", "Mysql", pointList.size()) + "\n" + FederateUtils.flatPointList(pointList));
        return pointList;
    }


    public <T> List<T> executeSql(String sql, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        return service.resultSet2List(resultSet, resultClass);
    }

    public <T> T executeSql(String sql, Class<T> resultClass, Boolean listFlag) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (listFlag) {
            // to do something
            return null;
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            return service.resultSet2Object(resultSet, resultClass);
        }
    }
}

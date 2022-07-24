package com.suda.federate.sql.common;

import com.suda.federate.config.DbConfig;
import com.suda.federate.driver.FederateDriver;
import com.suda.federate.sql.function.FD_Knn;
import com.suda.federate.sql.function.FD_RangeCount;
import com.suda.federate.sql.function.FD_RangeQuery;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM.FUNCTION;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.suda.federate.application.Main.LOGGER;

public class FederateQuery {

    /**
     * key: db name; value: db driver
     */
    public Map<String, FederateDriver> connections = new HashMap<>();

    public FederateQuery(List<DbConfig> configList) throws SQLException, ClassNotFoundException {
        for (DbConfig config : configList) {
            Class.forName(config.getDriver());
            connections.put(config.getName(), FederateDriver.getInstance(config));
        }
    }

    /**
     * query: select Knn (P, K) from table_name;
     * result: List<Point>，The K nearest neighbors of point P in table_name.
     *
     * @param point query location
     * @param k "K" nearest neighbors
     */
    private List<FD_Point> knnQuery(FD_Point point, Integer k) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//knn主函数
        Double minRadius = Double.MAX_VALUE;
        // 初始化查询半径
        for (String dbName : connections.keySet()) {
            FederateDriver driver = connections.get(dbName);
            // 生成目标 SQL
            String sql = SQLGenerator.generateKnnRadiusQuerySQL(point, k, driver.databaseType);
            Double ans = driver.executeSql(sql, Double.class, false);
            minRadius = ans < minRadius ? ans : minRadius;
        }

        double l = 0.0, u = minRadius, e = 1e-5;
        double threshold = minRadius;
        while (u - l >= e) {
            threshold = (l + u) / 2;
            int count = rangeCount(point, threshold);
            //hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
            if (count > k) {
                u = threshold;
            } else if (count < k) {
                l = threshold;
            } else {
                break;
            }
        }
        List<FD_Point> pointList = rangeQuery(point, threshold);
        return pointList;
    }

    /**
     * query: select RangeCounting (P, radius) from table_name;
     * result: Integer，The number of points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    private Integer rangeCount(FD_Point point, Double radius) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Integer> ansList = new ArrayList<>();
        // 读取参数
        // TODO: plaintext query
        for (String dbName : connections.keySet()) {
            FederateDriver driver = connections.get(dbName);
            // 生成目标 SQL
            String sql = SQLGenerator.generateRangeCountingSQL(point, radius, driver.databaseType);
            LOGGER.info(String.format("\n%s Target SQL: ", dbName) + sql);
            // 执行 SQL
            Integer ans = driver.executeSql(sql, Integer.class, false);
            ansList.add(ans);
            LOGGER.info(String.format("\n%s RangeCount Result: ", dbName) + ans);
        }
        // TODO: secure summation
        return setSummation(ansList, Integer.class);
    }


    /**
     * query: select RangeQuery (P, radius) from table_name;
     * result: List<point>，points whose distance from P < radius in table_name.
     *
     * @param point  query location
     * @param radius range count radius
     */
    private List<FD_Point> rangeQuery(FD_Point point, Double radius) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {//select * 获取knn结果
        Map<String, List<FD_Point>> pointMapList = new HashMap<>();
        // 读取参数
        // TODO: plaintext query
        for (String dbName : connections.keySet()) {
            FederateDriver driver = connections.get(dbName);
            // 生成 SQL
            String sql = SQLGenerator.generateRangeQuerySQL(point, radius, driver.databaseType);
            LOGGER.info(String.format("\n%s Target SQL: ", dbName) + sql);
            // 执行 SQL
            List<FD_Point> pointList = driver.executeSql(sql, FD_Point.class);
            pointMapList.put(dbName, pointList);
            LOGGER.info(String.format("\n%s RangeQuery Result:", dbName) + pointList.toString());
        }
        // TODO: secure union
        return setUnion(pointMapList);
    }


    /**
     * don't mind, just for test
     */
    private <T> List<T> setUnion(Map<String, List<T>> listMap) {
        List<T> list = new ArrayList<>();
        for (String name : listMap.keySet()) {
            list.addAll(listMap.get(name));
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private boolean setCompare(List<Float> list, Float k) {
        return true;
    }

    private <T extends Number> T setSummation(List<T> list, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Double ans = list.stream().mapToDouble(Number::doubleValue).sum();
        if (clazz == Integer.class || clazz == Long.class) {
            long intPart = ans.longValue();
            return clazz.getConstructor(String.class).newInstance(Long.toString(intPart));
        } else {
            return clazz.getConstructor(String.class).newInstance(Double.toString(ans));
        }
    }

    public void query(SQLExpression expression) throws Exception {
        if (expression.function == FUNCTION.RANGE_COUNT) {
            FD_RangeCount rangeCounting = new FD_RangeCount(expression);
            Integer result = rangeCount(rangeCounting.point, rangeCounting.radius);
            LOGGER.info("\nAggregation Result:" + result);
        } else if (expression.function == FUNCTION.RANGE_QUERY) {
            FD_RangeQuery rangeQuery = new FD_RangeQuery(expression);
            List<FD_Point> pointList = rangeQuery(rangeQuery.point, rangeQuery.radius);
            LOGGER.info("\nAggregation Result: " + pointList.toString());
        } else if (expression.function == FUNCTION.KNN) {
            FD_Knn knnQuery = new FD_Knn(expression);
            List<FD_Point> pointList = knnQuery(knnQuery.point, knnQuery.k);
            LOGGER.info("\nAggregation Result: " + pointList.toString());
        } else {
            throw new Exception("type not support.");
        }
    }
}

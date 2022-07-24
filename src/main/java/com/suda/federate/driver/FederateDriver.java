package com.suda.federate.driver;

import com.suda.federate.config.DbConfig;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.utils.ENUM.DATABASE;
import com.suda.federate.utils.FederateUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FederateDriver {

    public DATABASE databaseType;

    /***
     * sql executor
     * @param sql sql for execution
     * @return List<resultClass>
     * @throws SQLException
     */
    public abstract <T> List<T> executeSql(String sql, Class<T> resultClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    /**
     * sql executor
     * @param sql sql for execution
     * @param resultClass
     * @param ListFlag 无实际作用，占位，重载
     * @return resultClass
     */
    public abstract <T> T executeSql(String sql, Class<T> resultClass, Boolean ListFlag) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    public static FederateDriver getInstance(DbConfig config) throws SQLException {
        if (config.getType() == DATABASE.POSTGRESQL) {
            return new PostgresqlDriver(config);
        } else {
            return null;
        }
    }

    public static <T> List<T> resultSet2List(ResultSet resultSet, Class<T> clazz) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T t = resultSet2Object(resultSet, clazz);
            resultList.add(t);
        }
        return resultList;
    }

    public static <T> T resultSet2Object(ResultSet resultSet, Class<T> clazz) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (resultSet.isBeforeFirst()) {
            // 跳过头指针
            resultSet.next();
        }
        if (clazz == Integer.class || clazz == Double.class || clazz == String.class) {
            return clazz.getConstructor(String.class).newInstance(resultSet.getObject(1).toString());
        } else if (clazz == FD_Point.class) {
            String content = resultSet.getObject(1).toString();
            List<Float> temp = FederateUtils.parseNumFromString(content, Float.class);
            return clazz.getConstructor(String.class, Float.class, Float.class).newInstance("_", temp.get(0), temp.get(1));
        } else if (clazz == HashMap.class) {
            Map<String, Object> mmap = new HashMap<>();
            int count = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= count; i++) {
                mmap.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
            }
            return clazz.getConstructor(Map.class).newInstance(mmap);
        } else {
            return null;
        }
    }




//    /***
//     * batch sql executor
//     * @param expression sql waiting for execution
//     * @return
//     * @throws SQLException
//     */
//    public Map<String, resultType> executeSqlBatch(SQLExpression expression) throws SQLException {
//        Map<String, resultType> resultMap = new HashMap<>();
//        for (FederateDriver driver : connections) {
//            resultMap.put("123",driver.executeSql(expression)) ;
//        }
//    }
}
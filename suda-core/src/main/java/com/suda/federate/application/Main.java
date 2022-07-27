package com.suda.federate.application;

import com.suda.federate.config.DbConfig;
import com.suda.federate.driver.FederateDriver;
import com.suda.federate.sql.common.FederateQuery;
import com.suda.federate.sql.common.SQLExpression;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static List<FederateDriver> driverList = new ArrayList<>();
    // 0: console, 1: file
//    private static int STDOUT = 0;


    public static void main(String[] args) throws SQLException {
        try {
            // TODO 读取配置文件
            String configFile = "config.json", queryFile = "query.json";
            // TODO 解析 config.json，配置初始化
            List<DbConfig> configList = FederateUtils.configInitialization(configFile);
            // TODO 解析 query.json，获取原始 SQL
            List<SQLExpression> sqlExpressions = SQLExpression.parseSQLExpression(queryFile);

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
                // TODO: query
                FederateQuery federateQuery = new FederateQuery(configList);
                federateQuery.query(expression);
                System.out.println("===========================================================================");
            }

        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error(buildErrorMessage(e));
        }
    }

    // System.out.println(content) 不显示行号，找不到是哪 print 的
//    public static void print(String content) {
//        if (STDOUT == 0) {
//            System.out.println(content);
//        } else if (STDOUT == 1) {
//            LOGGER.info(content);
//        }
//    }


    public static String getStackTraceString(Throwable ex) {//(Exception ex) {
        StackTraceElement[] traceElements = ex.getStackTrace();

        StringBuilder traceBuilder = new StringBuilder();

        if (traceElements != null && traceElements.length > 0) {
            for (StackTraceElement traceElement : traceElements) {
                traceBuilder.append("\t").append(traceElement.toString());
                traceBuilder.append("\n");
            }
        }
        return traceBuilder.toString();
    }

    //构造异常堆栈信息
    public static String buildErrorMessage(Exception ex) {

        String result;
        String stackTrace = getStackTraceString(ex);
        String exceptionType = ex.toString();
        String exceptionMessage = ex.getMessage();

        result = String.format("%s : %s \r\n %s", exceptionType, exceptionMessage, stackTrace);

        return result;
    }
}
package com.suda.federate.application;

import com.suda.federate.sql.executor.PostgresqlExecutor;
import com.suda.federate.sql.executor.SQLExecutor;
import com.suda.federate.sql.merger.SQLMerger;
import com.suda.federate.sql.translator.PostgresqlTranslator;
import com.suda.federate.sql.translator.SQLTranslator;
import com.suda.federate.sql.type.FD_Double;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.driver.FederateDBDriver;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static List<FederateDBDriver> driverList = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        try {
            // TODO 读取配置文件
            String configPath = null, queryPath = null;
            String env = Objects.requireNonNull(Main.class.getResource("")).getProtocol();
            if (env.equals("file")) {
                // IDE 中运行
                configPath = FederateUtils.getResourcePath("config.json");
                queryPath = FederateUtils.getResourcePath("query.json");
            } else if (env.equals("jar")) {
                // jar 包运行
                configPath = FederateUtils.getJarPath("config.json");
                queryPath = FederateUtils.getJarPath("query.json");
            } else {
                LOGGER.error("unknown environment.");
                System.exit(-1);
            }
//            System.out.println(configPath);
//            System.out.println(queryPath);
            // TODO 解析 config.json，连接数据库
            Map<String, List<Connection>> connectionMap = FederateUtils.parseConfigJson(configPath);
            // TODO 解析 query.json，获取原始 SQL
            Object[] tempObjs = FederateUtils.parseQueryJson(queryPath);
            String originalSql = (String) tempObjs[0];
//            LOGGER.info("originalSQL: " + originalSql);
            System.out.println("originalSQL: " + originalSql);
            List<FD_Variable> variables = (List<FD_Variable>) tempObjs[1];

            // TODO SQL Translator
            SQLTranslator sqlTranslator = new PostgresqlTranslator();
            String unoptimizedSql = sqlTranslator.translate(originalSql, variables);
            System.out.println("translatedSQL: " + unoptimizedSql);

            // TODO SQL Optimizer
            String optimizedSql = unoptimizedSql;

            // TODO SQL Executor
            SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();
            List<ResultSet> resultSets = sqlExecutor.executeSql(connectionMap, optimizedSql);

            // TODO Results Merger
            SQLMerger sqlMerger = new SQLMerger();
            List<FD_Variable> results = FederateUtils.results2FDType(resultSets, FD_Double.class);
            System.out.println(results);
            FD_Double ans = (FD_Double) sqlMerger.sum(results, FD_Double.class);
            System.out.println(ans);


        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(String.valueOf(e));
        } finally {
            for (FederateDBDriver driver : driverList) {
                driver.closeConnection();
            }
        }
    }
}
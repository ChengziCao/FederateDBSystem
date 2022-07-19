package com.suda.federate.application;

import com.suda.federate.driver.FederateDBDriver;
import com.suda.federate.sql.executor.PostgresqlExecutor;
import com.suda.federate.sql.executor.SQLExecutor;
import com.suda.federate.sql.expression.SQLExpression;
import com.suda.federate.sql.merger.SQLMerger;
import com.suda.federate.sql.translator.PostgresqlTranslator;
import com.suda.federate.sql.translator.SQLTranslator;
import com.suda.federate.sql.type.FD_Double;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static List<FederateDBDriver> driverList = new ArrayList<>();
    // 0: console, 1: file
    private static int STDOUT = 0;

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

            // TODO 解析 config.json，连接数据库
            Map<String, Connection> connectionMap = FederateUtils.parseConfigJson(configPath);

            // TODO 解析 query.json，获取原始 SQL
            List<SQLExpression> sqlExpressions = SQLExpression.generateSQLExpression(queryPath);
            for (SQLExpression expression : sqlExpressions) {

                print(String.format("====================== NO.%d query statement =============================", sqlExpressions.indexOf(expression)));
                print("Original SQL:");
                print(expression.build());
                // TODO SQL Translator
                SQLTranslator sqlTranslator = new PostgresqlTranslator();
                String translatedSql = sqlTranslator.translate(expression);
                print("Target SQL: ");
                print(translatedSql);
                // TODO SQL Optimizer
                // translatedSql = translatedSql;
                // TODO SQL Executor
                SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();

                Map<String, ResultSet> resultSetMap = sqlExecutor.executeSqlBatch(connectionMap, translatedSql);

                // TODO Results Merger
                SQLMerger sqlMerger = new SQLMerger();
                // List<FD_Variable> results = FD_Variable.results2FDVariable(resultSets, FD_Double.class);
                print("Query Result: ");
                for (String siloName : resultSetMap.keySet()) {
                    ResultSet rs = resultSetMap.get(siloName);
                    print(siloName);
                    print(FD_Variable.results2FDVariable(rs, FD_Double.class).toString());
                }

                // FD_Double ans = (FD_Double) sqlMerger.sum(results, FD_Double.class);
                // System.out.println(ans);
                print("=============================================================================");
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(String.valueOf(e));
        } finally {
            for (FederateDBDriver driver : driverList) {
                driver.closeConnection();
            }
        }
    }

    public static void print(String content) {
        if (STDOUT == 0) {
            System.out.println(content);
        } else if (STDOUT == 1) {
            LOGGER.info(content);
        }
    }
}
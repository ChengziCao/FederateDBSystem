package com.suda.federate.application;

import com.suda.federate.config.ModelConfig;
import com.suda.federate.function.*;
import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.silo.FederateDBClient;
import com.suda.federate.utils.FederateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;

import static com.suda.federate.utils.FederateUtils.buildErrorMessage;
import static com.suda.federate.utils.FederateUtils.parseSQLExpression;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String queryFile = "query-local.json";
            String modelFile = "model-local.json";
            // Scanner scanner = new Scanner(System.in);
            // do {
            //
            //     queryFile = scanner.nextLine();
            // } while (queryFile.equalsIgnoreCase("exit"));


            initialization(modelFile);
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);
            // for (SQLExpression expression : sqlExpressions) {
            //     System.out.printf("====================== NO.%d public query statement =============================%n", sqlExpressions.indexOf(expression));
            //     if (expression.getFunction().equals(SQLExpression.Function.RANGE_COUNT)) {
            //         FederateRangeCount.publicQuery(expression);
            //     } else if (expression.getFunction().equals(SQLExpression.Function.RANGE_QUERY)) {
            //         FederateRangeQuery.publicQuery(expression);
            //     } else if (expression.getFunction().equals(SQLExpression.Function.POLYGON_RANGE_QUERY)) {
            //         FederatePolygonRangeQuery.publicQuery(expression);
            //     } else if (expression.getFunction().equals(SQLExpression.Function.RKNN)) {
            //         FederateRKNN.publicQuery(expression);
            //     } else if (expression.getFunction().equals(SQLExpression.Function.KNN)) {
            //         FederateKNN.publicQuery(expression);
            //     }
            //     System.out.println("===========================================================================");
            // }

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d privacy query statement =============================%n", sqlExpressions.indexOf(expression));
                if (expression.getFunction().equals(SQLExpression.Function.RANGE_COUNT)) {
                    FederateRangeCount.privacyQuery(expression);
                } else if (expression.getFunction().equals(SQLExpression.Function.RANGE_QUERY)) {
                    FederateRangeQuery.privacyQuery(expression);
                } else if (expression.getFunction().equals(SQLExpression.Function.POLYGON_RANGE_QUERY)) {
                    FederatePolygonRangeQuery.privacyQuery(expression);
                } else if (expression.getFunction().equals(SQLExpression.Function.KNN)) {
                    FederateKNN.privacyQuery(expression);
                } else if (expression.getFunction().equals(SQLExpression.Function.RKNN)) {
                    FederateRKNN.privacyQuery(expression);
                }
                System.out.println("===========================================================================");
            }
        } catch (Exception e) {
            LOGGER.error(buildErrorMessage(e));
        } finally {
            System.exit(0);
        }
    }


    public static void initialization(String modelFile) throws IOException {
        ModelConfig modelConfig = FederateUtils.parseModelConfig(modelFile);
        ModelConfig.Schemas schema = modelConfig.getSchemas().get(0);
        for (ModelConfig.Tables table : schema.getTables()) {
//            Map<String, String> oneSiloMap = new HashMap<>();
            int id = 1;
            for (ModelConfig.Feds feds : table.getFeds()) {
//                oneSiloMap.put(feds.getEndpoint(), feds.getSiloTableName());
                FederateQuery.endpoints.add(feds.getEndpoint());
                FederateDBClient client = new FederateDBClient(feds.getIp(), feds.getPort(), id++);
                FederateQuery.federateDBClients.add(client);
                // FederateQuery.federateClientMap.put(feds.getEndpoint(), client);
            }
//            tableMap.put(table.getName(), oneSiloMap);
        }
        FederateQuery.executorService = Executors.newFixedThreadPool(FederateQuery.federateDBClients.size());
    }
}
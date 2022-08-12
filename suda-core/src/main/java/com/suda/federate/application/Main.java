package com.suda.federate.application;

import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.spatial.FederateQuerier;
import com.suda.federate.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.suda.federate.utils.FederateUtils.buildErrorMessage;
import static com.suda.federate.utils.FederateUtils.parseSQLExpression;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String queryFile = "query.json";
            String modelFile = "model.json";
            FederateQuerier querier = new FederateQuerier(modelFile);
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);
            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d public query statement =============================%n", sqlExpressions.indexOf(expression));
                querier.fedSpatialPublicQuery(expression);
                System.out.println("===========================================================================");
            }

            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d privacy query statement =============================%n", sqlExpressions.indexOf(expression));
                querier.fedSpatialPrivacyQuery(expression);
                System.out.println("===========================================================================");
            }
        } catch (Exception e) {
            LOGGER.error(buildErrorMessage(e));
        }
    }
}
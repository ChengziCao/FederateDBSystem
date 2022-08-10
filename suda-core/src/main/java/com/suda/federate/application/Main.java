package com.suda.federate.application;

import com.suda.federate.query.FederateQuerier;
import com.suda.federate.rpc.FederateService.SQLExpression;
import com.suda.federate.utils.LogUtils;

import java.util.List;

import static com.suda.federate.utils.FederateUtils.buildErrorMessage;
import static com.suda.federate.utils.FederateUtils.parseSQLExpression;

public class Main {
    public static void main(String[] args) {
        try {
            String queryFile = "query.json";
            String modelFile = "model.json";
            FederateQuerier querier = new FederateQuerier(modelFile);
            List<SQLExpression> sqlExpressions = parseSQLExpression(queryFile);
            for (SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d query statement =============================%n", sqlExpressions.indexOf(expression));
                querier.query(expression, false);
                System.out.println("===========================================================================");
            }
        } catch (Exception e) {
            LogUtils.error(buildErrorMessage(e));
        }
    }
}
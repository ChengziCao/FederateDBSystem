package com.suda.federate.application;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.suda.federate.function.FederateKNN;
import com.suda.federate.function.FederateRKNN;
import com.suda.federate.rpc.FederateService;
import com.suda.federate.utils.LogUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.suda.federate.application.Main.initialization;
import static com.suda.federate.utils.FederateUtils.buildErrorMessage;
import static com.suda.federate.utils.FederateUtils.parseSQLExpression;


public class TestMain {
    // x: longitude, y: latitude
    private static final Integer k = 10;
    private static final Integer siloNumber = 3;
    private static final Integer testNum = 1;
    private static final String tableName = "osm_tianjin";

    public static void main(String[] args) {
        afterFunction();
    }

    @Test
    public void testKNN() {
        try {
            JSONArray jsonArray = generateKNNTestQueryJsonArray(tableName, k, testNum);
            List<FederateService.SQLExpression> sqlExpressions = parseSQLExpression(jsonArray);

            for (FederateService.SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d privacy query statement =============================%n", sqlExpressions.indexOf(expression));
                if (expression.getFunction().equals(FederateService.SQLExpression.Function.KNN)) {
                    FederateKNN.publicQuery(expression);
                    FederateKNN.privacyQuery(expression);
                }
                System.out.println("===========================================================================");
            }
        } catch (Exception e) {
            LogUtils.debug(buildErrorMessage(e));
        } finally {
            afterFunction();
            System.exit(0);
        }
    }

    @Test
    public void testRKNN() {
        try {
            JSONArray jsonArray = generateRkNNTestQueryJsonArray(tableName, k, testNum);
            List<FederateService.SQLExpression> sqlExpressions = parseSQLExpression(jsonArray);

            for (FederateService.SQLExpression expression : sqlExpressions) {
                System.out.printf("====================== NO.%d privacy query statement =============================%n", sqlExpressions.indexOf(expression));
                if (expression.getFunction().equals(FederateService.SQLExpression.Function.RKNN)) {
                    FederateRKNN.privacyQuery(expression);
                    // FederateRKNN.publicQuery(expression);
                }
                System.out.println("===========================================================================");
            }
        } catch (Exception e) {
            LogUtils.debug(buildErrorMessage(e));
        } finally {
            afterFunction();
            System.exit(0);
        }
    }

    @BeforeAll
    public static void beforeFunction() {
        try {
            // TODO: start container
            List<String> tempList = new ArrayList<>();
            executive("docker start spatial");
            for (int i = 0; i < siloNumber; i++) {
                executive(String.format("docker start postgis%d", i + 1));
                tempList.add(String.valueOf(i + 1));
            }
            TimeUnit.SECONDS.sleep(4);
            LogUtils.debug("start container completed.");
            // TODO: run driver
            executive(String.format("docker exec -d spatial bash -c \"cd root &&  ./start-driver.sh %s\"", String.join(" ", tempList)));
            TimeUnit.SECONDS.sleep(4);
            LogUtils.debug("driver has started.");

            // TODO: parse config
            String modelFile = "model-local.json";
            initialization(modelFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void afterFunction() {
        for (int i = 0; i < siloNumber; i++) {
            executive(String.format("docker kill postgis%d", i + 1));
        }
        executive("docker exec -d spatial bash -c \"cd root &&  ./stop-all.sh\"");
        executive("docker kill spatial");
        LogUtils.debug("container has stopped.");
    }

    public static JSONArray generateKNNTestQueryJsonArray(String tableName, Integer k, Integer testNum) {
        Double[][] queryPoints = {{117.7418942, 38.984194}};
        Random random = new Random();
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (int i = 0; i < testNum; i++) {
            int tempIndex = random.nextInt(queryPoints.length);
            listMap.add(generateKNNMap(tableName, k, queryPoints[tempIndex][0], queryPoints[tempIndex][1]));
        }
        return new JSONArray(listMap);
    }

    public static JSONArray generateRkNNTestQueryJsonArray(String tableName, Integer k, Integer testNum) {
        Double[][] queryPoints = {{117.7418942, 38.984194}};
        Random random = new Random();
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (int i = 0; i < testNum; i++) {
            int tempIndex = random.nextInt(queryPoints.length);
            listMap.add(generateRKNNMap(tableName, k, queryPoints[tempIndex][0], queryPoints[tempIndex][1]));
        }
        return new JSONArray(listMap);
    }

    public static Map<String, Object> generateKNNMap(String tableName, Integer k, Double x, Double y) {
        JSONArray params = new JSONArray();
        JSONObject param1 = new JSONObject();
        param1.put("type", "int");
        param1.put("value", k);
        JSONObject param2 = new JSONObject();
        param2.put("type", "point");
        param2.put("value", String.format("%s %s", x.toString(), y.toString()));

        params.add(param1);
        params.add(param2);

        return new HashMap<String, Object>() {{
            put("function", "Knn");
            put("table", tableName);
            put("params", params);
        }};
    }

    public static Map<String, Object> generateRKNNMap(String tableName, Integer k, Double x, Double y) {
        JSONArray params = new JSONArray();
        JSONObject param1 = new JSONObject();
        param1.put("type", "int");
        param1.put("value", k);
        JSONObject param2 = new JSONObject();
        param2.put("type", "point");
        param2.put("value", String.format("%s %s", x.toString(), y.toString()));

        params.add(param1);
        params.add(param2);

        return new HashMap<String, Object>() {{
            put("function", "RKnn");
            put("table", tableName);
            put("params", params);
        }};
    }


    /***
     * java 执行 cmd 命令
     * @param stmt 要执行的命令
     */
    public static void executive(String stmt) {
        Runtime runtime = Runtime.getRuntime();  // 获取Runtime实例
        // 执行命令
        try {
            String[] command = {"cmd", "/c", stmt};
            Process process = runtime.exec(command);
            // 标准输入流（必须写在 waitFor 之前）
            String inStr = consumeInputStream(process.getInputStream());
            // 标准错误流（必须写在 waitFor 之前）
            String errStr = consumeInputStream(process.getErrorStream()); // 若有错误信息则输出
            int proc = process.waitFor();

//            if (proc == 0) {
//                System.out.println("执行成功");
//            } else {
//                System.out.println("执行失败" + errStr);
//            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /***
     * 消费 InputStream，并返回
     * @param is
     * @return
     * @throws IOException
     */
    public static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            System.out.println(s);
            sb.append(s);
        }
        return sb.toString();
    }
}

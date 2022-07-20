package com.suda.federate.sql.common;

import com.suda.federate.sql.executor.PostgresqlExecutor;
import com.suda.federate.sql.executor.SQLExecutor;
import com.suda.federate.sql.expression.SQLExpression;
import com.suda.federate.sql.merger.SQLMerger;
import com.suda.federate.sql.translator.PostgresqlTranslator;
import com.suda.federate.sql.type.FD_Point;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.FederateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.suda.federate.application.Main.print;

public class FederateQuery {
    private Map<String, Connection> connectionMap;
    private FD_Point.Location point;
    private Integer k;
    private double knnRadiusQuery(String translatedSql) {//初始化查询半径
//        select ST_Distance(ST_GeographyFromText(p), ST_GeographyFromText(ST_AsText(geom)) ) as d from nyc_subway_stations order by d limit 1 offset k-1
//        int k = expres.getVariables();

        // TODO SQL Executor
        print("knnRadiusQuery Target SQL: ");
        print(translatedSql);
        SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();
        Map<String, ResultSet> resultSetMap = null;
        try {
            resultSetMap = sqlExecutor.executeSqlBatch(connectionMap, translatedSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO Results Merger
        SQLMerger sqlMerger = new SQLMerger();
        // List<FD_Variable> results = FD_Variable.results2FDVariable(resultSets, FD_Double.class);
        print("Query Result: ");
        Double radius=Double.MAX_VALUE;
        for (String siloName : resultSetMap.keySet()) {
            ResultSet rs = resultSetMap.get(siloName);
            print(siloName);
            try {
                String d=FederateUtils.getOneResult(rs);
                print("d="+d);
                radius=Math.min(radius,new Double(d));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // print(FD_Variable.resultSet2FDVariable(rs, FD_Double.class).toString());
        }
        print("init radius= "+radius);
//        select ST_Distance(ST_GeographyFromText(p), ST_GeographyFromText(ST_AsText(geom)) ) as d from nyc_subway_stations order by d limit 1 offset k-1
        return radius;
    }

    private Integer rangeCount(String translatedSql){//TODO select count(*) 获取数据条数 ,可参考hufu dPRangeCount

        print("rangeCount Target SQL: ");
        print(translatedSql);
        // TODO SQL Optimizer
        SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();
        Map<String, ResultSet> resultSetMap = null;
        try {
            resultSetMap = sqlExecutor.executeSqlBatch(connectionMap, translatedSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO Results Merger
        //TODO 代码复用
        SQLMerger sqlMerger = new SQLMerger();
        // List<FD_Variable> results = FD_Variable.results2FDVariable(resultSets, FD_Double.class);
        print("Query Result: ");
        int count=0;
        for (String siloName : resultSetMap.keySet()) {
            ResultSet rs = resultSetMap.get(siloName);
            print(siloName);
            try {
                String d=FederateUtils.getOneResult(rs);
                print("d="+d);
                count= Integer.parseInt(d) +count;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // print(FD_Variable.resultSet2FDVariable(rs, FD_Double.class).toString());
        }
        return count;
    }

    private Object rangeQuery(String translatedSql){//select * 获取knn结果
        print("rangeCount Target SQL: ");
        print(translatedSql);
        // TODO SQL Optimizer
        SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();
        Map<String, ResultSet> resultSetMap = null;
        try {
            resultSetMap = sqlExecutor.executeSqlBatch(connectionMap, translatedSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO Results Merger
        //TODO 代码复用
        SQLMerger sqlMerger = new SQLMerger();
        // List<FD_Variable> results = FD_Variable.results2FDVariable(resultSets, FD_Double.class);
        print("Query Result: ");
        for (String siloName : resultSetMap.keySet()) {
            ResultSet rs = resultSetMap.get(siloName);
            print(siloName);
            try {
                FederateUtils.printResultSet(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // print(FD_Variable.resultSet2FDVariable(rs, FD_Double.class).toString());
        }
        return null;
    }
    private Object knnQuery(SQLExpression expression){//knn主函数
        double l=0;
        double u= knnRadiusQuery(expression.clone()
                .setColumns(new ArrayList<String>(){{add("ST_distance(ST_GeomFromText('POINT(" +String.valueOf(point.x)+" "+String.valueOf(point.y) +")',st_srid(location)), location) as d");}})
                .setOrder("d")
                .setFilters(new ArrayList<>())
                .setLimit(1)
                .setOffset(k-1)
                .build());
        double e=1e-5;// TODO hufu 1e-6
        int loop=0;
        while(l+e<=u){
            double thres=(l+u)/2;

            int count = rangeCount(expression.clone()
                    .setColumns(new ArrayList<String>(){{add("count(*)");}})
                    .setFilters(new ArrayList<String>(){{add("ST_distance(ST_GeomFromText('POINT(" +String.valueOf(point.x)+" "+String.valueOf(point.y) +")',st_srid(location)), location) <= "+ thres);}})
                    .setLimit(1)
                    .build());
            loop++;
            //TODO hufu有个        if (Math.abs(res.getKey() - k) < res.getValue()) { 提前终止，什么意思
            if (count > k) {
                u = thres;
            } else if (count < k) {
                l = thres;
            }else{
                print("loop ="+loop+", "+thres);
                return rangeQuery(expression.clone()
                        .setColumns(new ArrayList<String>(){{add("count(*)");}})
                        .setFilters(new ArrayList<String>(){{add("ST_distance(ST_GeomFromText('POINT(" +String.valueOf(point.x)+" "+String.valueOf(point.y) +")',st_srid(location)), location) <= "+ thres);}})
                        .build());
            }
        }
        print("loop ="+loop);
        print("out of loop, with approximate result: ");
        double approx_r=u;
        rangeQuery(expression.clone()
                .setColumns(new ArrayList<String>(){{add("id");}})
                .setFilters(new ArrayList<String>(){{add("ST_distance(ST_GeomFromText('POINT(" +String.valueOf(point.x)+" "+String.valueOf(point.y) +")',st_srid(location)), location) <= "+ approx_r);}})
                .setLimit(k)
                .build());
        return null;
        /**
         *Original SQL: select id from osm_sh where FD_KNN ($P, location, $K) limit 100
         *P=POINT(121.45611 31.253359), k=3
         * out of loop, with approximate result:
         * rangeCount Target SQL:
         * select id from osm_sh where ST_distance(ST_GeomFromText('POINT(121.45611 31.253359)',st_srid(location)), location) <= 0.02105044940185353 limit 100
         * Query Result:
         * POSTGRESQL:db1
         * POSTGRESQL:db2
         * id:3731182030
         * id:4077014854
         * id:4077014855
         *
         */

    }
    private void setUnion(String  translatedSql){
        // TODO SQL Executor
        SQLExecutor<ResultSet> sqlExecutor = new PostgresqlExecutor();
        Map<String, ResultSet> resultSetMap = null;
        try {
            resultSetMap = sqlExecutor.executeSqlBatch(connectionMap, translatedSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO Results Merger
        SQLMerger sqlMerger = new SQLMerger();
        // List<FD_Variable> results = FD_Variable.results2FDVariable(resultSets, FD_Double.class);
        print("Query Result: ");
        for (String siloName : resultSetMap.keySet()) {
            ResultSet rs = resultSetMap.get(siloName);
            print(siloName);
            try {
                FederateUtils.printResultSet(rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // print(FD_Variable.resultSet2FDVariable(rs, FD_Double.class).toString());
        }
    }
    public Object query(Map<String, Connection> connectionMap, SQLExpression expression) throws Exception {
        this.connectionMap = connectionMap;
        List<FD_Variable> variables = expression.getVariables();
        for(FD_Variable variable : variables){
            if("K".equals(variable.name)){
                k= (Integer) variable.value;
            }else if ("P".equals(variable.name)) {
                try{
                    point= (FD_Point.Location) variable.value;
                }catch (Exception e) {
                    print("get p warning" +expression.getQueryType());
                }
            }
        }
        if (ENUM.equals(expression.queryType, ENUM.FUNCTION.DISTANCE)) {
            // TODO
            rangeQuery((new PostgresqlTranslator()).translate(expression));
        } else if (ENUM.equals(expression.queryType, ENUM.FUNCTION.KNN)) {
            knnQuery(expression);
        } else if (ENUM.equals(expression.queryType, ENUM.FUNCTION.RKNN)) {
            return null;
        } else if (ENUM.equals(expression.queryType, ENUM.FUNCTION.CONTAINS)) {
            rangeQuery((new PostgresqlTranslator()).translate(expression));;// TODO
        } else {
            throw new Exception("type not support.");
        }
        return null;



    }

}

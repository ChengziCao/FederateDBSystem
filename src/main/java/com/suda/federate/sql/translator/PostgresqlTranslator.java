package com.suda.federate.sql.translator;

import com.suda.federate.sql.expression.SQLExpression;
import com.suda.federate.sql.function.FD_Function;
import com.suda.federate.sql.type.FD_Variable;
import com.suda.federate.utils.ENUM;

import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class PostgresqlTranslator implements SQLTranslator {

    // original sql: select F.id, DF_distance($P, F.location) as dis from nyc_homicides_copy where DF_distance($P, F.location) < 10 order by dis;
    // $P=(583571 4506714)
    // target sql: select id, ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) as dis from nyc_homicides_copy where ST_Distance(ST_GeomFromText('POINT(583571 4506714)',st_srid(location)),location) < 1000 order by dis;;

    @Override
    public String translate(SQLExpression expression) {
        // TODO 变量替换
        for (FD_Variable var : expression.variables) {
            expression.columns = expression.columns.stream().
                    map(x -> x.replaceAll(Matcher.quoteReplacement("$") + var.name, var.translate2PostgresqlFormat()))
                    .collect(Collectors.toList());
            expression.filters = expression.filters.stream()
                    .map(x -> x.replaceAll(Matcher.quoteReplacement("$") + var.name, var.translate2PostgresqlFormat()))
                    .collect(Collectors.toList());
        }
        // TODO 函数替换
        for (FD_Function function : expression.functions) {

            expression.columns = expression.columns.stream().
                    map(x -> x.replaceAll(function.name, function.translate2PostgresqlFormat()))
                    .collect(Collectors.toList());
            expression.filters = expression.filters.stream()
                    .map(x -> x.replaceAll(function.name, function.translate2PostgresqlFormat()))
                    .collect(Collectors.toList());
        }
        return expression.build();
    }


    // public String geomFromText(String value, String type) throws Exception {
    //     if (ENUM.equals(type, ENUM.DATA_TYPE.POINT)) {
    //         return String.format("ST_GeomFromText('POINT(%s)',st_srid(location))", value);
    //     } else {
    //         throw new Exception("type not support");
    //     }
    // }
}

# DataFederateSystem

## Program entry

```
com.suda.federate.application.Main.main()
```

## Requirement

* Apache Maven 3.6.0+
* Java 8
* PostgreSQL 13 + PostGIS 3.0

## Start

### debug

1. edit `config.json` and `query.json` in DataFederateSystem/src/main/resources
2. run com.suda.federate.application.Main.main()

## release

1. edit `config.json` and `query.json` in DataFederateSystem/release
2. `package.sh`  or  `package.bat`
3. `run.sh` or `run.bat`

## Design

### class diagram

未完成

```mermaid
classDiagram

class SQLTranslator{
    +translate(string originalSql)
}

SQLTranslator ..> FD_Variable
SQLTranslator ..> FD_Function
    class FD_Variable{
        +string name
        +obejct value
        +variable_replace()
    }
    class FD_Function{
        +function_replace()
    }
    FD_Variable <|.. FD_INT
    FD_Variable <|.. FD_POINT

    FD_Function <|.. FD_DISTANCE
    FD_Function <|.. FD_KNN    
```

### workflow

- 从 query.json 中读取 original sql （我们定义的SQL）和 variables

- 解析 variables 生成 FD_Variable 对象
- 将 original sql 和 FD_Variable 传递给 SQLTranslator，生成翻译后的 SQL（能够被对应database直接执行的SQL）
- SQL Optimizer
- SQL Executor
- Result Memger

```mermaid
graph TD
    start([开始])
    start-->d1_input1
    start-->d1_input2
    start-->d2_input1
    start-->d2_input2

subgraph datasource1
    d1_input1[/config.json/]
    d1_input2[/query.json/]
    d1_input1-->|获取数据源配置|d1_conn[建立连接]
    d1_input2-->|获取原始SQL|d1_translator[SQL Translator]
    d1_translator-->|"翻译后的SQL"|d1_optimizer[SQL Optimizer]
    d1_optimizer-->|优化后的SQL|d1_executor[SQL Executor]
    d1_conn-->|数据源连接|d1_executor
end

subgraph datasource2
    d2_input1[/config.json/]
    d2_input2[/query.json/]
    d2_input1-->|获取数据源配置|d2_conn[建立连接]
    d2_input2-->|获取原始SQL|d2_translator[SQL Translator]
    d2_translator-->|"翻译后的SQL"|d2_optimizer[SQL Optimizer]
    d2_optimizer-->|优化后的SQL|d2_executor[SQL Executor]
    d2_conn-->|数据源连接|d2_executor
end
memger[Result Memger]
d1_executor-->|datasource1查询结果|memger
d2_executor-->|datasource2查询结果|memger
memger-->|最终结果|final_resulat[/Final Result/]
final_resulat-->end_([结束])
```

## Federate Variable & Function

定义几种需要的 variable Type，比如

- `FD_Point`：二维空间上的一个坐标

定义几种允许执行的 function，比如

- `FD_Distance (Point p1, Point p2) `: 返回 p1 和 p2 的距离
- `FD_Knn (Point p, F.loaction, k) `: 返回在 F 中 p 的 k 近邻点

query.json 示例

```json
{
  "query": "select F.id, FD_distance($P,F.location) as dis from nyc_homicides_copy where FD_distance($P F.location) < $K order by dis;",
  "variables": [
    {
      "name": "P",
      "type": "point",
      "value": "583571,4506714"
    },
    {
      "name": "K",
      "type": "int",
      "value": 100
    }
  ]
}
```


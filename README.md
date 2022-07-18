# DataFederateSystem

## Program Entry

```
com.suda.federate.application.Main.main()
```

## Requirement

* Apache Maven 3.6.0+
* Java 8
* PostgreSQL 13 + PostGIS 3.0

## Start

- debug
  1. edit `config.json` and `query.json` in DataFederateSystem/src/main/resources
  1. run com.suda.federate.application.Main.main()


- release
  1. edit `config.json` and `query.json` in DataFederateSystem/release
  1. `package.sh`  or  `package.bat``
  1. ``run.sh` or `run.bat`


## Design

### class diagram

未完成

```mermaid
classDiagram

%% class SQLTranslator{
%%     +translate(string originalSql)
%% }

%% SQLTranslator ..> FD_Variable
%% SQLTranslator ..> FD_Function

class FD_Type{
    <<interface>>
    +string translate2PostgresqlFormat()
}

class FD_Variable{
    <<abstract>>
    +string name
    +obejct value
}
class FD_Function{
    <<abstract>>
    +List~FD_Variable~ params
}

FD_Variable <|-- FD_INT
FD_Variable <|-- FD_POINT
FD_Variable <|-- FD_LINESTRING

FD_Function <|-- FD_DISTANCE
FD_Function <|-- FD_KNN
FD_Function <|-- FD_RKNN


FD_Type <|.. FD_Variable 
FD_Type <|.. FD_Function
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
    d1_input2-->|获取原始SQL|d1_expresssion[SQL Expression]
    d1_expresssion-->|"内部SQL表达式"|d1_translator[SQL Translator]
    d1_translator-->|翻译后可执行的SQL|d1_executor[SQL Executor]
    d1_conn-->|数据源连接|d1_executor
end

subgraph datasource2
    d2_input1[/config.json/]
    d2_input2[/query.json/]
    d2_input1-->|获取数据源配置|d2_conn[建立连接]
    d2_input2-->|获取原始SQL|d2_expresssion[SQL Expression]
    d2_expresssion-->|"内部SQL表达式"|d2_translator[SQL Translator]
    d2_translator-->|翻译后可执行的SQL|d2_executor[SQL Executor]
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

- 基础数据类型
  - `FD_Int`
  - `FD_String`
  - `FD_Double`

- 空间数据类型
  - `FD_Point`：二维空间上的一个坐标，使用空格隔开： `"value":"583571 4506714"`
  - `FD_Line`：多个 FD_Point 构成的集合，使用逗号隔开：`"value":"588881 4506445, 590946 4521077, 5941796 4503794, 600689 4506179, 578274 4499580"`


定义几种允许执行的 function，比如

- `FD_Distance (Point p1, Point p2) `: 返回 p1 和 p2 的距离

- `FD_KNN (Point p, F.loaction, k) `: 返回在 F 中 p 的 k 近邻点
- `FD_RKNN`
- ~~FD_RangeCount~~
- ~~FD_RangeSearch~~

## 查询示例

相关说明

- 支持单个查询（json格式），多个查询（json_array格式）[JSON在线解析及格式化验证 - JSON.cn](https://www.json.cn/#)

- query 字段中使用 $var_name 表示一个变量

- variables 字段中支持的 type 为 ENUM.FD_DATA_TYPE 中所定义的枚举类型。（与Federate Variable一一对应）

测试表（共3982条数据）：

- 10.10.64.117:54321/gis
  - nyc_data
  - `DELETE from nyc_data where id > 2000`
- 10.10.64.117:54322/gis
  - nyc_data
  - `DELETE from nyc_data where id <= 2000`

测试表格式

| id   | location                                 |
| ---- | ---------------------------------------- |
| 1    | POINT(592158.665764157 4502210.89236731) |
| 2    | POINT(588654.951612275 4517855.38265668) |
| 3    | POINT(605800.81502458 4505730.60839577)  |





FD_Distance

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







```json
{
    "query":"select id, location from nyc_homicides_copy where FD_Contains(ST_GeomFromText(LINESTRING (poly_point_set)), location);",
    "variables":[
      {
        "name":"poly_point_set",
        "type":"lineString",
        "value":"588881 4506445, 590946 4521077, 5941796 4503794, 600689 4506179, 578274 4499580"
      }
    ]
  }
```






# DataFederateSystem

## Program entry

```
com.suda.federate.application
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
2.  `package.sh`  or  `package.bat`
3.  `run.sh` or `run.bat`

General process

```
user --> query.json -> original sql -> SQL translator --> SQL optimator -> SQL Executor -> driver -> datasource

user <- secure merger <- results <- datasource
```

## Federate Type



## Federate Funtion

- DF_distance (Point p1, Point p2) : return the distance between p1 and p2.


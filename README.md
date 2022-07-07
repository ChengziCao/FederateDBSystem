# DataFederateSystem

程序入口：

```
com.suda.federate.application
```


大致流程：

```

cli -> raw sql -> optimized sql -> driver -> datasource

cli <- secure merger <- results <- datasource


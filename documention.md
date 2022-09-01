# 环境配置

## 容器环境

Docker 安装：[Home - Docker](https://www.docker.com/)

### PostGIS

```
docker run -it --name postgis1 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123465 -e ALLOW_IP_RANGE=0.0.0.0/0 -p 54322:5432 -d kartoza/postgis:13-3
```



### MySQL

```
docker run -itd --name mysql1 -p 33061:3306 -e MYSQL_ROOT_PASSWORD=123465 mysql
```



```mysql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for osm_sh
-- ----------------------------
DROP TABLE IF EXISTS `osm_sh`;
CREATE TABLE `osm_sh`  (
  `id` bigint NOT NULL,
  `location` geometry NOT NULL,
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `timestamp` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

## 数据配置

测试数据库（共4w条数据）：

- `jdbc:mysql://127.0.0.1:33061/gis`
  - osm_sh
  - DELETE from osm_sh where id <= 4963651393;
- `jdbc:postgresql://127.0.0.1:54322/gis`
  - osm_sh
  - DELETE from osm_sh where id > 4963651393;

| id        | location                      | user       | timestamp           |
| --------- | ----------------------------- | ---------- | ------------------- |
| 172817255 | POINT(31.2030452 121.3360092) | XD346      | 2021-03-30 17:00:23 |
| 172817276 | POINT(31.1820961 121.337143)  | Austin Zhu | 2017-01-20 08:48:04 |
| 172817294 | POINT(31.1992382 121.3362152) | XD346      | 2021-03-30 17:00:23 |

# 运行方法



## 相关规范说明

- 支持单个查询（json格式），多个查询（jsonArray格式）[JSON在线解析及格式化验证 - JSON.cn](https://www.json.cn/#)
- 函数名称，大小写不敏感，RangeCount 或者 FD_RangeCount 均可。
- 参数类型，大小写不敏感，int 或 FD_int 均可。


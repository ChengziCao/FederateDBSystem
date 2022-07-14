package com.suda.federate.driver;

/**
 * 单例模式，一种数据库 全局只需要一个 driver 即可
 */
public class FederateDBFactory {

    public static PostgresqlDriver postgresqlDriver = null;

    public static FederateDBDriver getDriverInstance(String type) {
        return postgresqlDriver == null ? postgresqlDriver = new PostgresqlDriver() : postgresqlDriver;
        // if (ENUM.equals(type, ENUM.FD_DATABASE.POSTGRESQL)) {
        //     return postgresqlDriver == null ? postgresqlDriver=new PostgresqlDriver() : postgresqlDriver;
        // } else if (ENUM.equals(type, ENUM.FD_DATABASE.MYSQL)) {
        //     return null;
        // } else {
        //     return null;
        // }
    }

}

package com.suda.federate.config;

import com.suda.federate.utils.ENUM;
import com.suda.federate.utils.ENUM.DATABASE;

public class DbConfig {
    String name;
    String driver;
    String user;
    String password;
    String url;
    DATABASE type;

    public DbConfig(String name, String driver, String user, String password, String url, String type) {
        this.name = name;
        this.driver = driver;
        this.user = user;
        this.password = password;
        this.url = url;
        this.type = ENUM.str2DATABASE(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DATABASE getType() {
        return type;
    }

    public void setType(DATABASE type) {
        this.type = type;
    }
}

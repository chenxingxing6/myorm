package org.apache.ibatis.config;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:59
 */
public class BatisConfig {
    public static String jdbcDriver;
    public static String jdbcUrl;
    public static String username;
    public static String password;
    public static Integer initConnectCount;
    public static Integer maxConnects;
    public static Integer incrementCount;

    public static String getJdbcDriver() {
        return jdbcDriver;
    }

    public static void setJdbcDriver(String jdbcDriver) {
        BatisConfig.jdbcDriver = jdbcDriver;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        BatisConfig.jdbcUrl = jdbcUrl;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        BatisConfig.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        BatisConfig.password = password;
    }

    public static Integer getInitConnectCount() {
        return initConnectCount;
    }

    public static void setInitConnectCount(Integer initConnectCount) {
        BatisConfig.initConnectCount = initConnectCount;
    }

    public static Integer getMaxConnects() {
        return maxConnects;
    }

    public static void setMaxConnects(Integer maxConnects) {
        BatisConfig.maxConnects = maxConnects;
    }

    public static Integer getIncrementCount() {
        return incrementCount;
    }

    public static void setIncrementCount(Integer incrementCount) {
        BatisConfig.incrementCount = incrementCount;
    }
}

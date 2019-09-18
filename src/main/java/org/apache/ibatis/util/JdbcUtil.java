package org.apache.ibatis.util;

import org.apache.ibatis.config.BatisConfig;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

/**
 * User: lanxinghua
 * Date: 2019/9/18 20:55
 * Desc: jdbc工具类
 */
public class JdbcUtil {
    public static BatisConfig batisConfig;
    private static boolean autoCommit = false;
    private static Connection conn;
    private static Properties properties;

    static {
        initConfig();
    }

    private static void initConfig(){
        if (properties == null){
            InputStream is = JdbcUtil.class.getResourceAsStream("/application.properties");
            try {
                properties = new Properties();
                properties.load(is);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (is != null){
                    try {
                        is.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        batisConfig.jdbcDriver = properties.getProperty("config.jdbcDriver");
        batisConfig.jdbcUrl = properties.getProperty("config.jdbcUrl");
        batisConfig.userName = properties.getProperty("config.userName");
        batisConfig.password = properties.getProperty("config.password");
        batisConfig.initConnectCount = Integer.valueOf(properties.getProperty("config.initConnectCount"));
        batisConfig.maxConnects = Integer.valueOf(properties.getProperty("config.maxConnects"));
        batisConfig.incrementCount = Integer.valueOf(properties.getProperty("config.incrementCount"));
    }

    public static Connection getConn(){
        if (isValid()){
            loadDriver();
            try {
                conn = DriverManager.getConnection(batisConfig.jdbcUrl, batisConfig.userName, batisConfig.password);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void transaction(){
        try {
            conn.setAutoCommit(autoCommit);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Statement statement(){
        Statement statement = null;
        getConn(); // 获取连接
        transaction();    // 设置默认的提交事务
        try{
            statement = conn.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
        return statement;
    }

    public static PreparedStatement preparedStatement(String sql){
        PreparedStatement ps = null;
        getConn();
        transaction();
        try{
            ps = conn.prepareStatement(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ps;
    }

    /**
     * 查询
     * @param sql
     * @param params
     * @return
     */
    public static ResultSet query(String sql, List<Object> params, Connection c){
        if (c != null){
            conn = c;
        }
        if (sql == null || sql.trim().isEmpty() || !sql.trim().toLowerCase().startsWith("select")){
            throw new RuntimeException("你的SQL语句为空或不是查询语句");
        }
        ResultSet resultSet = null;
        try{
            if (params.size() >0){
                PreparedStatement ps = preparedStatement(sql);
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                resultSet = ps.executeQuery();
            }else {
                Statement statement = statement();
                resultSet = statement.executeQuery(sql);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static boolean execute(String sql, List<Object> params){
        if (sql == null || sql.trim().isEmpty() || sql.trim().toLowerCase().startsWith("select")){
            throw new RuntimeException("你的SQL语句为空或有误");
        }
        boolean result = false;
        sql = sql.trim();
        sql = sql.toLowerCase();
        String prefix = sql.substring(0, sql.indexOf(" "));

        if (params.size() >0){
            PreparedStatement ps = preparedStatement(sql);
            Connection conn = null;
            try {
                conn = ps.getConnection();
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, typeof(params.get(i)));
                }
                ps.executeUpdate();
                commit(conn);
                result = true;
            }catch (Exception e){
                System.out.println(prefix + "执行失败 " + e.getMessage());
                rallback(conn);
            }
        }else {
            Statement st = statement();
            Connection conn = null;
            try {
                conn = st.getConnection();
                st.executeUpdate(sql);
                commit(conn);
                result = true;
            }catch (Exception e){
                System.out.println(prefix + "执行失败 " + e.getMessage());
                rallback(conn);
            }
        }

        return result;
    }



    /**
     * 加载驱动
     */
    private static void loadDriver(){
        try {
            Class.forName(batisConfig.jdbcDriver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 检查缓存的连接是否不可以被使用 ，不可以被使用true
     * @return
     */
    private static boolean isValid(){
        if (conn!=null) {
            try{
                if (conn.isClosed() || !conn.isValid(3)){
                    return true;
                }else {
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 提交事务
     * @param c
     */
    private static void commit(Connection c){
        try{
            if (c !=null && !autoCommit){
                c.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 回滚
     */
    private static void rallback(Connection c){
        try{
            if (c != null && !autoCommit){
                c.rollback();
                System.out.println("回滚");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     **/
    public static void release(Object cloaseable) {
        if (cloaseable != null) {
            if (cloaseable instanceof ResultSet) {
                ResultSet rs = (ResultSet) cloaseable;
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cloaseable instanceof Statement) {
                Statement st = (Statement) cloaseable;
                try {
                    st.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cloaseable instanceof Connection) {
                Connection c = (Connection) cloaseable;
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断数据类型
     */
    private static Object typeof(Object o) {
        Object r = o;
        if (o instanceof java.sql.Timestamp) {
            return r;
        }
        // 将 java.util.Date 转成 java.sql.Date
        if (o instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) o;
            return new java.sql.Date(d.getTime());
        }
        // 将 Character 或 char 变成 String
        if (o instanceof Character || o.getClass() == char.class) {
            return String.valueOf(o);
        }
        return r;
    }
}

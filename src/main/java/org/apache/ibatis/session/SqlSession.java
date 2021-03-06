package org.apache.ibatis.session;

import org.apache.ibatis.executor.Executor;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * @Author: cxx
 * @Date: 2019/9/18 13:00
 */
public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;
    private Connection connection;
    private Executor executor;
    private boolean isUse = false;

    public SqlSession(SqlSessionFactory factory, Connection connection, boolean isUse) {
        this.sqlSessionFactory = factory;
        this.connection = connection;
        this.executor = this.sqlSessionFactory.getExecutor(this);
        this.isUse = isUse;
    }

    public <T> T run(Method method, Object[] args){
        this.isUse = true;
        return executor.run(method, args, connection);
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }
}

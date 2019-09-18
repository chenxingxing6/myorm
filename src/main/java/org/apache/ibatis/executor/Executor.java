package org.apache.ibatis.executor;

import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;

/**
 * @Author: cxx
 * @Date: 2019/9/18 13:04
 */
public class Executor {
    private SqlSession sqlSessionProxy;


    public <T> T selectOne(String id, Object paramter, Connection connection){
        return null;
    }
}

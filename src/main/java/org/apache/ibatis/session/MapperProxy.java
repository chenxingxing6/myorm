package org.apache.ibatis.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Role: lanxinghua
 * Date: 2019/9/18 14:48
 * Desc:
 */
public class MapperProxy implements InvocationHandler {
    private SqlSession sqlSession;

    public MapperProxy(SqlSession sqlSession){
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return sqlSession.run(method, args);
    }
}

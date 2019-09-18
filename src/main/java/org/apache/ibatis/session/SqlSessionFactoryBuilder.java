package org.apache.ibatis.session;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:54
 * SqlSessionFactory工厂构建中
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(String packageName){
        SqlSessionFactory sqlSessionFactory = SqlSessionFactory.getInstance();
        sqlSessionFactory.build(packageName);
        return sqlSessionFactory;
    }
}

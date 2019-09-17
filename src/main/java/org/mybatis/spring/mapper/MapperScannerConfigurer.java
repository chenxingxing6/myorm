package org.mybatis.spring.mapper;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:46
 */
public class MapperScannerConfigurer {
    private String basePackage;

    private String sqlSessionFactoryBeanName;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getSqlSessionFactoryBeanName() {
        return sqlSessionFactoryBeanName;
    }

    public void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
        this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
    }
}

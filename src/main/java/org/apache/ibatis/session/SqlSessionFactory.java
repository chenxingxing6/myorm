package org.apache.ibatis.session;

import javax.sql.DataSource;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:39
 */
public class SqlSessionFactory {
    private static SqlSessionFactory instance;

    private String configLocation;

    private DataSource dataSource;

    private String[] mapperLocations;

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }
}

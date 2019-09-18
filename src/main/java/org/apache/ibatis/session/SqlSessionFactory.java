package org.apache.ibatis.session;

import org.apache.ibatis.Function;
import org.apache.ibatis.config.BatisConfig;
import org.apache.ibatis.executor.Executor;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:39
 */
public class SqlSessionFactory {
    private static SqlSessionFactory instance;
    private ClassLoader classLoader;
    private String mapperPackage;
    private List<String> mapperClassNames;
    private List<SqlSession> sqlSessions;
    private Map<String, Function> functionMap;
    private BatisConfig config;

    private String configLocation;

    private DataSource dataSource;

    private String[] mapperLocations;

    private SqlSessionFactory(){}

    public static SqlSessionFactory getInstance() {
        if (instance == null){
            return createInstance();
        }else {
            return instance;
        }
    }

    private static synchronized SqlSessionFactory createInstance(){
        if (instance == null){
            return new SqlSessionFactory();
        }
        return instance;
    }

    public void build(String packageName){
        if (this.classLoader == null){
            this.classLoader = ClassLoader.getSystemClassLoader();
        }
        this.mapperPackage = this.classLoader.getResource("").getPath() + packageName.replaceAll(".", "/");
    }

    public Executor getExecutor(SqlSession sqlSession){
        return null;
    }

    public void addMapper(Function function){
        functionMap.put(function.getFunctionName(), function);
    }

    private void init() {
        this.functionMap = new HashMap<>();
        this.sqlSessions = new ArrayList<>();

        // 全局配置
        config = new BatisConfig();
        config.setJdbcUrl("");
        config.setUsername("");
        config.setPassword("");
        config.setJdbcDriver("");

        // 遍历mapper
        doScanClass(mapperPackage);
        initMapper();
    }

    private void initMapper(){
        if (mapperClassNames.isEmpty()){
            return;
        }
        for (String mapperClassName : mapperClassNames) {

        }
    }

    /**
     * 扫描mapper接口
     * @param mapperPackage
     */
    private void doScanClass(String mapperPackage){
        try {
            URL url = this.getClass().getClassLoader().getResource("/" + mapperPackage.replaceAll(".", "/"));
            File file = new File(url.getFile());
            for (File f : file.listFiles()) {
                if (f.isDirectory()){
                    doScanClass(mapperPackage + "." + f.getName());
                }else {
                    String className = mapperPackage + "." + f.getName().replace(".class", "");
                    this.mapperClassNames.add(className);
                }
            }
        }catch (Exception e){

        }

    }

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

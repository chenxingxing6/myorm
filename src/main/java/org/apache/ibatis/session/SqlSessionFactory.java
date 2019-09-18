package org.apache.ibatis.session;

import org.apache.ibatis.Function;
import org.apache.ibatis.config.BatisConfig;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.util.JdbcUtil;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.management.RuntimeOperationsException;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:39
 */
public class SqlSessionFactory {
    private static SqlSessionFactory instance;
    private ClassLoader classLoader;
    private String mapperPackage;
    private List<String> mapperxmls;
    private List<SqlSession> sqlSessions;
    private Map<String, Function> functionMap;

    private int maxConnects = 100;
    private int incrementCount = 10;

    private SqlSessionFactory(){}

    public static SqlSessionFactory getInstance() {
        if (instance == null){
            return createInstance();
        }else {
            return instance;
        }
    }

    private void init() {
        this.functionMap = new HashMap<>();
        this.sqlSessions = new ArrayList<>();
        this.mapperxmls = new ArrayList<>();

        // 遍历mapper
        doScanClass(mapperPackage);

        initMapper();
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
        this.mapperPackage = packageName;
    }

    public Executor getExecutor(SqlSession sqlSession){
        return new Executor(this.functionMap, sqlSession);
    }

    public void addMapper(Function function){
        functionMap.put(function.getFunctionName(), function);
    }

    public SqlSession getSqlSession(){
        if (this.functionMap == null){
            init();
        }
        while (true){
            for (SqlSession sqlSession : sqlSessions) {
                if (!sqlSession.isUse()){
                    Connection conn = sqlSession.getConnection();
                    try {
                        if (!conn.isValid(3)){
                            sqlSession.setConnection(JdbcUtil.getConn());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                sqlSession.setUse(true);
                return sqlSession;
            }
            // 根据配置是否增加对应数量的连接
            if (sqlSessions.size() <= maxConnects - incrementCount) {
                createSqlSession(incrementCount);
            }else if ((sqlSessions.size() < maxConnects) && (sqlSessions.size() > maxConnects - incrementCount)){
                createSqlSession(maxConnects - sqlSessions.size());
            }
        }
    }

    private void createSqlSession(int count){
        if (maxConnects > 0 && maxConnects < sqlSessions.size()){
            throw new RuntimeException("超出最大的连接数");
        }
        try {
            for (int i = 0; i < count; i++) {
                sqlSessions.add(new SqlSession(this, JdbcUtil.getConn(), false));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T>T getMapper(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MapperProxy(this.getSqlSession()));
    }


    private void initMapper(){
        if (mapperxmls.isEmpty()){
            return;
        }
        for (String mappxmlPath : mapperxmls) {
            this.getMapper(mappxmlPath);
        }
    }

    private void getMapper(String xmlPath){
        try {
            Element root = new SAXReader().read(new FileInputStream(xmlPath)).getRootElement();
            for(Iterator iterator = root.elementIterator(); iterator.hasNext();){
                Function function = new Function();
                Element element = (Element) iterator.next();
                function.setFunctionName(element.attributeValue("id").trim());
                function.setResultType(element.attributeValue("resultType".trim()));
                function.setParameterType(element.attributeValue("parameterType".trim()));
                function.setSql(element.getText().trim());
                function.setSqlType(element.getName().trim());
                this.addMapper(function);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 扫描mapper接口
     * @param mapperPackage
     */
    private void doScanClass(String mapperPackage){
        try {
            URL url = this.getClass().getResource("/" + mapperPackage.replace(".", "/"));
            File file = new File(url.getPath());
            for (File f : file.listFiles()) {
                if (f.isDirectory()){
                    doScanClass(mapperPackage + "." + f.getName());
                }else {
                    this.mapperxmls.add(f.getPath());
                }
            }
        }catch (Exception e){

        }
    }
}

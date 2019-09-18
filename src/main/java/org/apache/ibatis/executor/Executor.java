package org.apache.ibatis.executor;

import javafx.util.Pair;
import org.apache.ibatis.Function;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.util.JdbcUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: cxx
 * @Date: 2019/9/18 13:04
 */
public class Executor {
    private SqlSession sqlSessionProxy;
    private Map<String, Function> functionMap;
    private static Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");

    public Executor(Map<String, Function> functionMap, SqlSession sqlSession){
        this.functionMap = functionMap;
        this.sqlSessionProxy = sqlSession;
    }

    public <T> T run(Method method, Object[] args, Connection connection){
        Function function = functionMap.get(method.getName());
        if (function == null){
            throw new RuntimeException("MapperXml配置文件有误");
        }
        try {
            Map<Integer/*位置序号*/, Pair<String/*key*/, String/*type*/>> indexParamMap = new HashMap<>();
            List<String> annotationParams = new ArrayList<>();
            Annotation[][] annotations = method.getParameterAnnotations();
            for (Annotation[] annotation : annotations) {
                for (Annotation a : annotation) {
                    if (a instanceof Param){
                        Param param = (Param) a;
                        annotationParams.add(param.value());
                    }
                }
            }
            if (!annotationParams.isEmpty()){
                if (annotationParams.size() != method.getParameterTypes().length){
                    throw new RuntimeException("参数个数不匹配");
                }
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    String typeName = method.getParameterTypes()[i].getTypeName();
                    Pair<String, String> pair= new Pair<>(annotationParams.get(i), typeName);
                    indexParamMap.put(i+1, pair);
                }
                function.setIndexParamMap(indexParamMap);
            }
            Matcher matcher = pattern.matcher(function.getSql());
            while (matcher.find()){
                String key = matcher.group(1);
            }
            String sql = function.getSql().replaceAll("\\#\\{(.+?)\\}", "?");
            List<Object> params = new ArrayList<>();
            params.addAll(Arrays.asList(args));
            ResultSet resultSet = JdbcUtil.query(sql, params, connection);
            Class clazz = Class.forName(function.getResultType());
            this.sqlSessionProxy.setUse(true);
            return (T)handler(resultSet, clazz);
        }catch (Exception e){
            e.printStackTrace();
            this.sqlSessionProxy.setUse(false);
            return null;
        }
    }

    private Object handler(ResultSet rs, Class<?> clazz){
        Object obj = null;
        try {
            while (rs.next()){
                // 反射构建返回对象
                obj = clazz.newInstance();
                // 获取结果集的数据源
                ResultSetMetaData rsmeta = rs.getMetaData();
                // 获取结果集中字段树
                int count = rsmeta.getColumnCount();

                // 遍历
                for (int i = 0; i < count; i++) {
                    try {
                        String columnName = rsmeta.getColumnName(i + 1);
                        String javaPropertieName = lineToHump(columnName);
                        Field field = obj.getClass().getDeclaredField(javaPropertieName);
                        field.setAccessible(true);
                        field.set(obj, rs.getObject(columnName));
                    }catch (Exception e){
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            return obj;
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    public static String lineToHump(String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

package org.apache.ibatis.executor;

import javafx.util.Pair;
import org.apache.ibatis.Function;
import org.apache.ibatis.annotations.*;
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
    private static String regex = "\\#\\{(.+?)\\}";
    private static Pattern pattern = Pattern.compile(regex);

    public Executor(Map<String, Function> functionMap, SqlSession sqlSession){
        this.functionMap = functionMap;
        this.sqlSessionProxy = sqlSession;
    }

    public <T> T run(Method method, Object[] args, Connection connection){
        // 注解优先使用@Select @Insert @Delete @Update
        if (method.getAnnotations().length !=0 && method.getAnnotations().length > 1){
            throw new RuntimeException("该方法上只能有一个注解");
        }
        if (method.getAnnotations().length == 1){
            Function function = new Function();
            String sql = "";
            String sqlType = "";
            if (method.isAnnotationPresent(Insert.class)){
                Insert insert = method.getAnnotation(Insert.class);
                sql = insert.value();
                sqlType = "insert";

            }else if (method.isAnnotationPresent(Delete.class)){
                Delete insert = method.getAnnotation(Delete.class);
                sql = insert.value();
                sqlType = "delete";
            }else if (method.isAnnotationPresent(Update.class)){
                Update insert = method.getAnnotation(Update.class);
                sql = insert.value();
                sqlType = "update";
            }else if (method.isAnnotationPresent(Select.class)){
                Select insert = method.getAnnotation(Select.class);
                sql = insert.value();
                sqlType = "select";
            }
            function.setSql(sql);
            function.setFunctionName(method.getName());
            function.setParameterType("");
            function.setSqlType(sqlType);
            function.setResultType(method.getReturnType().getTypeName());
            functionMap.put(method.getName(), function);
        }

        Function function = functionMap.get(method.getName());
        if (function == null){
            throw new RuntimeException("注解或Xml配置文件有误");
        }
        try {
            function.setParamMap(getParamsMap(method, args));

            List<Object> parseArgs = new ArrayList<>();
            Matcher matcher = pattern.matcher(function.getSql());
            Map<String, Object> paramMap = function.getParamMap();

            // mapperxml参数个数
            int xmlParamCount = 0;
            while (matcher.find()){
                xmlParamCount++;
            }

            int i = 0;
            matcher = pattern.matcher(function.getSql());
            while (matcher.find()){
                String key = matcher.group(1);
                String prefix = key.contains(".") == true ? key.split("\\.")[0] : null;
                key = key.contains(".") == true ? key.split("\\.")[1] : key;
                if (paramMap.get(key) !=null){
                    parseArgs.add(paramMap.get(key));
                }else {
                    // 参数为对象，和xml里面的字段一一映射
                    if (paramMap.entrySet().size() != xmlParamCount){
                        try {
                            Object obj = paramMap.get(prefix);
                            Field field = obj.getClass().getDeclaredField(key);
                            field.setAccessible(true);
                            parseArgs.add(field.get(obj));
                        }catch (Exception e){
                            e.printStackTrace();
                            parseArgs.add(null);
                            continue;
                        }

                    }else {
                        parseArgs.add(paramMap.get(i + ""));
                    }
                }
                i++;
            }
            String sql = function.getSql().replaceAll(regex, "?");
            if (!isQuery(function.getSqlType())){
                this.sqlSessionProxy.setUse(true);
                return (T) JdbcUtil.execute(sql, parseArgs, connection);
            }
            ResultSet resultSet = JdbcUtil.query(sql, parseArgs, connection);
            Class clazz = Class.forName(function.getResultType());
            this.sqlSessionProxy.setUse(true);
            return (T)handlerResult(resultSet, clazz);
        }catch (Exception e){
            e.printStackTrace();
            this.sqlSessionProxy.setUse(false);
            return null;
        }
    }

    private boolean isQuery(String sqlType){
        sqlType = sqlType.toLowerCase();
        switch (sqlType){
            case "create":
            case "alter":
            case "drop":
            case "truncate":
            case "insert":
            case "update":
            case "delete":{
                return false;
            }
            case "select":{
                return true;
            }
            default:{
                break;
            }
        }
        return false;
    }

    /**
     * 获取参数Map（@param进行解析）
     * @return
     */
    private Map<String, Object> getParamsMap(Method method, Object[] args){
        // 该集合用于记录参数索引与参数名称的对应关系
        final SortedMap<Integer, String> map = new TreeMap<Integer, String>();
        // 获取所有@Param注解
        String name = null;
        Annotation[][] annotations = method.getParameterAnnotations();

        // 遍历方法参数
        int paramIndex = 0;
        for (Annotation[] annotation : annotations) {
            for (Annotation a : annotation) {
                if (a instanceof Param){
                    Param param = (Param) a;
                    name = param.value();
                }
            }
            // 没有@param注解 (0,1,2)
            if (name == null){
                name = String.valueOf(map.size());
            }
            map.put(paramIndex, name);
            paramIndex ++;
        }

        // 参数个数
        int paramCount = map.entrySet().size();
        Map<String, Object> param = new HashMap<>();
        if(args == null || paramCount == 0){
        }else {
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                param.put(entry.getValue(), args[entry.getKey()]);
            }
        }
        return param;
    }


    /**
     * 返回值进行处理
     * @param rs
     * @param clazz
     * @return
     */
    private Object handlerResult(ResultSet rs, Class<?> clazz){
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

    /**
     * 下划线转驼峰
     */
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

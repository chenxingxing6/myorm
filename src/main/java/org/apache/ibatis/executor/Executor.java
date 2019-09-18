package org.apache.ibatis.executor;

import com.test.entry.User;
import org.apache.ibatis.Function;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: cxx
 * @Date: 2019/9/18 13:04
 */
public class Executor {
    private SqlSession sqlSessionProxy;
    private Map<String, Function> functionMap;

    public Executor(Map<String, Function> functionMap, SqlSession sqlSession){
        this.functionMap = functionMap;
        this.sqlSessionProxy = sqlSession;
    }

    public <T> T selectOne(String id, Object parameter, Connection connection){
        Function function = functionMap.get(id);
        try {
            /*String sql = "select * from sys_role";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            */
            String sql = function.getSql().replace("#{id}", "?");
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, parameter.toString());
            ResultSet resultSet = ps.executeQuery();

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

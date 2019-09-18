package org.apache.ibatis.executor;

import com.test.entry.User;
import org.apache.ibatis.Function;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

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

            // 反射构建返回对象
            Object resultObject = Class.forName(function.getResultType()).newInstance();
            User user = new User();
            user.setName("lxh");
            user.setAge(22);
            resultObject = user;

            this.sqlSessionProxy.setUse(true);
            return (T) resultObject;

        }catch (Exception e){
            e.printStackTrace();
            this.sqlSessionProxy.setUse(false);
            return null;
        }
    }
}

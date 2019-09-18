package com.demo;

import com.alibaba.fastjson.JSON;
import com.test.entry.User;
import com.test.mapper.UserMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import javax.annotation.Resource;
import java.lang.ref.WeakReference;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:42
 */
public class MapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void test01(){
        User user = userMapper.getUserName("1000");
        System.out.println(JSON.toJSONString(user));
    }

    @Test
    public void test02(){
        // //使用弱引用创建SqlSessionFactoryBuilder,保证下次GC时回收该对象。
        WeakReference<SqlSessionFactoryBuilder> builder = new WeakReference<>(new SqlSessionFactoryBuilder());

        SqlSessionFactory factory = builder.get().build("mapper");
        UserMapper userMapper = factory.getMapper(UserMapper.class);
        User user = userMapper.getUserName("100");
        System.out.println(JSON.toJSONString(user));
    }
}

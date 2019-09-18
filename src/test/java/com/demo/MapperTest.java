package com.demo;

import com.alibaba.fastjson.JSON;
import com.test.entry.Role;
import com.test.mapper.RoleMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.lang.ref.WeakReference;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:42
 */
public class MapperTest {

    @Test
    public void test01(){
        // //使用弱引用创建SqlSessionFactoryBuilder,保证下次GC时回收该对象。
        WeakReference<SqlSessionFactoryBuilder> builder = new WeakReference<>(new SqlSessionFactoryBuilder());

        SqlSessionFactory factory = builder.get().build("mapper");
        RoleMapper roleMapper = factory.getMapper(RoleMapper.class);
        Role role = roleMapper.getRoleById(1L);
        System.out.println(JSON.toJSONString(role));
    }
}

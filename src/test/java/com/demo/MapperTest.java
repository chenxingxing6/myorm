package com.demo;

import com.alibaba.fastjson.JSON;
import com.test.entry.Role;
import com.test.mapper.RoleMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // Role role1 = roleMapper.getRoleById(1L);
        // System.out.println(JSON.toJSONString(role1));

        Role role2 = roleMapper.getRoleByIdAndDeptId(1L, 34L);
        System.out.println(JSON.toJSONString(role2));
    }


    public static void main(String[] args) {
        String sql = "select * from sys_role where role_id = #{id} and dept_id = #{dept_id}";
        String regex = "\\#\\{(.+?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()){
            String key = matcher.group(1);
            System.out.println(key);
        }
    }
}

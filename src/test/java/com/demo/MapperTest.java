package com.demo;

import com.alibaba.fastjson.JSON;
import com.test.entry.Role;
import com.test.mapper.RoleMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:42
 */
public class MapperTest {
    private RoleMapper roleMapper;

    @Before
    public void init(){
        // 使用弱引用创建SqlSessionFactoryBuilder,保证下次GC时回收该对象。
        WeakReference<SqlSessionFactoryBuilder> builder = new WeakReference<>(new SqlSessionFactoryBuilder());
        String mapxmlPath = "mapper";
        SqlSessionFactory factory = builder.get().build(mapxmlPath);
        roleMapper = factory.getMapper(RoleMapper.class);
    }

    /**
     * 查询（普通 & 有@param注解）
     */
    @Test
    public void test01(){
        Role role1 = roleMapper.getRoleById(1L);
        System.out.println("普通方式："+JSON.toJSONString(role1));

        Role role2 = roleMapper.getRoleByIdAndDeptId(1L, 34L);
        System.out.println("@Param注解："+JSON.toJSONString(role2));
    }

    /**
     * 删除
     */
    @Test
    public void test02(){
        int result = roleMapper.deleteById(36L);
        System.out.println(result >= 1 ? "删除成功" : "删除失败");
    }

    /**
     * 插入
     */
    @Test
    public void test03(){
        Role role = new Role();
        role.setRoleId(Long.valueOf(new Random().nextInt(100)));
        role.setDeptId(1L);
        role.setRemark("remark");
        role.setRoleName("roleName");
        role.setCreateTime(new Date());
        int result = roleMapper.insert(role);
        System.out.println(result >= 1 ? "插入成功" : "插入失败");
    }

    /**
     * 修改
     */
    @Test
    public void test04(){
        int result = roleMapper.updateRoleName(10L, "update remark");
        System.out.println(result >= 1 ? "修改成功" : "修改失败");
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

        Role role = new Role();
        role.setRoleId(Long.valueOf(new Random().nextInt(100)));
        role.setDeptId(1L);
        role.setRemark("remark");
        role.setRoleName("roleName");
        role.setCreateTime(new Date());
        try {
            Field field = role.getClass().getDeclaredField("roleId");
            field.setAccessible(true);
            System.out.println(field.get(role));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}



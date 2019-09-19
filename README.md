## 手写ORM

---
#### 一、主要完成的功能【基本CURD是可以支持的】
> 1.通过自己实现的ORM,进行增删改查demo   
> 2.@Param注解解析，支持注解到对象和基本数据类型  
> 3.用dtd文件定义Mapper.xml文档的合法构建模块   
> 4.根据resultType，对结果进行处理    
> 5.除了xml配置方式外，新加注解方式@Select @Insert @Delete @Update


---

#### 二、Mybatis原理
###### 我们先复习一下Mybatis的实现原理：
1.SqlSessionFactory是线程安全的   
2.qlSession是单线程对象，因为它是非线程安全的

![avatar](https://raw.githubusercontent.com/chenxingxing6/myorm/master/img/1.jpg)

流程描述：  
> 1.加载Mybatis全局配置文件并解析，生成Configuration对象和MapperdStatement  
2.SqlSessionFactoryBuilder通过Configuration对象构建SqlSessionFactory  
3.通过SqlSessionFactory获取sqlSession  
4.sqlSession和数据库进行交互

---

#### 三、MyORM实现
![avatar](https://raw.githubusercontent.com/chenxingxing6/myorm/master/img/2.jpg)

我的实现思路
> 1.解析配置文件，初始化数据库连接，创建sqlSession池，交给SqlSessionFactory管理   
2.创建Execute,底层调用JDBC操作数据库   
3.创建MapperProxy代理对象，动态代理Mapper接口   
4.大体架子搭建好后，可以继续完善，比如@Param注解.       
5.测试就直接使用单测测试就可以了

---

#### 四、MyORM项目结构
![avatar](https://raw.githubusercontent.com/chenxingxing6/myorm/master/img/3.jpg)

---
```sql
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色'
```

```sql
INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (1, '超级管理员', '最高权限', 34, '2018-07-31 19:27:42');
INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (2, '管理员', '权限比较少', 9, '2018-07-31 19:28:58');
INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (3, 'IT经理', 'IT用户使用', 34, '2018-12-30 22:42:15');
```

---

##### 4.1 增删改查Demo

![avatar](https://raw.githubusercontent.com/chenxingxing6/myorm/master/img/4.jpg)

---

![avatar](https://raw.githubusercontent.com/chenxingxing6/myorm/master/img/5.jpg)


---

```java
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
}
```


---
##### 4.2 RoleMapper.java
```java
package com.test.mapper;

import com.test.entry.Role;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:48
 */
public interface RoleMapper {
    public Role getRoleById(Long id);

    public Role getRoleByIdAndDeptId(Long id, @Param("deptId") Long deptId);

    public int deleteById(Long id);

    public int insert(@Param("role") Role role);

    public int updateRoleName(@Param("roleId") Long roleId, @Param("roleName") String name);
}
```


---
##### 4.3 RoleMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper SYSTEM "myorm.dtd">
<mapper namespace="com.test.mapper.RoleMapper">
  <select id="getRoleById" parameterType="java.lang.Long" resultType ="com.test.entry.Role">
    select * from sys_role where role_id = #{id}
  </select>

  <select id="getRoleByIdAndDeptId" resultType ="com.test.entry.Role">
    select * from sys_role where role_id = #{id} and dept_id = #{deptId}
  </select>

  <delete id="deleteById" parameterType="java.lang.Long">
    delete from sys_role where role_id = #{id}
  </delete>

  <insert id="insert">
    insert into sys_role (role_id, role_name, remark, dept_id, create_time)
    values (#{role.roleId}, #{role.roleName}, #{role.remark}, #{role.deptId}, #{role.createTime})
  </insert>

  <update id="updateRoleName">
    update sys_role set role_name = #{roleName} where role_id = #{roleId}
  </update>
</mapper>
```
注意：@Param()中的value一定要和实体对象Role字段相同。


---
##### 4.4 myorm.dtd 对mapperxml文档的合法构建（简单使用一下）
```dtd
<!ELEMENT mapper (select* | insert* | update* | delete* | sql*)+>

<!ELEMENT select (#PCDATA | select)*>

<!ELEMENT insert (#PCDATA)>
<!ELEMENT update (#PCDATA)>
<!ELEMENT delete (#PCDATA)>
<!ELEMENT sql (#PCDATA)>

<!ATTLIST mapper namespace CDATA #IMPLIED>

<!ATTLIST select
id CDATA #REQUIRED
parameterType CDATA #IMPLIED
resultType CDATA #IMPLIED
>

<!ATTLIST delete
id CDATA #REQUIRED
parameterMap CDATA #IMPLIED
parameterType CDATA #IMPLIED
>

<!ATTLIST insert
id CDATA #REQUIRED
parameterMap CDATA #IMPLIED
parameterType CDATA #IMPLIED
>

<!ATTLIST update
id CDATA #REQUIRED
parameterMap CDATA #IMPLIED
parameterType CDATA #IMPLIED
>
```

---
4.5 通过注解方式【优先使用注解，没注解使用xml配置】
```html
@Select("select * from sys_role where role_id = #{id}")
public Role selectRoleById(Long id);


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
```

---



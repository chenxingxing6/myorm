## 手写ORM

---
#### 一、主要完成的功能【基本CURD是可以支持的】
> 1.通过自己实现的ORM,进行增删改查demo   
> 2.@Param注解解析，支持注解到对象和基本数据类型  
> 3.用dtd文件定义Mapper.xml文档的合法构建模块   
> 4.根据resultType，对结果进行处理

---

#### 二、Mybatis原理
###### 我们先复习一下Mybatis的实现原理：
1.SqlSessionFactory是线程安全的   
2.qlSession是单线程对象，因为它是非线程安全的


![avatar]("https://github.com/chenxingxing6/myorm/blob/master/img/1.jpg")

###### 流程描述：  
1.加载Mybatis全局配置文件并解析，生成Configuration对象和MapperdStatement  
2.SqlSessionFactoryBuilder通过Configuration对象构建SqlSessionFactory  
3.通过SqlSessionFactory获取sqlSession  
4.sqlSession和数据库进行交互

---

#### 三、MyORM实现
![avatar]("https://github.com/chenxingxing6/myorm/blob/master/img/2.jpg")

##### 我的实现思路
1.解析配置文件，初始化数据库连接，创建sqlSession池，交给SqlSessionFactory管理   
2.创建Execute,底层调用JDBC操作数据库   
3.创建MapperProxy代理对象，动态代理Mapper接口   
4.大体架子打好后，可以继续完善，比如@Param注解...

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

INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (1, '超级管理员', '最高权限', 34, '2018-07-31 19:27:42');
INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (2, '管理员', '权限比较少', 9, '2018-07-31 19:28:58');
INSERT INTO cloud_disk.sys_role (role_id, role_name, remark, dept_id, create_time) VALUES (3, 'IT经理', 'IT用户使用', 34, '2018-12-30 22:42:15');

```

---
```java
package com.test.entry;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:37
 */
public class User implements Serializable {
    private Long roleId;

    private String roleName;

    private String remark;

    private Long deptId;

    private Date createTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
```

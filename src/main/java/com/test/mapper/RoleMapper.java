package com.test.mapper;

import com.test.entry.Role;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: cxx
 * @Date: 2019/9/18 0:48
 */
public interface RoleMapper {
    public Role getRoleById(Long id);

    public Role getRoleByIdAndDeptId(@Param("id") Long id, @Param("deptId") Long deptId);
}

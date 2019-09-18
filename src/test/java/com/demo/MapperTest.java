package com.demo;

import com.alibaba.fastjson.JSON;
import com.test.entry.User;
import com.test.mapper.UserMapper;
import org.junit.Test;

import javax.annotation.Resource;

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
}

package com.test.entry;

import java.io.Serializable;

/**
 * @Author: cxx
 * @Date: 2019/9/18 12:37
 */
public class User implements Serializable {
    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

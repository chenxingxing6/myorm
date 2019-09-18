package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @Author: cxx
 * @Date: 2019/9/19 0:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();
}

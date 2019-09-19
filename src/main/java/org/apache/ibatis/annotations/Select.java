package org.apache.ibatis.annotations;

import jdk.nashorn.internal.ir.annotations.Reference;

import java.lang.annotation.*;

/**
 * User: lanxinghua
 * Date: 2019/9/19 15:46
 * Desc:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value();
}

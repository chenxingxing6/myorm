package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * User: lanxinghua
 * Date: 2019/9/19 15:46
 * Desc:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
    String value();
}

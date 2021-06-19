package com.distributed.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author Ray
 * @date created in 2021/6/18 11:44
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnExclude.class)
public @interface ConditionalOnExclude {
    ServiceEnum[] value() default {};
}

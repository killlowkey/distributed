package com.distributed.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author Ray
 * @date created in 2021/6/17 20:05
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnNotRegistry.class)
public @interface ConditionalOnNotRegistry {

}

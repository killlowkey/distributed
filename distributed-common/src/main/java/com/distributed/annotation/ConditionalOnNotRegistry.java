package com.distributed.annotation;

import java.lang.annotation.*;

/**
 * @author Ray
 * @date created in 2021/6/17 20:05
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnExclude(Services.REGISTRY)
//@Conditional(OnNotRegistry.class)
public @interface ConditionalOnNotRegistry {

}

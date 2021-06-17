package com.distributed.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 自定义条件注解
 *
 * @author Ray
 * @date created in 2021/6/17 20:05
 */
@ConditionalOnClass
public class OnNotRegistry implements Condition {

    private static final String REGISTRY_SERVICE_NAME = "Registry-Service";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        // 不是 Registry 服务就返回 true，允许实例该 Bean
        return !REGISTRY_SERVICE_NAME.equals(environment.getProperty("spring.application.name", ""));
    }
}

package com.distributed.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ray
 * @date created in 2021/6/18 11:44
 */
public class OnExclude implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String appName = getCurrentApplicationName(context.getEnvironment());
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnExclude.class.getName());
        List<Services> serviceList = Objects.requireNonNull(attrs).get("value")
                .stream()
                .flatMap(array -> Arrays.stream((Services[])array))
                .collect(Collectors.toList());
        for (Services service : serviceList) {
            if (service.getServiceName().equalsIgnoreCase(appName)) {
                return false;
            }
        }

        return true;
    }

    private String getCurrentApplicationName(Environment environment) {
        return environment.getProperty("spring.application.name", "");
    }
}

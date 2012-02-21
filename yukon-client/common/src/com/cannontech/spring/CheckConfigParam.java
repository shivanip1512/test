package com.cannontech.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.exception.NotAuthorizedException;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckConfigParam {
    String[] value();
    String expecting() default "TRUE";
    Class<? extends RuntimeException> throwable() default NotAuthorizedException.class;
    String errorMessage() default "Not authorized to use this feature.";
}
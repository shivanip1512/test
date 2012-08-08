package com.cannontech.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.core.roleproperties.YukonRoleProperty;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRoleProperty {
    YukonRoleProperty[] value() default {};
    boolean requireAll() default false;
}

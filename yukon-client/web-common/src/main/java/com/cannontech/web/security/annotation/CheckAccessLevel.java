package com.cannontech.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAccessLevel {
    YukonRoleProperty property();
    AccessLevel level();
}
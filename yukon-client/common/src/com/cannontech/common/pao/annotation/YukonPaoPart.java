package com.cannontech.common.pao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YukonPaoPart {
    public abstract String tableName() default ":auto";
    public abstract String idColumnName() default ":auto";
}

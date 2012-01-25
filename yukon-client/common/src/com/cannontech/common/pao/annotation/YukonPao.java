package com.cannontech.common.pao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.pao.PaoType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YukonPao {
    public final static String AUTO_DETECT = ":auto";

    public abstract boolean tableBacked() default true;
    public abstract String tableName() default AUTO_DETECT;
    public abstract String idColumnName() default AUTO_DETECT;
    public abstract PaoType[] paoTypes() default {};
}

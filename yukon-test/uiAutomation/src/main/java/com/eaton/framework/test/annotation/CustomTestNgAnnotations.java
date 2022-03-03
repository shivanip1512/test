package com.eaton.framework.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomTestNgAnnotations {

    public boolean refreshPage() default true;
    
    public String urlToRefresh() default "";    
}

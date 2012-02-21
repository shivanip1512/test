package com.cannontech.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;

@Aspect
public class MasterConfigInterceptor {

    private ConfigurationSource configurationSource;

    @Pointcut("within(@com.cannontech.spring.CheckConfigParam *)")
    public void beanAnnotatedWithCheckConfigParam() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("publicMethod() && beanAnnotatedWithCheckConfigParam()")
    public void publicMethodInsideAClassMarkedWithAtCheckConfigParam() {}

    @Before("publicMethodInsideAClassMarkedWithAtCheckConfigParam()")
    public void checkParam(JoinPoint p) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        CheckConfigParam checker = p.getTarget().getClass().getAnnotation(CheckConfigParam.class);

        if (checker != null) {
            String[] value = checker.value();
            Class<? extends RuntimeException> throwable = checker.throwable();
            String expecting = checker.expecting();

            for (String param : value) {
                // Check all parameters and throw the provided exception type
                // if any of the parameters do not match the expected result
                boolean matched = expecting.equalsIgnoreCase(configurationSource.getString(param));

                if (!matched) {
                    Constructor<?> constructor = null;

                    // Constructor with String as the only parameter
                    try {
                        constructor = throwable.getDeclaredConstructor(String.class);
                        RuntimeException throwme = throwable.cast(constructor.newInstance(checker.errorMessage()));
                        throw throwme;
                    }  catch (NoSuchMethodException e) { }

                    // Fallback to constructor with no parameters
                    try {
                        constructor = throwable.getDeclaredConstructor();
                        RuntimeException throwme = throwable.cast(constructor.newInstance());
                        throw throwme;
                    }  catch (NoSuchMethodException e) { }

                }
            }
        }
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

}
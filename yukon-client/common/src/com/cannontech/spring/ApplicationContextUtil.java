package com.cannontech.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is used to create application context for given resourceLocation.
 */

public final class ApplicationContextUtil {
    public static ApplicationContext context;

    public static ApplicationContext getApplicationContext(String resourceLocation) {
        if (context == null)
            context = new ClassPathXmlApplicationContext(resourceLocation);
        return context;
    }

}

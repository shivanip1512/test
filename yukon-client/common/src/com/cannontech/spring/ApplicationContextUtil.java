package com.cannontech.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Singleton class to create application context and use that context.
 */

public enum ApplicationContextUtil {
    INSTANCE;
    ApplicationContext context;

    public ApplicationContext getApplicationContext(String resourceLocation) {
        if (context == null)
            context = new ClassPathXmlApplicationContext(resourceLocation);
        return context;
    }

}

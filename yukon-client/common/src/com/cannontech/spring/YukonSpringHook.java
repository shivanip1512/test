package com.cannontech.spring;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

public class YukonSpringHook {
    static ApplicationContext applicationContext;
    
    public static ApplicationContext getContext() {
        BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
        BeanFactoryReference bfr = bfl.useBeanFactory("com.cannontech.context.main");
        ApplicationContext context = (ApplicationContext) bfr.getFactory();
        return context;
    }
    
    public static ApplicationContext getServicesContext() {
        BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
        BeanFactoryReference bfr = bfl.useBeanFactory("com.cannontech.context.services");
        ApplicationContext context = (ApplicationContext) bfr.getFactory();
        return context;
    }
    
    public static Object getBean(String name) {
        return getContext().getBean(name);
    }
    

}

package com.cannontech.spring;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.incrementer.NextValueHelper;

public class YukonSpringHook {

    private static String WEB_BEAN_FACTORY_KEY = "com.cannontech.context.web";
    private static String CLIENT_BEAN_FACTORY_KEY = "com.cannontech.context.client";
    private static String beanFactoryKey;
    
    static {
        if(CtiUtilities.isRunningAsWebApplication()) {
            beanFactoryKey = WEB_BEAN_FACTORY_KEY;
        }
        else {
            beanFactoryKey = CLIENT_BEAN_FACTORY_KEY;
        }
    }
    
    static ApplicationContext applicationContext;
    
    public synchronized static ApplicationContext getContext() {
        // this is synchronized to ensure that non-web applications
        // don't try to access it for the first time from multiple threads
        BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
        BeanFactoryReference bfr = bfl.useBeanFactory(beanFactoryKey);
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
    
    public static NextValueHelper getNextValueHelper() {
        // I'm not sure if this is the best spot for this...
        return (NextValueHelper) getBean("nextValueHelper");
    }
    
    public static TransactionTemplate getTransactionTemplate() {
        return (TransactionTemplate) getBean("transactionTemplate");
    }    
    
}

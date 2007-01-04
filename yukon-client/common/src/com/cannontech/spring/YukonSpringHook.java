package com.cannontech.spring;

import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

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
    
    private static ApplicationContext applicationContext = null;
    private static BeanFactoryReference beanFactoryRef = null;
    
    public synchronized static ApplicationContext getContext() {
        if (applicationContext == null) {
            // this is synchronized to ensure that non-web applications
            // don't try to access it for the first time from multiple threads
            BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
            beanFactoryRef = bfl.useBeanFactory(beanFactoryKey);
            applicationContext = (ApplicationContext) beanFactoryRef.getFactory();
        }
        return applicationContext;
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

    public static Timer getGlobalTimer() {
        return (Timer) getBean("globalTimer");
    }    
    
    public static ScheduledExecutorService getGlobalExecutor() {
        return (ScheduledExecutorService) getBean("globalScheduledExecutor");
    }
    
    public static void shutdownContext() {
        if (beanFactoryRef != null) {
            applicationContext = null;
            beanFactoryRef.release();
            beanFactoryRef = null;
        }
    }
    
}

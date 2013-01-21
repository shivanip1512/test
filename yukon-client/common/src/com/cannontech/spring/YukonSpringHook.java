package com.cannontech.spring;

import java.util.Timer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.database.incrementer.NextValueHelper;

public class YukonSpringHook {
    private static Logger log = YukonLogManager.getLogger(YukonSpringHook.class);
    public static String WEB_BEAN_FACTORY_KEY = "com.cannontech.context.web";
    public static String COMMON_BEAN_FACTORY_KEY = "com.cannontech.context.common";
    public static String NOTIFICATION_BEAN_FACTORY_KEY = "com.cannontech.context.notif";
    public static String SERVICES_BEAN_FACTORY_KEY = "com.cannontech.context.services";
    private static String defaultFactoryKey;
    private static String currentFactoryKey;
    
    static {
        if(CtiUtilities.isRunningAsWebApplication()) {
            defaultFactoryKey = WEB_BEAN_FACTORY_KEY;
        }
        else {
            defaultFactoryKey = COMMON_BEAN_FACTORY_KEY;
        }
    }
    
    private static ApplicationContext applicationContext = null;
    private static BeanFactoryReference beanFactoryRef = null;
    
    public synchronized static ApplicationContext getContext() {
        if (applicationContext == null) {
            createContext(defaultFactoryKey);
        }
        return applicationContext;
    }
    
    /**
     * This is used to get a special context. It should be called once at the beginning of
     * a program. Other, possibly shared, code can then call getContext() and get the
     * same copy of the context.
     * @param contextName
     * @return
     */
    public synchronized static void setDefaultContext(String contextName) {
        if (applicationContext == null) {
            log.info("Setting default context to: " + contextName);
            defaultFactoryKey = contextName;
        } else {
            throw new RuntimeException("Attempted to switch context to " + contextName + " after it was initialized");
        }
    }

    private synchronized static void createContext(String contextName) {
        log.info("Creating context: " + contextName);
        currentFactoryKey = contextName;
        BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
        beanFactoryRef = bfl.useBeanFactory(currentFactoryKey);
        applicationContext = (ApplicationContext) beanFactoryRef.getFactory();
    }
    
    public static Object getBean(String name) {
        return getContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> expectedType) {
        return getContext().getBean(name, expectedType);
    }

    public static <T> T getBean(Class<T> expectedType) {
        BeanFactoryUtils.beanOfTypeIncludingAncestors(getContext(), expectedType);
        return BeanFactoryUtils.beanOfTypeIncludingAncestors(getContext(), expectedType);
    }

    public static NextValueHelper getNextValueHelper() {
        // I'm not sure if this is the best spot for this...
        return (NextValueHelper) getBean("nextValueHelper");
    }
    
    public static TransactionTemplate getTransactionTemplate() {
        return getBean("transactionTemplate", TransactionTemplate.class);
    }

    public static Timer getGlobalTimer() {
        return getBean("globalTimer", Timer.class);
    }    
    
    public static ScheduledExecutor getGlobalExecutor() {
        return getBean("globalScheduledExecutor", ScheduledExecutor.class);
    }
    
    public static void shutdownContext() {
        if (beanFactoryRef != null) {
            log.info("Shutting down context: " + currentFactoryKey);
            applicationContext = null;
            beanFactoryRef.release();
            beanFactoryRef = null;
            currentFactoryKey = null;
        } else {
            log.warn("shutdownContext called before context was initialized");
        }
    }
    
}

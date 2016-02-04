package com.cannontech.spring;

import java.util.Timer;

import org.apache.log4j.Logger;
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
    public static String SIMULATORS_BEAN_FACTORY_KEY = "com.cannontech.context.simulators";
    private static String factoryKey;
    
    private static ApplicationContext applicationContext = null;
    private static BeanFactoryReference beanFactoryRef = null;
    
    public synchronized static ApplicationContext getContext() {
        if (applicationContext == null) {
            createContext();
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
            factoryKey = contextName;
        } else {
            throw new RuntimeException("Attempted to switch context to " + contextName + " after it was initialized");
        }
    }

    private synchronized static void createContext() {
        if (factoryKey == null) {
            if (CtiUtilities.isRunningAsWebApplication()) {
                factoryKey = WEB_BEAN_FACTORY_KEY;
            } else {
                factoryKey = COMMON_BEAN_FACTORY_KEY;
            }
        }
        log.info("Creating context: " + factoryKey);
        BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance();
        beanFactoryRef = bfl.useBeanFactory(factoryKey);
        applicationContext = (ApplicationContext) beanFactoryRef.getFactory();
    }

    /**
     * Get a bean based on its name. This method requires the caller to cast the return value.
     * 
     * @deprecated Use {@link #getBean(Class)} or {@link #getBean(String, Class)} instead.
     */
    @Deprecated
    public static Object getBean(String name) {
        return getContext().getBean(name);
    }

    /**
     * Get the bean with the given name and type. Use this only if where there is more than one bean
     * of the specified type in the context.  Normally, you should use {@link #getBean(Class)}.
     */
    public static <T> T getBean(String name, Class<T> expectedType) {
        return getContext().getBean(name, expectedType);
    }

    /**
     * Get a bean from the context.  This method works the most like autowiring and is therefore the preferred method
     * when using {@link YukonSpringHook}.
     */
    public static <T> T getBean(Class<T> expectedType) {
        return getContext().getBean(expectedType);
    }

    /**
     * @deprecated Inject or use {@link #getBean(Class)}
     */
    @Deprecated
    public static NextValueHelper getNextValueHelper() {
        return getBean(NextValueHelper.class);
    }
    
    /**
     * @deprecated Inject or use {@link #getBean(Class)}
     */
    @Deprecated
    public static TransactionTemplate getTransactionTemplate() {
        return getBean("transactionTemplate", TransactionTemplate.class);
    }

    /**
     * @deprecated Inject or use {@link #getBean(Class)}
     */
    @Deprecated
    public static Timer getGlobalTimer() {
        return getBean("globalTimer", Timer.class);
    }    
    
    /**
     * @deprecated Inject or use {@link #getBean(Class)}
     */
    @Deprecated
    public static ScheduledExecutor getGlobalExecutor() {
        return getBean("globalScheduledExecutor", ScheduledExecutor.class);
    }
    
    public static void shutdownContext() {
        if (beanFactoryRef != null) {
            log.info("Shutting down context: " + factoryKey);
            applicationContext = null;
            beanFactoryRef.release();
            beanFactoryRef = null;
            factoryKey = null;
        } else {
            log.warn("shutdownContext called before context was initialized");
        }
    }
}

package com.cannontech.common.events.service;

import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.impl.MethodLogDetail;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.TransactionExecutor;

/**
 * This class is a FactoryBean that produces implementation proxies that implement the event
 * logging behavior for the event logging interface specified by the serviceInterface property.
 * 
 * Specifically, this class will scan the serviceInterface for methods marked by the YukonEventLog
 * annotation. All methods in the serviceInterface need to be marked by the annotation
 * or this class will throw an error at startup.
 * 
 * Most of the work of this FactoryBean is done in the afterPropertiesSet method which determines
 * if and how each method can be translated into an EventLog object. Any errors detected at this
 * stage will prevent the system from starting up.
 * 
 * Once the bean has been produced, the invoke method looks up the logging information that was
 * generated in the initialization step, creates the EventLog object, and passes it to the 
 * TransactionExecutor to be executed in one of the supported manners.
 *
 */
public class EventLogFactoryBean implements FactoryBean, BeanClassLoaderAware, MethodInterceptor {
    
    private Logger log = YukonLogManager.getLogger(EventLogFactoryBean.class);
    private Class<?> serviceInterface;
    private Object serviceProxy;
    private ClassLoader classLoader;
    private EventLogDao eventLogDao;
    private EventLogService eventLogService;
    private TransactionExecutor transactionExecutor;
    
    @PostConstruct
    public void afterPropertiesSet() {
        if (serviceInterface == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        
        // Loop over all methods in interface and build the lookup map.
        Method[] methods = serviceInterface.getMethods();
        for (Method method : methods) {
            eventLogService.setupLoggerForMethod(method);
        }
        
        // Install ourself as the proxy interceptor.
        try {
            this.serviceProxy = new ProxyFactory(serviceInterface, this).getProxy(classLoader);
        } catch (Throwable e) {
            log.warn("caught exception creating proxy", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        // This method will be called whenever a method on the interface is invoked.
        Date date = new Date();
        
        // Lookup the MethodLogDetail object.
        Method method = methodInvocation.getMethod();
        MethodLogDetail methodLogDetail = eventLogService.getDetailForMethod(method);
        
        if (methodLogDetail == null) {
            throw new BadConfigurationException("Unable to log: " + methodInvocation);
        }

        // Check to see if logging is turned on for this type.
        if (!methodLogDetail.isLogging()){
            return null;
        }
        
        // Convert the arguments of the method call into database parameters.
        Object[] dbArguments = methodLogDetail.getValueMapper().map(methodInvocation.getArguments());
        
        // Build EventLog object that contains everything that will be logged.
        final EventLog eventLog = new EventLog();
        String eventType = methodLogDetail.getEventType();
        eventLog.setEventType(eventType);
        eventLog.setArguments(dbArguments);
        eventLog.setDateTime(date);
        
        // Create a runner that will be executed by the TransactionExecutor according to the 
        // specified transactionality.
        Runnable logRunner = new Runnable() {
            public void run() {
                try {
                    eventLogDao.insert(eventLog);
                    LogHelper.debug(log, "Event Logged: %s", eventLog);
                } catch (Exception e) {
                    log.warn("Unable to insert logging event", e);
                    LogHelper.warn(log, "Event Not Logged: %s", eventLog);
                }
            }
        };
        
        transactionExecutor.execute(logRunner, methodLogDetail.getTransactionality());
        
        // The interface methods should all return void.
        return null;
    }

    @Override
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Required
    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Autowired
    public void setTransactionExecutor(TransactionExecutor transactionExecutor) {
        this.transactionExecutor = transactionExecutor;
    }

    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }

    @Autowired
    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }
}

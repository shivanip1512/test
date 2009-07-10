package com.cannontech.common.events.service;

import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.dao.EventLogDao.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.mappers.LiteYukonUserToNameMapper;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TransactionExecutor;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;

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
public class EventLogFactoryBean implements FactoryBean, InitializingBean, BeanClassLoaderAware, MethodInterceptor {
    
    private Logger log = YukonLogManager.getLogger(EventLogFactoryBean.class);
    private Class<?> serviceInterface;
    private Object serviceProxy;
    private ClassLoader classLoader;
    private EventLogDao eventLogDao;
    private TransactionExecutor transactionExecutor;
    
    private static class MethodLogDetail {
        ExecutorTransactionality transactionality = ExecutorTransactionality.TRANSACTIONAL;
        ObjectMapper<Object[], Object[]> valueMapper = null;
        EventCategory eventCategory = null;
        String methodName = null;
        
        @Override
        public String toString() {
            return eventCategory.getFullName() + "." + methodName + ": " + valueMapper;
        }
    }
    
    private Map<Method, MethodLogDetail> methodLogDetailLookup = Maps.newHashMap();
    
    private static class ArgumentMapper<T> {
        public static <TT> ArgumentMapper<TT> create(Class<TT> javaType, int sqlType) {
            return new ArgumentMapper<TT>(javaType, sqlType, new PassThroughMapper<TT>());
        }
        
        public static <TT> ArgumentMapper<TT> create(Class<TT> javaType, int sqlType, ObjectMapper<? super TT, ? extends Object> objectMapper) {
            return new ArgumentMapper<TT>(javaType, sqlType, objectMapper);
        }
        
        private ArgumentMapper(Class<T> javaType, int sqlType, ObjectMapper<? super T, ? extends Object> objectMapper) {
            this.javaType = javaType;
            this.sqlType = sqlType;
            this.objectMapper = objectMapper;
        }
        
        public final Class<T> javaType;
        public final int sqlType;
        public final ObjectMapper<? super T, ?> objectMapper;
    }
    
    private List<ArgumentMapper<?>> argumentMappers;
    {
        // This area defines what arguments can be used in the interface methods.
        // The argumentMappers list is processed in order, by doing an instanceof
        // check on the Java type specified (primitive types are handled by their
        // wrapper class. When a match is found a column is searched
        // for that exactly matches the SQL type specified (so, adding a Types.REAL, won't
        // do anything unless a Types.REAL has been added as one of the database columns).
        // Refer to the EventLogDaoImpl to see which columns are actually supported.
        Builder<ArgumentMapper<?>> builder = ImmutableList.builder();
        builder.add(ArgumentMapper.create(Number.class, Types.BIGINT));
        builder.add(ArgumentMapper.create(String.class, Types.VARCHAR));
        builder.add(ArgumentMapper.create(LiteYukonUser.class, Types.VARCHAR, new LiteYukonUserToNameMapper()));
        argumentMappers = builder.build();
    }
    
    public void afterPropertiesSet() {
        if (serviceInterface == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        
        // Loop over all methods in interface and build the lookup map.
        Method[] methods = serviceInterface.getMethods();
        for (Method method : methods) {
            MethodLogDetail methodLogDetail = createDetailForMethod(method);
            log.debug("Created mapping: " + methodLogDetail);
            methodLogDetailLookup.put(method, methodLogDetail);
        }
        
        // Install ourself as the proxy interceptor.
        this.serviceProxy = new ProxyFactory(serviceInterface, this).getProxy(classLoader);
    }
    
    /**
     * Produces a MethodLogDetail object that can be cached for the particular method.
     * 
     * @param method
     * @return
     * @throws BadConfigurationException when method cannot be mapped to the database
     */
    private MethodLogDetail createDetailForMethod(final Method method) throws BadConfigurationException {
        MethodLogDetail methodLogDetail = new MethodLogDetail();

        YukonEventLog annotation = AnnotationUtils.getAnnotation(method, YukonEventLog.class);

        methodLogDetail.transactionality = annotation.transactionality();
        
        String categoryStr = annotation.category();
        if (categoryStr == null) {
            throw new BadConfigurationException("Could not find EventCategory for: " + method);
        }
        EventCategory category = EventCategory.createCategory(categoryStr);
        methodLogDetail.eventCategory = category;
        methodLogDetail.methodName = method.getName();
        
        final Class<?>[] parameterTypes = method.getParameterTypes();
        
        final List<ArgumentColumn> argumentColumns = eventLogDao.getArgumentColumns();
        
        ListMultimap<Integer,ArgumentColumn> availableArguments = ArrayListMultimap.create();
        for (ArgumentColumn argumentColumn : argumentColumns) {
            availableArguments.put(argumentColumn.sqlType, argumentColumn);
        }
        final List<ArgumentColumn> choosenColumns = Lists.newArrayListWithExpectedSize(parameterTypes.length);
        final List<ArgumentMapper<?>> choosenMappers = Lists.newArrayListWithExpectedSize(parameterTypes.length);
        
        for (Class<?> parameterType : parameterTypes) {
            boolean foundArgumentColumn = false;
            Class<?> parameterTypeToCompare = parameterType;
            if (parameterType.isPrimitive()) {
                // treat primitives as wrappers
                parameterTypeToCompare = ClassUtils.primitiveToWrapper(parameterType);
            }
            for (ArgumentMapper<?> argumentMapper : argumentMappers) {
                if (argumentMapper.javaType.isAssignableFrom(parameterTypeToCompare)) {
                    // this mapper handles the declared argument, now check if there are 
                    // columns left to store it
                    List<ArgumentColumn> availableArgumentsForType = availableArguments.get(argumentMapper.sqlType);
                    if (availableArgumentsForType.isEmpty()) continue;
                    
                    // remove column from available list
                    ArgumentColumn argumentColumn = availableArgumentsForType.remove(0);
                    choosenColumns.add(argumentColumn);
                    choosenMappers.add(argumentMapper);
                    foundArgumentColumn = true;
                    break;
                }
            }
            if (!foundArgumentColumn) {
                throw new BadConfigurationException("Unable to map event log method to database: " + method);
            }
        }
        
        // Create the mapper that will be used to convert the method arguments
        // to an array of arguments for the SQL, this mapper will get called
        // each time the method is invoked.
        // In the following, the length of the first array will equal the number of arguments in the method
        // and the length of the second array will equal the number of ArgumentColumns defined for the system.
        ObjectMapper<Object[], Object[]> argumentValueMapper = new ObjectMapper<Object[], Object[]>() {
            @Override
            public Object[] map(Object[] methodArguments) throws ObjectMappingException {
                Map<ArgumentColumn, Object> columnToValue = Maps.newHashMapWithExpectedSize(methodArguments.length);
                
                for (int i = 0; i < methodArguments.length; i++) {
                    Object methodArgumentValue = methodArguments[i];
                    ArgumentMapper<?> argumentMapper = choosenMappers.get(i);
                    ArgumentColumn dbColumn = choosenColumns.get(i);
                    
                    Object value = getValueWithMapper(methodArgumentValue, argumentMapper);
                    columnToValue.put(dbColumn, value);
                }
                
                Object[] dbArguments = new Object[argumentColumns.size()];
                for (int i = 0; i < argumentColumns.size(); i++) {
                    ArgumentColumn argumentColumn = argumentColumns.get(i);
                    // the following works for the unmapped columns because get will just return null
                    dbArguments[i] = columnToValue.get(argumentColumn);
                }
                return dbArguments;
            }
            
            @Override
            public String toString() {
                String result = Arrays.toString(parameterTypes) + " to " + choosenColumns;
                return result;
            }
        };
        methodLogDetail.valueMapper = argumentValueMapper; 
        
        return methodLogDetail;
    }
    
    /**
     * This method doesn't do much work, but it is broken out to preserve
     * the clean generic types of the ArgumentMapper.
     * @param <T>
     * @param methodArgumentValue
     * @param argumentMapper
     * @return
     */
    protected <T> Object getValueWithMapper(Object methodArgumentValue, ArgumentMapper<T> argumentMapper) {
        T value = argumentMapper.javaType.cast(methodArgumentValue);
        Object result = argumentMapper.objectMapper.map(value);
        return result;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // This method will be called whenever a method on the interface is invoked.
        Date date = new Date();
        
        // Lookup the MethodLogDetail object.
        Method method = methodInvocation.getMethod();
        MethodLogDetail methodLogDetail = methodLogDetailLookup.get(method);
        if (methodLogDetail == null) {
            throw new BadConfigurationException("Unable to log: " + methodInvocation);
        }
        
        // Convert the arguments of the method call into database parameters.
        Object[] dbArguments = methodLogDetail.valueMapper.map(methodInvocation.getArguments());
        
        // Build EventLog object that contains everything that will be logged.
        final EventLog eventLog = new EventLog();
        String eventType = getEventType(methodLogDetail);
        eventLog.setEventType(eventType);
        eventLog.setArguments(dbArguments);
        eventLog.setDateTime(date);
        
        // Create a runner that will be executed by the TransactionExecutor according to the 
        // specified transactionality.
        Runnable logRunner = new Runnable() {
            public void run() {
                try {
                    eventLogDao.insert(eventLog);
                    if (log.isDebugEnabled()) {
                    	log.debug("Event Logged: " + eventLog);
                    }
                } catch (Exception e) {
                    log.warn("Unable to insert logging event", e);
                }
            }
        };
        
        transactionExecutor.execute(logRunner, methodLogDetail.transactionality);
        
        // The interface methods should all return void.
        return null;
    }

    private String getEventType(MethodLogDetail methodLogDetail) {
        String eventType = methodLogDetail.eventCategory.getFullName() + "." + methodLogDetail.methodName;
        return eventType;
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

}

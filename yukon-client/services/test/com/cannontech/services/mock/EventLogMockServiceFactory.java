package com.cannontech.services.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class EventLogMockServiceFactory {
    
    
    public static <T> T getEventLogMockService(Class<T> eventLogInterfaceClass) {
        InvocationHandler emptyInvocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };

        Object proxyObject = 
            Proxy.newProxyInstance(eventLogInterfaceClass.getClassLoader(),
                                   new Class[] {eventLogInterfaceClass}, emptyInvocationHandler);

        return eventLogInterfaceClass.cast(proxyObject);
    }
}
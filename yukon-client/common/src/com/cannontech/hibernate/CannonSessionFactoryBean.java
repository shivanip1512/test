package com.cannontech.hibernate;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.cannontech.clientutils.CTILogger;

public class CannonSessionFactoryBean extends AnnotationSessionFactoryBean {

    private Object sessionFactoryProxy;

    public CannonSessionFactoryBean() {
        InvocationHandler handler = new InvocationHandler() {
            boolean initialized = false;
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                synchronized (this) {
                    if (!initialized) {
                        CTILogger.info("First Hibernate SessionFactory method called, initializing");
                        lateInitialization();
                        initialized = true;
                    }
                }
                return method.invoke(getSuperObject(), args);
            }
        };
        sessionFactoryProxy = Proxy.newProxyInstance(CannonSessionFactoryBean.class.getClassLoader(), 
                                                             new Class[] {SessionFactory.class}, 
                                                             handler);

    }
    
    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, HibernateException, IOException {
    }
    
    protected void lateInitialization() {
        try {
            super.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize Hibernate SessionFactory within the 'Late Proxy'", e);
        }
    }
    
    protected Object getSuperObject() {
        return super.getObject();
    }
    
    @Override
    public Object getObject() {
        return sessionFactoryProxy;
    }

}

package com.cannontech.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CannonClassPathXmlApplicationContext extends
        ClassPathXmlApplicationContext {

    public CannonClassPathXmlApplicationContext(String configLocation) throws BeansException {
        super(configLocation);
    }

    public CannonClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        super(configLocations, parent);
    }

    public CannonClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) throws BeansException {
        super(configLocations, refresh, parent);
    }

    public CannonClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        super(configLocations, refresh);
    }

    public CannonClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        super(configLocations);
    }
    
    @Override
    protected void onRefresh() throws BeansException {
        super.onRefresh();
        getBeanFactory().addBeanPostProcessor(new RequiredAnnotationBeanPostProcessor());
    }
    
}

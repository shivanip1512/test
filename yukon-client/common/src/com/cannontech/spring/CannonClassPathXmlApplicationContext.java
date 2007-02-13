package com.cannontech.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CannonClassPathXmlApplicationContext extends
        ClassPathXmlApplicationContext {

    public CannonClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        super(configLocations);
    }
    
    @Override
    protected void onRefresh() throws BeansException {
        super.onRefresh();
        getBeanFactory().addBeanPostProcessor(new RequiredAnnotationBeanPostProcessor());
    }
    
}

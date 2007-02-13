package com.cannontech.spring;

import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class CannonXmlWebApplicationContext extends XmlWebApplicationContext {
    private RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor = 
        new RequiredAnnotationBeanPostProcessor();
    @Override
    protected void onRefresh() {
        super.onRefresh();
        getBeanFactory().addBeanPostProcessor(requiredAnnotationBeanPostProcessor);
    }
}

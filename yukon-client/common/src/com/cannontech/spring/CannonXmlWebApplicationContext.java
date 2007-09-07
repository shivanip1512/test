package com.cannontech.spring;

import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * This is a special version of a XmlWebApplicationContext that knows how
 * to add the requiredAnnotationBeanPostProcessor and sets the default
 * location of the XML files to be /WEB-INF/contexts/.
 */
public class CannonXmlWebApplicationContext extends XmlWebApplicationContext {
    private RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor = 
        new RequiredAnnotationBeanPostProcessor();
    @Override
    protected void onRefresh() {
        super.onRefresh();
        getBeanFactory().addBeanPostProcessor(requiredAnnotationBeanPostProcessor);
    }
    
    @Override
    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null) {
        	return new String[] {"/WEB-INF/contexts/" + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        }
        else {
        	return new String[] {"/WEB-INF/contexts/applicationContext.xml"};
        }
    }
}

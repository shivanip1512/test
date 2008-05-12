package com.cannontech.spring;

import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * This is a special version of a XmlWebApplicationContext that knows how
 * to add the requiredAnnotationBeanPostProcessor and sets the default
 * location of the XML files to be /WEB-INF/contexts/.
 */
public class CannonXmlWebApplicationContext extends XmlWebApplicationContext {
    private static final String SHARED_WEB_CONFIG_LOCATION = "/WEB-INF/contexts/sharedWebContext.xml";
    private final RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor = 
        new RequiredAnnotationBeanPostProcessor();
    @Override
    protected void onRefresh() {
        super.onRefresh();
        getBeanFactory().addBeanPostProcessor(requiredAnnotationBeanPostProcessor);
    }
    
    @Override
    public String[] getConfigLocations() {
        String[] locations = super.getConfigLocations();
        String[] tempArray = new String[locations.length + 1];
        tempArray[0] = SHARED_WEB_CONFIG_LOCATION;
        System.arraycopy(locations, 0, tempArray, 1, locations.length);
        return tempArray;
    }
    
    @Override
    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null) {
            return new String[] {"/WEB-INF/contexts/" + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        }
        return new String[] {"/WEB-INF/contexts/applicationContext.xml"};
    }
    
}

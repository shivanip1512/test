package com.cannontech.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.CtiUtilities;

public class YukonBaseXmlApplicationContext extends AbstractXmlApplicationContext {

    /**
     * Create a new FileSystemXmlApplicationContext with the given parent,
     * loading the definitions from the given XML files and automatically
     * refreshing the context.
     * @param parent the parent context
     * @param configLocations array of file paths
     * @throws BeansException if context creation failed
     */
    public YukonBaseXmlApplicationContext(ApplicationContext parent, String... configLocation) throws BeansException {
        super(parent);
        setConfigLocations(configLocation);
        refresh();
    }

    @Override
    protected Resource getResourceByPath(String path) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemResource(CtiUtilities.getYukonBase() + "/" + path);
    }
}

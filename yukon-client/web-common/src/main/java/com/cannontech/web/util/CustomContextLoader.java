package com.cannontech.web.util;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.cannontech.spring.ApplicationContextUtil;

public class CustomContextLoader extends ContextLoader {

    public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";
    public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

    @Override
    protected ApplicationContext loadParentContext(ServletContext servletContext) {
        String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
        String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);
        return (ApplicationContext) ApplicationContextUtil.INSTANCE.getApplicationContext(locatorFactorySelector).getBean(
            parentContextKey);
    }
}

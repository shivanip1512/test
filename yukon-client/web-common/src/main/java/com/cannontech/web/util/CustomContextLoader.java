package com.cannontech.web.util;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.cannontech.spring.ApplicationContextUtil;


/**
 * Performs the actual initialization work for the root application context.
 * Parent context is defined in web.xml using context-param with param-name locatorFactorySelector and
 * parentContextKey.
 * spring 5 do not provide any default implementation of loadParentContext which caters loading of parent
 * context (locatorFactorySelector & parentContextKey).
 * Created {@link CustomContextLoader} class to support parent context
 */
public class CustomContextLoader extends ContextLoader {

    public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";
    public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

    @Override
    protected ApplicationContext loadParentContext(ServletContext servletContext) {
        String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
        String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);
        return (ApplicationContext) ApplicationContextUtil.getApplicationContext(locatorFactorySelector).getBean(
            parentContextKey);
    }
}

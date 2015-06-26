package com.cannontech.web.widget.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

/*
 * Class to provide WebApplicationContext. In this case it will provide
 * widgetDispatcher-servlet context
 */
public class WebApplicationContextAwareBean {

    private static WebApplicationContext context;

    @Autowired
    public WebApplicationContextAwareBean(WebApplicationContext webAppContext) {
        context = webAppContext;
    }

    public static WebApplicationContext getWebApplicationContext() {
        return context;
    }
}

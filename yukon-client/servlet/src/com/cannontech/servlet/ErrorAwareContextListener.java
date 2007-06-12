package com.cannontech.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.servlet.filter.ErrorHelperFilter;

public abstract class ErrorAwareContextListener implements ServletContextListener {
    private WebApplicationContext applicationContext;
    public ErrorAwareContextListener() {
        super();
    }
    
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        if (ErrorHelperFilter.doesStartupErrorExist(servletContext)) {
            CTILogger.error("Skipping load of " + this.getClass().getSimpleName() + " because of previous errors");
            return;
        }
        try {
            doContextInitialized(sce);
        } catch (RuntimeException re) {
            handleException(servletContext, re);
            throw re;
        } catch (Throwable t) {
            handleException(servletContext, t);
            throw new RuntimeException("Wrapping an unknown error", t);
        }
    }
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public abstract void doContextInitialized(ServletContextEvent sce);

    private void handleException(ServletContext servletContext, Throwable e) {
        ErrorHelperFilter.recordStartupError(servletContext, e);
        CTILogger.error("Servlet startup error in " + getClass(), e);
    }
    
    protected WebApplicationContext getApplicationContext() {
        return applicationContext;
    }
    

}

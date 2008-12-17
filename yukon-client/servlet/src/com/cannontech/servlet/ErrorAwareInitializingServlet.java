package com.cannontech.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.web.util.ErrorHelperFilter;

public abstract class ErrorAwareInitializingServlet extends HttpServlet {
    private boolean startupErrorsAreFatal;

    public ErrorAwareInitializingServlet() {
        super();
    }
    
    @Override
    public final void init() throws ServletException {
    }

    @Override
    public final void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        ServletContext servletContext = servletConfig.getServletContext();
        if (ErrorHelperFilter.doesStartupErrorExist(servletContext)) {
            CTILogger.error("Skipping load of " + this.getServletName() + " because of previous errors");
            return;
        }
        try {
            doInit(servletConfig);
        } catch (ServletException e) {
            handleException(servletContext, e);
            throw e;
        } catch (RuntimeException re) {
            handleException(servletContext, re);
            throw re;
        } catch (Throwable t) {
            handleException(servletContext, t);
            throw new ServletException("Wrapping an unknown error", t);
        }
    }

    protected abstract void doInit(ServletConfig servletConfig) throws ServletException;

    private void handleException(ServletContext servletContext, Throwable e) {
        if (startupErrorsAreFatal) {
            ErrorHelperFilter.recordStartupError(servletContext, e);
        }
        CTILogger.error("Servlet startup error in " + getClass(), e);
    }
    
    public boolean isStartupErrorsAreFatal() {
        return startupErrorsAreFatal;
    }

    public void setStartupErrorsAreFatal(boolean startupErrorsAreFatal) {
        this.startupErrorsAreFatal = startupErrorsAreFatal;
    }
    
    

}

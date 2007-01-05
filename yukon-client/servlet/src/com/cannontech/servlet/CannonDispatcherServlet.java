package com.cannontech.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.web.servlet.DispatcherServlet;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.servlet.filter.ErrorHelperFilter;

public class CannonDispatcherServlet extends DispatcherServlet {
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        if (ErrorHelperFilter.doesStartupErrorExist(servletConfig.getServletContext())) {
            CTILogger.error("Skipping load of " + servletConfig.getServletName() + " because of previous errors");
            return;
        }
        try {
            super.init(servletConfig);
        } catch (ServletException e) {
            handleException(servletConfig, e);
            throw e;
        } catch (RuntimeException re) {
            handleException(servletConfig, re);
            throw re;
        } catch (Throwable t) {
            handleException(servletConfig, t);
            throw new ServletException("Wrapping an unknown error", t);
        }
    }
    
    private void handleException(ServletConfig servletConfig, Throwable e) {
        ErrorHelperFilter.recordStartupError(servletConfig.getServletContext(), e);
        CTILogger.error("Servlet startup error in " + servletConfig.getServletName(), e);
    }

}

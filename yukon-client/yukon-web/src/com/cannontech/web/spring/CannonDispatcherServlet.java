package com.cannontech.web.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.CannonXmlWebApplicationContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.ErrorHelperFilter;
import com.google.common.collect.Lists;

public class CannonDispatcherServlet extends DispatcherServlet {
    private HandlerInterceptor securityInterceptor;
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        if (ErrorHelperFilter.doesStartupErrorExist(servletConfig.getServletContext())) {
            CTILogger.error("Skipping load of " + servletConfig.getServletName() + " because of previous errors");
            return;
        }
        try {
            super.init(servletConfig);
            setSecurityInterceptor();
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
    
    @Override
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {

        HandlerExecutionChain handler = super.getHandler(request);
        if (handler == null)
            return null;

        handler.addInterceptor(securityInterceptor);
        return handler;
    }
    
    @Override
    protected WebApplicationContext initWebApplicationContext() throws BeansException {
        setContextClass(CannonXmlWebApplicationContext.class);
        return super.initWebApplicationContext();
    }
    
    private void setSecurityInterceptor() {
        securityInterceptor = (HandlerInterceptor) this.getWebApplicationContext().getBean("webSecurityInterceptor");
    }
    
    private void handleException(ServletConfig servletConfig, Throwable e) {
        ErrorHelperFilter.recordStartupError(servletConfig.getServletContext(), e);
        CTILogger.error("Servlet startup error in " + servletConfig.getServletName(), e);
    }
    
    /**
     * No handler found -> set appropriate HTTP response status for API
     */
    @Override
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean apiLoginrequest = ServletUtil.isPathMatch(request, Lists.newArrayList("/api/**"));
        if (apiLoginrequest) {
            String url = ServletUtil.getFullURL(request);
            throw new NoHandlerFoundException(request.getMethod(), url,
                new ServletServerHttpRequest(request).getHeaders());
        }
        super.noHandlerFound(request, response);
    }

}

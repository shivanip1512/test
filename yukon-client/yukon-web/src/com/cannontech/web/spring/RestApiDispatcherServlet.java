package com.cannontech.web.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.web.api.errorHandler.ApiExceptionHandler;
import com.cannontech.web.util.ErrorHelperFilter;

/**
 * A DispatcherServlet with specialized exception handling to return API error responses.
 */
public class RestApiDispatcherServlet extends DispatcherServlet {
    private HandlerInterceptor securityInterceptor;
    
    public RestApiDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }
    
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
        if (handler == null) {
            return null;
        }

        handler.addInterceptor(securityInterceptor);
        return handler;
    }
    
    private void setSecurityInterceptor() {
        securityInterceptor = (HandlerInterceptor) getWebApplicationContext().getBean("webSecurityInterceptor");
    }
    
    private void handleException(ServletConfig servletConfig, Throwable e) {
        ErrorHelperFilter.recordStartupError(servletConfig.getServletContext(), e);
        CTILogger.error("Servlet startup error in " + servletConfig.getServletName(), e);
    }
    
    /**
     * No handler found -> set appropriate HTTP response status for API.
     */
    @Override
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ApiExceptionHandler.noHandlerFoundException(request, response);
    }

    /**
     *  Handle HttpRequestMethodNotSupportedException for API calls.
     */
    @Override
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            ApiExceptionHandler.handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex,
                request, response);
            return null;
        }
        return super.processHandlerException(request, response, handler, ex);
    }
}

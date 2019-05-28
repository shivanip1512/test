package com.cannontech.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FriendlyExceptionResolver;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class ErrorHelperFilter implements Filter {

    private Logger log = YukonLogManager.getLogger(ErrorHelperFilter.class);
    private static final String SERVLET_STARTUP_ERROR = "comcannontechSERVLET_STARTUP_ERROR";
    private static final String SERVLET_STARTUP_ERROR_ROOT_MESSAGE = "comcannontechSERVLET_STARTUP_ERROR_ROOT_MESSAGE";
    public static final String LOG_KEY = "comcannontechSERVLET_ERROR_LOG_KEY";
    private ServletContext servletContext;

    private Set<Class<? extends Throwable>> exceptionToIgnore = new HashSet<Class<? extends Throwable>>();
    {
        exceptionToIgnore.add(HttpSessionRequiredException.class);
        exceptionToIgnore.add(ClientAbortException.class);
    }

    public void init(FilterConfig arg0) throws ServletException {
        servletContext = arg0.getServletContext();
    }

    private String getRequestInfo(ServletRequest req) {
        req.getParameterMap();
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            String pathInfo = httpReq.getPathInfo();
            String contextPath = httpReq.getContextPath();
            StringBuffer requestURL = httpReq.getRequestURL();
            List<String> parameterList = new ArrayList<String>(httpReq.getParameterMap().size());
            Set parameterNames = httpReq.getParameterMap().keySet();
            for (Iterator iter = parameterNames.iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                String values;
                // avoid printing password values to the log
                if (key.equalsIgnoreCase("password")) {
                    values = "password removed";
                } else {
                    values = StringUtils.join(httpReq.getParameterValues(key), ",");
                }
                parameterList.add(key + "={" + values + "}");
            }

            String parameters = StringUtils.join(parameterList.iterator(), ",");

            String remoteAddr = httpReq.getRemoteAddr();
            String queryString = httpReq.getQueryString();
            // avoid printing password values to the log
            if (queryString != null) {
                queryString = queryString.replaceAll("(?i)password=[^&]*&?", "password=<password removed>&");
            }

            return "contextPath=" + contextPath + "; pathInfo=" + pathInfo + "; parameters=" + parameters
                + "; remoteAddr=" + remoteAddr + "; requestURL=" + requestURL + "; queryString=" + queryString;
        }
        return "[non http request]";
    }

    private void handleAjaxErrorResponse(ServletResponse resp, Throwable t) throws IOException {
        if (resp instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) resp;
            if (httpResp.isCommitted()) {
                throw new RuntimeException("Can't output AJAX exception, already committed", t);
            }
            httpResp.reset();
            httpResp.resetBuffer();
            httpResp.setStatus(500);
            try {
                httpResp.getWriter().print(t.getMessage());
            } catch (IllegalStateException e) {
                throw new RuntimeException("Can't output AJAX exception, OutputStream already opened", t);
            }
            return;
        }
        throw new RuntimeException("Can't output AJAX exception, not HttpServletResponse", t);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        CTILogger.debug("Starting request handling: " + getRequestInfo(request));

        // first check if our server came up okay
        Object attribute = servletContext.getAttribute(SERVLET_STARTUP_ERROR);
        if (attribute instanceof Throwable) {
            Throwable startupException = (Throwable) attribute;
            Throwable rootCause = null;
            rootCause = CtiUtilities.getRootCause(startupException);
            servletContext.setAttribute(SERVLET_STARTUP_ERROR_ROOT_MESSAGE, rootCause.getMessage());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/startupError.jsp");
            requestDispatcher.forward(request, response);
            return;
        }
        try {
            HttpServletRequest hReq = (HttpServletRequest) request;
            HttpSession session = hReq.getSession(false);
            if (session != null) {
                LiteYukonUser yukonUser = ServletUtil.getYukonUser(session);
                if (yukonUser != null) {
                	ThreadContext.push("yukonuser", yukonUser);
                }
            }
            ThreadContext.put("path", hReq.getRequestURI());
            chain.doFilter(request, response);
        } catch (Error e) {
            Throwable rc = CtiUtilities.getRootCause(e);
            String key = setupUniqueLogKey(request);
            CTILogger.error("Servlet error filter caught an Error processing {" + key + "}: " + getRequestInfo(request),
                e);
            if (ServletUtil.isAjaxRequest(request)) {
                handleAjaxErrorResponse(response, rc);
            } else {
                throw e;
            }
        } catch (RuntimeException re) {
            Throwable rc = CtiUtilities.getRootCause(re);
            String key = setupUniqueLogKey(request);
            handleException(request, re, rc, key);
            if (ServletUtil.isAjaxRequest(request)) {
                handleAjaxErrorResponse(response, rc);
            } else {
                throw re;
            }
        } catch (Throwable t) {
            Throwable rc = CtiUtilities.getRootCause(t);
            String key = setupUniqueLogKey(request);
            handleException(request, t, rc, key);
            if (ServletUtil.isAjaxRequest(request)) {
                handleAjaxErrorResponse(response, rc);
            } else {
                throw new ServletException(t);
            }
        } finally {
        	ThreadContext.remove("yukonuser");
        	ThreadContext.remove("path");
        }

    }

    private void handleException(ServletRequest request, Throwable t, Throwable rc, String key) {
        Level level = Level.ERROR;
        if (ignoreException(t)) {
            level = Level.DEBUG;
        }
        log.log(level, "Servlet error filter caught an exception processing {" + key + "}: " + getRequestInfo(request),
            t);
        log.log(level, "Root cause was: ", rc);
    }

    private boolean ignoreException(Throwable t) {
        Throwable exception = t;
        while (exception != null) {
            if (exceptionToIgnore.contains(exception.getClass())) {
                return true;
            }
            exception = ExceptionUtils.getCause(exception);
        }
        return false;
    }

    private String setupUniqueLogKey(ServletRequest request) {
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        request.setAttribute(LOG_KEY, uniqueKey);
        return uniqueKey;
    }

    public void destroy() {
    }

    public static void recordStartupError(ServletContext servletContext, Throwable t) {
        servletContext.setAttribute(SERVLET_STARTUP_ERROR, t);
    }

    public static boolean doesStartupErrorExist(ServletContext servletContext) {
        return servletContext.getAttribute(SERVLET_STARTUP_ERROR) != null;
    }

    public static String getFriendlyExceptionMessage(ServletContext servletContext, Throwable throwable) {

        WebApplicationContext webContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        FriendlyExceptionResolver friendlyExceptionResolver =
            (FriendlyExceptionResolver) webContext.getBean("friendlyExceptionResolver");

        String friendlyExceptionMessage = friendlyExceptionResolver.getFriendlyExceptionMessage(throwable);

        return friendlyExceptionMessage;
    }

}

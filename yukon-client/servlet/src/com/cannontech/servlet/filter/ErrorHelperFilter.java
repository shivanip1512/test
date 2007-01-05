package com.cannontech.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

public class ErrorHelperFilter  implements Filter {

	private static final String SERVLET_STARTUP_ERROR = "comcannontechSERVLET_STARTUP_ERROR";
    private static final String SERVLET_STARTUP_ERROR_ROOT_MESSAGE = "comcannontechSERVLET_STARTUP_ERROR_ROOT_MESSAGE";
    private ServletContext servletContext;

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
				String values = StringUtils.join(httpReq.getParameterValues(key), ",");
				parameterList.add(key + "={" + values + "}");
			}
			
			String parameters = StringUtils.join(parameterList.iterator(), ",");
			String remoteAddr = httpReq.getRemoteAddr();
			String queryString = httpReq.getQueryString();
			return "contextPath=" + contextPath +
			    "; pathInfo=" + pathInfo + 
				"; parameters=" + parameters + 
				"; remoteAddr=" + remoteAddr + 
				"; requestURL=" + requestURL +
				"; queryString=" + queryString;
		}
		return "[non http request]";
	}
	
	private void handleAjaxErrorResponse(ServletResponse resp, Throwable t) throws IOException {
		if (resp instanceof HttpServletResponse) {
			HttpServletResponse httpResp = (HttpServletResponse)resp;
			if (httpResp.isCommitted()) {
				throw new RuntimeException("Can't output AJAX exception, already committed",t);
			}
			httpResp.reset();
			httpResp.resetBuffer();
			httpResp.setStatus(500);
			try {
				httpResp.getWriter().print(t.getMessage());
			} catch (IllegalStateException e) {
				throw new RuntimeException("Can't output AJAX exception, OutputStream already opened",t);
			}
			return;
		}
		throw new RuntimeException("Can't output AJAX exception, not HttpServletResponse",t);
	}
	
	private boolean isAjaxRequest(ServletRequest req) {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String header = httpReq.getHeader("X-Requested-With");
			if (header != null) {
				return header.startsWith("XMLHttpRequest");
			}
		}
		return false;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		CTILogger.debug("Starting request handling: " + getRequestInfo(request));
        
        // first check if our server came up okay
        Object attribute = servletContext.getAttribute(SERVLET_STARTUP_ERROR);
        if (attribute instanceof Throwable) {
            Throwable startupException = (Throwable) attribute;
            Throwable rootCause = null;
            rootCause = CtiUtilities.getRootCause(startupException);
            servletContext.setAttribute(SERVLET_STARTUP_ERROR_ROOT_MESSAGE, rootCause.getMessage());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("startupError.jsp");
            requestDispatcher.forward(request, response);
            return;
        }
		try {
			chain.doFilter(request, response);
		} catch (Error e) {
			Throwable rc = CtiUtilities.getRootCause(e);
            CTILogger.error("Servlet error filter caught an Error processing: " + getRequestInfo(request), rc);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, rc);
			} else {
				throw e;
			}
		} catch (RuntimeException re) {
		    Throwable rc = CtiUtilities.getRootCause(re);
			CTILogger.error("Servlet error filter caught a RuntimeException processing: " + getRequestInfo(request), rc);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, rc);
			} else {
				throw re;
			}
		} catch (Throwable t) {
		    Throwable rc = CtiUtilities.getRootCause(t);
			CTILogger.error("Servlet error filter caught a Throwable processing: " + getRequestInfo(request), rc);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, rc);
			} else {
				throw new ServletException(t);
			}
		}
		
	}

	public void destroy() {
	}
    
    public static void recordStartupError(ServletContext servletContext, Throwable t) {
        servletContext.setAttribute(SERVLET_STARTUP_ERROR, t);
    }

    public static boolean doesStartupErrorExist(ServletContext servletContext) {
        return servletContext.getAttribute(SERVLET_STARTUP_ERROR) != null;
    }
    
}

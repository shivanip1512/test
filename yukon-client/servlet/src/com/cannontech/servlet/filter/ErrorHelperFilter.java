package com.cannontech.servlet.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;

public class ErrorHelperFilter  implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
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
				throw new RuntimeException("Can't output AJAX exception, already committed");
			}
			httpResp.resetBuffer();
			httpResp.setStatus(500);
			httpResp.getWriter().print(t.getMessage());
			return;
		}
		throw new RuntimeException("Can't output AJAX exception, not HttpServletResponse");
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
		try {
			chain.doFilter(request, response);
		} catch (Error e) {
			CTILogger.error("Servlet error filter caught an Error processing: " + getRequestInfo(request), e);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, e);
			} else {
				throw e;
			}
		} catch (RuntimeException re) {
			CTILogger.error("Servlet error filter caught a RuntimeException processing: " + getRequestInfo(request), re);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, re);
			} else {
				throw re;
			}
		} catch (Throwable t) {
			CTILogger.error("Servlet error filter caught a Throwable processing: " + getRequestInfo(request), t);
			if (isAjaxRequest(request)) {
				handleAjaxErrorResponse(response, t);
			} else {
				throw new ServletException(t);
			}
		}
		
	}

	public void destroy() {
	}

}

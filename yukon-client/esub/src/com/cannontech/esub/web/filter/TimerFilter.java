package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;

/**
 * Filter that times how long a request takes.
 * @author alauinger
 */
public class TimerFilter implements Filter {

	// Save the filter config for getting at servlet context
	private FilterConfig config;
	
	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fc) throws ServletException {
		config = fc;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws IOException, ServletException {
		
		long before = System.currentTimeMillis();
    	chain.doFilter(req, resp);
	    long after = System.currentTimeMillis();

    	String name = "";
	    if (req instanceof HttpServletRequest) {
      		name = ((HttpServletRequest)req).getRequestURI();
    	}	

    	CTILogger.info(name + ": " + (after - before) + "ms");
  	}
	

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

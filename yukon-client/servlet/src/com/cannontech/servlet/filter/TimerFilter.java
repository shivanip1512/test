package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.util.ServletUtil;

/**
 * Filter that times how long a request takes.
 * @author alauinger
 */
public class TimerFilter implements Filter {
    private Logger log = YukonLogManager.getLogger(TimerFilter.class);
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
		if (ServletUtil.isAjaxRequest(req))
			log.debug(name + ": " + (after - before) + "ms");
		else
			log.info(name + ": " + (after - before) + "ms");
  	}

	public void destroy() { }
	public void init(FilterConfig arg0) throws ServletException { }
}

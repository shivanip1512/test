package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter for all JSP that ensure no caching
 * @author ryan
 * 
 */
public class NoCacheFilter implements Filter 
{
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
	public void doFilter( ServletRequest req, ServletResponse resp, FilterChain chain)
                    throws IOException, ServletException
	{
        ((HttpServletResponse)resp).setHeader("Pragma", "no-cache");
        ((HttpServletResponse)resp).setHeader("Cache-control", "no-cache, must-revalidate");
        ((HttpServletResponse)resp).setDateHeader("Expires", 0);

        chain.doFilter(req,resp);
  	}
	

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

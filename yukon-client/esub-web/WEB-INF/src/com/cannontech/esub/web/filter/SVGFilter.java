package com.cannontech.esub.web.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Description Here
 * @author alauinger
 */
public class SVGFilter implements Filter {
	 
	private FilterConfig config;
	
	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		this.config = null;
	}


	/**
	 * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws IOException, ServletException {


		HttpServletRequest hreq = (HttpServletRequest)req;
		HttpServletResponse hres = (HttpServletResponse)resp;

		String currentURI = hreq.getRequestURL().toString();
        String currentURL = hreq.getRequestURI();
	
		config.getServletContext().log("currentURI: " + currentURI);
		config.getServletContext().log("currentURL: " + currentURL);
	}
	

	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}



}

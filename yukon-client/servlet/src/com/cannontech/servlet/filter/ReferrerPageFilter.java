package com.cannontech.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;

/**
 * Filter that keeps track of referrer pages and sets 
 * referrer variables in the session for "Back" button functionality.
 * @author jdayton
 */
public class ReferrerPageFilter implements Filter {

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
				
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		HttpSession session = request.getSession(false);
		if(session != null)
		{
			CtiNavObject navigator = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
			if(navigator == null)
				navigator = new CtiNavObject();
			
			navigator.setNavigation(request.getRequestURL().toString());
			
			session.setAttribute(ServletUtil.NAVIGATE, navigator);
		}
		
		chain.doFilter(req,resp);
    }
	

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Forwards all request for any file that matches this filter to
 * the /images directory
 * 
 * @author alauinger
 */
public class ImageFilter implements Filter {

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
			
		ServletContext sc = config.getServletContext();
		
		HttpServletRequest hreq = (HttpServletRequest)req;
		HttpServletResponse hres = (HttpServletResponse)resp;
		 
		String uri = hreq.getRequestURI();
		String conPath = hreq.getContextPath();

		String imgPath= uri.replaceFirst(conPath, "");
		
		if( imgPath.startsWith("/images/") ) {					
			chain.doFilter(req,resp);		
		}
		else {
			imgPath = imgPath.substring(imgPath.lastIndexOf("/"));
			config.getServletContext().getRequestDispatcher("/images" + imgPath).forward(req, resp);
		}
			
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

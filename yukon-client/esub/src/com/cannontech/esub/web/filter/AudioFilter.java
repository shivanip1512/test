package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Forwards all request for any file matching this filter
 * the /audio directory
 * 
 * @author alauinger
 */
public class AudioFilter implements Filter {

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
				
		
		HttpServletRequest hreq = (HttpServletRequest)req;
		 
		String uri = hreq.getRequestURI();
		String conPath = hreq.getContextPath();

		if(!(uri.endsWith(".wav"))) {
			chain.doFilter(req,resp);
			return;
		}
		
		String audioPath= uri.replaceFirst(conPath, "");
		if( audioPath.startsWith("/esub/audio/") ) {					
			chain.doFilter(req,resp);		
		}
		else {
			audioPath = audioPath.substring(audioPath.lastIndexOf("/"));
			config.getServletContext().getRequestDispatcher("/esub/audio" + audioPath).forward(req, resp);
		}
			
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

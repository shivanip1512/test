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
 * the /css directory
 * 
 * @author alauinger
 */
public class CSSFilter implements Filter {

	private FilterConfig config;
	
	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	@Override
    public void init(FilterConfig fc) throws ServletException {
		config = fc;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
    public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws IOException, ServletException {
				
		
		HttpServletRequest hreq = (HttpServletRequest)req;
		 
		String uri = hreq.getRequestURI();
		String conPath = hreq.getContextPath();

		if(!(uri.endsWith(".css"))) {
			chain.doFilter(req,resp);
			return;
		}
		
		String cssPath= uri.replaceFirst(conPath, "");
		if( cssPath.startsWith("/esub/css/") ) {					
			chain.doFilter(req,resp);		
		}
        else if (cssPath.startsWith("/oneline/css/"))
        {
              
            config.getServletContext().getRequestDispatcher(cssPath).forward(req, resp);
            
        }      
		else {
			cssPath = cssPath.substring(cssPath.lastIndexOf("/"));
			config.getServletContext().getRequestDispatcher("/esub/css" + cssPath).forward(req, resp);
		}
			
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
    public void destroy() {
		config = null;
	}

}

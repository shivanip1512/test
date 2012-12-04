package com.cannontech.esub.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Forwards all request for any file matching this filter
 * the /js directory
 * 
 * @author alauinger
 */
public class JSFilter implements Filter {

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

		if(!(uri.endsWith(".js"))) {
			chain.doFilter(req,resp);
			return;
		}
		
		String jsPath= uri.replaceFirst(conPath, "");
		if( jsPath.startsWith("/esub/js/") ) {					
			chain.doFilter(req,resp);		
		}
        else
		if (jsPath.startsWith("/oneline/"))
        {
              
            jsPath = StringUtils.replace(jsPath, "/oneline/", "/JavaScript/");  
            config.getServletContext().getRequestDispatcher(jsPath).forward(req, resp);
            
        }      
            
        else 
            {
    			jsPath = jsPath.substring(jsPath.lastIndexOf("/"));
    			String finalPath = "/esub/js" + jsPath;
                config.getServletContext().getRequestDispatcher(finalPath).forward(req, resp);
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

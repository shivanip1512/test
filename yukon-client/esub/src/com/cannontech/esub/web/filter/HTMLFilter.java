package com.cannontech.esub.web.filter;

import java.io.File;
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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.esub.util.HTMLOptions;

/**
 * Forwards all request for any file that matches this filter to
 * the /images directory
 * 
 * @author alauinger
 */
public class HTMLFilter implements Filter {

	private FilterConfig config;
	private HTMLGenerator htmlGenerator = new HTMLGenerator();
	
	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fc) throws ServletException {
		config = fc;
		htmlGenerator.getGenOptions().setStaticHTML(false);
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

		resp.setContentType("text/html");
				
		String uri = hreq.getRequestURI();
		
		// Do nothing if this isn't an html request
		if(!(uri.endsWith(".html"))) {
			chain.doFilter(req,resp);
			return;
		}
		
		String conPath = hreq.getContextPath();

		String jlxPath= uri.replaceFirst(conPath, "");
		jlxPath = sc.getRealPath(jlxPath);
		
		//Assume this ends with .html
		jlxPath = jlxPath.substring(0, jlxPath.length()-5) + ".jlx";
		
		try {

			Drawing d = new Drawing();
			d.load(jlxPath);
		 
			//Check if this user has access to this drawing!	
			LiteYukonUser user = (LiteYukonUser) hreq.getSession(false).getAttribute(LoginController.YUKON_USER);
			if( AuthFuncs.checkRole(user, d.getMetaElement().getRoleID()) != null) {
				htmlGenerator.generate(hres.getWriter(), d);
			}
		}
		catch(Exception e ) {
			CTILogger.error(e);
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

}

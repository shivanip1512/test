/*
 * Created on Nov 5, 2003
 */
package com.cannontech.cttp.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.cache.PILCommandCache;
import com.cannontech.cttp.Cttp;

/**
 * Handles cttp.
 * @author aaron
 */
public class CttpServlet extends HttpServlet {

	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		resp.setContentType("text/xml");
				
		Cttp cttp = Cttp.getInstance();
		try {		
			cttp.handleMessage(req.getInputStream(), resp.getOutputStream());
		}
		catch(Throwable e) {
			CTILogger.error("ouch", e);
		}
		}


	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);
		//make sure the cache is fired up
		PILCommandCache.getInstance();
	}

}

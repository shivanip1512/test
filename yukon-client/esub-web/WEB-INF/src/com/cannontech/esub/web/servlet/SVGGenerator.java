package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description Here
 * @author alauinger
 */
public class SVGGenerator extends HttpServlet {


	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		super.doGet(req, resp);
		
		log("svg generator invoked-Get");
		
		resp.setContentType("text/html");
		
		Writer w = resp.getWriter();
		
		w.write("svg generator invoked");
		w.flush();
		
		log("svg generator done-Get");
	}

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);
		log("loaded...");
	}

	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
/*	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		super.service(req, resp);
		
		log("svg generator invoked");
		
		resp.setContentType("text/html");
		
		resp.getOutputStream().write("svg gen invoked".getBytes());
//		resp.getWriter().write("svg generator invoked");
		//resp.getWriter().flush();
		
		log("svg generator done");
	}*/

}

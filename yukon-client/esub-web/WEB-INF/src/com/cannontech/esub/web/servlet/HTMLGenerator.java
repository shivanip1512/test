package com.cannontech.esub.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.esub.editor.Drawing;

/**
 * Description Here
 * @author alauinger
 */
public class HTMLGenerator extends HttpServlet {

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		//super.doGet(req, resp);
		
		resp.setContentType("text/html");
		
		ServletContext sc = getServletContext();
		String uri = req.getRequestURI();
		String conPath = req.getContextPath();

		String jlxPath= uri.replaceFirst(conPath, "");
		jlxPath = sc.getRealPath(jlxPath);
		
		//Assume this ends with .html
		jlxPath = jlxPath.substring(0, jlxPath.length()-5) + ".jlx";
		
		
		BufferedReader rdr;
		Writer w = resp.getWriter();
		
		try {

			Drawing d = new Drawing();
			d.load(jlxPath);
			
			//DrawingUpdater du = new DrawingUpdater(d);
			//du.run();
			/*LxGraph g = new LxGraph();
			g.read(jlxPath);
			*/
			//com.cannontech.esub.util.SVGGenerator gen = new com.cannontech.esub.util.SVGGenerator();
			//gen.generate(w, d);	
			
			com.cannontech.esub.util.HTMLGenerator gen = new com.cannontech.esub.util.HTMLGenerator();
			gen.generate(w, d);
		}
		catch(Exception e ) {
			e.printStackTrace(new PrintWriter(w));
			//log(e.getMessage());
		}

	}

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);		
	}

}

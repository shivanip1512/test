package com.cannontech.esub.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.loox.jloox.LxGraph;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.util.DrawingUpdater;


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
		
		resp.setContentType("image/svg+xml");
		
		ServletContext sc = getServletContext();
		String uri = req.getRequestURI();
		String conPath = req.getContextPath();

		String jlxPath= uri.replaceFirst(conPath, "");
		jlxPath = sc.getRealPath(jlxPath);
		
		//Assume this ends with .svg
		jlxPath = jlxPath.substring(0, jlxPath.length()-4) + ".jlx";
		
		
		BufferedReader rdr;
		Writer w = resp.getWriter();
		
		try {

			Drawing d = new Drawing();
			d.load(jlxPath);
			
			DrawingUpdater du = new DrawingUpdater(d);
			du.run();
			/*LxGraph g = new LxGraph();
			g.read(jlxPath);
			*/
			com.cannontech.esub.util.SVGGenerator gen = new com.cannontech.esub.util.SVGGenerator();
			gen.generate(w, d);	
		}
		catch(Exception e ) {
			e.printStackTrace(new PrintWriter(w));
			//log(e.getMessage());
		}

		
/*		log("request URI: " + req.getRequestURI());
		
		String uri = req.getRequestURI();
		
		if( uri.endsWith(".svg") ) {
			uri = uri.substring(0, uri.length()-4);
		}
		
		log("new uri: " + uri);
		
		Properties sysprops = System.getProperties();
		String sep = sysprops.getProperty("file.separator");
		String path = null;
		
		try {
  			ServletContext sc = this.getServletContext();
		    path = sc.getRealPath(uri);
		    log(path);
		    (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(path + c + 
		    
  			FileOutputStream fos = new FileOutputStream(path + c + filename);
  byte[] bytes = notes.getBytes();
  fos.write(bytes);
  out.println("Created " + filename + " in " + path);
} catch (Exception e) {
  //out.println(e.toString());*/
}
	
		
	

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);
		log("loaded...");
	}

}

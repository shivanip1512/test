package com.cannontech.servlet;

/**
 * GraphGenerator generates a gif, png, svg, or jpg image of a graph based on the state
 * of an instance of com.cannontech.graph.GraphBean stored in the session.
 *
 * If an instance of GraphBean isn't found, one is created and placed in the session.
 * 
 * All parameters are optional. 
 * (ie if you provide no parameters the same graph will be generated as last time)
 * 
 * 	
 * Parameters
 * 
 * gdefid	- GraphDefinition ID
 * start	- Start date string, see dtFormat optional parameter for formatting
 * period	- See com.cannontech.util.ServletUtil for valid period strings
 * view		- See com.cannontech.graph.model.TrendModelType for valid view strings
 * option	- ?See com.cannontech.graph.model.TrendModelType for valid option masks(?)
 * format	- gif | png | jpg | svg
 * dtFormat - overrides the date format, see java.text.SimpleDateFormat
 * 				Default is MM:dd:yyyy:HH:mm:ss
 * width	- Width of the generated graph
 * height	- Height of the generated graph	
 * 
 * @author: Aaron Lauinger
 * @author: Stacey Nebben
 */

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;

public class GraphGenerator extends javax.servlet.http.HttpServlet {
		
	// Maybe this could be moved to a location so that it can
	// be shared by various clients (jsps/servlets) of the GraphBean
	private static final String GRAPH_BEAN_SESSION_KEY = "graphBean";	
	
/**
 * 
 * Creation date: (12/9/99 3:24:30 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public synchronized void  doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	try
	{	 	
		HttpSession session = req.getSession(false);
				
		com.cannontech.graph.GraphBean localBean = (com.cannontech.graph.GraphBean)session.getAttribute(GRAPH_BEAN_SESSION_KEY);
		if(localBean == null)
		{
			session.setAttribute(GRAPH_BEAN_SESSION_KEY, new com.cannontech.graph.GraphBean());
			localBean = (com.cannontech.graph.GraphBean)session.getAttribute(GRAPH_BEAN_SESSION_KEY);
		}

		{			
			String param;
			String param2;
			
			param = req.getParameter("gdefid");
			if( param != null)
				localBean.setGdefid(Integer.parseInt( param));
				
			param = req.getParameter("start");
			if( param != null) 								
				localBean.setStart(param);

			param = req.getParameter("period");
			if( param != null)
				localBean.setPeriod(param);
	
			param = req.getParameter("view");
			if( param != null)
				localBean.setViewType(Integer.parseInt(param));
	
			param = req.getParameter("option");
			if( param != null)
				localBean.setOption(Integer.parseInt(param));
	
			param = req.getParameter("format");
			if( param != null)
				localBean.setFormat(param);

			param = req.getParameter("width");
			param2 = req.getParameter("height");
			if(param != null && param2 != null) {
				localBean.setSize(Integer.parseInt(param),Integer.parseInt(param2));
			}
		}

		localBean.updateCurrentPane();
		javax.servlet.ServletOutputStream out = null;
		try
		{	
			// Try to defeat caching
			resp.setHeader("Cache-Control","no-store"); //HTTP 1.1
			resp.setHeader("Pragma","no-cache"); 		//HTTP 1.0
	 		resp.setDateHeader ("Expires", 0); 			//prevents caching at the proxy server
	 	
	    	out = resp.getOutputStream();
	    	
	    	localBean.encode(out);
			out.flush();
		}
		catch( java.io.IOException ioe )
		{
			ioe.printStackTrace();
		}			

	}
	catch( Throwable t )
	{
		CTILogger.error("An exception was throw in GraphGenerator:  ", t);
		t.printStackTrace();
	}
}

/**
 * POST method not supported
 * Creation date: (12/9/99 3:39:10 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	resp.sendError( javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}
}

package com.cannontech.servlet;

/**
 * GraphGenerator generates a png, svg, or jpg image of a graph based on the parameters
 * passed to it.
 *
 * Parameters:
 *	graphBean - takes in an instance of the Graphbean

 * Creation date: (12/9/99 3:23:27 PM)
 * @author: Aaron Lauinger
 * updated for bean use - 11/20/02 Stacey Nebben
 */

import com.cannontech.common.util.LogWriter;
import com.cannontech.database.data.web.User;

public class GraphGenerator extends javax.servlet.http.HttpServlet {

	private static com.cannontech.common.util.LogWriter logger = null;
	private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
/**
 * GraphGenerator constructor comment.
 */
public GraphGenerator() {
	super();
}
/**
 * Insert the method's description here.
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
		javax.servlet.http.HttpSession session = req.getSession(false);
		if (session == null)
			resp.sendRedirect("/login.jsp");

		resp.setHeader("Cache-Control","no-store"); //HTTP 1.1
		resp.setHeader("Pragma","no-cache"); 		//HTTP 1.0
	 	resp.setDateHeader ("Expires", 0); 			//prevents caching at the proxy server

		com.cannontech.graph.GraphBean localBean = (com.cannontech.graph.GraphBean)session.getAttribute("graphBean");
		if(localBean == null)
			System.out.println("!!! BEAN IS NULL !!! ");
			
		localBean.updateCurrentPane();
		javax.servlet.ServletOutputStream out = null;
		try
		{	
	    	out = resp.getOutputStream();
			localBean.getGraph().encodePng(out);
			out.flush();
		}
		catch( java.io.IOException ioe )
		{
			ioe.printStackTrace();
		}			

	}
	catch( Throwable t )
	{
		logger.log("An exception was throw in GraphGenerator:  " + t.getMessage(), LogWriter.ERROR );
		t.printStackTrace();
	}
	finally
	{						
		System.gc();
	}
}
/**
 * Insert the method's description here.
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
/**
 * Initialzes the logger and sets the swing look and feel.
 * Not sure if the look and feel setting is necessary.
 * Creation date: (12/7/99 9:55:11 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	try
	{
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

		synchronized (this)
	{		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream("graphgen.log");
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter("GraphGenerator", com.cannontech.common.util.LogWriter.DEBUG, writer);

			logger.log("Starting up....", LogWriter.INFO );
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}		
	}
	
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	
	super.init(config);
}
}

package com.cannontech.servlet;

/**
 * PointDisplayColor expects one parameter,
 * id - the id of the point
 *
 * outputs the 32 bit hex value of the color the point should be displayed in.
 * 
 * An instance of PointChangeCache must available in the servlets context
 * accessible under the key PointChangeCache.SERVLET_CONTEXT_ID
 *
 * Creation date: (1/2/2001 1:31:51 PM)
 * @author: Aaron Lauinger
 */
public class PointDisplayColor extends javax.servlet.http.HttpServlet {
	private com.cannontech.common.util.LogWriter logger = null;

	private final String normalColor = "999999";
	private final String alarmColor =  "FF0000";
/**
 * PointDisplayColor constructor comment.
 */
public PointDisplayColor() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 1:39:38 PM)
 * @param config javax.servlet.ServletConfig
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	super.init(config);
	try
	{
		java.io.FileOutputStream out = new java.io.FileOutputStream("ptalarmcolor.log");
		java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
		logger = 
			new com.cannontech.common.util.LogWriter(
				"PointDisplayColor", 
				com.cannontech.common.util.LogWriter.DEBUG, 
				writer); 

		logger.log("Starting up....", com.cannontech.common.util.LogWriter.INFO);
	}
	catch (java.io.FileNotFoundException e)
	{
		e.printStackTrace();
	}
}
/**
 * service method comment.
 */
public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{	
	resp.setContentType("text/plain");
	
	//required parameter
	Object idStr = req.getAttribute("id");
	PointChangeCache pCC = (PointChangeCache) getServletContext().getAttribute(PointChangeCache.SERVLET_CONTEXT_ID);

	if( pCC == null )
	{
		resp.getWriter().write("-xConn");
		resp.getWriter().flush();
		logger.log("Unable to find a point change cache in the servlet context!", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

	if( idStr == null )
	{
		resp.getWriter().write("-xid");
		resp.getWriter().flush();
		logger.log("Didn't receive a point id returning 'xid'", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

	com.cannontech.message.dispatch.message.Signal signal =
		pCC.getSignal( Long.parseLong((String) idStr) );

	if( signal != null )
	{
		logger.log("id:  " + idStr + " output:  " + alarmColor, com.cannontech.common.util.LogWriter.DEBUG );
		resp.getWriter().write(alarmColor);
	}
	else
	{
		logger.log("id:  " + idStr + " output:  " + normalColor, com.cannontech.common.util.LogWriter.DEBUG );
		resp.getWriter().write(normalColor);
	}

	resp.getWriter().flush();
}
}

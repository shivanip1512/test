package com.cannontech.servlet;

/**
 * Insert the type's description here.
 * Creation date: (3/30/00 3:15:02 PM)
 * @author: 
 */
public class SignalDescription extends javax.servlet.http.HttpServlet {

	private static final java.text.NumberFormat format;
	static //static initializer for format
	{
		format = new java.text.DecimalFormat();
		format.setMaximumFractionDigits(2);
	}
	
	private com.cannontech.common.util.LogWriter logger = null;
/**
 * PointData constructor comment.
 */
public SignalDescription() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/30/00 4:11:41 PM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException
{
	super.init(config);

	// Add a point change cache to our servlet context
	//PointChangeCache pCC = new PointChangeCache();
	//pCC.connect(); -- removed since point data also does this
	
	//getServletContext().setAttribute(PointChangeCache.SERVLET_CONTEXT_ID, pCC );
	
	try
	{
		java.io.FileOutputStream out = new java.io.FileOutputStream("signaldesc.log");
		java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
		logger = 
			new com.cannontech.common.util.LogWriter(
				"SignalDescription", 
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
 * Insert the method's description here.
 * Creation date: (3/30/00 3:15:32 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void service(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{
	resp.setContentType("text/plain");
	
	//required parameter
	Object idStr = req.getAttribute("id");
	PointChangeCache pCC = (PointChangeCache) getServletContext().getAttribute(PointChangeCache.SERVLET_CONTEXT_ID);

	if( pCC == null )
	{
		resp.getWriter().write("xConn");
		resp.getWriter().flush();
		logger.log("Unable to find a point change cache in the servlet context!", com.cannontech.common.util.LogWriter.INFO );
		return;
	}
	
	if( idStr == null )
	{
		resp.getWriter().write("xid");
		resp.getWriter().flush();
		logger.log("Didn't receive a point id returning 'xid'", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

		
	Long id = null;
	try
	{
		id = new Long(idStr.toString());
	}
	catch( NumberFormatException nfe )
	{
		resp.getWriter().write("xid");
		resp.getWriter().flush();
		logger.log("Couldn't conver point id into a long returning 'xid'", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

	com.cannontech.message.dispatch.message.Signal signal = (com.cannontech.message.dispatch.message.Signal) pCC.getSignal( id.longValue() );

	if( signal == null )
	{		
		return;
	}

	resp.getWriter().write( signal.getDescription() );
	resp.getWriter().flush();	
}
}

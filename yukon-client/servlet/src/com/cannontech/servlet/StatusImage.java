package com.cannontech.servlet;

/**
 * Insert the type's description here.
 * Creation date: (8/22/00 2:08:10 PM)
 * @author:  Aaron Lauinger 
 */
public class StatusImage extends javax.servlet.http.HttpServlet {
	private com.cannontech.common.util.LogWriter logger = null;
/**
 * StatusImage constructor comment.
 */
public StatusImage() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 2:08:48 PM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	super.init(config);
	synchronized (this)
	{		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream("statusimage.log", true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter("StatusImage", com.cannontech.common.util.LogWriter.DEBUG, writer);
			logger.log("Starting up....", com.cannontech.common.util.LogWriter.INFO);
		}
		catch (java.io.FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
		
}
/**
 * Parameters:
 * id 	 	- 	The id of the status point
 * [0..n]	-  	The name of the image to display
 */
public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{ 
	String image = null;
	
	String idStr = (String) req.getAttribute("id");
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
	
	com.cannontech.message.dispatch.message.PointData data = (com.cannontech.message.dispatch.message.PointData) pCC.getValue( id.longValue() );

	if( data == null )
	{
		resp.getWriter().write("nodata");
		resp.getWriter().flush();
		logger.log("Couldn't find a value for the pointid", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

	String val = String.valueOf((long) data.getValue());

	for( java.util.Enumeration e = req.getAttributeNames(); e.hasMoreElements(); )
	{
		String status = (String) e.nextElement();

		if( status.equals(val) )
		{
			image = (String) req.getAttribute(status);
			break;
		}
	}

	if( image == null )
	{
		resp.getWriter().write("noimage");
		resp.getWriter().flush();
		logger.log("Couldn't find an image", com.cannontech.common.util.LogWriter.INFO );
		return;
	}

	resp.getWriter().write(image);	
	resp.getWriter().flush();
	
	return;
}
}

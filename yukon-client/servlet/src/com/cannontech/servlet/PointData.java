package com.cannontech.servlet;

import com.cannontech.common.cache.PointChangeCache;

/**
 * Insert the type's description here.
 * Creation date: (3/30/00 3:15:02 PM)
 * @author: 
 */
public class PointData extends javax.servlet.http.HttpServlet {

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
public PointData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/00 12:14:36 PM)
 */
public void destroy() 
{
	/*PointChangeCache pCC = (PointChangeCache) getServletContext().getAttribute(PointChangeCache.SERVLET_CONTEXT_ID);

	if( pCC != null )
	{
		pCC.disconnect();
	}
	
	logger.log("destroyed....", com.cannontech.common.util.LogWriter.INFO );
	
	super.destroy();*/
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/00 12:19:07 PM)
 */
public void finalize() throws Throwable
{
	logger.log("finalized....", com.cannontech.common.util.LogWriter.INFO );
	super.finalize();
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
	PointChangeCache.getPointChangeCache().connect();
	// Add a point change cache to our servlet context
/*	PointChangeCache pCC = new PointChangeCache();
	pCC.connect();
	
	getServletContext().setAttribute(PointChangeCache.SERVLET_CONTEXT_ID, pCC );
	
	try
	{
		java.io.FileOutputStream out = new java.io.FileOutputStream("pointdata.log");
		java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
		logger = 
			new com.cannontech.common.util.LogWriter(
				"PointData", 
				com.cannontech.common.util.LogWriter.DEBUG, 
				writer); 

		logger.log("Starting up....", com.cannontech.common.util.LogWriter.INFO);
	}
	catch (java.io.FileNotFoundException e)
	{
		e.printStackTrace();
	}*/
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

	//Sometimes we need getParameter instead (why is this?)
	if( idStr == null )
		idStr = req.getParameter("id");
	
	Object decStr = req.getAttribute("dec");
	
	if( decStr == null )
		decStr = req.getParameter("dec");
	
	Object lastChangeStr = req.getAttribute("lastchange");
	
	if( lastChangeStr == null )
		lastChangeStr = req.getParameter("lastchange");
				
	int numberOfDecimals = -1;
	
	try {
		if( decStr != null ) 
			numberOfDecimals = Integer.parseInt(decStr.toString());
	}
	catch( NumberFormatException ne) {
		ne.printStackTrace();
	}
		
	PointChangeCache pCC = PointChangeCache.getPointChangeCache(); //(PointChangeCache) getServletContext().getAttribute(PointChangeCache.SERVLET_CONTEXT_ID);

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
	
	if( idStr.toString().equalsIgnoreCase("lastchange"))
	{
		java.util.Date change = pCC.getLastChange();
		if( change != null )
			resp.getWriter().write(pCC.getLastChange().toString());
		else
			resp.getWriter().write("-");
			
		resp.getWriter().flush();
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
		resp.getWriter().write("-");
		resp.getWriter().flush();
		return;
	}

	if( lastChangeStr != null ) 
	{
		resp.getWriter().write( data.getPointDataTimeStamp().toString() );
	}
	else
	if( data.getType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
	{				
		resp.getWriter().write( pCC.getState( data.getId(), data.getValue(), "yukon"));
	}
	else
	{
		if( numberOfDecimals != -1 ) {
			java.text.DecimalFormat decFormat = new java.text.DecimalFormat();
			decFormat.setMinimumFractionDigits(numberOfDecimals);
			decFormat.setMaximumFractionDigits(numberOfDecimals);
			resp.getWriter().write( decFormat.format(data.getValue()) );			
		}
		else {
			resp.getWriter().write( format.format(data.getValue()) );
		}
	}
	
	resp.getWriter().flush();
}
}

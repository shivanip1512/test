package com.cannontech.servlet;

import com.cannontech.clientutils.CTILogger;

/**
 * GraphGenerator generates a gif or jpg image of a graph based on the parameters
 * passed to it.
 * My most sincere apologies for the doGet method.  May it be rewritten someday.
 *
 * Parameters:
 *	gdefid = The graph definition id
 *  width = width of the image
 *  height = height of the image
 *  format = gif | jpg
 *  start = mm:dd:yyyy:hh:mm:ss
 *  end = mm:dd:yyyy:hh:mm:ss
 *  db = database alias
 *  model = [ (com.cannontech.graph.model.GraphModelType.DATA_VIEW_MODEL = 0) |
 *			  (com.cannontech.graph.model.GraphModelType.BAR_GRAPH_MODEL = 1) | 
 *			  (com.cannontech.graph.model.GraphModelType.LOAD_DURATION_CURVE_MODEL = 2) ] 
 *			  (default = 0)
 * Creation date: (12/9/99 3:23:27 PM)
 * @author: 
 */

public class TabularDataGenerator extends javax.servlet.http.HttpServlet {
	
	static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	
/**
 * Creation date: (12/9/99 3:24:30 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	try
	{		
		CTILogger.debug("doGet invoked");	
		javax.servlet.http.HttpSession session = req.getSession(false);
	
		if (session == null)
			resp.sendRedirect("/login.jsp");
	
		resp.setHeader("Cache-Control","no-store"); //HTTP 1.1
		resp.setHeader("Pragma","no-cache"); //HTTP 1.0
	 	resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server

	 	//User user = (User) session.getValue("USER");

		//Grab the parameters
		int gDefId = Integer.parseInt( (String) req.getAttribute("gdefid") );			
		java.util.Date start = dateFormat.parse( (String) req.getAttribute("start"));		
		java.util.Date end = dateFormat.parse( (String) req.getAttribute("end"));
		String dbAlias = req.getAttribute("db").toString();
		int modelType = com.cannontech.graph.model.TrendModelType.LINE_VIEW;
		String modelTypeStr = req.getParameter("model");
		if( modelTypeStr != null )
			modelType = Integer.parseInt(modelTypeStr);
			
		// page indicates the page requested which happens to be
		// the day between the start and end, starting with 1		
		String pageStr = (String) req.getAttribute("page");
		
		// Create the graph def with the given id and retrieve it from the db	
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Integer(gDefId));		
	
		if (gDef != null)
		{			
			java.sql.Connection conn = null;
			try
			{
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
				gDef.setDbConnection(conn);
				gDef.retrieve();

				// Lose the reference to the connection
				gDef.setDbConnection(null);
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();		 
			}
			finally
			{   //make sure to close the connection
				try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
			}

			// If a page is indicated, just do that day....
			if( pageStr != null )
			{				
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime(start);
				
				int startDay = cal.get( java.util.Calendar.DAY_OF_YEAR ) + 
								Integer.parseInt(pageStr) - 1;
								
				cal.set( java.util.Calendar.DAY_OF_YEAR, startDay );
				start = cal.getTime();

				cal.setTime(end);				
				cal.set( java.util.Calendar.DAY_OF_YEAR, startDay + 1);
				end = cal.getTime();				
			}
			

			gDef.getGraphDefinition().setStartDate( start );
			gDef.getGraphDefinition().setStopDate( end );							
				
			com.cannontech.graph.Graph graph = new com.cannontech.graph.Graph();
			graph.setDatabaseAlias(dbAlias);			
			graph.setCurrentGraphDefinition(gDef);
	
			graph.setViewType(modelType);
			
			// Define the peak series....
			for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
			{
				com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

			}
			

		
			graph.update();

			javax.servlet.ServletOutputStream out = null;
			
			try
			{
				//grab the stream and encode the html into it				
				System.out.println("Before resp.getOutputStream()");
		    	out = resp.getOutputStream();
				System.out.println("After resp.getOutputStream()");
				graph.encodeTabularHTML(out);
				out.flush();
			}
			catch( java.io.IOException ioe )
			{
				ioe.printStackTrace();
			}			
		}
		else
		{
			CTILogger.error("Null graphdefinition!");
		}	

	}
	catch( Throwable t )
	{
		CTILogger.error("Exception occurd in TabularDataGenerator:  ", t);
		t.printStackTrace();
	}
	finally
	{						
		System.gc();
	}		
}
}

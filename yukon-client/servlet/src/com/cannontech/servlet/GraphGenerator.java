package com.cannontech.servlet;

/**
 * GraphGenerator generates a gif or jpg image of a graph based on the parameters
 * passed to it.
 * My most sincere apologies for the doGet method.  May it be rewritten someday.
 *
 * Parameters:
 *	gdefid = The graph definition id
 *  width = width of the image
 *  height = height of the image
 *  format = gif | svg
 *  start = mm:dd:yyyy:hh:mm:ss
 *  end = mm:dd:yyyy:hh:mm:ss
 *  db = database alias to use
 *  model = [ (com.cannontech.graph.model.GraphModelType.DATA_VIEW_MODEL = 0) |
 *			  (com.cannontech.graph.model.GraphModelType.BAR_GRAPH_MODEL = 1) | 
 *			  (com.cannontech.graph.model.GraphModelType.LOAD_DURATION_CURVE_MODEL = 2) ] 
 *			  (default = 0)
 *  loadfactor = [ true | false ] (default = true) Determines whether to show loadfactor
 * Creation date: (12/9/99 3:23:27 PM)
 * @author: Aaron Lauinger
 */

import com.cannontech.common.util.LogWriter;

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

	 	//User user = (User) session.getValue("USER");
	 	
		//Grab the parameter
			
		int gDefId= Integer.parseInt( req.getParameter("gdefid") );
		
		int width = Integer.parseInt( req.getParameter("width") );
		int height = Integer.parseInt( req.getParameter("height") );
		String format = req.getParameter("format");
		String dbAlias = req.getParameter("db");
		String loadFactor = req.getParameter("loadfactor");		
		int modelType = com.cannontech.graph.model.GraphModelType.DATA_VIEW_MODEL;
		
		boolean showLoadFactor = true;
		
		if( format == null )
				format = "gif";
	
		//default to showing load factor
		if( loadFactor != null && loadFactor.equalsIgnoreCase("false"))
			showLoadFactor = false;
				
		java.util.Date start = dateFormat.parse(req.getParameter("start"));		
		java.util.Date end = dateFormat.parse(req.getParameter("end"));

		String modelTypeStr = req.getParameter("model");
		if( modelTypeStr != null )
			modelType = Integer.parseInt(modelTypeStr);
			
		
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Long(gDefId));
	
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
					
			gDef.getGraphDefinition().setStartDate( start );
			gDef.getGraphDefinition().setStopDate( end );
						
			com.cannontech.graph.Graph graph = new com.cannontech.graph.Graph();
			graph.setDatabaseAlias(dbAlias);
			graph.setSize(width, height);
			graph.setCurrentGraphDefinition(gDef);
			graph.setShowLoadFactor(showLoadFactor);
			graph.setSeriesType( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);

			graph.setModelType(modelType);

			// Define the peak series....
			for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
			{
				com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

				if ( graph.isPeakSeries( gds.getType()) )
				{
					graph.setHasPeakSeries( true );
					break;
				}
			}
			
			graph.update();			

			javax.servlet.ServletOutputStream out = null;
			
			try
			{				
		    	out = resp.getOutputStream();		   		
				if( format.equalsIgnoreCase("gif") )								
					graph.encodeGif(out);
				else
				if( format.equalsIgnoreCase("jpg") )
					;//graph.encodeJPG(out);
				else
				if( format.equalsIgnoreCase("svg") )
					//graph.encodeSVG(out);
					
				out.flush();
			}
			catch( java.io.IOException ioe )
			{
				ioe.printStackTrace();
			}			
		}
		else
		{
				logger.log("Graphdefinition is null!", LogWriter.ERROR );
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

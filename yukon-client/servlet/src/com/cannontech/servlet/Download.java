package com.cannontech.servlet;

/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import javax.servlet.http.HttpServlet;

import com.cannontech.common.util.LogWriter;

public class Download extends HttpServlet
{

	private static com.cannontech.common.util.LogWriter logger = null;
	static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
/**
 * MessageServlet constructor comment.
 */
public Download() {
	super();
}
public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	resp.sendError( javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);	
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 9:55:11 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	try
	{		
		synchronized (this)
	{		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream("download.log");
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter("Download", com.cannontech.common.util.LogWriter.DEBUG, writer);

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

	String []exportArray = null;

/**
 * Insert the method's description here.
 * Creation date: (12/9/99 3:39:10 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
	throws javax.servlet.ServletException, java.io.IOException {
	try
	{
		logger.log("doPost invoked", LogWriter.DEBUG);
		javax.servlet.http.HttpSession session = req.getSession(false);

		if (session == null)
			resp.sendRedirect("/login.jsp");

		resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server

		int width = 556;
		int height = 433;
		//Grab the parameters
		if( req.getParameter("width") != null)
			width = Integer.parseInt( req.getParameter("width") );
		if( req.getParameter("height") != null)
			height = Integer.parseInt( req.getParameter("height") );
		
		String extension = req.getParameter("ext");
		int gDefId = Integer.parseInt(req.getParameter("gdefid"));
		java.util.Date start = dateFormat.parse(req.getParameter("start"));
		java.util.Date end = start; //just as an init!
		String dbAlias = req.getParameter("db").toString();
		int selectedPane = com.cannontech.graph.GraphDefines.GRAPH_PANE;
		int modelType = com.cannontech.graph.model.TrendModelType.LINE_VIEW;
		String period = req.getParameter("period");
		System.out.println("PERIOD ====== " + period);
		String modelTypeStr = req.getParameter("model");
		if (modelTypeStr != null)
			modelType = Integer.parseInt(modelTypeStr);

		// page indicates the page requested which happens to be
		// the day between the start and end, starting with 1		
		//String pageStr = (String) req.getParameter("page");

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
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{ //make sure to close the connection
				try
				{
					if (conn != null)
						conn.close();
				}
				catch (java.sql.SQLException e2)
				{
					e2.printStackTrace();
				};
			}

			// If a page is indicated, just do that day....
			//if (pageStr != null)
			{
				end = com.cannontech.util.ServletUtil.getEndingDateOfInterval( start, period );
				start = com.cannontech.util.ServletUtil.getStartingDateOfInterval( start, period );

				gDef.getGraphDefinition().setStartDate(start);
				System.out.println("START ====== " + start);				
				gDef.getGraphDefinition().setStopDate(end);
				System.out.println("STOP ====== " + end);

			
				//java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				//cal.setTime(start);

				//int startDay = cal.get(java.util.Calendar.DAY_OF_YEAR);
				////+ Integer.parseInt(pageStr) - 1;

				//cal.set(java.util.Calendar.DAY_OF_YEAR, startDay);
				//start = cal.getTime();

				//cal.setTime(end);
				//cal.set(java.util.Calendar.DAY_OF_YEAR, startDay + period);
				//end = cal.getTime();
			}

			//gDef.getGraphDefinition().setStartDate(start);
			//gDef.getGraphDefinition().setStopDate(end);

			com.cannontech.graph.Graph graph = new com.cannontech.graph.Graph();
			graph.setDatabaseAlias(dbAlias);
			graph.setCurrentGraphDefinition(gDef);
		
			graph.setViewType(modelType);
			if( width > 0 && height > 0)
				graph.setSize(width, height);

			// Define the peak series....
			for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
			{
				com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

				
			}

			System.out.println(" *** SERVLET, RIGHT BEFORE THE GRAPH.UPDATE()!!!");
			graph.update();
			System.out.println(" *** SERVLET, RIGHT AFTER THE GRAPH.UPDATE()!!!");
			javax.servlet.ServletOutputStream out = null;

			try
			{
				System.out.println(" *** SERVLET, RESP.GETOUTPUTSTREAM!!!");
				out = resp.getOutputStream();

				if (extension.equalsIgnoreCase("csv"))
				{
					resp.setContentType("text/comma-separated-values");

					com.cannontech.graph.model.TrendModel tModel = graph.getTrendModel();

					com.cannontech.graph.exportdata.ExportDataFile eDataFile = new com.cannontech.graph.exportdata.ExportDataFile
					(selectedPane, graph.getFreeChart(), graph.getTrendModel().getChartName(), graph.getTrendModel());

					eDataFile.setExtension(extension);
					String[] data = eDataFile.createCSVFormat();
					if( data != null)
						for (int i = 0; i < data.length; i++)
							out.write(data[i].getBytes());
				}
				else if (extension.equalsIgnoreCase("pdf"))
				{
					resp.setContentType("application/pdf");
					graph.encodePDF(out);
				}
				else if (extension.equalsIgnoreCase("jpeg"))
				{
					resp.setContentType("image/jpeg");
					graph.encodeJpeg(out);
				}
				else if (extension.equalsIgnoreCase("png"))
				{
					resp.setContentType("image/x-png");
					graph.encodePng(out);
				}
				else if (extension.equalsIgnoreCase(""))
				{
					resp.setContentType("image/x-png");
					graph.encodePng(out);
				}


				out.flush();
				System.out.println("*** Just tried to flush the out!");
			}
			catch (java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		else
		{
			logger.log("Null graphdefinition!", LogWriter.ERROR);
		}

	}
	catch (Throwable t)
	{
		logger.log("Exception occurd in TabularDataGenerator:  " + t.getMessage(), LogWriter.ERROR);
		t.printStackTrace();
	}
	finally
	{
		System.out.println(" *** SERVLET, FINALLY!!!");
		System.gc();
	}
}
}
package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 11:51:56 AM)
 * @author: 
 */
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.GenericDBCacheHandler;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.WebGraphRole;


public class WebGraph implements Runnable
{
	private GregorianCalendar nextRunTime = null;
	private Graph graph = null;
	
	private java.util.List allPredefinedGraphsList = null;
	public java.util.Date startDate = null;

	private String homeDirectory = null;
	private Integer createTimeInterval = null;//interval in seconds between calculations



	private Thread strtThread = null;

	//since this guy uses the cache, he better care about DBUpdates
	private GenericDBCacheHandler dbCacheHandler = null;


	/**
	 * WebGraph constructor comment.
	 */
	public WebGraph()
	{
		super();
		graph = new Graph();	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 2:50:28 PM)
	 */
	private void createGDefReports(com.cannontech.database.data.lite.LiteGraphDefinition liteGraphDef)
	{	
		int gdefid = liteGraphDef.getGraphDefinitionID();
		
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID( new Integer( gdefid ));
	
		if (gDef != null)
		{		
			java.sql.Connection conn = null;
			try
			{
				conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
				gDef.setDbConnection(conn);
				gDef.retrieve();
				gDef.setDbConnection(null);
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();		 
			}
			finally
			{   //make sure to close the connection
				try
				{ 
					if( conn != null ) conn.close(); 
				}
				catch( java.sql.SQLException e2 ) 
				{ 
					e2.printStackTrace(); 
				}
			}
					
						
			//graph.setSize(width, height);
			graph.setStartDate(getStartDate());
			graph.setGraphDefinition(gDef);
			graph.setViewType(GraphRenderers.LINE);
	
			// Graph .png file creation
			String fileName = getFileName(GraphRenderers.LINE, gDef.getGraphDefinition().getName());
			graph.setUpdateTrend(true);
			graph.update();
			writePNG(fileName);
	
			StringBuffer buf = new StringBuffer("<HTML><LINK REL=\"stylesheet\" HREF=\"CannonStyle.ccs\" TYPE=\"text/css\"><CENTER>");		
			//Tabular .html file creation.
			fileName = getFileName( GraphRenderers.TABULAR, gDef.getGraphDefinition().getName());
			buf.append(getHTMLBuffer( new TabularHtml()));
	
			//Summary .html file creation
			fileName = getFileName( GraphRenderers.SUMMARY, gDef.getGraphDefinition().getName());
			buf.append("<BR><BR>");
			buf.append( getHTMLBuffer(new PeakHtml()));
			buf.append( getHTMLBuffer(new UsageHtml()));
			buf.append("</CENTER></HTML>");
			writeHTML( buf.toString() , fileName );
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/4/2000 2:27:20 PM)
	 */
	private void figureNextRunTime()
	{
		if( getNextRunTime() == null )
		{
			nextRunTime = new GregorianCalendar();
		}
		else
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			long nowInMilliSeconds = tempCal.getTime().getTime();
			long aggIntInMilliSeconds = getWebgraphRunInterval().longValue() * 1000;
			long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;
	
			if( tempSeconds < (nextRunTime.getTime().getTime() + aggIntInMilliSeconds) )
			{
				tempSeconds += aggIntInMilliSeconds;
			}
			
			nextRunTime = new GregorianCalendar();
			nextRunTime.setTime(new java.util.Date(tempSeconds));
			
			getStartDate();
		}
		com.cannontech.clientutils.CTILogger.info("Next RunTime Interval: " + nextRunTime.getTime());
	}
	/**
	 * The model[][] where the first [] holds 0:LeftModels and 1:RightModels and
	 *  the second[] is all of the points associated with the LorR model it is.
	 * Insert the method's description here.
	 * Creation date: (5/17/2001 4:49:15 PM)
	 * @return GraphModelInterface [][]
	 */
	//public GraphModel[][] getCurrentModels()
	private TrendModel getTrendModel()
	{
	//	return graph.getCurrentModels();
		return graph.getTrendModel();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/2001 9:45:11 AM)
	 * @return java.lang.String
	 * @param fileType int
	 * @param gDefName java.lang.String
	 */
	private String getFileName(int fileType, String gDefName)
	{
		switch( fileType )
		{
			case GraphRenderers.TABULAR:
				return getHomeDirectory() + gDefName + ".html";
			
			case GraphRenderers.SUMMARY:
				return getHomeDirectory() + gDefName + ".html";

			case GraphRenderers.LINE:
			default:
				return getHomeDirectory() + gDefName + ".png";
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/2001 9:55:03 AM)
	 * @return java.lang.String
	 */
	private String getHomeDirectory()
	{
		if( homeDirectory == null )
		{
			try
			{
				java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
				homeDirectory = bundle.getString("webgraph_home_directory");
				com.cannontech.clientutils.CTILogger.info("Home Directory from config.properties: " + homeDirectory);
			}
			catch( Exception e)
			{
				homeDirectory = "C:/Temp/";
				com.cannontech.clientutils.CTILogger.info("Home Directory NOT found in config.properties, default to: " + homeDirectory);
				com.cannontech.clientutils.CTILogger.info("Add row 'webgraph_home_directory' to config.properties.");
			}
			java.io.File file = new java.io.File( homeDirectory);
			file.mkdirs();
		}
		return homeDirectory;
	}
	/**
	 * Update the Summary pane.
	 *  Calls the peaks html code and the usage code
	 * Creation date: (11/15/00 4:11:14 PM)
	 */
	private String getHTMLBuffer( HTMLBuffer htmlBuffer)
	{
		StringBuffer buf = null;
	
		buf = new StringBuffer("<HTML><CENTER>");
		
		TrendModel model = getTrendModel();
		htmlBuffer.setModel( model);
		htmlBuffer.getHtml( buf );
	
		return buf.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 12:01:31 PM)
	 * @return java.util.GregorianCalendar
	 */
	private GregorianCalendar getNextRunTime()
	{
		return nextRunTime;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/4/2000 2:27:20 PM)
	 */
	private void getPredefinedGraphs()
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			allPredefinedGraphsList = cache.getAllGraphDefinitions();
			com.cannontech.clientutils.CTILogger.info(allPredefinedGraphsList);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 3:38:05 PM)
	 * @return java.util.Date
	 */
	private java.util.Date getStartDate()
	{
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.set( java.util.Calendar.DAY_OF_YEAR, getNextRunTime().get( java.util.Calendar.DAY_OF_YEAR ) );
		tempCal.set( java.util.Calendar.HOUR_OF_DAY, 0);
		tempCal.set( java.util.Calendar.MINUTE, 0);
		tempCal.set( java.util.Calendar.SECOND, 0);
		GregorianCalendar start = new GregorianCalendar();
		start.setTime (new java.util.Date(tempCal.getTime().getTime()));
		startDate = start.getTime();
	
		return startDate;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/7/2000 11:43:39 AM)
	 * @return java.lang.Integer
	 */
	private java.lang.Integer getWebgraphRunInterval()
	{
		if (createTimeInterval == null)
		{
			try
			{
				java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
				createTimeInterval = new Integer(bundle.getString("webgraph_run_interval"));
				com.cannontech.clientutils.CTILogger.info("RunTime Interval from config.properties: " + createTimeInterval + " seconds.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				createTimeInterval = new Integer(900);
				com.cannontech.clientutils.CTILogger.info("RunTime Interval NOT found in config.properties, default to: " + createTimeInterval + " seconds.");
				com.cannontech.clientutils.CTILogger.info("Add row 'webgraph_run_interval' to config.properties.");
			}
		}
		return createTimeInterval;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 11:53:11 AM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) 
	{
/*		ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession()){
			System.exit(-1);			
		}
	  	
		if(session == null) 		
			System.exit(-1);
				
		if(!session.checkRole(WebGraphRole.ROLEID)) 
		{
		  CTILogger.info("User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.");
		  System.exit(-1);				
		}		*/
		com.cannontech.clientutils.CTILogger.info("WebGraph - Yukon Version: " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() + " - Yukon Database Version: " +com.cannontech.common.version.VersionTools.getDatabaseVersion());
	
		System.setProperty("cti.app.name", "WebGraph");
		WebGraph webGraph = new WebGraph();
		
		//start the process
		webGraph.start();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 4:47:44 PM)
	 */
	//private void writeFile(String bufferString, String fileName)
	//{
	private void writePNG( String fileName )
	{
		try
		{
			java.io.FileOutputStream fOut = new java.io.FileOutputStream(fileName);
			graph.setSize(700, 500);
			graph.encodePng(fOut);
			fOut.close();
		}
		catch (java.io.FileNotFoundException fnf)
		{
			fnf.printStackTrace();
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 4:47:44 PM)
	 */
	private void writeHTML(String outString, String fileName)
	{
	
		char [] text = outString.toCharArray();
		if (outString != null)	//otherwords this is defaulted as a pdf format.
		{
			try
			{
				java.io.FileWriter writer = new java.io.FileWriter( fileName );
	
				for( int i = 0; i < text.length; i++ )
				{
					if( new Character(text[i]) != null)
						writer.write( text[ i ] );
				}
						
				writer.close();
			}
			catch ( java.io.IOException e )
			{
				e.printStackTrace();
			}
		}
		else
			com.cannontech.clientutils.CTILogger.error("Text variable is null -> writeHTML");
		
	
	}

	/** 
	 * Start us
	 */
	public void start()
	{
		dbCacheHandler = new GenericDBCacheHandler("WebGraph")
		{
			public void handleDBChangeMsg(DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase treeObject)
			{
				if (!msg.getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
				{
					if( msg.getDatabase() == DBChangeMsg.CHANGE_GRAPH_DB)
					{
						com.cannontech.clientutils.CTILogger.info("DBChangeMSG received, updating graphDefinitionCache.");
						getPredefinedGraphs();
					}
				}
			}			
		};
		DefaultDatabaseCache.getInstance().addDBChangeListener( dbCacheHandler );
		
		strtThread = new Thread( this, "WebGraph" );
		strtThread.start();
	}

	/** 
	 * Stop us
	 */
	public void stop()
	{
		try
		{
			dbCacheHandler.getClientConnection().disconnect();
			DefaultDatabaseCache.getInstance().removeDBChangeListener( dbCacheHandler );
			
			Thread t = strtThread;
			strtThread = null;
			t.interrupt();
		}
		catch (Exception e)
		{}
	}
	
	public boolean isRunning()
	{
		return strtThread != null;
	}

	public void run()
	{		
		java.util.Date now = null;
		figureNextRunTime();
		getPredefinedGraphs();
		
		try
		{
			for (;;)
			{
				now = new java.util.Date();
				
				if( getNextRunTime().getTime().compareTo(now) <= 0)
				{
					com.cannontech.clientutils.CTILogger.info("Started Web Graphs, Tabular and Summary Reports Generation.");
					
					for (int i = 0; i < allPredefinedGraphsList.size(); i++)
					{
						com.cannontech.clientutils.CTILogger.info( allPredefinedGraphsList.get(i).toString());
						createGDefReports(((com.cannontech.database.data.lite.LiteGraphDefinition) allPredefinedGraphsList.get(i)));
					}
			
					com.cannontech.clientutils.CTILogger.info("Finished Reports Generation.");
					figureNextRunTime();
				}
		
					Thread.sleep(5000);
					System.gc();
		//			com.cannontech.clientutils.CTILogger.info(" $Free/ " +Runtime.getRuntime().freeMemory());
		//			com.cannontech.clientutils.CTILogger.info(" $Total/ "+ Runtime.getRuntime().totalMemory());
					//com.cannontech.clientutils.CTILogger.info("Sleeping!!!");
			}		
		}
		catch (InterruptedException ie)
		{
			com.cannontech.clientutils.CTILogger.info("Interrupted Exception!!!");
		}

	}


}
package com.cannontech.webgraph;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 11:51:56 AM)
 * @author: 
 */
import com.klg.jclass.util.swing.encode.*;
import java.util.GregorianCalendar;

import com.cannontech.graph.Graph;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendModelType;
public class WebGraph
{
	private GregorianCalendar nextRunTime = null;
	private Graph graph = null;
	
	private java.util.List allPredefinedGraphsList = null;
	public java.util.Date startDate = null;
	public java.util.Date stopDate = null;

	private final int GRAPH_GIF = 0;
	private final int TABULAR_HTML = 1;
	private final int SUMMARY_HTML = 2;

	private static final String VERSION = "2.0.1";	
	private String homeDirectory = null;
	private Integer createTimeInterval = null;//interval in seconds between calculations
		
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private DBChangeMessageListener dbChangeListener;

	private class DBChangeMessageListener extends Thread
	{
		public void run()
		{			
			while(true)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException ie)
				{
					return;
				}
				if( WebGraph.this.connToDispatch.isValid() )
				{
					Object msg = WebGraph.this.connToDispatch.read(0);

					if( msg != null )
					{
						if( msg instanceof com.cannontech.message.dispatch.message.DBChangeMsg )
						{
							System.out.println(" DB Change MSG recieved, will update TrendingCache...");
							com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage((com.cannontech.message.dispatch.message.DBChangeMsg)msg);
							WebGraph.this.getPredefinedGraphs("yukon");
						}
					}
				}
			}
		}
	}
 
/**
 * WebGraph constructor comment.
 */
public WebGraph()
{
	super();
	graph = new Graph();

	initConnToDispatch();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:50:28 PM)
 */
public void createGDefReports(com.cannontech.database.data.lite.LiteGraphDefinition liteGraphDef)
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
				
		gDef.getGraphDefinition().setStartDate( getStartDate() );
		gDef.getGraphDefinition().setStopDate( getStopDate() );
					
		graph.setDatabaseAlias("yukon");
		//graph.setSize(width, height);
		graph.setCurrentGraphDefinition(gDef);
		graph.setShowLoadFactor(false);
		graph.setSeriesType( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);

		graph.setModelType(TrendModelType.LINE_MODEL );

		// Define the peak series....
		/*
		for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
		{
			com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

			if ( graph.isPeakSeries( gds.getType()) )
			{
				graph.setHasPeakSeries( true );
				break;
			}
		}*/

		// Graph .gif file creation
		String fileName = getFileName( GRAPH_GIF, gDef.getGraphDefinition().getName());
		graph.setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);
		graph.setUpdateTrend(true);
		graph.update();
		writeGIF(fileName);

		StringBuffer buf = new StringBuffer("<HTML><LINK REL=\"stylesheet\" HREF=\"CannonStyle.ccs\" TYPE=\"text/css\"><CENTER>");		
		//Tabular .html file creation.
		fileName = getFileName( TABULAR_HTML, gDef.getGraphDefinition().getName());
		//graph.update();	//Don't have to do this again because same data is reused for tabular and graph series
		buf.append(getHTMLBuffer( graph.getSeriesType()));

		//Summary .html file creation
		fileName = getFileName( SUMMARY_HTML, gDef.getGraphDefinition().getName());
		graph.setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);			
		graph.update();
		buf.append("<BR><BR>");
		buf.append( getHTMLBuffer(graph.getSeriesType()));
		
		graph.setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES);
		graph.update();
		buf.append( getHTMLBuffer(graph.getSeriesType() ));
		buf.append("</CENTER></HTML>");
		writeHTML( buf.toString() , fileName );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void figureNextRunTime()
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
		getStopDate();
	}
}
/**
 * The model[][] where the first [] holds 0:LeftModels and 1:RightModels and
 *  the second[] is all of the points associated with the LorR model it is.
 * Insert the method's description here.
 * Creation date: (5/17/2001 4:49:15 PM)
 * @return GraphModelInterface [][]
 */
//public GraphModel[][] getCurrentModels()
public TrendModel getTrendModel()
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
		case GRAPH_GIF:
			return getHomeDirectory() + gDefName + ".gif";

		case TABULAR_HTML:
			return getHomeDirectory() + gDefName + ".html";
		
		case SUMMARY_HTML:		
		default:
			return getHomeDirectory() + gDefName + ".html";
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
			System.out.println("[" + new java.util.Date() + "]  Home Directory found in config.properties is " + homeDirectory);
		}
		catch( Exception e)
		{
			homeDirectory = "C:/Temp/";
			System.out.println("[" + new java.util.Date() + "]  Home Directory was NOT found in config.properties, defaulted to " + homeDirectory);
			System.out.println("[" + new java.util.Date() + "]  Add row named 'webgraph_home_directory' to config.properties with the home directory.");
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
private String getHTMLBuffer( String seriesType)
{
	HTMLBuffer htmlData = null;
	StringBuffer buf = null;
	try
	{
		if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
		{
			htmlData = new TabularHtml();
		}
		else if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) )
			htmlData = new PeakHtml();
		else if (seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) )
			htmlData = new UsageHtml();
		else
			htmlData = new TabularHtml();	//default on error(?) to graph_series
	
		buf = new StringBuffer("<HTML><CENTER>");
	
		TrendModel model = getTrendModel();
		htmlData.setModel( model);
		htmlData.getHtml( buf );
	}
	catch(Throwable t )
	{
		t.printStackTrace();
	}
	return buf.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 12:01:31 PM)
 * @return java.util.GregorianCalendar
 */
public GregorianCalendar getNextRunTime()
{
	return nextRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void getPredefinedGraphs(String databaseAlias)
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	if ( allPredefinedGraphsList != null)
		allPredefinedGraphsList.clear();
	
	synchronized(cache)
	{
		allPredefinedGraphsList = cache.getAllGraphDefinitions();
		System.out.println(allPredefinedGraphsList);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:38:05 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDate()
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
 * Creation date: (10/24/2001 3:38:05 PM)
 * @return java.util.Date
 */
public java.util.Date getStopDate()
{
	GregorianCalendar tempCal = new GregorianCalendar();
	tempCal.set( java.util.Calendar.DAY_OF_YEAR, getNextRunTime().get( java.util.Calendar.DAY_OF_YEAR ) + 1);
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
public java.lang.Integer getWebgraphRunInterval()
{
	if (createTimeInterval == null)
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			createTimeInterval = new Integer(bundle.getString("webgraph_run_interval"));
			System.out.println("[" + new java.util.Date() + "]  Run Time interval was found in config.properties is " + createTimeInterval + " seconds.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			createTimeInterval = new Integer(900);
			System.out.println( "[" + new java.util.Date() + "]  Aggregation interval was NOT found in config.properties, defaulted too " + createTimeInterval + " seconds.");
			System.out.println("[" + new java.util.Date() + "]  Add row named 'webgraph_run_interval' to config.properties with the time between calculations in seconds");
		}
	}
	return createTimeInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 2:04:04 PM)
 */
private void initConnToDispatch()
{
	String host = null;
	int port;
	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		host = bundle.getString("dispatch_machine");
		port = (new Integer(bundle.getString("dispatch_port"))).intValue();
	}
	catch ( java.util.MissingResourceException mre )
	{
		mre.printStackTrace();
		host = "127.0.0.1";
		port = 1510;
	}
	catch ( NumberFormatException nfe )
	{
		nfe.printStackTrace();
		port = 1510;
	}

	connToDispatch = new com.cannontech.message.dispatch.ClientConnection();

	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Web Graph Server");
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );
	
	connToDispatch.setHost(host);
	connToDispatch.setPort(port);
	connToDispatch.setAutoReconnect(true);
	connToDispatch.setRegistrationMsg(reg);

	try
	{
		connToDispatch.connectWithoutWait();
	}
	catch ( Exception e )
	{
		e.printStackTrace();
	}
	dbChangeListener = new DBChangeMessageListener();
	dbChangeListener.start();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:53:11 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	System.setProperty("cti.app.name", "WebGraph");
	WebGraph webGraph= new WebGraph();
	
	System.out.println("[" + new java.util.Date() + "]  Version: " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() +VERSION+ "  --   Web Graph Started.");
	
	java.util.Date now = null;
	webGraph.figureNextRunTime();
	webGraph.getPredefinedGraphs("yukon");
	
	for (;;)
	{
		now = new java.util.Date();
		
		if (webGraph.getNextRunTime().getTime().compareTo(now) <= 0)
		{
			System.out.println("[" + new java.util.Date() + "]  Started Web Graphs, Tabular and Summary Reports Generation.");
			
			for (int i = 0; i < webGraph.allPredefinedGraphsList.size(); i++)
			{
				System.out.println( webGraph.allPredefinedGraphsList.get(i).toString());
				webGraph.createGDefReports(((com.cannontech.database.data.lite.LiteGraphDefinition) webGraph.allPredefinedGraphsList.get(i)));
			}
	
			System.out.println("[" + new java.util.Date() + "]  Finished Reports Generation.");
			webGraph.figureNextRunTime();
		}

		try
		{
			Thread.sleep(5000);
			System.gc();
			System.out.println(" $Free/ " +Runtime.getRuntime().freeMemory());
			System.out.println(" $Total/ "+ Runtime.getRuntime().totalMemory());
			//System.out.println("Sleeping!!!");
		}
		catch (InterruptedException ie)
		{
			System.out.println("Interrupted Exception!!!");
			return;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:47:44 PM)
 */
//private void writeFile(String bufferString, String fileName)
//{
public void writeGIF( String fileName )
{
	try
	{
		java.io.FileOutputStream fOut = new java.io.FileOutputStream(fileName);
//		graph.encodeGif(fOut);
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
		System.out.println("@@ Text variable is null -> html format");
	

}
}

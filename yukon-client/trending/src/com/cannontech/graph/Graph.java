package com.cannontech.graph;

/**
 * The main graphing class.
 * main in this class starts up the standalone application.
 *
 * Creation date: (12/15/99 10:13:31 AM)
 * @author:  Aaron Lauinger 
 */
import java.awt.Rectangle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.w3c.dom.Element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.exportdata.ExportDataFile;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.jfreechart.chart.YukonChartPanel;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.util.ServletUtil;

public class Graph implements GraphDefines
{
	private long lastUpdateTime = 0;

	private java.util.Date startDate = ServletUtil.getToday();	
	
	private static final int DEFAULT_GIF_WIDTH = 556;
	private static final int DEFAULT_GIF_HEIGHT = 433;
	
	private int height = DEFAULT_GIF_HEIGHT;
	private int width = DEFAULT_GIF_WIDTH;

	private JFreeChart freeChart = null;
	private TrendModel trendModel = null;

	private int [] pointIDs = null;
	private GraphDefinition graphDefinition = null;
	private String period = ServletUtil.ONEDAY;
	private java.lang.String title = null;
		
	private static final int DEFAULT_INTERVAL_RATE = 300;  //rate is in seconds
	private int minIntervalRate = DEFAULT_INTERVAL_RATE;

	private boolean updateTrend = true;
	private StringBuffer htmlString = null;
	
	private ClientConnection connToDispatch;
	private TrendProperties trendProperties;

	//types of generations, GDEF is the original or default, POINT is for ad-hoc or no predefined gdef exists.
	private static final int GDEF_GENERATION = 0;
	private static final int POINT_GENERATION = 1;
	
	private int generationType = GDEF_GENERATION;
/**
 * Graph constructor comment.
 */
public Graph()
{
	super();
	// LOAD PEAKPOINTHISTORY
	//  THIS IS ONLY TEMPORARY UNTIL WE FIGURE OUT A TIMER WAY OF DOING THIS.
	//  12/11/02 SN  (It's probably 2004 by now...HA!)    
	/*
   	com.cannontech.database.cache.TimedDatabaseCache cache = com.cannontech.database.cache.TimedDatabaseCache.getInstance();
	synchronized(cache)
	{
		cache.setUpdateTimeInMillis(21600000);
		java.util.List peakPoints = cache.getAllPeakPointHistory();
		java.util.Iterator iter = peakPoints.iterator();
		while( iter.hasNext() )
		{
			com.cannontech.database.db.point.PeakPointHistory pt = (com.cannontech.database.db.point.PeakPointHistory) iter.next();
			com.cannontech.clientutils.CTILogger.debug(" PEAK POINT TS/VALUE = " + pt.getPointID() + " | " + pt.getTimeStamp().getTime() + " | " + pt.getValue());
		}
	}		*/
} 

/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:48:47 PM)
 * @return com.klg.jclass.chart.JCChart
 */
/*
public JCChart getChart()
{
	return getGraph().getChart();
}
*/
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:14:03 PM)
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection()
{
	if( connToDispatch == null)
		connect();	
	return connToDispatch;
}
private void connect() 
{
	String host = "127.0.0.1";
	int port = 1510;
	try
	{
		host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
		port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
	}
	catch( Exception e)
	{
		CTILogger.error( e.getMessage(), e );
	}

	connToDispatch = new ClientConnection();

	Registration reg = new Registration();
	reg.setAppName(CtiUtilities.getAppRegistration());
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

	//dbChangeListener = new DBChangeMessageListener();
	//dbChangeListener.start();
}

public void encodeCSV(java.io.OutputStream out) throws java.io.IOException
{
	synchronized (Graph.class)
	{
		ExportDataFile eDataFile = new ExportDataFile(
			getViewType(),
			getFreeChart(), 
			getTrendModel().getChartName(), 
			getTrendModel());
	
		eDataFile.setExtension("csv");
	
		String data[] = eDataFile.createCSVFormat();
		for( int i = 0; i < data.length; i++ )
		{
			if( data[i] != null)
				out.write( data[ i ].getBytes() );
		}
	}
}

/**
 * Encodes a gif on the given stream.  This method is used heavily
 * by the readmeter server and really should be as quick as possible.
 * Creation date: (10/12/00 2:54:13 PM)
 * @param out java.io.OutputStream
 */
public void encodeGif(java.io.OutputStream out) throws java.io.IOException
{
	// SN 7.17.02
	// GIF does not work.  
	// An error of " too many colors for a gif" is returned.
	synchronized(Graph.class)
	{
		YukonChartPanel cp = new YukonChartPanel(freeChart);
//		update();

		// FIXFIX
		// Should be able to get rid of this temporary jframe,
		// but i had trouble with memory leaking when i reused a static
		// jframe 
		// 11/00 -acl
		javax.swing.JFrame encFrame = new javax.swing.JFrame();

		encFrame.setSize(getWidth(),getHeight() );
		cp.setSize(getWidth(),getHeight());
		encFrame.getContentPane().add( cp, "Center");
		
		//make sure to create its peer
		encFrame.addNotify();

		// FIXFIX			
		// Should make this encoder static and instantiate it lazily
		// 11/00 -acl
		Acme.JPM.Encoders.GifEncoder encoder = new Acme.JPM.Encoders.GifEncoder(freeChart.createBufferedImage(500, 500), out);
		encoder.encode();

		encFrame.getContentPane().removeAll();
		encFrame.dispose();		
	}
}

/**
 * Encodes a gif on the given stream.  This method is used heavily
 * by the readmeter server and really should be as quick as possible.
 * Creation date: (10/12/00 2:54:13 PM)
 * @param out java.io.OutputStream
 */
public void encodeJpeg(java.io.OutputStream out) throws java.io.IOException
{
	synchronized(Graph.class)
	{
		ChartUtilities.writeChartAsJPEG(out, getFreeChart(), getWidth(), getHeight());
	}
}
public void encodePng(java.io.OutputStream out) throws java.io.IOException
{
	synchronized(Graph.class)
	{
		ChartUtilities.writeChartAsPNG(out, getFreeChart(), getWidth() , getHeight());
	}
}

public void encodeSVG(java.io.OutputStream out) throws java.io.IOException
{	
	synchronized(Graph.class)
	{		
		org.w3c.dom.DOMImplementation domImpl =
			org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
		
		// Create an instance of org.w3c.dom.Document
		org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);
			
		//Create an instance of the SVG Generator
		org.apache.batik.svggen.SVGGraphics2D svgGenerator = 
		new org.apache.batik.svggen.SVGGraphics2D(document);
		
		getFreeChart().draw(svgGenerator, new Rectangle(getWidth(),getHeight()));
		
		Element svgRoot = svgGenerator.getRoot();
		if( getGenerationType() == GDEF_GENERATION)
			svgRoot.setAttributeNS(null,"gdefid", String.valueOf(getGraphDefinition().getGraphDefinition().getGraphDefinitionID()));
		else if( getGenerationType() == POINT_GENERATION)
			svgRoot.setAttributeNS(null,"pointid", String.valueOf(getPointIDs()[0]));	//use the first pointID as the attribute value? TODO
			
		// Finally, stream out SVG to the standard output 
		// is this a good encoding to use?
		java.io.Writer writer = new java.io.OutputStreamWriter(out,"ISO-8859-1");
		svgGenerator.stream(svgRoot, writer, true);		
	}
}

/**
 * Encodes a tabular representation on the given output stream.
 * Creation date: (11/17/00 11:14:55 AM)
 * @param out java.io.OutputStream
 */
public void encodePeakHTML(java.io.OutputStream out) throws java.io.IOException 
{
	StringBuffer buf = new StringBuffer();
	PeakHtml tabHtml = new PeakHtml();		

	tabHtml.setModel( getTrendModel() );
	tabHtml.getHtml(buf);
	out.write(buf.toString().getBytes());	
}

public void encodeSummmaryHTML(java.io.OutputStream out) throws java.io.IOException 
{
	encodePeakHTML(out);
	encodeUsageHTML(out);
}
	
public void encodePDF(java.io.OutputStream out) throws java.io.IOException
{
	synchronized(Graph.class)
	{
		try
		{
			com.klg.jclass.util.swing.encode.page.PDFEncoder encoder = new com.klg.jclass.util.swing.encode.page.PDFEncoder();
			encoder.encode(getFreeChart().createBufferedImage(getWidth(), getHeight()), out);
			out.flush();	
		}		
		catch( java.io.IOException io )
		{
			io.printStackTrace();
		}
		catch( com.klg.jclass.util.swing.encode.EncoderException ee )
		{
			ee.printStackTrace();
		}
	}
}
/**
 * Encodes a tabular representation on the given output stream.
 * Creation date: (11/17/00 11:14:55 AM)
 * @param out java.io.OutputStream
 */
public void encodeTabularHTML(java.io.OutputStream out) throws java.io.IOException 
{
	StringBuffer buf = new StringBuffer();
	TabularHtml tabHtml = new TabularHtml();
		
	tabHtml.setModel( getTrendModel());
	tabHtml.getHtml(buf);
	out.write(buf.toString().getBytes());
}

/**
 * Encodes a tabular representation on the given output stream.
 * Creation date: (11/17/00 11:14:55 AM)
 * @param out java.io.OutputStream
 */
public void encodeUsageHTML(java.io.OutputStream out) throws java.io.IOException 
{
	StringBuffer buf = new StringBuffer();		
	UsageHtml usageHtml = new UsageHtml();

	usageHtml.setModel( getTrendModel());
	usageHtml.getHtml(buf);
	out.write(buf.toString().getBytes());	
}

/**
 * Returns graphDefinition, but first retrieves and sets the 
 *  GraphDefinition to the current cache retrieval.
 * @return graphDefinition com.cannontech.database.data.graph.GraphDefinition
 */
public GraphDefinition getGraphDefinition()
{
	return graphDefinition;
}

/**
 * Set the graphDefinition.
 * Flags updateTrend = true (forces trend to update on every set of graphDefinition field).
 * @param graphDefinition com.cannontech.database.graph.GraphDefinition
 */
public void setGraphDefinition(GraphDefinition newGraphDefinition)
{
	if( getGraphDefinition() == null ||
		getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue() != newGraphDefinition.getGraphDefinition().getGraphDefinitionID().intValue())
	{
		setUpdateTrend(true);
	}
	else
	{
		System.out.println( "SAME GRAPH DEFs");
	}
	//ASSUMPTION!!! If you are setting the gDef, you must want to use it.  You can only use pointIDs -OR- graphDefinitionID...not both!
	setGenerationType(GDEF_GENERATION);
	graphDefinition = newGraphDefinition;
	getTrendProperties().setGdefName(graphDefinition.getGraphDefinition().getName());
}

/**
 * Set the graphDefinition using only the GraphDefinitionID.
 * Creates a GraphDefinition and calls setGraphDefinition(GraphDefinition)
 * @param graphDefinitionID int values of the GraphDefinition
 */
public void setGraphDefinition(LiteGraphDefinition liteGraphDefinition)
{
	if( liteGraphDefinition != null)
	{
		GraphDefinition gDef = (GraphDefinition)LiteFactory.createDBPersistent(liteGraphDefinition);
		try
		{
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, gDef);
			gDef = (GraphDefinition)t.execute();
		}
		catch(Exception e)
		{
			CTILogger.error(e.getMessage(), e);
		}
		setGraphDefinition(gDef);
	}
}

/**
 * Set the graphDefinition using only the GraphDefinitionID.
 * Creates a GraphDefinition and calls setGraphDefinition(GraphDefinition)
 * @param graphDefinitionID int values of the GraphDefinition
 */
public void setGraphDefinition(int liteGraphDefinitionID)
{
	if( liteGraphDefinitionID > 0)
	{
		LiteGraphDefinition liteGraphDef = GraphFuncs.getLiteGraphDefinition(liteGraphDefinitionID);
		setGraphDefinition(liteGraphDef);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 4:49:15 PM)
 * @return TrendModel [][]
 */
public TrendModel getTrendModel()
{
	return trendModel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/2001 11:23:16 AM)
 * @return String
 */
public String getPeriod()
{
	return period;
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 9:32:40 AM)
 * @return com.jrefinery.chart.JFreeChart
 */
public synchronized JFreeChart getFreeChart() 
{
	if( freeChart == null)
	{
		freeChart = ChartFactory.createTimeSeriesChart("Yukon Trending Application", "Date/Time", "Value", new TimeSeriesCollection(), true, true, true);
		freeChart.setBackgroundPaint(java.awt.Color.white);
	}
	return freeChart;
}

public StringBuffer getHtmlString()
{
	return htmlString;
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
public int getMinIntervalRate(	) 
{
	return minIntervalRate;
}
/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 3:54:09 PM)
 * @return int
 */
public int getViewType()
{
	return getTrendProperties().getViewType();
}
public int getOption()
{
	return getTrendProperties().getOptionsMaskSettings();
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/00 10:08:37 AM)
 * @return java.lang.String
 */
public java.lang.String getTitle() {
	return title;
}  

public boolean isUpdateTrend()
{
	long now = (new java.util.Date()).getTime();
	CTILogger.debug( "LAST = " + new java.util.Date(lastUpdateTime) );
	CTILogger.debug( "NOW  = " + new java.util.Date(now) );
	CTILogger.debug( "waiting until  = " + new java.util.Date(lastUpdateTime + (getMinIntervalRate() * 1000)) );	
	if( (lastUpdateTime + (getMinIntervalRate() *1000)) <= now)
	{
		updateTrend = true;
		lastUpdateTime = now;
	}
	return updateTrend;
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
private int retrieveIntervalRate() 
{
	/*
	java.util.Vector pointsArrayVector = new java.util.Vector();
	for (int i = 0; i < getCurrentGraphDefinition().getGraphDataSeries().size(); i++)
	{
		com.cannontech.database.db.graph.GraphDataSeries gds =
			(com.cannontech.database.db.graph.GraphDataSeries) getCurrentGraphDefinition().getGraphDataSeries().get(i);
		pointsArrayVector.add(new Integer(gds.getPointID().intValue()));
		//for LoadDuration we want to only use the peak point if it exists, default to all pointsArray.
		if ( gds.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES))
			haveAPeakPoint = true;
	}
	*/
	java.util.Vector pointsVector = new java.util.Vector();
	
	for (int i = 0; i < getTrendModel().getTrendSeries().length; i ++)
	{
		pointsVector.add(getTrendModel().getTrendSeries()[i].getPointId());
	}
//	for (int i = 0; i < currentModels.length; i++)
//		for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
//			for (int k = 0; k < currentModels[i][j].points.length; k++)
//				pointsVector.add(currentModels[i][j].points[k]);
	Integer[] pointsArray = new Integer[pointsVector.size()];
	pointsVector.toArray(pointsArray);

	
	long timer = System.currentTimeMillis();
	int minRate = DEFAULT_INTERVAL_RATE;	//defaults to 900 seconds (15 mins) SN 5/23/01

	if( pointsArray.length <= 0 )
		return minRate;		// could return whatever is in Graph.intervalRate otherwise
		
	StringBuffer sql = new StringBuffer	("SELECT MIN(DSR.INTERVALRATE) FROM POINT P, DEVICESCANRATE DSR WHERE ");
					
	if( pointsArray.length > 0  )
	{
		sql.append(" P.POINTID IN ( ");
		sql.append(pointsArray[0].toString());		
	}
		
	for( int i = 1; i < pointsArray.length; i++ )
	{
		sql.append(" , ");
		sql.append(pointsArray[i].toString());		
	}

	sql.append(" ) AND P.PAOBJECTID = DSR.DEVICEID");
		
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			CTILogger.info(getClass() + ":  Error getting database connection.");
			return minIntervalRate;
		}
		else
		{
			stmt = conn.prepareStatement(sql.toString());
			rset = stmt.executeQuery();
			while( rset.next())
			{
				if( rset.getInt(1) > 0  )
					minRate = rset.getInt(1);
			}
		}
	}
			
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	if( minRate < 60 )
		return 60;
	else
		return minRate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 3:02:39 PM)
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 */
//public void setGraphDefinitionID(int newGraphDefinitionID)
//{
////	if( graphDefinitionID != newGraphDefinitionID)
//	{
//		graphDefinitionID = newGraphDefinitionID;
//		retrieveGraphDefinition();
//	}	
//} 
/**
 ** Both Server and Client use **
 * Insert the method's description here.
 * Creation date: (3/27/00 10:25:04 AM)
 * @param width int
 * @param height int
 */
public void setPeriod(String newPeriod) 
{
	if(!period.equalsIgnoreCase(newPeriod))
	{
		period = newPeriod;
		setUpdateTrend(true);
	}	
}  

public void setHtmlString(StringBuffer newHtmlString)
{
	htmlString = newHtmlString;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2001 12:03:38 PM)
 * @param newRate int
 */
public void setMinIntervalRate( int newRate)
{
	this.minIntervalRate = newRate;
}

/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 3:53:34 PM)
 * @param newModelType int
 */
public void setViewType(int newViewType)
{
//	setUpdateTrend(true);
	getTrendProperties().setViewType(newViewType);
}

public void setStartDate(java.util.Date newStartDate)
{
	if( startDate.compareTo((Object)newStartDate) != 0 )
	{
		CTILogger.info("Changing Date!");
		startDate = newStartDate;
		setUpdateTrend(true);
	}
}

public java.util.Date getStartDate()
{
	return ServletUtil.getStartingDateOfInterval( startDate, getPeriod());
}

public java.util.Date getStopDate()
{
	return ServletUtil.getEndingDateOfInterval( startDate, getPeriod() );
}

/**
 ** Both Server and Client use **
 * Insert the method's description here.
 * Creation date: (3/27/00 10:25:04 AM)
 * @param width int
 * @param height int
 */

public void setSize( int width, int height)
{
	this.width = width;
	this.height = height;
}  
public int getWidth( )
{
	return width;
}  
public int getHeight()
{
	return height;
}  


/**
 * Insert the method's description here.
 * Creation date: (1/24/00 10:08:37 AM)
 * @param newTitle java.lang.String
 */
public void setTitle(java.lang.String newTitle)
{
	title = newTitle;
}  

public void setTrendModel(TrendModel newTrendModel)
{
	trendModel = newTrendModel;
}

public void setUpdateTrend(boolean update)
{
	updateTrend = update;
}
public void update()
{
	if( isUpdateTrend())
	{
		TrendModel newModel = null;
		if( getGenerationType() == GDEF_GENERATION)
		{
			newModel = new TrendModel(getGraphDefinition(), getStartDate(), getStopDate(), getTrendProperties()); 
		}
		else if ( getGenerationType() == POINT_GENERATION)
		{
			String chartTitle = "";
			LitePoint [] lps = new LitePoint[pointIDs.length];
			for(int i = 0; i < pointIDs.length; i++)
			{
				LitePoint lp = PointFuncs.getLitePoint(pointIDs[i]);
				lps[i] = lp;
				if( i > 0)
					chartTitle += ", ";
				chartTitle += lp.getPointName();
			}
			chartTitle += " Trend";
			newModel = new TrendModel(getStartDate(), getStopDate(), chartTitle, lps);
		}
		setTrendModel( newModel );
		updateTrend = false;		
	}
	getTrendModel().setTrendProps( getTrendProperties());
	freeChart = getTrendModel().refresh();
}
/** Send a command message to dispatch to scan paobjects (or all meters if paobjects is null)
 * at the alternate scan rate for duration of 0 secs (once).
 **/
public void getDataNow(java.util.List paobjects)
{
	Multi multi = new Multi();

	if( paobjects == null)
	{	
		DefaultDatabaseCache cache = new DefaultDatabaseCache();
		paobjects = cache.getAllDevices();	//populate our list of paobjects with all devices then!
	}
	
	for (int i = 0; i < paobjects.size(); i++)
	{
		LiteYukonPAObject paobject = (LiteYukonPAObject)paobjects.get(i);
		if(DeviceTypesFuncs.hasDeviceScanRate(paobject.getType()))
		{
			CTILogger.info("Alternate Scan Rate Command Message for DEVICE ID: " + paobject.getLiteID() + " Name: " + paobject.getPaoName());
			Command messageCommand = new Command();
			messageCommand.setOperation(Command.ALTERNATE_SCAN_RATE);
			messageCommand.setPriority(14);
			
			java.util.Vector opArgList = new java.util.Vector(4);
			opArgList.add(new Long(-1));	//token
			opArgList.add(new Long(new Integer(paobject.getLiteID()).longValue()));	//deviceID
			opArgList.add(new Long(-1));	//open?
			opArgList.add(new Long(0));	//duration in secs
			
			messageCommand.setOpArgList(opArgList);
			multi.getVector().add(messageCommand);
		}
	}
	
	if( multi.getVector().size() > 0 )
		getClientConnection().write(multi);
}
	/**
	 * @return
	 */
	public TrendProperties getTrendProperties()
	{
		if( trendProperties == null)
			trendProperties = new TrendProperties();
		return trendProperties;
	}

	/**
	 * @param properties
	 */
	public void setTrendProperties(	TrendProperties properties)
	{
		trendProperties = properties;
	}

	/**
	 * @param item
	 */
	public void create(DBPersistent item) 
	{
		if( item != null )
		{
			try
			{
				Transaction t = Transaction.createTransaction(Transaction.INSERT, item);
				item = t.execute();

				//write the DBChangeMessage out to Dispatch since it was a Successfull ADD
				DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_ADD);
			
				for( int i = 0; i < dbChange.length; i++)
				{
					DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
					getClientConnection().write(dbChange[i]);
				}
			}
			catch( TransactionException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	/**
	 * @param item
	 */
	public void delete(DBPersistent item)
	{
		try
		{
			Transaction t = Transaction.createTransaction( Transaction.DELETE, item);
			item = t.execute();
			
			//write the DBChangeMessage out to Dispatch since it was a Successfull DELETE
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_DELETE);
					
			for( int i = 0; i < dbChange.length; i++)
			{
				DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
				getClientConnection().write(dbChange[i]);
			}
			/**TODO think about this a bit more, is this really what we want to do?*/
//			if( getFreeChart().getPlot() instanceof org.jfree.chart.plot.CategoryPlot)
//			{
//				((CategoryPlot)getFreeChart().getPlot()).setDataset(null);
//				((CategoryPlot)getFreeChart().getPlot()).setSecondaryDataset(0, null);
////				((CategoryPlot)getFreeChart().getPlot()).setDataset(1, null);
//			}
//			else if( getFreeChart().getPlot() instanceof org.jfree.chart.plot.XYPlot)
//			{
//				((XYPlot)getFreeChart().getPlot()).setDataset(null);
//				((CategoryPlot)getFreeChart().getPlot()).setSecondaryDataset(0, null);
////				((XYPlot)getFreeChart().getPlot()).setDataset(1, null);
//			}
		}
		catch( TransactionException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
	}
	/**
	 * @param item
	 */
	public void update(DBPersistent item)
	{
		try
		{
			Transaction t = Transaction.createTransaction( Transaction.UPDATE, item);
			item = (GraphDefinition)t.execute();
			
			//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_UPDATE);
					
			for( int i = 0; i < dbChange.length; i++)
			{
				DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
				getClientConnection().write(dbChange[i]);
			}
		}
		catch( TransactionException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		//force an update on the GDEF update
		setUpdateTrend(true);
	}
	/**
	 * @return
	 */
	public int getGenerationType()
	{
		return generationType;
	}

	/**
	 * @param i
	 */
	public void setGenerationType(int i)
	{
		generationType = i;
	}

	/**
	 * @return
	 */
	public int[] getPointIDs()
	{
		return pointIDs;
	}

	/**
	 * @param points
	 */
	public void setPointIDs(int[] pointIDs)
	{
		if( getPointIDs() == null || !pointIDs.equals(getPointIDs()))	//will these ever be equal?
		{
			setUpdateTrend(true);
		}
		else
		{
			System.out.println( "SAME POINTS");
		}
		//ASSUMPTION!!! If you are setting the pointIDs, you must want to use them.  You can only use pointIDs -OR- graphDefinitionID...not both!
		setGenerationType(POINT_GENERATION);
		this.pointIDs = pointIDs;
		getTrendProperties().setGdefName("AD HOC - Points");
	}

	/**
	 * Creates an array on size 1.
	 * @param points
	 */
	public void setPointIDs(int pointID)
	{
		int [] pointIDs = new int[]{pointID};
		setPointIDs(pointIDs);
	}
}

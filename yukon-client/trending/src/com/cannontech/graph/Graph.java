package com.cannontech.graph;

/**
 * The main graphing class.
 * main in this class starts up the standalone application.
 *
 * It would be nice if the application specific(swing) code was
 * moved somewhere else.  In its present form it mixes its service
 * functionality (encodeXXX) as a library with standalone application
 * code.
 *
 * Creation date: (12/15/99 10:13:31 AM)
 * @author:  Aaron Lauinger 
 */
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.util.ServletUtil;
import com.jrefinery.chart.JFreeChart;

public class Graph implements GraphDataFormats, GraphDefines, TrendModelType, com.jrefinery.chart.event.ChartChangeListener
{
public void chartChanged(com.jrefinery.chart.event.ChartChangeEvent event)
{
	if( event.getSource() == freeChart)
	{
		com.cannontech.clientutils.CTILogger.info(" ***** CHART CHANGED EVENT ***** " + event.getType());
	}
}
	private java.lang.String DB_ALIAS = "yukon";
	private java.lang.String databaseAlias = DB_ALIAS;	//defaults set to yukon! 5/24/01
	
	private int viewType = LINE_VIEW;

	// This is the type of data series the graph will load
	// !HACK!HACK! AL- a value of null will cause all types to be loaded!
	
	private int seriesMask = 0x00;
	
	private static final int DEFAULT_GIF_WIDTH = 556;
	private static final int DEFAULT_GIF_HEIGHT = 433;
	
	private int height = DEFAULT_GIF_HEIGHT;
	private int width = DEFAULT_GIF_WIDTH;

	private boolean showLoadFactor = true;
	private final int LF_DECIMAL_PLACES = 3;

	private JFreeChart freeChart = null;
	private TrendModel trendModel = null;

	private com.cannontech.database.data.graph.GraphDefinition currentGraphDefinition = null;
	private int currentTimePeriod = ServletUtil.getIntValue( ServletUtil.ONEDAY );  //default to first period in drop down periodComboBox

	private java.lang.String title = null;
		
	private static final int DEFAULT_INTERVAL_RATE = 300;  //rate is in seconds
	private int minIntervalRate = DEFAULT_INTERVAL_RATE;

	private int options_mask_holder = 0x00;
	private boolean updateTrend = true;
	private StringBuffer htmlString = null;
/**
 * Graph constructor comment.
 */
public Graph()
{
	super();
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
		com.cannontech.jfreechart.chart.YukonChartPanel cp = 
			new com.cannontech.jfreechart.chart.YukonChartPanel(freeChart);
//		update();

		// FIXFIX
		// Should be able to get rid of this temporary jframe,
		// but i had trouble with memory leaking when i reused a static
		// jframe 
		// 11/00 -acl
		javax.swing.JFrame encFrame = new javax.swing.JFrame();

		encFrame.setSize(getWidth(),getHeight() );
		cp.setSize(getWidth(),getHeight());
		javax.swing.JPanel panel = new javax.swing.JPanel();
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
		com.jrefinery.chart.ChartUtilities cu = new com.jrefinery.chart.ChartUtilities();
		cu.writeChartAsJPEG(out, getFreeChart(), getWidth(), getHeight());
	}
}
public void encodePng(java.io.OutputStream out) throws java.io.IOException
{
	synchronized(Graph.class)
	{
		com.jrefinery.chart.ChartUtilities cu = new com.jrefinery.chart.ChartUtilities();
		cu.writeChartAsPNG(out, getFreeChart(), getWidth() , getHeight());
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
		
		svgRoot.setAttributeNS(null,"gdefid",getCurrentGraphDefinition().getGraphDefinition().getGraphDefinitionID().toString() );
		
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
public void encodePDF(java.io.OutputStream out) throws java.io.IOException
{
	// NOT IMPLEMENTE CORRECTLY YET!!!!  9/04/02 SN

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

	finally
	{
		try
		{
			if( out != null )
				out.close();
		} 
		catch( java.io.IOException ioe )
		{
			ioe.printStackTrace();
		}
	}//end finally

}//end encodePDFFormat()
/**
 * Encodes a tabular representation on the given output stream.
 * Creation date: (11/17/00 11:14:55 AM)
 * @param out java.io.OutputStream
 */
public void encodeTabularHTML(java.io.OutputStream out) throws java.io.IOException 
//public void encodeTabularHTML(java.io.PrintWriter out) throws java.io.IOException 
{
	StringBuffer buf = new StringBuffer();
	TabularHtml tabHtml = new TabularHtml();
		
	tabHtml.setModel( getTrendModel());
	tabHtml.getHtml(buf);
	out.write(buf.toString().getBytes());
//	out.write(buf.toString().toCharArray());
}

public void getTabularHTML(StringBuffer buffer) throws java.io.IOException 
{
	TabularHtml tabHtml = new TabularHtml();
	tabHtml.setModel( getTrendModel());
	tabHtml.getHtml(buffer);
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
 * Insert the method's description here.
 * Creation date: (5/16/2001 11:23:16 AM)
 * @return int
 */
public com.cannontech.database.data.graph.GraphDefinition getCurrentGraphDefinition()
{	
	return currentGraphDefinition;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 4:49:15 PM)
 * @return GraphModelInterface [][]
 */
public TrendModel getTrendModel()
{
	return trendModel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/2001 11:23:16 AM)
 * @return int
 */
public int getCurrentTimePeriod()
{
	return currentTimePeriod;
}
public String getDatabaseAlias() 
{
	return databaseAlias;
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 9:32:40 AM)
 * @return com.jrefinery.chart.JFreeChart
 */
public synchronized com.jrefinery.chart.JFreeChart getFreeChart() 
{
	if( freeChart == null)
	{
		freeChart = com.jrefinery.chart.ChartFactory.createTimeSeriesChart("Yukon Trending Application", "Date/Time", "Value", new com.jrefinery.data.TimeSeriesCollection(), true);
		freeChart.setBackgroundPaint(java.awt.Color.white);
	}
	return freeChart;
}

public StringBuffer getHTMLString()
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
	return viewType;
}
public int getOptionsMaskHolder()
{
	return options_mask_holder;
}
public int getSeriesMask()
{
	return seriesMask;
}

/**
 * Insert the method's description here.
 * Creation date: (9/13/2001 3:05:18 PM)
 * @return boolean
 */
public boolean getShowLoadFactor() {
	return showLoadFactor;
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
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(getDatabaseAlias());

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
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
public void setCurrentGraphDefinition(com.cannontech.database.data.graph.GraphDefinition gDef) 
{
	currentGraphDefinition = gDef;
} 
/**
 ** Both Server and Client use **
 * Insert the method's description here.
 * Creation date: (3/27/00 10:25:04 AM)
 * @param width int
 * @param height int
 */
public void setCurrentTimePeriod(int newTimePeriod) 
{
	currentTimePeriod = newTimePeriod;
}  
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 5:01:10 PM)
 * @param dbAlias java.lang.String
 */
public void setDatabaseAlias(String dbAlias) 
{
	databaseAlias = dbAlias;
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
	viewType = newViewType;
}
public void setOptionsMaskHolder(int newMask, boolean setMasked)
{
	// when setMasked = true, the newMask will be added to the options_mask
	// when setMasked = false, the newMask will be removed from the options_mask
	if( setMasked)
		options_mask_holder |= newMask;
	else
	{
		//check to make sure it's there if we are going to remove it
		if( (options_mask_holder & newMask) != 0)
		{
			options_mask_holder ^= newMask;
		}
	}
	if( getTrendModel() != null)
		getTrendModel().setOptionsMask(getOptionsMaskHolder());
}

public void setSeriesMask(int newMask, boolean setMasked)
{
	// when setMasked = true, the newMask will be added to the options_mask
	// when setMasked = false, the newMask will be removed from the options_mask
	if( setMasked)
		seriesMask |= newMask;
	else
	{
		//check to make sure it's there if we are going to remove it
		if( (seriesMask & newMask) != 0)
		{
			seriesMask ^= newMask;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/13/2001 3:03:38 PM)
 * @param value boolean
 */
public void setShowLoadFactor(boolean value)
{
	showLoadFactor = value;
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
//EXAMPLE OF USING A POINT TO GRAPH INSTEAD OF A GRAPHDEFINITION.
/*	java.util.Date stop = new java.util.Date();
	java.util.Date start = new java.util.Date(stop.getTime() - (86400000 * 2));
	java.sql.Connection conn = null;
	try
	{
		com.cannontech.database.db.point.Point[] pointArray = new com.cannontech.database.db.point.Point[2];
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		com.cannontech.database.db.point.Point p = new com.cannontech.database.db.point.Point();
		p.setPointID( new Integer(9) );

		p.setDbConnection(conn);
		p.retrieve();
		pointArray[0] = p;
		p.setDbConnection(null);

		TrendModel newModel = new TrendModel(start, stop, "Point Chart Test",  pointArray);
		setTrendModel( newModel );
		freeChart = getTrendModel().setupFreeChart(getModelType());
		
	}
	catch(java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try {
		if( conn != null ) conn.close();
		} catch( java.sql.SQLException e2 ) { }
	}
*/////////////////////////////////////////////////////////////////////
	
	if( isUpdateTrend())
	{
		TrendModel newModel = new TrendModel(getCurrentGraphDefinition(), getOptionsMaskHolder()); 
		setTrendModel( newModel );
		updateTrend = false;		
	}
	freeChart = getTrendModel().refresh(getViewType());
}

}

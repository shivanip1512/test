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
import com.klg.jclass.chart.JCChart;
import com.cannontech.message.dispatch.message.*;
import com.cannontech.graph.model.*;
import com.cannontech.graph.buffer.html.*;
import com.cannontech.graph.GraphDataCalculations;
import com.cannontech.util.ServletUtil;

public class Graph implements GraphDataFormats, GraphModelType
{
	private java.lang.String DB_ALIAS = "yukon";
	private java.lang.String databaseAlias = DB_ALIAS;	//defaults set to yukon! 5/24/01
	
	private int modelType = DATA_VIEW_MODEL;
	private int modelChartType = JCChart.PLOT;

	// This is the type of data series the graph will load
	// !HACK!HACK! AL- a value of null will cause all types to be loaded!
	
	private String currentSeriesType = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES;

	// Signals that a peak series exists and it peaks will be displayed on summary tab.
	private boolean hasPeakSeries = false;
	
	private static final int DEFAULT_GIF_WIDTH = 556;
	private static final int DEFAULT_GIF_HEIGHT = 433;
	
	private int height = DEFAULT_GIF_HEIGHT;
	private int width = DEFAULT_GIF_WIDTH;

	private boolean showLoadFactor = true;
	private final int LF_DECIMAL_PLACES = 3;

	private com.klg.jclass.chart.JCChart chart = null;

	private com.cannontech.database.data.graph.GraphDefinition currentGraphDefinition = null;
	private int currentTimePeriod = ServletUtil.getIntValue( ServletUtil.ONEDAY );  //default to first period in drop down periodComboBox

	private GraphColors graphColors = new GraphColors();
	
	// JFrame to use to encode gif's, this is never displayed
	// only access through static getter...it is instantiated
	// and it's peer is created lazily
	private javax.swing.JFrame encodeFrame = null;
	
	private java.lang.String title = null;
		
	GraphModel[][] currentModels = null;

	private static final int DEFAULT_INTERVAL_RATE = 300;  //rate is in seconds
	private int minIntervalRate = DEFAULT_INTERVAL_RATE;

	public static String[] exportArray = null;
	private javax.swing.JFrame exportDataFrame = null;
	public static java.util.TreeMap treeMap = new java.util.TreeMap(); //keys array of values' timestamps


/**
 * Graph constructor comment.
 */
public Graph()
{
	super();
} 
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 10:46:02 AM)
 * @param gModel com.cannontech.graph.model.GraphModel
 */
public static void buildBarGraphTreeMap(GraphModel gModel)
{
	java.util.TreeMap tree = new java.util.TreeMap();
	buildTreeMap(gModel);
	tree = getTreeMap();
	
	//time values
	java.util.Set keySet = treeMap.keySet();
	Double[] keyArray = new Double[keySet.size()];
	keySet.toArray(keyArray);
 
	java.util.Vector tempTimeStampVector = new java.util.Vector();
	java.util.Vector tempValuesVector = null;

	double[] tempTimeStamp = new double[keyArray.length];
	double[] tempValues = null;

	int maxSize = 0;
	
	for( int z = 0; z < gModel.getPointIDs().length; z++ )
	{
		int saveMeIndex = -1;
		tempValuesVector = new java.util.Vector();
		
		for( int x = 0; x < keyArray.length;x++ )	//go one extra for the header row?
		{
			Double[] values = (Double[])treeMap.get(keyArray[x]);
			tempTimeStamp[x] = keyArray[x].doubleValue();
			
			if(values[z] == null)
			{
				if( saveMeIndex < 0 )	// min index not set yet
				{
					saveMeIndex = x;
				}
			}
			else
			{
				if ( saveMeIndex >= 0 )
				{
					for ( int i = saveMeIndex; i <= x; i++)
					{
						tempValuesVector.add(values[z]);
					}
				}
				else
				{
					tempValuesVector.add(values[z]);
				}
				saveMeIndex = -1;	//reset
			}
		}

		if( tempValuesVector.size() > maxSize)
		{
			maxSize = tempValuesVector.size();
		}

		tempValues  = new double[maxSize];
		
		for ( int i = 0; i < tempValuesVector.size(); i++)
		{
			Double val = (Double)tempValuesVector.get(i);
			if( val == new Double(0.0))
			{
				System.out.println("Hello");
			}
			tempValues[i]  = ((Double)tempValuesVector.get(i)).doubleValue();
			
		}
		gModel.setXSeries(z, tempTimeStamp);
		gModel.setYSeries(z, tempValues);
		System.out.println("HERE" );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 10:46:02 AM)
 * @param gModel com.cannontech.graph.model.GraphModel
 */
public static void buildTreeMap(GraphModel gModel)
{
	java.util.TreeMap tree = new java.util.TreeMap();

	for( int k = 0; k < gModel.getNumSeries(); k++ )
	{
 		double[] timeStamp = gModel.getXSeries(k);
 		double[] values = gModel.getYSeries(k);

 		for( int l = 0; timeStamp != null && values != null &&  l < timeStamp.length; l++ )
 		{
	 		Double d = new Double(timeStamp[l]);
	 		Double[] objectValues = (Double[]) tree.get(d);
	 		if( objectValues == null )
	 		{	
		 		//objectValues is not in the key already
		 		objectValues = new Double[ gModel.getPointIDs().length];
		 		tree.put(d,objectValues);
	 		}

	 		objectValues[k] = new Double(values[l]);
		}
	}

	//set up a treeMap of keys and values for Graph exporting.
	setTreeMap( tree );

}
/**
 * Encodes a gif on the given stream.  This method is used heavily
 * by the readmeter server and really should be as quick as possible.
 * Creation date: (10/12/00 2:54:13 PM)
 * @param out java.io.OutputStream
 */
public void encodeGif(java.io.OutputStream out) throws java.io.IOException
{
	synchronized(Graph.class)
	{
		updateChart();

		// FIXFIX
		// Should be able to get rid of this temporary jframe,
		// but i had trouble with memory leaking when i reused a static
		// jframe 
		// 11/00 -acl
		javax.swing.JFrame encFrame = new javax.swing.JFrame();

		encFrame.setSize(width,height );
		getChart().setSize(width,height);
		encFrame.getContentPane().add( getChart(), "Center");
		
		//make sure to create its peer
		encFrame.addNotify();

		// FIXFIX			
		// Should make this encoder static and instantiate it lazily
		// 11/00 -acl
		Acme.JPM.Encoders.GifEncoder encoder = new Acme.JPM.Encoders.GifEncoder(chart.snapshot(), out);
		encoder.encode();

		encFrame.getContentPane().removeAll();
		encFrame.dispose();		
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
			
		for (int i = 0; currentModels != null && i < currentModels.length; i++)
			for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
			{
				tabHtml.setModel( currentModels[i][j] );
				tabHtml.getHtml(buf);
			}
					
		out.write(buf.toString().getBytes());	
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
		
	for (int i = 0; currentModels != null && i < currentModels.length; i++)
		for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
		{
			tabHtml.setModel( currentModels[i][j] );
			tabHtml.getHtml(buf);
		}
		
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
			
		for (int i = 0; currentModels != null && i < currentModels.length; i++)
			for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
			{
				usageHtml.setModel( currentModels[i][j] );
				usageHtml.getHtml(buf);
			}
			
		out.write(buf.toString().getBytes());	
}
/**
 ** Both Server and Client use **
 * Insert the method's description here.
 * Creation date: (12/15/99 10:17:01 AM)
 * @return com.klg.jclass.chart.JCChart
 */
public synchronized com.klg.jclass.chart.JCChart getChart()
{
	if( chart == null )
	{ 
		chart = new PrintableChart();
		chart.setVisible(false);
		com.klg.jclass.chart.JCAxis y2Axis = new com.klg.jclass.chart.JCAxis();
		y2Axis.setVertical(true);	
		y2Axis.setPlacement(com.klg.jclass.chart.JCAxis.MAX);	
		chart.getChartArea().setYAxis(1, y2Axis );				
	}
	 
	return chart;
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
public GraphModel[][] getCurrentModels()
{
	return this.currentModels;
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
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 5:06:03 PM)
 * @param seriesType java.lang.String
 */
public String getDatabaseAlias() 
{
	return databaseAlias;
}
/**
 * Not currently used, caused resource leaks.  
 * Intention was to use a static instance of
 * a JFrame for encoding gif's to avoid creating a new one for
 * each request.  
 * Creation date: (12/15/99 10:35:06 AM)
 * @return javax.swing.JFrame
 */
private synchronized javax.swing.JFrame getEncodeFrame() {

	if( encodeFrame == null )
	{  
		encodeFrame = new javax.swing.JFrame();

		getEncodeFrame().setSize(width,height );
		getChart().setSize(width,height);
		getEncodeFrame().getContentPane().add( getChart(), "Center");
		
		//make sure to create its peer
		encodeFrame.addNotify();		
	}

	return encodeFrame;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 6:15:48 PM)
 * @return java.lang.String[]
 */
public String[] getExportArray()
{
	return exportArray;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 2:26:44 PM)
 * @return boolean
 */
public boolean getHasPeakSeries() {
	return hasPeakSeries;
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
 * Creation date: (7/10/2001 1:57:31 PM)
 * @return int
 */
public int getModelChartType()
{
	return modelChartType;
}
/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 3:54:09 PM)
 * @return int
 */
public int getModelType()
{
	return modelType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 5:06:03 PM)
 * @param seriesType java.lang.String
 */
public String getSeriesType() 
{
	return currentSeriesType;
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
/**
 * Insert the method's description here.
 * Creation date: (5/16/2001 4:18:28 PM)
 * @return java.util.TreeMap
 */
public static java.util.TreeMap getTreeMap()
{
	return treeMap;
}
/**
* Insert the method's description here.
* Creation date: (10/11/00 1:36:23 PM)
* @param model com.cannontech.graph.model.GraphModel
* @param yAxis com.klg.jclass.chart.JCAxis
* @param cdv com.klg.jclass.chart.ChartDataView
*/
private void initChartDataView(	GraphModel model,com.klg.jclass.chart.JCAxis xAxis,	com.klg.jclass.chart.JCAxis yAxis,com.klg.jclass.chart.ChartDataView cdv)
{
	java.text.SimpleDateFormat hourFormat = new java.text.SimpleDateFormat("H");
	java.text.SimpleDateFormat mmssFormat = new java.text.SimpleDateFormat("mmss");
	java.text.DecimalFormat lfValueFormat = new java.text.DecimalFormat();
	lfValueFormat.setMaximumFractionDigits(LF_DECIMAL_PLACES);

	cdv.setAutoLabel(true);
	cdv.setDataSource(model);
	cdv.setVisibleInLegend(true);
	cdv.setName(model.getName());
	cdv.setVisible(true);
	cdv.setXAxis(xAxis);
	cdv.setYAxis(yAxis);

	java.util.Iterator iter = cdv.getSeries().iterator();
	java.text.DecimalFormat decFormat = new java.text.DecimalFormat();
	int c = 0;

	while (iter.hasNext())
	{
		com.klg.jclass.chart.ChartDataViewSeries cdvs = (com.klg.jclass.chart.ChartDataViewSeries) iter.next();
		cdvs.getStyle().setSymbolShape(com.klg.jclass.chart.JCSymbolStyle.NONE);

		//if( model.getXSeries(c) != null)
		//{
		decFormat.setMinimumFractionDigits(model.getDecimalPlaces(c));
		decFormat.setMaximumFractionDigits(model.getDecimalPlaces(c));

		if (model instanceof DataViewModel || getModelChartType() == JCChart.PLOT)
		{
			//Change the color of the series's graph LINES
			if (getModelChartType() == JCChart.PLOT)
				cdvs.getStyle().setLineColor(model.getSeriesColors(c));
			else if (getModelChartType() == JCChart.BAR)
				cdvs.getStyle().setFillColor(model.getSeriesColors(c));
			else if (getModelChartType() == JCChart.AREA)
				cdvs.getStyle().setFillColor(model.getSeriesColors(c));


			if (model.getXSeries(c) != null)
			{
				//if ( model.getXSeries(c).length < 8 )
				//{
					//// If the cdvs has no values/timestamps, we don't want it to get in our way...
					//cdvs.setIncluded(false);
					//cdvs.setVisible(false);
					//cdvs.setVisibleInLegend(true);
				//}
				
				for (int i = 0; i < model.getXSeries(c).length; i++)
				{
					Double val = new Double(model.getYSeries(c)[i]);
					Double date = null;

					// Set up the flyover values for all model's graphs.
					date = new Double(model.getXSeries(c)[i] * 1000);
					com.klg.jclass.chart.JCChartLabel cl =
						new com.klg.jclass.chart.JCChartLabel(dwellValuesDateTimeformat.format(date).toString() + " ~ " + decFormat.format(val));
					cl.setDataIndex(new com.klg.jclass.chart.JCDataIndex(cdv, cdvs, c, i));
					cl.setAttachMethod(com.klg.jclass.chart.JCChartLabel.ATTACH_DATAINDEX);
					cl.setAnchor(com.klg.jclass.chart.JCChartLabel.AUTO);
					cl.setDwellLabel(true);
					chart.getChartLabelManager().addChartLabel(cl);
				}
			}
			else
			{
				// If the cdvs has no values/timestamps, we don't want it to get in our way...
				cdvs.setIncluded(false);
				cdvs.setVisible(false);
				cdvs.setVisibleInLegend(true);
			}
		}
		else if (model instanceof LoadDurationCurveModel)
		{
			//Change the color of the series's graph BARS.
			cdvs.getStyle().setFillColor(model.getSeriesColors(c));
			if (model.getXSeries(c) != null)
			{
				for (int i = 0; i < model.getXSeries(c).length; i++)
				{
					Double val = new Double(model.getYSeries(c)[i]);
					Double date = null;

					// Set up the flyover values for all model's graphs.
					date = new Double(((LoadDurationCurveModel) model).getXHours(c)[i] * 1000);
					com.klg.jclass.chart.JCChartLabel cl =
						new com.klg.jclass.chart.JCChartLabel(dwellValuesDateTimeformat.format(date).toString() + " ~ " + decFormat.format(val));
					cl.setDataIndex(new com.klg.jclass.chart.JCDataIndex(cdv, cdvs, c, i));
					cl.setAttachMethod(com.klg.jclass.chart.JCChartLabel.ATTACH_DATAINDEX);
					cl.setAnchor(com.klg.jclass.chart.JCChartLabel.SOUTH);
					cl.setDwellLabel(true);
					chart.getChartLabelManager().addChartLabel(cl);

					// Set up the Hourly labels for the bars.						
					// If the hour is top of the hour ( ex. 1:00:00)
					// We could potentially have two labels the same name.  So for those on exactly mm:ss = 00:00,
					//  the hourly value will be decremented in order to keep the labels unique.
					int hourLabel = new Integer(hourFormat.format(date)).intValue();
					if (new Integer(mmssFormat.format(date)).intValue() != 0)
						hourLabel++;
					else if (hourLabel == 0)
						hourLabel = 24;

					cl = new com.klg.jclass.chart.JCChartLabel(new Integer(hourLabel).toString());
					cl.setDataIndex(new com.klg.jclass.chart.JCDataIndex(cdv, cdvs, c, i));
					cl.setAttachMethod(com.klg.jclass.chart.JCChartLabel.ATTACH_DATAINDEX);
					cl.setAnchor(com.klg.jclass.chart.JCChartLabel.NORTH);
					chart.getChartLabelManager().addChartLabel(cl);
				}
			}
			else
			{
				// If the cdvs has no values/timestamps, we don't want it to get in our way...
				cdvs.setIncluded(false);
				cdvs.setVisible(false);
				cdvs.setVisibleInLegend(true);
			}
		}
		// ** NOTE ** Add more graphModels here when needed.
		String minString = decFormat.format(model.getMinimum(c));
		String maxString = decFormat.format(model.getMaximum(c));

		String labelString = model.getSeriesNames(c) + "  min: " + minString + "  max: " + maxString;
		if (getShowLoadFactor())
		{
			Object loadFactor = " --- "; //Load Factor Label, defaults to NaN for division by zero.
			double lf = GraphDataCalculations.calculateLoadFactor(model.getAreaOfSet(c), model.getMaxArea(c));
			if (lf != 0.0) //Check for division by zero.
				loadFactor = lfValueFormat.format(lf);

			labelString = labelString + "       |  Load Factor = " + loadFactor;
		}

		cdvs.setLabel(labelString);
		cdvs.setName("This is the name of data series" + c + ".");

		c++; //increment to the next series.
	}
}
/**
 * (Old name = initModels(...), changed 6/21/01 when LD model added - SN)
 * Creation date: (10/5/00 3:37:29 PM)
 * @return GraphModelInterface[]
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 * @param type String The type of series we want in the models
 */
private GraphModel[][] initGraphModels(com.cannontech.database.data.graph.GraphDefinition gDef, String seriesType )
{
	java.util.List dataSeries = gDef.getGraphDataSeries();
	java.util.Iterator iter = dataSeries.iterator();
	
	java.util.ArrayList rightPoints = new java.util.ArrayList(4);
	java.util.ArrayList leftPoints = new java.util.ArrayList(4);
	
	java.util.ArrayList rightSeriesTypes = new java.util.ArrayList(4);
	java.util.ArrayList leftSeriesTypes = new java.util.ArrayList(5);

	java.util.ArrayList rightUoMids = new java.util.ArrayList(4);
	java.util.ArrayList leftUoMids = new java.util.ArrayList(4);

	java.util.ArrayList rightLabels = new java.util.ArrayList(4);
	java.util.ArrayList leftLabels = new java.util.ArrayList(4);

	java.util.ArrayList rightDevices = new java.util.ArrayList(4);
	java.util.ArrayList leftDevices = new java.util.ArrayList(4);
		
	java.util.ArrayList rightColors = new java.util.ArrayList(4);
	java.util.ArrayList leftColors = new java.util.ArrayList(4);

	double leftMin = Double.MAX_VALUE;
	double rightMin = Double.MAX_VALUE;
	
	double leftMax = Double.MIN_VALUE;
	double rightMax = Double.MIN_VALUE;
		
	while( iter.hasNext() )
	{
		com.cannontech.database.db.graph.GraphDataSeries ds = (com.cannontech.database.db.graph.GraphDataSeries) iter.next();

		if(  seriesType == null || ds.getType().equalsIgnoreCase(seriesType) )
		{
			if(  ds.getAxis() != null && ( ds.getAxis().charValue() == 'R' || ds.getAxis().charValue() == 'r') )
			{
				rightPoints.add( ds.getPointID() );
				rightSeriesTypes.add( ds.getType() );
				rightUoMids.add( ds.getUoMId() );				
				rightColors.add( com.cannontech.common.gui.util.Colors.getColor(ds.getColor().intValue()) );
				rightLabels.add( ds.getLabel() );
				rightDevices.add( ds.getDeviceName() );							
		
				if( gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'N' ||
					gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'n' )
				{
					if( gDef.getGraphDefinition().getRightMin().doubleValue() < rightMin )
						rightMin = gDef.getGraphDefinition().getRightMin().doubleValue();
				
					if( gDef.getGraphDefinition().getRightMax().doubleValue() > rightMax )
						rightMax = gDef.getGraphDefinition().getRightMax().doubleValue();	
				}		
			}
			else
			{
				leftPoints.add( ds.getPointID() );
				leftSeriesTypes.add( ds.getType() );
				leftUoMids.add( ds.getUoMId() );
				leftColors.add( com.cannontech.common.gui.util.Colors.getColor(ds.getColor().intValue()) );			
				leftLabels.add( ds.getLabel() );
				leftDevices.add( ds.getDeviceName() );
						
				if( gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'N' ||
					gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'n' )
				{
					if( gDef.getGraphDefinition().getLeftMin().doubleValue() < leftMin )
						leftMin = gDef.getGraphDefinition().getLeftMin().doubleValue();
	
					if( gDef.getGraphDefinition().getLeftMax().doubleValue() > leftMax )
						leftMax = gDef.getGraphDefinition().getLeftMax().doubleValue();
				}			
			}
		} 
	}

	GraphModel leftModel = null;
	GraphModel rightModel = null;
	
	if( leftPoints.size() > 0 )
	{
		if ( getModelType() == DATA_VIEW_MODEL )
			leftModel = new DataViewModel();
		else if ( getModelType() == BAR_GRAPH_MODEL )
			leftModel = new DataViewModel();
		else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
			leftModel = new LoadDurationCurveModel();
			
		leftModel.setStartDate(gDef.getGraphDefinition().getStartDate());
		leftModel.setEndDate(gDef.getGraphDefinition().getStopDate());
		leftModel.setName(gDef.getGraphDefinition().getName());
		leftModel.setLabelMin(leftMin);
		leftModel.setLabelMax(leftMax);
		
		long[] points = new long[leftPoints.size()];
		String[] types = new String[leftPoints.size()];
		Integer[] uomIDs = new Integer[leftPoints.size()];
		String[] labels = new String[leftPoints.size()];
		String[] devices = new String[leftPoints.size()];
		java.awt.Color[] colors = new java.awt.Color[leftPoints.size()];
			
		for( int i = 0; i < points.length; i++ )
		{
			points[i] = ((Long) leftPoints.get(i)).longValue();
			types[i] = (String) leftSeriesTypes.get(i);
			uomIDs[i] = (Integer) leftUoMids.get(i);
			labels[i] = (String) leftLabels.get(i);
			devices[i] = (String) leftDevices.get(i);
			colors[i] = (java.awt.Color) leftColors.get(i);	
		}				

		leftModel.setPointIDs(points);	
		leftModel.setSeriesTypes(types);	
		leftModel.setSeriesNames(labels);
		leftModel.setSeriesDevices(devices);		
		leftModel.setSeriesColors(colors);		
	}
	
	if( rightPoints.size() > 0 )
	{
		if ( getModelType() == DATA_VIEW_MODEL )
			rightModel = new DataViewModel();
		else if ( getModelType() == BAR_GRAPH_MODEL )
			rightModel = new DataViewModel();
		else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
			rightModel = new LoadDurationCurveModel();
			
		rightModel.setStartDate(gDef.getGraphDefinition().getStartDate());
		rightModel.setEndDate(gDef.getGraphDefinition().getStopDate());
		rightModel.setName(gDef.getGraphDefinition().getName());
		rightModel.setLabelMin(rightMin);
		rightModel.setLabelMax(rightMax);
		
		long[] points = new long[rightPoints.size()];
		String[] types = new String[rightPoints.size()];
		Integer[] uomids = new Integer[rightPoints.size()];
		String[] labels = new String[rightPoints.size()];
		String[] devices = new String[rightPoints.size()];
		java.awt.Color[] colors = new java.awt.Color[rightPoints.size()];
		
		for( int i = 0; i < points.length; i++ )
		{
			points[i] = ((Long) rightPoints.get(i)).longValue();
			types[i] = (String) rightSeriesTypes.get(i);
			uomids[i] = (Integer) rightUoMids.get(i);
			labels[i] = (String) rightLabels.get(i);
			devices[i] = (String) rightDevices.get(i);			
			colors[i] = (java.awt.Color) rightColors.get(i);
		}
		
		rightModel.setPointIDs(points);
		rightModel.setSeriesTypes(types);
		rightModel.setSeriesNames(labels);
		rightModel.setSeriesDevices(devices);		
		rightModel.setSeriesColors(colors);
	}

	GraphModel[][] retVal = null;
	
	if ( getModelType() == DATA_VIEW_MODEL )
	{
		retVal = new DataViewModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new DataViewModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new DataViewModel[1];
			retVal[1][0] = rightModel;
		}
	}
	else if ( getModelType() == BAR_GRAPH_MODEL )
	{
		retVal = new DataViewModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new DataViewModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new DataViewModel[1];
			retVal[1][0] = rightModel;
		}
	}
	else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
	{
		retVal = new LoadDurationCurveModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new LoadDurationCurveModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new LoadDurationCurveModel[1];
			retVal[1][0] = rightModel;
		}
	}	
	return retVal;	
}
/**
 * (Old name = initModels(...), changed 6/21/01 when LD model added - SN)
 * Creation date: (10/5/00 3:37:29 PM)
 * @return GraphModelInterface[]
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 * @param type String The type of series we want in the models
 */
private GraphModel[][] initGraphModels(com.cannontech.database.data.graph.GraphDefinition gDef, String seriesType, String anotherSeriesType )
{
	java.util.List dataSeries = gDef.getGraphDataSeries();
	java.util.Iterator iter = dataSeries.iterator();
	
	java.util.ArrayList rightPoints = new java.util.ArrayList(4);
	java.util.ArrayList leftPoints = new java.util.ArrayList(4);
	
	java.util.ArrayList rightSeriesTypes = new java.util.ArrayList(4);
	java.util.ArrayList leftSeriesTypes = new java.util.ArrayList(5);
	 
	java.util.ArrayList rightUoMids = new java.util.ArrayList(4);
	java.util.ArrayList leftUoMids = new java.util.ArrayList(4);

	java.util.ArrayList rightLabels = new java.util.ArrayList(4);
	java.util.ArrayList leftLabels = new java.util.ArrayList(4);

	java.util.ArrayList rightDevices = new java.util.ArrayList(4);
	java.util.ArrayList leftDevices = new java.util.ArrayList(4);
		
	java.util.ArrayList rightColors = new java.util.ArrayList(4);
	java.util.ArrayList leftColors = new java.util.ArrayList(4);

	double leftMin = Double.MAX_VALUE;
	double rightMin = Double.MAX_VALUE;
	
	double leftMax = Double.MIN_VALUE;
	double rightMax = Double.MIN_VALUE;
		
	while( iter.hasNext() )
	{
		com.cannontech.database.db.graph.GraphDataSeries ds = (com.cannontech.database.db.graph.GraphDataSeries) iter.next();

		if(  seriesType == null || ds.getType().equalsIgnoreCase(seriesType) 
			|| ds.getType().equalsIgnoreCase(anotherSeriesType))
		{
			if(  ds.getAxis() != null && ( ds.getAxis().charValue() == 'R' || ds.getAxis().charValue() == 'r') )
			{
				rightPoints.add( ds.getPointID() );
				rightSeriesTypes.add( ds.getType() );
				rightUoMids.add( ds.getUoMId() );			
				rightColors.add( com.cannontech.common.gui.util.Colors.getColor(ds.getColor().intValue()) );
				rightLabels.add( ds.getLabel() );
				rightDevices.add( ds.getDeviceName() );							
		
				if( gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'N' ||
					gDef.getGraphDefinition().getAutoScaleRightAxis().charValue() == 'n' )
				{
					if( gDef.getGraphDefinition().getRightMin().doubleValue() < rightMin )
						rightMin = gDef.getGraphDefinition().getRightMin().doubleValue();
				
					if( gDef.getGraphDefinition().getRightMax().doubleValue() > rightMax )
						rightMax = gDef.getGraphDefinition().getRightMax().doubleValue();	
				}		
			}
			else
			{
				leftPoints.add( ds.getPointID() );
				leftSeriesTypes.add( ds.getType() );
				leftUoMids.add( ds.getUoMId() );
				leftColors.add( com.cannontech.common.gui.util.Colors.getColor(ds.getColor().intValue()) );			
				leftLabels.add( ds.getLabel() );
				leftDevices.add( ds.getDeviceName() );
						
				if( gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'N' ||
					gDef.getGraphDefinition().getAutoScaleLeftAxis().charValue() == 'n' )
				{
					if( gDef.getGraphDefinition().getLeftMin().doubleValue() < leftMin )
						leftMin = gDef.getGraphDefinition().getLeftMin().doubleValue();
	
					if( gDef.getGraphDefinition().getLeftMax().doubleValue() > leftMax )
						leftMax = gDef.getGraphDefinition().getLeftMax().doubleValue();
				}			
			}
		} 
	}

	GraphModel leftModel = null;
	GraphModel rightModel = null;
	
	if( leftPoints.size() > 0 )
	{
		if ( getModelType() == DATA_VIEW_MODEL )
			leftModel = new DataViewModel();
		else if ( getModelType() == BAR_GRAPH_MODEL )
			leftModel = new DataViewModel();
		else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
			leftModel = new LoadDurationCurveModel();
			
		leftModel.setStartDate(gDef.getGraphDefinition().getStartDate());
		leftModel.setEndDate(gDef.getGraphDefinition().getStopDate());
		leftModel.setName(gDef.getGraphDefinition().getName());
		leftModel.setLabelMin(leftMin);
		leftModel.setLabelMax(leftMax);
		
		long[] points = new long[leftPoints.size()];
		String[] types = new String[leftPoints.size()];
		Integer[] uomIDs = new Integer[leftPoints.size()];
		String[] labels = new String[leftPoints.size()];
		String[] devices = new String[leftPoints.size()];
		java.awt.Color[] colors = new java.awt.Color[leftPoints.size()];
			
		for( int i = 0; i < points.length; i++ )
		{
			points[i] = ((Long) leftPoints.get(i)).longValue();
			types[i] = (String) leftSeriesTypes.get(i);
			uomIDs[i] = (Integer) leftUoMids.get(i);
			labels[i] = (String) leftLabels.get(i);
			devices[i] = (String) leftDevices.get(i);
			colors[i] = (java.awt.Color) leftColors.get(i);	
		}				

		leftModel.setPointIDs(points);	
		leftModel.setSeriesTypes(types);	
		leftModel.setSeriesNames(labels);
		leftModel.setSeriesDevices(devices);		
		leftModel.setSeriesColors(colors);		
	}
	
	if( rightPoints.size() > 0 )
	{
		if ( getModelType() == DATA_VIEW_MODEL )
			rightModel = new DataViewModel();
		else if ( getModelType() == BAR_GRAPH_MODEL )
			rightModel = new DataViewModel();
		else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
			rightModel = new LoadDurationCurveModel();
			
		rightModel.setStartDate(gDef.getGraphDefinition().getStartDate());
		rightModel.setEndDate(gDef.getGraphDefinition().getStopDate());
		rightModel.setName(gDef.getGraphDefinition().getName());
		rightModel.setLabelMin(rightMin);
		rightModel.setLabelMax(rightMax);
		
		long[] points = new long[rightPoints.size()];
		String[] types = new String[rightPoints.size()];
		Integer[] uomIDs = new Integer[rightPoints.size()];
		String[] labels = new String[rightPoints.size()];
		String[] devices = new String[rightPoints.size()];
		java.awt.Color[] colors = new java.awt.Color[rightPoints.size()];
		
		for( int i = 0; i < points.length; i++ )
		{
			points[i] = ((Long) rightPoints.get(i)).longValue();
			types[i] = (String) rightSeriesTypes.get(i);
			uomIDs[i] = (Integer) rightUoMids.get(i);
			labels[i] = (String) rightLabels.get(i);
			devices[i] = (String) rightDevices.get(i);			
			colors[i] = (java.awt.Color) rightColors.get(i);
		}
		
		rightModel.setPointIDs(points);
		rightModel.setSeriesTypes(types);
		rightModel.setSeriesNames(labels);
		rightModel.setSeriesDevices(devices);		
		rightModel.setSeriesColors(colors);
	}

	GraphModel[][] retVal = null;
	
	if ( getModelType() == DATA_VIEW_MODEL )
	{
		retVal = new DataViewModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new DataViewModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new DataViewModel[1];
			retVal[1][0] = rightModel;
		}
	}
	else if ( getModelType() == BAR_GRAPH_MODEL )
	{
		retVal = new DataViewModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new DataViewModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new DataViewModel[1];
			retVal[1][0] = rightModel;
		}
	}
	else if ( getModelType() == LOAD_DURATION_CURVE_MODEL )
	{
		retVal = new LoadDurationCurveModel[2][];
		if( leftModel != null )
		{
			retVal[0] = new LoadDurationCurveModel[1];
			retVal[0][0] = leftModel;
		}

		if( rightModel != null )
		{
			retVal[1] = new LoadDurationCurveModel[1];
			retVal[1][0] = rightModel;
		}
	}	
	return retVal;	
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 2:29:14 PM)
 * @return boolean
 * @param seriesType java.lang.String
 */
public static boolean isPeakSeries(String seriesType)
{
	return seriesType.equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);
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
	
	for (int i = 0; i < currentModels.length; i++)
		for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
			for (int k = 0; k < currentModels[i][j].points.length; k++)
				pointsVector.add(new Long(currentModels[i][j].points[k]));

	Long[] pointsArray = new Long[pointsVector.size()];
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
			System.out.println(getClass() + ":  Error getting database connection.");
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
/**
 * Build an array of the points and their data.
 *  Array contains the Date, Time, Point, PointData
 * Creation date: (4/17/2001 3:15:18 PM)
 */
public void setExportArray()
{
	long timer = System.currentTimeMillis();

	valueFormat.setGroupingUsed(false);
	GraphModel gModel= null;
	try
	{
		GraphModel [][] currentModels = getCurrentModels();
			
		for (int i = 0; currentModels != null && i < currentModels.length; i++)
			for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
			{
				if ( (currentModels[i][j]) instanceof DataViewModel )
					gModel = currentModels[i][j];
				else if( (currentModels[i][j]) instanceof LoadDurationCurveModel)
					gModel = currentModels[i][j];

				// Build a tree map of the current model
				java.util.TreeMap tree = new java.util.TreeMap();
				buildTreeMap ( gModel );
				tree = getTreeMap();
				
				//time values
				java.util.Set keySet = treeMap.keySet();
				Double[] keyArray = new Double[keySet.size()];
				keySet.toArray(keyArray);

				// setup size and labels for export array 
				int csvRowCount = keySet.size() + 1;	//add one for the heading's row
				int csvColCount = gModel.getPointIDs().length + 2;	// +2 cols -> 1 for date, 1 for time

				//csvColCount always > 0 (because of +2)		
				if( csvRowCount > 0 )
					exportArray = new String[csvRowCount * csvColCount];
				else
					exportArray = new String[csvColCount];

				// label the DATE and TIME data cols
				exportArray[0] = "DATE";	// column 1
				exportArray[csvRowCount] = "TIME";	//column 2
				for( int x = 1; x < keyArray.length + 1; x++ )
				{
					Double ts1 = keyArray[x - 1]; // use x-1 to include the 0 position of the keyarray.
					
					// date data for exportArray
					exportArray[x] = (String) (dateFormat.format(new java.util.Date(ts1.longValue() * 1000)).toString());
					// time data for exportArray			
					exportArray[csvRowCount + x] = (String) (timeFormat.format(new java.util.Date(ts1.longValue() *1000)).toString());
				}

				//Go through all the points one by one and add them in the array
				for( int z = 0; z < gModel.getPointIDs().length; z++ )
				{
					
					valueFormat.setMaximumFractionDigits(gModel.getDecimalPlaces(z));
					valueFormat.setMinimumFractionDigits(gModel.getDecimalPlaces(z));

					// label the export file columns with their devide + model name
					exportArray[csvRowCount * (z+2)] =
						(gModel.getSeriesDevices(z) + " " + gModel.getSeriesNames(z));
						

					for( int x = 1; x < keyArray.length + 1;x++ )	//go one extra for the header row?
					{
						Double[] values = (Double[])treeMap.get(keyArray[x - 1]);
						if(values[z] == null)
							exportArray[(csvRowCount * (z+2)) + x ] = "";
						else
							exportArray[(csvRowCount * (z+2)) + x ] = valueFormat.format(values[z]);
					
					}
				
				}
			}
		
	}//end try
	catch(Exception e)
	{
		System.out.println(" Exception caught in Graph.setExportArray()");
		e.printStackTrace();
	}
//	System.out.println(" @EXPORT ARRAY - Took " + (System.currentTimeMillis() - timer) +" milis to update tabular pane.");
	valueFormat.setGroupingUsed(true);
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 2:23:09 PM)
 * @param value boolean
 */
public void setHasPeakSeries(boolean value)
{
	hasPeakSeries = value;
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
 * Creation date: (7/23/2001 11:11:53 AM)
 * @param newChartType int
 */
public void setModelChartType(int newModelChartType)
{
	modelChartType = newModelChartType;
}
/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 3:53:34 PM)
 * @param newModelType int
 */
public void setModelType(int newModelType)
{
	modelType = newModelType;

	if( modelType == LOAD_DURATION_CURVE_MODEL )
	{
		//For one day only we use the BAR chart format, otherwise it is still plotted.
		if( getCurrentTimePeriod() == ServletUtil.getIntValue( ServletUtil.TODAY )
			|| getCurrentTimePeriod() == ServletUtil.getIntValue( ServletUtil.ONEDAY ) )
			setModelChartType( JCChart.BAR );
		else
			setModelChartType( JCChart.PLOT );
	}
	else if ( modelType == DATA_VIEW_MODEL )
	{
		setModelChartType( JCChart.PLOT );
	}
	else if( modelType == BAR_GRAPH_MODEL )
	{
		setModelChartType( JCChart.BAR );
	}
	else if( modelType == DONT_CHANGE_MODEL )
	{
		//in that case, do nothing!
	}
	else
		setModelChartType( JCChart.PLOT );	//default !!

	// ** NOTE ** Add more chart types to this list!!
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 5:06:03 PM)
 * @param seriesType java.lang.String
 */
public void setSeriesType(String newSeriesType)
{
	currentSeriesType = newSeriesType;
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
public void setSize(int width, int height) 
{
	this.width = width;
	this.height = height;

	getChart().setPreferredSize(new java.awt.Dimension(width,height));
}  
/**
 * Insert the method's description here.
 * Creation date: (1/24/00 10:08:37 AM)
 * @param newTitle java.lang.String
 */
public void setTitle(java.lang.String newTitle) {
	title = newTitle;
}  
/**
 * Insert the method's description here.
 * Creation date: (5/16/2001 3:46:40 PM)
 * @param tree java.util.TreeMap
 */
public static void setTreeMap(java.util.TreeMap newTreeMap)
{
	treeMap = newTreeMap;
}
/** Both Client and Server use**
 * Force the graph models to update.
 * Creation date: (11/17/00 4:30:51 PM)
 */

public void update()
{
	long timer = System.currentTimeMillis();
	if( getSeriesType() == com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES )
		currentModels = initGraphModels( currentGraphDefinition, getSeriesType(), null);
	else if( getSeriesType() == com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES )
		currentModels = initGraphModels( currentGraphDefinition, getSeriesType(), com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES );
	else if( getSeriesType() == com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES )
		currentModels = initGraphModels( currentGraphDefinition, getSeriesType(), null);

	for (int i = 0; i < currentModels.length; i++)
		for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
		{
			if( getModelType() == DATA_VIEW_MODEL)
			{
				DataViewModel model = (DataViewModel) currentModels[i][j];
				if (model == null)
					continue;

				model.setDatabaseAlias(getDatabaseAlias());
				model.hitDatabase();
			}
			else if( getModelType() == BAR_GRAPH_MODEL)
			{
				DataViewModel model = (DataViewModel) currentModels[i][j];
				if (model == null)
					continue;

				model.setDatabaseAlias(getDatabaseAlias());
				model.hitDatabase();
			}
			else if( getModelType() == LOAD_DURATION_CURVE_MODEL )
			{
				LoadDurationCurveModel model = (LoadDurationCurveModel) currentModels[i][j];
				if (model == null)
					continue;

				model.setDatabaseAlias(getDatabaseAlias());
				model.hitDatabase();
			}
			//** NOTE ** Add more model types here.
		}

	// Get and Set the Minimum Scan(interval) Rate of the points' data collection
	int newIntervalRate = retrieveIntervalRate();
	setMinIntervalRate(newIntervalRate);
	//System.out.println(" @UPDATE - Took " + (System.currentTimeMillis() - timer) +" milis to update.");
}
/**
 ** Both Server and Client Use **
 * Uses the graphMethodInterface to decide which graph model is to be respresented.
 * updateChart ensures that the current graph definition is represented on the
 * current chart.
 * Creation date: (12/15/99 10:44:09 AM) - revised for more than one model 6/27/01 SN
 */
public void updateChart()
{
	long timer = System.currentTimeMillis();
	// Set up models for the type of model we are currently viewing
	GraphModel[][] models = currentModels;

	JCChart chart = getChart();

	chart.setBatched(true);
	chart.setVisible(true);

	chart.setBackground(java.awt.Color.white);
	chart.setHeader(new javax.swing.JLabel("<html><font size=\"+1\"><center>" + 
		currentGraphDefinition.getGraphDefinition().getName() + "</center><center>" 
		+ dateFormat.format(currentGraphDefinition.getGraphDefinition().getStartDate()) 
		+ " - " + dateFormat.format(currentGraphDefinition.getGraphDefinition().getStopDate() ) 
		+ "</center></font></html>"));
	chart.getHeader().setVisible(true);

	// Remove any/all existing labels from the chart
	if ( chart.getChartLabelManager().getChartLabels() != null)
		chart.getChartLabelManager().removeAllChartLabels();
		
	//If the model has no graphDataSeries (points) then we can exit here.
	if( models[0] == null && models[1] == null)
	{
		chart.getLegend().setVisible(false);
		chart.setBatched(false);	//Redraws the graph all at once rather than with each change
		return;
	}

	// Setup the chart's legend
	com.klg.jclass.chart.JCLegend legend = chart.getLegend();
	legend.setAnchor( com.klg.jclass.chart.JCLegend.SOUTH );
	legend.setVisible(true);
	legend.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
	chart.setLegend(legend);

	com.klg.jclass.chart.JCAxis xAxis = chart.getChartArea().getXAxis(0);
	com.klg.jclass.chart.JCAxis y1Axis = chart.getChartArea().getYAxis(0);
	com.klg.jclass.chart.JCAxis y2Axis = chart.getChartArea().getYAxis(1);

	if( getModelType() == LOAD_DURATION_CURVE_MODEL
		&& getModelChartType() == JCChart.BAR)    // Has no xAxis labels visible
	{
		xAxis.setAnnotationMethod(com.klg.jclass.chart.JCAxis.VALUE);
		xAxis.setMax( currentGraphDefinition.getGraphDefinition().getStopDate().getTime()/1000 + 2000);
	}
	else  // if ( getModelType() == DATA_VIEW_MODEL 
		  //		|| getModelType() == BAR_GRAPH_MODEL )
	{
		// Time Labels on the xAxis
		xAxis.setAnnotationMethod(com.klg.jclass.chart.JCAxis.TIME_LABELS);
		xAxis.setTimeBase(new java.util.Date(0));
		xAxis.setTimeUnit( com.klg.jclass.chart.JCAxis.SECONDS );
		xAxis.setMax( currentGraphDefinition.getGraphDefinition().getStopDate().getTime()/1000 );
	}

	xAxis.setMin( currentGraphDefinition.getGraphDefinition().getStartDate().getTime()/1000 );
	xAxis.setGridVisible(true);
		
	y1Axis.setGridVisible(true);
	y1Axis.getGridStyle().setLineColor(java.awt.Color.black);
	y1Axis.setMinIsDefault(true); 
	y1Axis.setMaxIsDefault(true);
	
	y2Axis.setMinIsDefault(true);
	y2Axis.setMaxIsDefault(true);

	graphColors.resetCurrentLineColor();
	
	java.util.ArrayList dataViews = new java.util.ArrayList(8);
	
	// Left Models (left yAxis).... models[0]
	if( models[0] != null )
	{
		y1Axis.setVisible(true);
		
		for( int j = 0; j < models[0].length; j++ )
		{
			GraphModel model = models[0][j];
			if( getModelType() == BAR_GRAPH_MODEL)
			{
				buildBarGraphTreeMap(model);
			}
			
			if( model == null)
				continue;

			com.klg.jclass.chart.ChartDataView cdv = new com.klg.jclass.chart.ChartDataView();
			// ** NOTE ** Also Update initChartDataView in order to accomadate new models.
			initChartDataView(model, xAxis, y1Axis, cdv );
			
			dataViews.add(cdv);

			if( model.getLabelMin() != Double.MAX_VALUE )
				y1Axis.setMin(model.getLabelMin());

			if( model.getLabelMax() != Double.MIN_VALUE )
				y1Axis.setMax(model.getLabelMax());				
		}
	}
	else
	{
		y1Axis.setVisible(false);
	}

	// Right Models (right yAxis).... models[1]	
	if( models[1] != null )
	{
		y2Axis.setVisible(true);
		
		for( int j = 0; j < models[1].length; j++ )
		{
			GraphModel model = models[1][j];
		
			if( model == null)
				continue;

			com.klg.jclass.chart.ChartDataView cdv = new com.klg.jclass.chart.ChartDataView();
			// ** NOTE ** Also Update initChartDataView in order to accomadate new models.
			initChartDataView(model, xAxis, y2Axis, cdv );

			dataViews.add(cdv);


			if( model.getLabelMin() != Double.MAX_VALUE )
				y2Axis.setMin(model.getLabelMin());

			if( model.getLabelMax() != Double.MIN_VALUE )
				y2Axis.setMax(model.getLabelMax());				
		}
	}
	else
	{
		y2Axis.setVisible(false);
	}	

	chart.setDataView(dataViews);
		
	// Set chart type for each dataseries, according to the type of model it is.
	for(int i = 0; i < chart.getNumData(); i++)
	{
		// chartType is local, set during loop through the models[][].
		if( chart.getDataView(i) != null)
			chart.getDataView(i).setChartType(getModelChartType());
	}
	
	chart.setBatched(false);
	System.out.println(" @GRAPH PANE - Took " + (System.currentTimeMillis() - timer) +" milis to update chart.");
}
}

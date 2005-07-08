package com.cannontech.graph.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.urls.TimeSeriesURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.database.db.point.PeakPointHistory;
import com.cannontech.graph.GraphColors;
import com.cannontech.jfreechart.chart.Dataset_MinMaxValues;
import com.cannontech.jfreechart.chart.HorizontalSkipLabelsCategoryAxis;
import com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax;
import com.cannontech.jfreechart.chart.XYAreaRenderer_MinMax;
import com.cannontech.jfreechart.chart.XYStepAreaRenderer_MinMax;
import com.cannontech.jfreechart.chart.XYStepRenderer_MinMax;
import com.cannontech.jfreechart.chart.YukonStandardLegend;

public class TrendModel implements com.cannontech.graph.GraphDefines 
{
	private TreeMap treeMap = null;
	private java.text.SimpleDateFormat TITLE_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMMMM dd, yyyy");
	private java.text.SimpleDateFormat LEGEND_DATE_FORMAT = new java.text.SimpleDateFormat("[EEE MMM dd, yyyy]");
	private java.text.SimpleDateFormat TRANSLATE_DATE= new java.text.SimpleDateFormat("HHmmss");
	private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
	private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
	
	private TrendProperties trendProps;
	private int localViewType = GraphRenderers.DEFAULT;
	private boolean hasBarRenderer = false;	//flag true when at least one of the trendseries items is a bar or bar3d renderer type
	private boolean hasAreaRenderer = false;	//flag true when at least one of the trendSeries items is an area renderer type
	
	// dataset is an array of size 2.
	//Dataset will be/should be the same size as trendSeries[]
	// Left and Right (Primary and Secondary respectively) yAxis datasets.	 N/A
//	private AbstractDataset [] dataset = null;
	private TrendSerie trendSeries[] = null;
	private java.util.Date startDate = null;
	private java.util.Date	stopDate = null;
    
	private YukonStandardLegend legend = null;
	private String chartName = "Yukon Trending";
    
	//Used for load duration to determine which point to reference all others by.  null is valid.
	private Integer primaryGDSPointID = null;

	// Multiple axis setup - default 2 dimension (0 = left, 1 = right)
	private Character [] autoScale = new Character[]{
		new Character('Y'), new Character('Y')
	};
	private Double [] scaleMin = new Double[]{
		new Double(0.0), new Double(0.0)
	};
	private Double [] scaleMax = new Double[]{
		new Double(100.0), new Double(100.0)
	};

/**
 * Constructor for TrendModel.
 * The startDate and stopDate values will default to the start/stop dates of newGraphDef.
 * setStartDate(...) and setStopDate(...) must be explicitely called otherwise.
 * @param graphDefinition the graphDefinition to graph
 * @param TrendProperties the properties to include
 */
public TrendModel(GraphDefinition gdef, TrendProperties props)
{
	this(gdef, gdef.getGraphDefinition().getStartDate(), gdef.getGraphDefinition().getStopDate(), props);
}

/**
 * Constructor for TrendModel.
 * @param graphDefinition the graphDefinition to graph
 * @param startDate the date to start the trend (exclusive)
 * @param stopDate the date to stop the trend (inclusive)
 * @param TrendProperties the properties to include
 */
public TrendModel(GraphDefinition gdef, java.util.Date newStartDate, java.util.Date newStopDate, TrendProperties props)
{
	// Inititialize chart properties
	setStartDate(newStartDate);
	setStopDate(newStopDate);
	setChartName(gdef.getGraphDefinition().getName());
	
	setAutoScale(gdef.getGraphDefinition().getAutoScaleLeftAxis(), PRIMARY_AXIS);
	setScaleMax(gdef.getGraphDefinition().getLeftMax(), PRIMARY_AXIS);
	setScaleMin(gdef.getGraphDefinition().getLeftMin(), PRIMARY_AXIS);
	
	setAutoScale(gdef.getGraphDefinition().getAutoScaleRightAxis(), SECONDARY_AXIS);
	setScaleMax(gdef.getGraphDefinition().getRightMax(), SECONDARY_AXIS);
	setScaleMin(gdef.getGraphDefinition().getRightMin(), SECONDARY_AXIS);


	// Inititialize series properties
	java.util.Vector dsVector = new java.util.Vector(5);	//some small initial capactiy
	for (int i = 0; i < (gdef.getGraphDataSeries().size()); i++)
	{
		GraphDataSeries gds = (GraphDataSeries)gdef.getGraphDataSeries().get(i);
		TrendSerie serie = new TrendSerie(gds);
		dsVector.add(serie);
	}

	if( !dsVector.isEmpty())
	{
		TrendSerie[] tempArray = new TrendSerie[dsVector.size()];
		dsVector.toArray(tempArray);
		setTrendSeries(tempArray);
		hitDatabase_Basic();
//		hitDatabase_Basic(GDSTypes.BASIC_TYPE);
//		hitDatabase_Basic(GDSTypes.YESTERDAY_TYPE);
//		hitDatabase_Basic(GDSTypes.USAGE_TYPE);
		
		//Peak and (specific)Date types
		for (int i = 0; i < getTrendSeries().length; i++)
		{
			if (GDSTypesFuncs.isDateType(getTrendSeries()[i].getTypeMask()) ||
				GDSTypesFuncs.isPeakType(getTrendSeries()[i].getTypeMask()))
			{
				hitDatabase_Date(getTrendSeries()[i].getTypeMask(), i);
			}
		}
	}
	else
	{
		CTILogger.info(" GraphDefinition contains NO graphDataSeries Items");
	}
	setTrendProps(props);
	
}

public TrendModel(java.util.Date newStartDate, java.util.Date newStopDate, String newChartName, LitePoint [] litePoints)
{
	// Inititialize chart properties
	setStartDate(newStartDate);
	setStopDate(newStopDate);
	setChartName(newChartName);

	GraphColors colors = new GraphColors();
	
	// Inititialize series properties	
	TrendSerie [] tempArray = new TrendSerie[litePoints.length];
	for (int i = 0; i < litePoints.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId(new Integer(((LitePoint)litePoints[i]).getPointID()));
		tempSerie.setLabel(((LitePoint)litePoints[i]).getPointName());
		
		//Use valid graph Colors, reuses colors when all have been used
		tempSerie.setColor(colors.getNextLineColor());
		tempArray[i] = tempSerie;
	}
	setTrendSeries(tempArray);
	hitDatabase_Basic();		
//	hitDatabase_Basic(GDSTypes.BASIC_GRAPH_TYPE);
}

/**
 * Takes parallel arrays representing point ids with their point names 
 * @param newStartDate
 * @param newStopDate
 * @param newChartName
 * @param ptID_
 * @param ptNames_
 */
public TrendModel(java.util.Date newStartDate, java.util.Date newStopDate, String newChartName, int[] ptID_, String[] ptNames_ )
{
	if( ptID_ == null || ptNames_ == null 
		|| (ptID_.length != ptNames_.length) )
		return;

	// Inititialize chart properties
	setStartDate(newStartDate);
	setStopDate(newStopDate);
	setChartName(newChartName);

	GraphColors colors = new GraphColors();
	// Inititialize series properties	
	TrendSerie [] tempArray = new TrendSerie[ptID_.length];
	for (int i = 0; i < ptID_.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId( new Integer(ptID_[i]) );
		tempSerie.setTypeMask( GDSTypes.BASIC_GRAPH_TYPE );
		tempSerie.setLabel( ptNames_[i] );
		//Use valid graph Colors, reuses colors when all have been used
		tempSerie.setColor(colors.getNextLineColor());

		tempArray[i] = tempSerie;
	}		
	setTrendSeries(tempArray);
	hitDatabase_Basic();
//	hitDatabase_Basic(GDSTypes.BASIC_GRAPH_TYPE);
}

public Character getAutoScale(int axisIndex)
{
	if( axisIndex >= autoScale.length)
		return autoScale[PRIMARY_AXIS];	//not found return
	return autoScale[axisIndex];
}

public Double getScaleMax(int axisIndex)
{
	if( axisIndex >= scaleMax.length)
		return scaleMax[PRIMARY_AXIS];	//not found return
	return scaleMax[axisIndex];
}
public Double getScaleMin(int axisIndex)
{
	if( axisIndex >= scaleMin.length)
		return scaleMin[PRIMARY_AXIS];	//not found return
	return scaleMin[axisIndex];
}

public String getChartName()
{
	return chartName;
}

private Dataset_MinMaxValues[] getDataset_MinMaxValues(int index)
{
	Vector minMaxValues = new Vector();
	int count =0;
	if( getTrendSeries() != null)
	{
		for( int j = 0; j < getTrendSeries().length; j++)
		{
			TrendSerie serie = getTrendSeries()[j];
			if( serie != null && (GDSTypesFuncs.isGraphType(serie.getTypeMask())))
			{
				if( serie.getAxis().equals(axisChars[index]))
				{
					if( serie.getMaximumValue() != null && serie.getMinimumValue() != null)
					{
						minMaxValues.add(new Dataset_MinMaxValues(serie.getMinimumValue().doubleValue(), serie.getMaximumValue().doubleValue()));
					}
					else
					{
						minMaxValues.add(null);
					}
				}				
			}
		}
	}
	
	Dataset_MinMaxValues[] minMaxValuesArray = new Dataset_MinMaxValues[minMaxValues.size()];
	for (int i = 0; i < minMaxValues.size(); i++)
	{
		Dataset_MinMaxValues value = (Dataset_MinMaxValues)minMaxValues.get(i);
		minMaxValuesArray[i] = value;
	}
	return minMaxValuesArray;
}
public void setAutoScale(Character newAutoScale, int axisIndex)
{
	//Assumed the index location already exists!!!
	autoScale[axisIndex] = newAutoScale;
}

public void setScaleMin(Double newMin, int axisIndex)
{
	scaleMin[axisIndex] = newMin;
}
public void setScaleMax(Double newMax, int axisIndex)
{
	scaleMax[axisIndex] = newMax;
}

private Axis getDomainAxis()
{
	if( hasBarRenderer())//getViewType() == GraphRenderers.BAR_3D || getViewType() == GraphRenderers.BAR_3D)
	{
		HorizontalSkipLabelsCategoryAxis catAxis = new HorizontalSkipLabelsCategoryAxis(getTrendProps().getDomainLabel());
		if( (getOptionsMaskSettings()  & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
			catAxis.setLabel(getTrendProps().getDomainLabel_LD());
		
		((HorizontalSkipLabelsCategoryAxis)catAxis).setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		catAxis.setTickMarksVisible(true);
		//Only allow 3 lines for the category labels
		catAxis.setMaximumCategoryLabelLines(3);
		//Allow as much room as possible for the category labels
		catAxis.setMaximumCategoryLabelWidthRatio(Float.MAX_VALUE);
		return catAxis;
	}
	else //if( getViewType() == GraphRenderers.LINE || getViewType() == GraphRenderers.STEP || getViewType() == GraphRenderers.LINE_SHAPES)
	{
		if((getOptionsMaskSettings()  & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
		{
			NumberAxis domainAxis = new NumberAxis(getTrendProps().getDomainLabel_LD());
			domainAxis.setAutoRange(false);
			domainAxis.setUpperBound(100);
			domainAxis.setTickMarksVisible(true);
			((NumberAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
		else
		{
			DateAxis domainAxis = new DateAxis(getTrendProps().getDomainLabel());//, timeline);
			domainAxis.setAutoRange(false);
			domainAxis.setMaximumDate(getStopDate());
			domainAxis.setMinimumDate(getStartDate());
			domainAxis.setTickMarksVisible(true);
			((DateAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
	}
}	
private StringBuffer getSQLQueryString(int seriesTypeMask, int pointid)
{
	String beginTSCompare = ">";
	// include the midnight reading for usage, PER DAVID!!!
	if(GDSTypesFuncs.isUsageType(seriesTypeMask))
		beginTSCompare += "=";
	
	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE, MILLIS "
	+ "FROM RAWPOINTHISTORY WHERE POINTID IN (");
	sql.append( pointid);

	sql.append(")  AND ((TIMESTAMP "+beginTSCompare+" ? AND TIMESTAMP <= ? ) ");
	sql.append(" ) ORDER BY POINTID, TIMESTAMP");
	return sql;	
}

private Integer getPrimaryGDSPointId()
{
	if( primaryGDSPointID == null)
	{
		for (int i = 0; i < getTrendSeries().length; i++)
		{
			if( GDSTypesFuncs.isPrimaryType(getTrendSeries()[i].getTypeMask()))
				primaryGDSPointID = getTrendSeries()[i].getPointId();
		}
	}
	return primaryGDSPointID;
}			
private YukonStandardLegend getLegend()
{
	//Legend setup
	if( legend == null)
		legend = new YukonStandardLegend();

	return legend;
}

public int getOptionsMaskSettings()
{
	return getTrendProps().getOptionsMaskSettings();
}

public java.util.Date getStartDate()
{
	return startDate;
}

public java.util.Date getStopDate()
{
	return stopDate;
}
private java.util.ArrayList getSubtitles()
{
	//Chart Titles
	java.util.ArrayList subtitleList = new java.util.ArrayList();
	TextTitle chartTitle = new TextTitle(TITLE_DATE_FORMAT.format(getStartDate()) + " - " + TITLE_DATE_FORMAT.format(getStopDate()));	
	subtitleList.add(chartTitle);
	return subtitleList;
}

private TextTitle getTitle()
{
	//Chart Title
	TextTitle chartTitle = new TextTitle( getChartName().toString());
	return chartTitle;
}

public TrendSerie[] getTrendSeries()
{
	return trendSeries;
}

//left side axis

private NumberAxis getRangeAxis(int axisIndex)
{
	NumberAxis rangeAxis = null;
//	if( getViewType() == GraphRenderers.BAR_3D)
//		rangeAxis = new NumberAxis3D(getTrendProps().getRangeLabel(axisIndex));
//	else
		rangeAxis = new NumberAxis(getTrendProps().getRangeLabel(axisIndex));	

	
	if( getAutoScale(axisIndex).charValue() != 'Y')
	{
		rangeAxis.setAutoRange(false);
		rangeAxis.setUpperBound(getScaleMax(axisIndex).doubleValue());
		rangeAxis.setLowerBound(getScaleMin(axisIndex).doubleValue());
	}
	rangeAxis.setTickMarksVisible(true);
	rangeAxis.setAutoRangeIncludesZero(false);
	return rangeAxis;
}

/**
 * Returns the most recent value from RPH without exceeding the maxTS millis timestamp value
 * @param pointID
 * @param maxTS
 * @return
 */
public Pair getMostRecentMarkerValue(int pointID, long maxTS)
{
	Pair timeAndValue = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try{
	
		//Collect the NEXT most RECENT RPH entry for this pointid
		StringBuffer sqlMaxQuery = new StringBuffer("SELECT TIMESTAMP, VALUE, MILLIS " + 
			" FROM RAWPOINTHISTORY WHERE POINTID = " + pointID +
			" AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY RPH2 " +
			" WHERE RPH2.POINTID = " + pointID + " AND RPH2.TIMESTAMP <= ? ) ");
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		pstmt = conn.prepareStatement(sqlMaxQuery.toString());
		
		pstmt.setTimestamp(1, new java.sql.Timestamp( maxTS ));
		rset = pstmt.executeQuery();
		while( rset.next() )
		{
			java.sql.Timestamp ts = rset.getTimestamp(1);
			Double val = new Double(rset.getDouble(2));
			short millis = rset.getShort(3);
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTimeInMillis(ts.getTime());
			tempCal.set(Calendar.MILLISECOND, millis);
			Long time = new Long(tempCal.getTimeInMillis());
								
			timeAndValue = new Pair(time, val);
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
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
			return null;
		}
	}
	return timeAndValue;							
}	
/**
 * Returns the trendSeries value.
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * @param int seriesTypeMask
 * @return com.cannontech.graph.model.TrendSerie[]
 */
private void hitDatabase_Basic() 
{
	java.util.Date timerStart = new java.util.Date();
	GregorianCalendar tempCal = null;

	if( getTrendSeries() == null)
		return;


	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	if( conn == null )
	{
		CTILogger.info(":  Error getting database connection.");
		return;
	}

	try
	{
		for (int s = 0; s < getTrendSeries().length; s++)
		{
			if (GDSTypesFuncs.isPeakType(getTrendSeries()[s].getTypeMask()) ||
				GDSTypesFuncs.isDateType(getTrendSeries()[s].getTypeMask()) )
			{
				//Do nothing!!!
			}
			
			else if( GDSTypesFuncs.isGraphType(getTrendSeries()[s].getTypeMask()) ||
				GDSTypesFuncs.isYesterdayType(getTrendSeries()[s].getTypeMask()) ||
				GDSTypesFuncs.isUsageType(getTrendSeries()[s].getTypeMask()))

			{
				StringBuffer sql = getSQLQueryString(getTrendSeries()[s].getTypeMask(), getTrendSeries()[s].getPointId().intValue());
				pstmt = conn.prepareStatement(sql.toString());
				
				int day = 0;			
				long startTS = 0;
				long stopTS = 0;
				if (GDSTypesFuncs.isYesterdayType(getTrendSeries()[s].getTypeMask()))
				{
					day = -1;
					tempCal = new GregorianCalendar();
					tempCal.setTime((Date)getStartDate().clone());
					tempCal.add(Calendar.DATE, day);
					startTS = tempCal.getTimeInMillis();
		
					tempCal.setTime((Date)getStopDate().clone());
					tempCal.add(Calendar.DATE, day);
					stopTS = tempCal.getTimeInMillis();
					for (int i = 0; i < getTrendSeries().length; i++)
					{
						if (GDSTypesFuncs.isYesterdayType(getTrendSeries()[i].getTypeMask()))
							getTrendSeries()[i].setLabel(getSerieLabel(getTrendSeries()[i].getLabel(), new Date(startTS)));
					}
					CTILogger.info("START DATE > " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
				}
				else if (GDSTypesFuncs.isUsageType(getTrendSeries()[s].getTypeMask()))
				{
					//Changed the start date to be only the startDate specified, not the whole previous day.
					// The getSQL... query generation is changed to be inclusive of the start date rather than only greater than.
					tempCal = new GregorianCalendar();
					tempCal.setTime((Date)getStartDate().clone());
					startTS = tempCal.getTimeInMillis();
					
					tempCal.setTime((Date)getStopDate().clone());
					tempCal.add(Calendar.DATE, 1);
					stopTS = tempCal.getTimeInMillis();
					CTILogger.info("START DATE >= " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
					day = 0;
				}
				else
				{
					startTS = getStartDate().getTime();
					stopTS = getStopDate().getTime();
					CTILogger.info("START DATE > " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
				}
	
			
				Pair timeAndValue = null;
				//contains org.jfree.data.time.TimeSeriesDataItem values.
				Vector timeAndValueVector = new Vector();			
				int lastPointId = -1;
	
				boolean addNext = true;
				boolean firstOne = true;
				
				java.util.Date start = getStartDate();
				
				tempCal = new GregorianCalendar();
				tempCal.setTime((Date)getStartDate().clone());
				tempCal.add(Calendar.DATE, 1);
				java.util.Date stop = tempCal.getTime();
	
				if( GDSTypesFuncs.isMarkerType(getTrendSeries()[s].getTypeMask()) && getTrendSeries()[s].getPointId().intValue() != PointTypes.SYS_PID_THRESHOLD)
				{
					timeAndValue = getMostRecentMarkerValue(getTrendSeries()[s].getPointId().intValue(), startTS);
					if( timeAndValue != null)
						timeAndValueVector.add(timeAndValue);
				}

				pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( stopTS ));
	
				rset = pstmt.executeQuery();
				 	
				while( rset.next() )
				{
					int pointID = rset.getInt(1);
	
					if( pointID != lastPointId)
					{
						if( lastPointId != -1)	//not the first one!
						{
							//Save the data you've collected into the array of models (chartmodelsArray).
							for (int i = 0; i < getTrendSeries().length; i++)
							{
								if( getTrendSeries()[i].getPointId().intValue() == lastPointId 
//									&& (getTrendSeries()[i].getTypeMask() & seriesTypeMask) == seriesTypeMask
									)
									getTrendSeries()[i].setTimeAndValueVector((Vector)timeAndValueVector.clone());
							}
							timeAndValueVector.clear();
						}
						lastPointId = pointID;
						
						if( GDSTypesFuncs.isUsageType(getTrendSeries()[s].getTypeMask()))
						{
							addNext = true;
							firstOne = true;
							start = getStartDate();
							
							tempCal = new GregorianCalendar();
							tempCal.setTime((Date)getStartDate().clone());
							tempCal.add(Calendar.DATE, 1);
							stop = new java.util.Date (tempCal.getTimeInMillis());
						}
					}
					
//					new pointid in rset.
					//init everything, a new freechartmodel will be created with the change of pointid.
					java.sql.Timestamp ts = rset.getTimestamp(2);
					Double val = new Double(rset.getDouble(3));
					short millis = rset.getShort(4);
					
					tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(ts.getTime());
					tempCal.set(Calendar.MILLISECOND, millis);
					tempCal.add(Calendar.DATE, Math.abs(day));	//map timestamps to current start/stop range.
					Long time = new Long(tempCal.getTimeInMillis());					
					if( GDSTypesFuncs.isUsageType(getTrendSeries()[s].getTypeMask()))
					{
						java.util.Date tsDate = new java.util.Date(ts.getTime());
	
						if(tsDate.compareTo(start) < 0)
						{	
							timeAndValue = new Pair(time, val);
						}
						else if(tsDate.compareTo(start) == 0)
						{	
							timeAndValue = new Pair(time, val);
							timeAndValueVector.add(timeAndValue);
							firstOne = false;
						}
						else if( tsDate.compareTo(stop) >= 0)
						{
							if(firstOne)
							{
								if( timeAndValue == null)
									timeAndValue = new Pair(time, val);						

								timeAndValueVector.add(timeAndValue);
								firstOne = false;
							}
							if( addNext )
							{
								timeAndValue = new Pair(time, val);
								timeAndValueVector.add(timeAndValue);
								addNext = false;							
							}
	
							if(stop.getTime() < getStopDate().getTime())
							{
								start = stop;
								tempCal = new GregorianCalendar();
								tempCal.setTime((Date)start.clone());
								tempCal.add(Calendar.DATE, 1);
								stop = new java.util.Date(tempCal.getTimeInMillis());
								addNext = true;
							}
	
						}
						else
						{
							if( firstOne)
							{
								if( timeAndValue == null)
									timeAndValue = new Pair(time, val);

								timeAndValueVector.add(timeAndValue);
								firstOne = false;
							}
						}
					}
					else
					{
						timeAndValue = new Pair(time, val);
						timeAndValueVector.add(timeAndValue);
					}
				}	//END WHILE
				
				if( GDSTypesFuncs.isMarkerType(getTrendSeries()[s].getTypeMask()) && getTrendSeries()[s].getPointId().intValue() != PointTypes.SYS_PID_THRESHOLD)
				{
					if( timeAndValue != null)	//Need to add the very last time possible to complete a proper renderering of the whole day (or current value).
												//Use the "Value" of the most recently collected timeAndValue Pair
					{
						Double mostRecentValue = (Double)timeAndValue.getSecond();
						timeAndValue = new Pair(new Long(stopTS), mostRecentValue);
						timeAndValueVector.add(timeAndValue);
						lastPointId = getTrendSeries()[s].getPointId().intValue();
					}
				}				
				
				if( !timeAndValueVector.isEmpty())
				{
//					for (int i = 0; i < getTrendSeries().length; i++)
					{
						if( getTrendSeries()[s].getPointId().intValue() == lastPointId
//							&& (getTrendSeries()[i].getTypeMask() & seriesTypeMask) == seriesTypeMask
							)
							getTrendSeries()[s].setTimeAndValueVector((Vector)timeAndValueVector.clone());
	
					}
					timeAndValueVector.clear();
				}
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
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
			return;
		}	
		java.util.Date timerStop = new java.util.Date();
//		CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
//				" Secs for Trend Data Load ( for type:" + GDSTypesFuncs.getType(seriesTypeMask) + " )");
	}
}
private void hitDatabase_Date(int seriesTypeMask, int serieIndex) 
{
	java.util.Date timerStart = new java.util.Date();
	GregorianCalendar tempCal = new GregorianCalendar();

	if( getTrendSeries()[serieIndex]== null)
		return;

	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE, MILLIS " + 
	" FROM RAWPOINTHISTORY WHERE POINTID = " + getTrendSeries()[serieIndex].getPointId().intValue() +
	" AND (TIMESTAMP > ? AND TIMESTAMP <= ? ) ORDER BY POINTID, TIMESTAMP");
	 
	if( sql == null)
		return;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			CTILogger.info(":  Error getting database connection.");
			return;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			int day = 0;
			long startTS = 0;
			long stopTS = 0;

			if (GDSTypesFuncs.isPeakType(seriesTypeMask))
			{
				if (GDSTypesFuncs.isPeakType(getTrendSeries()[serieIndex].getTypeMask()))
				{ 
					day = retrievePeakIntervalTranslateDays(serieIndex);
					if ( day == -1 )
						return;
				}
			}
			else if (GDSTypesFuncs.isDateType(seriesTypeMask))
			{
				if (GDSTypesFuncs.isDateType(getTrendSeries()[serieIndex].getTypeMask()))
				{
					Date tempDate = new Date(Long.valueOf(getTrendSeries()[serieIndex].getMoreData()).longValue());
					day = TimeUtil.differenceInDays(getStartDate(), tempDate);
					if ( tempDate.getTime() > getStartDate().getTime())
						day = -day;
				}
			}

			tempCal.setTime(getStartDate());
			tempCal.add(Calendar.DATE, - day);
			startTS = tempCal.getTimeInMillis();
			
			tempCal.setTime(getStartDate());
			tempCal.add(Calendar.DATE, -day+1);
			stopTS = tempCal.getTimeInMillis();
			getTrendSeries()[serieIndex].setLabel(getSerieLabel(getTrendSeries()[serieIndex].getLabel(), new Date(startTS)));			
			
			CTILogger.info("START DATE > " + new Timestamp(startTS) + "  -  STOP DATE <= " + new Timestamp(stopTS));
			pstmt.setTimestamp(1, new Timestamp( startTS ));
			pstmt.setTimestamp(2, new Timestamp( stopTS ));

			rset = pstmt.executeQuery();
			 
			Pair timeAndValue = null;
			//contains org.jfree.data.time.TimeSeriesDataItem values.
			Vector timeAndValueVector = new Vector();
			int lastPointId = -1;

			boolean addNext = true;
			boolean firstOne = true;
			java.util.Date start = getStartDate();
			
			tempCal.setTime((Date)start.clone());
			tempCal.add(Calendar.DATE, 1);
			java.util.Date stop = tempCal.getTime();

			while( rset.next() )
			{
				int pointID = rset.getInt(1);

				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						//Save the data you've collected into the array of models (chartmodelsArray).
						//MAY NOT NEED THIS CAUSE ONLY HAVE ONE POINTID!
						if( getTrendSeries()[serieIndex].getPointId().intValue() == lastPointId 
								&& (getTrendSeries()[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
							getTrendSeries()[serieIndex].setTimeAndValueVector((Vector)timeAndValueVector.clone());
							
						timeAndValueVector.clear();
					}
					lastPointId = pointID;
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				Double val = new Double(rset.getDouble(3));
				short millis = rset.getShort(4);
				
				tempCal = new GregorianCalendar();
				tempCal.setTimeInMillis(ts.getTime());
				tempCal.set(Calendar.MILLISECOND, millis);
				tempCal.add(Calendar.DATE, day);	//map timestamps to current start/stop range.
				Long time = new Long(tempCal.getTimeInMillis());
				
				timeAndValue = new Pair(time, val);
				timeAndValueVector.add(timeAndValue);
							
			}	//END WHILE
			
			if( !timeAndValueVector.isEmpty())
			{
				// Repeat the interval x # of days with Peak_interval data series.
				if (GDSTypesFuncs.isPeakType(seriesTypeMask ) || GDSTypesFuncs.isDateType(seriesTypeMask))
				{
					int size = timeAndValueVector.size();
					
					long numDays = (getStopDate().getTime() - getStartDate().getTime()) / 86400000;
					for ( long i = 1; i < numDays; i++)
					{
						for (int j = 0; j < size; j++)
						{
							Pair repeatTimeAndValue = (Pair)timeAndValueVector.get(j);
							Double v = (Double)repeatTimeAndValue.getSecond();
							tempCal.setTimeInMillis(((Long)repeatTimeAndValue.getFirst()).longValue());
							tempCal.add(Calendar.DATE, (int)i);
							Long t = new Long(tempCal.getTimeInMillis());
							
							timeAndValue = new Pair(t, v);
							timeAndValueVector.add(timeAndValue);
						}					
					}
				}
				
				if( getTrendSeries()[serieIndex].getPointId().intValue() == lastPointId
					&& (getTrendSeries()[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
				{

					getTrendSeries()[serieIndex].setTimeAndValueVector((Vector)timeAndValueVector.clone());
				}
				timeAndValueVector.clear(); 
			}
			else 
			{
				return;
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
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
			return;
		}	
		java.util.Date timerStop = new java.util.Date();
		CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for Trend Data Load ( for type:" + GDSTypesFuncs.getType(seriesTypeMask) + " )");
	}
}

private int retrievePeakIntervalTranslateDays(int serieIndex)
{
	int translateDays = 0;

	List peakPoints = getPeakPointHistory(serieIndex);
	if( peakPoints.size() > 0 )
	{
		java.util.Iterator iter = peakPoints.iterator();
		while( iter.hasNext() )
		{
			PeakPointHistory pph = (PeakPointHistory) iter.next();
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.setTime(pph.getTimeStamp().getTime());
			String time = TRANSLATE_DATE.format(cal.getTime());

			if( Integer.valueOf(time).intValue() == 0)	//must have Day+1 00:00:00 instead of Day 00:00:01+
			{	
				cal.add(Calendar.DATE, -1);
			}
			else
			{
				cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
				cal.set(java.util.Calendar.MINUTE, 0);
				cal.set(java.util.Calendar.SECOND, 0);
				cal.set(java.util.Calendar.MILLISECOND, 0);
			}
			translateDays = TimeUtil.differenceInDays(startDate, cal.getTime());
			CTILogger.info(" PEAK POINT TS/VALUE = " + pph.getPointID() + " | " + pph.getTimeStamp().getTime() + " | " + pph.getValue());
			break;
		}
	}
	else
		return -1;	//no peak found

	return translateDays;
}

private List getPeakPointHistory(int serieIndex)
{
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code
			
	//temp code
	timerStart = new java.util.Date();
	//temp code
			
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	List peakPointHist = new ArrayList();
	
	try
	{
/*
 * Deprecating this query as it sucks.  Takes too much time and kills trending.  Trying a new 
 * approach of selecting everything but only allowing one row to be returned. SN 20040804
		String sqlString = " SELECT RPH1.POINTID POINTID, RPH1.VALUE VALUE, MIN(RPH1.TIMESTAMP) TIMESTAMP " +
			   " FROM RAWPOINTHISTORY RPH1 WHERE " +
			   " RPH1.TIMESTAMP >= ? " +
			   " AND RPH1.POINTID = " + getTrendSeries()[serieIndex].getPointId().intValue()+
			   " AND RPH1.VALUE = ( SELECT MAX(RPH2.VALUE) FROM RAWPOINTHISTORY RPH2 " +
			   " WHERE RPH1.POINTID = RPH2.POINTID " +
			   " AND RPH2.TIMESTAMP >= ? " +
			   " AND RPH2.POINTID = " + getTrendSeries()[serieIndex].getPointId().intValue() +
			   " ) GROUP BY RPH1.POINTID, RPH1.VALUE";
*/
		String sqlString = " SELECT RPH1.POINTID POINTID, RPH1.VALUE VALUE, RPH1.TIMESTAMP TIMESTAMP " +
			   " FROM RAWPOINTHISTORY RPH1 WHERE " +
			   " RPH1.TIMESTAMP >= ? " +
			   " AND RPH1.POINTID = " + getTrendSeries()[serieIndex].getPointId().intValue()+
			   " ORDER BY VALUE DESC, TIMESTAMP";

		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
		
		if( conn == null )
		{
			CTILogger.error(getClass() + ":  Error getting database connection.");
			return null;
		}
		else
		{
			pstmt = conn.prepareStatement(sqlString.toString());
			pstmt.setTimestamp(1, new Timestamp( Long.valueOf(getTrendSeries()[serieIndex].getMoreData()).longValue()));
			//pstmt.setTimestamp(2, new Timestamp( Long.valueOf(getTrendSeries()[serieIndex].getMoreData()).longValue()));
			CTILogger.info("PEAK START DATE > " + new Timestamp(Long.valueOf(getTrendSeries()[serieIndex].getMoreData()).longValue()));
		
			pstmt.setMaxRows(1);
			rset = pstmt.executeQuery();
			
			while (rset.next())
			{					
				Integer pointID = new Integer(rset.getInt(1));
				Double value = new Double(rset.getDouble(2));
				Timestamp ts = rset.getTimestamp(3);
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime(new Date(ts.getTime()));
				//cal.setTimeInMillis(ts.getTime());	// THIS IS A JDK1.4 thing
				com.cannontech.database.db.point.PeakPointHistory pph =	new com.cannontech.database.db.point.PeakPointHistory(pointID, cal, value);

				peakPointHist.add(pph);
			}
		}
				
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null )
				pstmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
				
		//temp code
		timerStop = new java.util.Date();
		CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for PeakPointHistoryLoader (" + peakPointHist.size() + " loaded)" );
		//temp code
	}
	return peakPointHist;
		
}

/**
 * Starting point for the demonstration application.
 */
public static void main(String[] args)
{
/*
	TestFreeChart demo = new TestFreeChart("Time Series Demo 1");
	demo.addWindowListener(new java.awt.event.WindowAdapter()
	{
		public void windowClosing(java.awt.event.WindowEvent e)
		{
			System.exit(0);
		};
	});
	demo.pack();
	demo.setVisible(true);
*/
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
protected void setChartName(String newChartName)
{
	chartName = newChartName;
}
    
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public void setStartDate(java.util.Date newStartDate)
{
	startDate = newStartDate;
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public void setStopDate(java.util.Date newStopDate)
{
	stopDate = newStopDate;
}
/*public Dataset getDataset(int index)
{
	if( dataset != null)
	{
		if( dataset[index] != null)
		{
			if( dataset[index] instanceof TimeSeriesCollection)
			{
				if(( (TimeSeriesCollection)dataset[index]).getSeries().isEmpty())
					return null; 
			}
			else if( dataset[index] instanceof XYSeriesCollection)
			{
				if(( (XYSeriesCollection)dataset[index]).getSeries().isEmpty())
					return null;
			} 
			else if( dataset[index] instanceof DefaultCategoryDataset)
			{
				if(( (DefaultCategoryDataset)dataset[index]).getRowKeys().isEmpty())
					return null;
			}
		}
		return dataset[index];
	}
	return null;
}*/
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public JFreeChart refresh()
{

	if( getViewType() == GraphRenderers.TABULAR || getViewType() == GraphRenderers.SUMMARY)
		return null;

	//Plot setup
	Plot plot = null;
	
	int datasetCount = 0;
	int currentIndex = -1;
	Vector indexCharacterVector = new Vector(2);	//will change size in future, currently only 2 axis permitted

	//The timeSeries returned depends on the options and if it is a category plot or not.
	//If isCategoryPlot, then timeSeries[0] will have one dataset with ALL of the series with a "bar" type renderer.  This is because we cannot
	// add a dataset to an existing dataset on a CategoryPlot like we can on a XYPlot.  All other renderer types will return one entry 
	// in the array for each trendSerie. 	
	Object[] timeSeries = YukonDataSetFactory.createDataset( getTrendSeries(), getOptionsMaskSettings(), hasBarRenderer(), getViewType());
	
	if( !hasBarRenderer())
	{
		// Create a new XYPlot, set the domain axis and a default renderer (renderer will be overridden based on trendSerie value)
		plot = new XYPlot(null, (ValueAxis)getDomainAxis(), null, null);
		
		//Weaken the alpha if this is an area type renderer, so we can see "through" it
		if ( hasAreaRenderer())
			plot.setForegroundAlpha(0.5f);

		for(int i = 0; i < getTrendSeries().length; i++)
		{
			XYItemRenderer rend = null;
			TrendSerie serie = getTrendSeries()[i];
			if( serie != null && 
					(GDSTypesFuncs.isGraphType(serie.getTypeMask()) ||
					 GDSTypesFuncs.isMarkerType(serie.getTypeMask()) ) )	//A valid "graph" serie is found
			{
				
				//Find which axis index we are using
				if( !indexCharacterVector.contains(serie.getAxis()))
					indexCharacterVector.add(serie.getAxis());
				currentIndex = indexCharacterVector.indexOf(serie.getAxis());
				

				//Add a new rangeAxis if needed
				if( ((XYPlot)plot).getRangeAxis(currentIndex) == null)
				{
					((XYPlot)plot).setRangeAxis(currentIndex, getRangeAxis(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1));
					
					if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]))
						((XYPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.BOTTOM_OR_LEFT);
					else if( serie.getAxis().equals(axisChars[SECONDARY_AXIS]))
						((XYPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.TOP_OR_RIGHT);

				}							
				
				//Create a new renderer for each serie
				rend = (XYItemRenderer)getRenderer(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1, serie.getRenderer());

				//If Marker type and SystemPoint, setup differently, setup as straight line (ValueMarker)
				if( GDSTypesFuncs.isMarkerType(serie.getTypeMask()) && serie.getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD)	//setup the threshold type
				{
					Marker marker = new ValueMarker(serie.getMultiplier().doubleValue());//, serie.getColor());
					marker.setPaint(serie.getColor());					
					marker.setLabelPaint(serie.getColor());
					marker.setLabel(serie.getMultiplier().toString());
					
					((XYPlot)plot).addRangeMarker(currentIndex, marker, Layer.BACKGROUND);
					if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]))
					{
						marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
						marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
					}
					else
					{
						marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
						marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
					}
				}
				rend.setSeriesPaint(0, serie.getColor());

				//TODO find out what urlGenerator does again.
				TimeSeriesURLGenerator urlg = new TimeSeriesURLGenerator(dateFormat, "user_trending.jsp", "gdefid", "startdate");
				rend.setURLGenerator(urlg);
				rend.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator(CATEGORY_FORMAT_MMM_dd_HH_mm.toPattern(), extendedTimeFormat, valueFormat));
				rend.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());

			
				((XYPlot)plot).setRenderer(datasetCount, rend);
				if( serie != null)// && timeSeries[i] != null)
				{
					if( timeSeries[i] instanceof TimeSeries)
					{
						if( ((XYPlot)plot).getDataset(datasetCount) == null)
							((XYPlot)plot).setDataset(datasetCount, new TimeSeriesCollection());
						((TimeSeriesCollection)((XYPlot)plot).getDataset(datasetCount)).addSeries((TimeSeries)timeSeries[i]);
					}
					else if( timeSeries[i] instanceof XYSeries)
					{
						if( ((XYPlot)plot).getDataset(datasetCount) == null)
							((XYPlot)plot).setDataset(datasetCount, new XYSeriesCollection());
						((XYSeriesCollection)((XYPlot)plot).getDataset(datasetCount)).addSeries((XYSeries)timeSeries[i]);
					}
					
					((XYPlot)plot).mapDatasetToRangeAxis(datasetCount, currentIndex);
						datasetCount++;
				}
			}
		}					

		if( ((XYPlot)plot).getRangeAxis() == null)	//lets create one by default so we can draw an "empty" trend at least
		{
			((XYPlot)plot).setRenderer( new XYAreaRenderer(XYAreaRenderer.LINES));
			((XYPlot)plot).setRangeAxis(0, getRangeAxis(0));	//give it a default range axis too!
			((XYPlot)plot).getRangeAxis().setVisible(false);	//hide the axis
			((XYPlot)plot).getDomainAxis().setVisible(false);	//hide the axis
		}
	}
	else //if( GraphRenderers.useCategoryPlot(getViewType()))	
	{
		// Create a new CategoryPlot, set the domain axis and a default renderer (renderer will be overridden based on trendSerie value)
		plot = new CategoryPlot( null, (HorizontalSkipLabelsCategoryAxis)getDomainAxis(), null, null);// new BarRenderer());

		CategoryItemRenderer catRend = null;
		int barSerieCount = 0;
		for(int i = 0; i < getTrendSeries().length; i++)
		{
			TrendSerie serie = getTrendSeries()[i];
			if( serie != null && GDSTypesFuncs.isGraphType(serie.getTypeMask()) )	//A valid "graph" serie is found
			{
				if( !(GDSTypesFuncs.isMarkerType(serie.getTypeMask()) && serie.getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) &&
				 ( (getViewType() == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(serie.getRenderer()) )
					|| GraphRenderers.useCategoryPlot(getViewType()) ) )
				{
//						Find which axis index we are using
					if( !indexCharacterVector.contains(serie.getAxis()))
						indexCharacterVector.add(serie.getAxis());
					currentIndex = indexCharacterVector.indexOf(serie.getAxis());


					if (((CategoryPlot)plot).getRangeAxis(currentIndex) == null)	//We have the first valid serie to add, setup the plot.
					{
						((CategoryPlot)plot).setRangeAxis(currentIndex, getRangeAxis(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1));
						if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]) )
							((CategoryPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.BOTTOM_OR_LEFT);
						else if (serie.getAxis().equals(axisChars[SECONDARY_AXIS]))
							((CategoryPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.TOP_OR_RIGHT);
					}

					if( catRend == null)
						catRend = (CategoryItemRenderer)getRenderer(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1, serie.getRenderer());
					else
						catRend = ((CategoryPlot)plot).getRenderer();

					if( catRend != null)
					{
						catRend.setSeriesPaint(barSerieCount++, serie.getColor());
						catRend.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
						catRend.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
						catRend.setItemURLGenerator(new StandardCategoryURLGenerator());
					
						((CategoryPlot)plot).setRenderer(datasetCount, catRend);
					}
				}
			}
		}
		if( catRend != null)	//need something to check to make sure we should really add this.
		{
			((CategoryPlot)plot).setDataset(datasetCount, (DefaultCategoryDataset)timeSeries[datasetCount]);
			((CategoryPlot)plot).mapDatasetToRangeAxis(datasetCount, currentIndex);
			datasetCount++;
		}
		//Do all other "non bar" types
		for(int i = 0; i < getTrendSeries().length; i++)
		{
			TrendSerie serie = getTrendSeries()[i];
			{					
				if( serie != null && 
						(GDSTypesFuncs.isGraphType(serie.getTypeMask()) ||
						 GDSTypesFuncs.isMarkerType(serie.getTypeMask()) ) )	//A valid "graph" serie is found
				{
					//Continue on for system point markers and for any other non bar renderer types
					if ( (GDSTypesFuncs.isMarkerType(serie.getTypeMask()) && serie.getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) ||
						 !( (getViewType() == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(serie.getRenderer()) )
									|| GraphRenderers.useCategoryPlot(getViewType()) )
									) 

					{

	//					Find which axis index we are using
						if( !indexCharacterVector.contains(serie.getAxis()))
							indexCharacterVector.add(serie.getAxis());
						currentIndex = indexCharacterVector.indexOf(serie.getAxis());
	
	
						if (((CategoryPlot)plot).getRangeAxis(currentIndex) == null)	//We have the first valid serie to add, setup the plot.
						{
							((CategoryPlot)plot).setRangeAxis(currentIndex, getRangeAxis(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1));
							if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]) )
								((CategoryPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.BOTTOM_OR_LEFT);
							else if (serie.getAxis().equals(axisChars[SECONDARY_AXIS]))
								((CategoryPlot)plot).setRangeAxisLocation( currentIndex, AxisLocation.TOP_OR_RIGHT);
						}
	
						catRend = (CategoryItemRenderer)getRenderer(serie.getAxis().equals(axisChars[PRIMARY_AXIS])?0:1, serie.getRenderer());
	
	
						if( GDSTypesFuncs.isMarkerType(serie.getTypeMask()) && serie.getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD)	//setup the value marker type
						{
							Marker marker = new ValueMarker(serie.getMultiplier().doubleValue());//, serie.getColor());
							marker.setPaint(serie.getColor());					
							marker.setLabelPaint(serie.getColor());
							marker.setLabel(serie.getMultiplier().toString());
							((CategoryPlot)plot).addRangeMarker(currentIndex, marker, Layer.BACKGROUND);								
							if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]))
							{
								marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
								marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
							}
							else
							{
								marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
								marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
							}
						}

						catRend.setSeriesPaint(0, serie.getColor());
						catRend.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
						catRend.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
						catRend.setItemURLGenerator(new StandardCategoryURLGenerator());
	
						((CategoryPlot)plot).setRenderer(datasetCount, catRend );
						if( serie != null  )
						{
//							if(!( GDSTypesFuncs.isMarkerType(serie.getTypeMask()) && serie.getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD) )
							{
								if( ((CategoryPlot)plot).getDataset(datasetCount) == null)
									((CategoryPlot)plot).setDataset(datasetCount, (DefaultCategoryDataset)timeSeries[datasetCount]);
							}
								((CategoryPlot)plot).mapDatasetToRangeAxis(datasetCount, currentIndex);
							
							datasetCount++;
						}
					}
				}				
			}
		}

		if( ((CategoryPlot)plot).getRangeAxis() == null)	//lets create one by default so we can draw an "empty" trend at least
		{
			((CategoryPlot)plot).setRangeAxis(0, getRangeAxis(0));	//give it a default range axis too!
			((CategoryPlot)plot).getRangeAxis().setVisible(false);	//hide the axis
			((CategoryPlot)plot).getDomainAxis().setVisible(false);	//hide the axis
		}
		//Force the bars to be drawn in the background
		((CategoryPlot)plot).setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
//		//Weaken the alpha if this is an area type renderer, so we can see "through" it
//		plot.setForegroundAlpha(0.5f);
	}	


	JFreeChart fChart = null;
	fChart = new JFreeChart(plot);
	fChart.setOldLegend( getLegend() );
	fChart.setTitle(getTitle());
	fChart.setSubtitles(getSubtitles());
	fChart.setBackgroundPaint(java.awt.Color.white);    
	return fChart;
 }

	/**
	 * Returns the renderer for the graph based on the GraphRenderers type value
	 * @return XYItemRenderer
	 */ 

	private AbstractRenderer getRenderer(int axisIndex, int rendType)
	{
		int tempViewType = (getViewType() == GraphRenderers.DEFAULT ? rendType : getViewType());
		AbstractRenderer rend = null;
		if( !hasBarRenderer())
		{
			switch (tempViewType)
			{
				case GraphRenderers.LINE:
				{
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						//TODO set tooltip generator.
						rend = new StandardXYItemRenderer_MinMax(XYAreaRenderer.LINES);
						((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYAreaRenderer(XYAreaRenderer.LINES);
					
					break;
				}
				case GraphRenderers.LINE_SHAPES:
				{
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						//TODO set tooltip generator.
						rend = new StandardXYItemRenderer_MinMax(XYAreaRenderer.SHAPES_AND_LINES);
						((StandardXYItemRenderer_MinMax)rend).setShapesFilled(false);
						((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYAreaRenderer(XYAreaRenderer.SHAPES_AND_LINES);
					break;
				}
					
				case GraphRenderers.LINE_AREA:
				{
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						//TODO set tooltip generator.
						rend = new XYAreaRenderer_MinMax(XYAreaRenderer.AREA);
						((XYAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYAreaRenderer(XYAreaRenderer.AREA);
					break;
				}
				case GraphRenderers.LINE_AREA_SHAPES:
				{
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						//TODO set tooltip generator.
						rend = new XYAreaRenderer_MinMax(XYAreaRenderer.AREA_AND_SHAPES);
						((XYAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYAreaRenderer(XYAreaRenderer.AREA_AND_SHAPES);
					break;
				}
				case GraphRenderers.STEP:
				{
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						rend = new XYStepRenderer_MinMax(XYStepRenderer_MinMax.MIN_MAX);
						((XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYStepRenderer();
					break;			
				}
				case GraphRenderers.STEP_SHAPES:
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						rend = new XYStepRenderer_MinMax(XYStepRenderer_MinMax.MIN_MAX_WITH_SHAPES);
						((XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYStepRenderer_MinMax(XYStepRenderer_MinMax.SHAPES);
					break;
				case GraphRenderers.STEP_AREA:
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						rend = new XYStepAreaRenderer_MinMax(XYStepAreaRenderer.AREA);
						((XYStepAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYStepAreaRenderer(XYStepAreaRenderer.AREA);
					break;			    
				case GraphRenderers.STEP_AREA_SHAPES:
					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
					{
						rend = new XYStepAreaRenderer_MinMax(XYStepAreaRenderer.AREA_AND_SHAPES);
						((XYStepAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
					}
					else
						rend = new XYStepAreaRenderer(XYStepAreaRenderer.AREA_AND_SHAPES);
					break;
					
	//			case GraphRenderers.TABULAR:
	//				return TABULAR_STRING;
	//			case GraphRenderers.SUMMARY:
	//				return SUMMARY_STRING;
	//			case GraphRenderers.DEFAULT:
	//				return DEFAULT_STRING;
				default:
					return null;
			}
		}	
		else	//we need to get a category type renderer
		{
			switch (tempViewType)
			{
				case GraphRenderers.LINE:
				{
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						//TODO set tooltip generator.
//						rend = new StandardXYItemRenderer_MinMax(XYAreaRenderer.LINES);
//						((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new LineAndShapeRenderer(true, false);
		
					break;
				}
				case GraphRenderers.LINE_SHAPES:
				{
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						//TODO set tooltip generator.
//						rend = new StandardXYItemRenderer_MinMax(XYAreaRenderer.SHAPES_AND_LINES);
//						((StandardXYItemRenderer_MinMax)rend).setShapesFilled(false);
//						((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
					rend = new LineAndShapeRenderer(true, true);
					break;
				}
		
				case GraphRenderers.LINE_AREA:
				{
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						//TODO set tooltip generator.
//						rend = new XYAreaRenderer_MinMax(XYAreaRenderer.AREA);
//						((XYAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new AreaRenderer();
					break;
				}
				case GraphRenderers.LINE_AREA_SHAPES:
				{
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						//TODO set tooltip generator.
//						rend = new XYAreaRenderer_MinMax(XYAreaRenderer.AREA_AND_SHAPES);
//						((XYAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new AreaRenderer();
					break;
				}
				case GraphRenderers.STEP:
				{
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						rend = new XYStepRenderer_MinMax(XYStepRenderer_MinMax.MIN_MAX);
//						((XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new CategoryStepRenderer();
					break;			
				}
				case GraphRenderers.STEP_SHAPES:
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						rend = new XYStepRenderer_MinMax(XYStepRenderer_MinMax.MIN_MAX_WITH_SHAPES);
//						((XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new CategoryStepRenderer();
					break;
				case GraphRenderers.STEP_AREA:
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						rend = new XYStepAreaRenderer_MinMax(XYStepAreaRenderer.AREA);
//						((XYStepAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new CategoryStepRenderer();
					break;			    
				case GraphRenderers.STEP_AREA_SHAPES:
//					if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
//					{
//						rend = new XYStepAreaRenderer_MinMax(XYStepAreaRenderer.AREA_AND_SHAPES);
//						((XYStepAreaRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(axisIndex);
//					}
//					else
						rend = new CategoryStepRenderer();
					break;
				case GraphRenderers.BAR:
					return new BarRenderer();
				case GraphRenderers.BAR_3D:
					return new BarRenderer3D(10,10);
		
	//			case GraphRenderers.TABULAR:
	//				return TABULAR_STRING;
	//			case GraphRenderers.SUMMARY:
	//				return SUMMARY_STRING;
	//			case GraphRenderers.DEFAULT:
	//				return DEFAULT_STRING;
				default:
					return null;
			}					
		}
		return rend;	
	}
	/**
	 * @return
	 */
	public TrendProperties getTrendProps()
	{
		if( trendProps == null)
			setTrendProps(new TrendProperties());
		return trendProps;
	}

	/**
	 * @param properties
	 */
	public void setTrendProps(TrendProperties properties)
	{
		trendProps = properties;
		
		if( getTrendSeries() != null)
		{
			boolean useMult = false;
			if(( trendProps.getOptionsMaskSettings() & GraphRenderers.GRAPH_MULTIPLIER_MASK) == GraphRenderers.GRAPH_MULTIPLIER_MASK)
				useMult = true;
			
			for (int i = 0; i < getTrendSeries().length; i++)
			{
				getTrendSeries()[i].setResolution(trendProps.getResolutionInMillis());
				getTrendSeries()[i].setUseMultiplier(useMult);
			}
		}
		
	}

	/**
	 * use the "view" types or the renderer from each serie in the GraphDefinition.  THis view is only for the graph as a whole, not the series.
	 * @return
	 */
	public int getViewType()
	{
		//Need to have a local variable so we can track when it changes, then perform some actions on the change.
		if( localViewType != getTrendProps().getViewType())
		{
			localViewType = getTrendProps().getViewType();
			if( localViewType == GraphRenderers.DEFAULT)
			{
				boolean value = false;
				for (int i = 0; i < getTrendSeries().length; i++)
				{
					if( GraphRenderers.useCategoryPlot(getTrendSeries()[i].getRenderer()))
						value = true;
				}
				setBarRenderer(value);
			}
			else
				setBarRenderer( GraphRenderers.useCategoryPlot(localViewType));
		}
		return localViewType;
	}

	/**
	 * GraphRenderers.TABULAR
	 */
	private void buildTabular()
	{	
		return;
	}
	/**
	 * GraphRenderers.SUMMARY
	 */
	private void buildSummary()
	{
		return;
	}
	/**
	 * @param series
	 */
	public void setTrendSeries(TrendSerie[] series) {
		trendSeries = series;
		
		for(int i = 0; i < getTrendSeries().length; i++)
		{
			if ( (getViewType()== GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(getTrendSeries()[i].getRenderer()) )
				|| GraphRenderers.useCategoryPlot(getViewType()) )
				setBarRenderer(true);
			if( (getViewType() == GraphRenderers.DEFAULT && GraphRenderers.isAreaGraph(getTrendSeries()[i].getRenderer()) )
				|| GraphRenderers.isAreaGraph(getViewType() ) )
				setAreaRenderer(true);
		}	

	}

	/**
	 * @return
	 */
	public boolean hasBarRenderer() {
		return hasBarRenderer;
	}

	/**
	 * @param b
	 */
	public void setBarRenderer(boolean b) {
		hasBarRenderer = b;
	}

	/**
	 * @return
	 */
	public boolean hasAreaRenderer() {
		return hasAreaRenderer;
	}

	/**
	 * @param b
	 */
	public void setAreaRenderer(boolean b) {
		hasAreaRenderer = b;
	}
	

	private String getSerieLabel(String label, Date dateToAppend)
	{
		String moreLabel = " "+ LEGEND_DATE_FORMAT.format(dateToAppend);			
		try
		{
			String tempLabel = label;
			if (tempLabel.length() > LEGEND_DATE_FORMAT.toPattern().length() )	//must be at least this long to even bother checking if its already there.
			{
				LEGEND_DATE_FORMAT.parse(tempLabel.substring(tempLabel.length() - LEGEND_DATE_FORMAT.toPattern().length()));
				//Remove any existing "date" text that could have been previously appended
				label = label.substring(0, tempLabel.length() - LEGEND_DATE_FORMAT.toPattern().length()).trim();
			}
		}
		catch( java.text.ParseException pe )
		{
		}
		finally{
			return label + moreLabel;
		}
	}

}
package com.cannontech.graph.model;

import java.awt.Color;
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
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.urls.TimeSeriesURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LitePoint;
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
	private java.text.SimpleDateFormat LEGEND_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMM dd, yyyy");
	private java.text.SimpleDateFormat TRANSLATE_DATE= new java.text.SimpleDateFormat("HHmmss");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
	
	private TrendProperties trendProps;
	
	// dataset is an array of size 2.
	// Left and Right (Primary and Secondary respectively) yAxis datasets.
	private AbstractDataset [] dataset = null;
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
		trendSeries = new TrendSerie[dsVector.size()];
		dsVector.toArray(trendSeries);
		hitDatabase_Basic(GDSTypes.BASIC_TYPE);
		hitDatabase_Basic(GDSTypes.YESTERDAY_TYPE);
		hitDatabase_Basic(GDSTypes.USAGE_TYPE);
		
		//Peak and (specific)Date types
		for (int i = 0; i < trendSeries.length; i++)
		{
			if (GDSTypesFuncs.isDateType(trendSeries[i].getTypeMask()) ||
				GDSTypesFuncs.isPeakType(trendSeries[i].getTypeMask()))
			{
				hitDatabase_Date(trendSeries[i].getTypeMask(), i);
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
	trendSeries = new TrendSerie[litePoints.length];
	for (int i = 0; i < litePoints.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId(new Integer(((LitePoint)litePoints[i]).getPointID()));
		tempSerie.setLabel(((LitePoint)litePoints[i]).getPointName());
		
		//Use valid graph Colors, reuses colors when all have been used
		tempSerie.setColor(colors.getNextLineColor());
		trendSeries[i] = tempSerie;
	}		
	hitDatabase_Basic(GDSTypes.BASIC_GRAPH_TYPE);
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
	trendSeries = new TrendSerie[ptID_.length];
	for (int i = 0; i < ptID_.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId( new Integer(ptID_[i]) );
		tempSerie.setTypeMask( GDSTypes.BASIC_GRAPH_TYPE );
		tempSerie.setLabel( ptNames_[i] );
		//Use valid graph Colors, reuses colors when all have been used
		tempSerie.setColor(colors.getNextLineColor());

		trendSeries[i] = tempSerie;
	}		
	hitDatabase_Basic(GDSTypes.BASIC_GRAPH_TYPE);
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

private void addRangeMarkers(Plot plot)
{
	XYItemRenderer rend = null;
	int rendCount = 0;	//A count for number of renderers actually created.
	for (int a = 0; a < 2; a++)
	{
		for( int i = 0; i < getTrendSeries().length; i++)
		{
			TrendSerie serie = getTrendSeries()[i];
			if( GDSTypesFuncs.isThresholdType(serie.getTypeMask()))
			{
				if( serie.getAxis().equals(axisChars[a]))
				{
					Marker threshold = new ValueMarker(serie.getMultiplier().doubleValue());//, serie.getColor());
					threshold.setPaint(serie.getColor());					
					threshold.setLabelPaint(serie.getColor());
					threshold.setLabel(serie.getMultiplier().toString());

					if( plot instanceof XYPlot)
					{
						((XYPlot)plot).getRangeAxis(a).setVisible(true);
						((XYPlot)plot).addRangeMarker(a, threshold, Layer.BACKGROUND);
					}
					else if( plot instanceof CategoryPlot)
					{
						((CategoryPlot)plot).getRangeAxis(a).setVisible(true);
						((CategoryPlot)plot).addRangeMarker(a, threshold, Layer.BACKGROUND);
					}							
					if( a == PRIMARY_AXIS)
					{
						threshold.setLabelAnchor(RectangleAnchor.TOP_LEFT);
						threshold.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
					}
					else
					{
						threshold.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
						threshold.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
					}
						
					rendCount++;
				}
			}
		}
	}
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
	if( getViewType() == GraphRenderers.BAR || getViewType() == GraphRenderers.BAR_3D)
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
//			SegmentedTimeline timeline = new SegmentedTimeline(SegmentedTimeline.HOUR_SEGMENT_SIZE, 1, 3);
////			timeline.setAdjustForDaylightSaving(true);
//			timeline.setStartTime(getStartDate().getTime());
			DateAxis domainAxis = new DateAxis(getTrendProps().getDomainLabel());//, timeline);
			domainAxis.setAutoRange(false);
//			domainAxis.setDateFormatOverride(dwellValuesDateTimeformat);
			domainAxis.setMaximumDate(getStopDate());
			domainAxis.setMinimumDate(getStartDate());
			domainAxis.setTickMarksVisible(true);
//			domainAxis.setLabelAngle(Math.PI/2);
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
	
	/*DEPRECATED CODE
	java.util.Vector validIDs = new java.util.Vector(trendSeries.length);	//guess on max capacity
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
			validIDs.add(trendSeries[i].getPointId());
	}

	if( validIDs.isEmpty())
		return null;
	*/		
	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE, MILLIS "
	+ "FROM RAWPOINTHISTORY WHERE POINTID IN (");
	sql.append( pointid);

	/* DEPRECATED CODE
	sql.append( (Integer)validIDs.get(0));
	for ( int i = 1; i <validIDs.size(); i ++)
	{
		sql.append(", " + ( (Integer)validIDs.get(i)));
	}
	*/
	sql.append(")  AND ((TIMESTAMP "+beginTSCompare+" ? AND TIMESTAMP <= ? ) ");
	sql.append(" ) ORDER BY POINTID, TIMESTAMP");
	return sql;	
}

private Integer getPrimaryGDSPointId()
{
	if( primaryGDSPointID == null)
	{
		for (int i = 0; i < trendSeries.length; i++)
		{
			if( GDSTypesFuncs.isPrimaryType(trendSeries[i].getTypeMask()))
				primaryGDSPointID = trendSeries[i].getPointId();
		}
	}
	return primaryGDSPointID;
}			
private YukonStandardLegend getLegend()
{
	//Legend setup
    if( legend == null)
    {
        legend = new YukonStandardLegend();
    }
/*
	java.util.Vector stats = null;

	if( (getOptionsMaskSettings() & GraphRenderers.LEGEND_LOAD_FACTOR_MASK) == GraphRenderers.LEGEND_LOAD_FACTOR_MASK ||
		(getOptionsMaskSettings() & GraphRenderers.LEGEND_MIN_MAX_MASK ) == GraphRenderers.LEGEND_MIN_MAX_MASK)
	{
		stats = new java.util.Vector(getDatasetSeriesCount());
		for( int i = 0; i < trendSeries.length; i++)
		{
			TrendSerie serie = trendSeries[i];
			String stat = "";					
			if(GraphDataSeries.isGraphType(serie.getTypeMask()))
			{
				if ((getOptionsMaskSettings() & GraphRenderers.LEGEND_LOAD_FACTOR_MASK) == GraphRenderers.LEGEND_LOAD_FACTOR_MASK)
				{
					double lf = serie.getLoadFactor();
					if( lf < 0)
						stat += "Load Factor: n/a";
					else
						stat += "Load Factor: " + LF_FORMAT.format(lf);
				}

				if( (getOptionsMaskSettings() & GraphRenderers.LEGEND_MIN_MAX_MASK) == GraphRenderers.LEGEND_MIN_MAX_MASK)
				{
					if( serie.getAxis().equals(new Character('L')))
					{					
						if( serie.getMinimumValue() == null ||	serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
							stat += "    Min:  n/a";
						else
							stat += "    Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());
						if( serie.getMaximumValue() == null ||	serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
							stat += "    Max:  n/a";
						else
							stat += "    Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
					}
					else if( serie.getAxis().equals(new Character('R')))
					{					
						if( serie.getMinimumValue() == null || serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
							stat += "    Min:  n/a";
						else
							stat += "    Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());
						if( serie.getMaximumValue() == null || serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
							stat += "    Max:  n/a";
						else
							stat += "    Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
					}
				}
				stats.add(stat);
			}
		}
	}

	if( stats != null)
	{
		String []statsString = new String[stats.size()];
		for ( int i = 0; i < stats.size(); i++)
		{
			statsString[i] = (String)stats.get(i);
		}
		legend.setOtherInfo(statsString);
	}	
	*/
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

private NumberAxis getRangeAxis(int axisIndex)	//LEFT
{
	NumberAxis rangeAxis = null;
	if( getViewType() == GraphRenderers.BAR_3D)
		rangeAxis = new NumberAxis3D(getTrendProps().getRangeLabel(axisIndex));
	else
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
 * Returns the trendSeries value.
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * @param int seriesTypeMask
 * @return com.cannontech.graph.model.TrendSerie[]
 */
private void hitDatabase_Basic(int seriesTypeMask) 
{
	java.util.Date timerStart = new java.util.Date();
	GregorianCalendar tempCal = null;

	if( trendSeries == null)
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
		for (int s = 0; s < trendSeries.length; s++)
		{
			if( (trendSeries[s].getTypeMask() & seriesTypeMask) == seriesTypeMask)
			{
				StringBuffer sql = getSQLQueryString(seriesTypeMask, trendSeries[s].getPointId().intValue());
				pstmt = conn.prepareStatement(sql.toString());
				
				int day = 0;			
				long startTS = 0;
				long stopTS = 0;
	
				if (GDSTypesFuncs.isYesterdayType(seriesTypeMask))
				{
					day = -1;
					tempCal = new GregorianCalendar();
					tempCal.setTime((Date)getStartDate().clone());
					tempCal.add(Calendar.DATE, day);
					startTS = tempCal.getTimeInMillis();
		
					tempCal.setTime((Date)getStopDate().clone());
					tempCal.add(Calendar.DATE, day);
					stopTS = tempCal.getTimeInMillis();
					for (int i = 0; i < trendSeries.length; i++)
					{
						if (GDSTypesFuncs.isYesterdayType(trendSeries[i].getTypeMask()))
						{
							trendSeries[i].setLabel(trendSeries[i].getLabel() + " ["+ LEGEND_DATE_FORMAT.format(new java.util.Date(startTS))+"]");
						}
					}
					CTILogger.info("START DATE > " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
				}
				else if (GDSTypesFuncs.isUsageType(seriesTypeMask))
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
	
				pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( stopTS ));
	
				rset = pstmt.executeQuery();
				
				TimeSeriesDataItem dataItem = null;
				//contains org.jfree.data.time.TimeSeriesDataItem values.
				TreeMap dataItemsMap = new TreeMap();			
				int lastPointId = -1;
	
				boolean addNext = true;
				boolean firstOne = true;
				
				java.util.Date start = getStartDate();
				
				tempCal = new GregorianCalendar();
				tempCal.setTime((Date)getStartDate().clone());
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
							for (int i = 0; i < getTrendSeries().length; i++)
							{
								if( trendSeries[i].getPointId().intValue() == lastPointId 
									&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
									trendSeries[i].setDataItemsMap((TreeMap)dataItemsMap.clone());
							}
							dataItemsMap.clear();
						}
						lastPointId = pointID;
						
						if( GDSTypesFuncs.isUsageType(seriesTypeMask))
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
					
					//new pointid in rset.
					//init everything, a new freechartmodel will be created with the change of pointid.
					java.sql.Timestamp ts = rset.getTimestamp(2);
					double val = rset.getDouble(3);
					short millis = rset.getShort(4);
					
					tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(ts.getTime());
					tempCal.set(Calendar.MILLISECOND, millis);
					tempCal.add(Calendar.DATE, Math.abs(day));	//map timestamps to current start/stop range.
					RegularTimePeriod tp = new Millisecond((Date)tempCal.getTime().clone());
					Long timeKey = new Long(tp.getStart().getTime());
									
					if( GDSTypesFuncs.isUsageType(seriesTypeMask))
					{
						java.util.Date tsDate = new java.util.Date(ts.getTime());
	
						if(tsDate.compareTo(start) < 0)
						{	
							dataItem = new TimeSeriesDataItem(tp, val);
						}
						else if(tsDate.compareTo(start) == 0)
						{	
							dataItem = new TimeSeriesDataItem(tp, val);
							dataItemsMap.put( timeKey, dataItem);
							firstOne = false;
						}
						else if( tsDate.compareTo(stop) >= 0)
						{
							if(firstOne)
							{
								if(dataItem == null)
								{
									dataItem = new TimeSeriesDataItem(tp, val);						
								}
								dataItemsMap.put(timeKey, dataItem);
								firstOne = false;
							}
							if( addNext )
							{
								dataItem = new TimeSeriesDataItem(tp, val);
								dataItemsMap.put(timeKey, dataItem);
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
								if(dataItem == null)
								{
									dataItem = new TimeSeriesDataItem(tp, val);						
								}
								dataItemsMap.put(timeKey, dataItem);
								firstOne = false;
							}
						}
					}
					else
					{
						dataItem = new TimeSeriesDataItem(tp, val);
						dataItemsMap.put(timeKey, dataItem);
					}
				}	//END WHILE
				
				if( !dataItemsMap.isEmpty())
				{
					for (int i = 0; i < getTrendSeries().length; i++)
					{
						if( trendSeries[i].getPointId().intValue() == lastPointId
							&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
							trendSeries[i].setDataItemsMap((TreeMap)dataItemsMap.clone());
	
					}
					dataItemsMap.clear();
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
		CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for Trend Data Load ( for type:" + GDSTypesFuncs.getType(seriesTypeMask) + " )");
	}
}
private void hitDatabase_Date(int seriesTypeMask, int serieIndex) 
{
	java.util.Date timerStart = new java.util.Date();
	GregorianCalendar tempCal = new GregorianCalendar();

	if( trendSeries[serieIndex]== null)
		return;

	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE, MILLIS " + 
	" FROM RAWPOINTHISTORY WHERE POINTID = " + trendSeries[serieIndex].getPointId().intValue() +
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
				if (GDSTypesFuncs.isPeakType(trendSeries[serieIndex].getTypeMask()))
				{ 
					day = retrievePeakIntervalTranslateDays(serieIndex);
					if ( day == -1 )
						return;
				}
			}
			else if (GDSTypesFuncs.isDateType(seriesTypeMask))
			{
				if (GDSTypesFuncs.isDateType(trendSeries[serieIndex].getTypeMask()))
				{
					Date tempDate = new Date(Long.valueOf(trendSeries[serieIndex].getMoreData()).longValue());
					day = TimeUtil.differenceInDays(getStartDate(), tempDate);
				}
			}

			if ( day == -1 )
						return;
						
			tempCal.setTime(getStartDate());
			tempCal.add(Calendar.DATE, - day);
			startTS = tempCal.getTimeInMillis();
			
			tempCal.setTime(getStartDate());
			tempCal.add(Calendar.DATE, -day+1);
			stopTS = tempCal.getTimeInMillis();
			trendSeries[serieIndex].setLabel(trendSeries[serieIndex].getLabel() + " ["+ LEGEND_DATE_FORMAT.format(new java.util.Date(startTS))+"]");
			
			CTILogger.info("START DATE > " + new Timestamp(startTS) + "  -  STOP DATE <= " + new Timestamp(stopTS));
			pstmt.setTimestamp(1, new Timestamp( startTS ));
			pstmt.setTimestamp(2, new Timestamp( stopTS ));

			rset = pstmt.executeQuery();
			 
			TimeSeriesDataItem dataItem = null;
			//contains org.jfree.data.time.TimeSeriesDataItem values.
			TreeMap dataItemsMap = new TreeMap();			
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
						if( trendSeries[serieIndex].getPointId().intValue() == lastPointId 
								&& (trendSeries[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
								trendSeries[serieIndex].setDataItemsMap((TreeMap)dataItemsMap.clone());
							
						dataItemsMap.clear();				
					}
					lastPointId = pointID;
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				double val = rset.getDouble(3);
				short millis = rset.getShort(4);
				
				tempCal = new GregorianCalendar();
				tempCal.setTimeInMillis(ts.getTime());
				tempCal.set(Calendar.MILLISECOND, millis);
				tempCal.add(Calendar.DATE, Math.abs(day));	//map timestamps to current start/stop range.
				RegularTimePeriod tp = new Millisecond((Date)tempCal.getTime().clone());
				Long timeKey = new Long(tp.getStart().getTime());
			
				dataItem = new TimeSeriesDataItem(tp, val);
				dataItemsMap.put(timeKey, dataItem);
							
			}	//END WHILE
			
			
			if( !dataItemsMap.isEmpty())
			{
				// Repeat the interval x # of days with Peak_interval data series.
				if (GDSTypesFuncs.isPeakType(seriesTypeMask ) || GDSTypesFuncs.isDateType(seriesTypeMask))
				{
					Object[] repeatables = dataItemsMap.entrySet().toArray();
					
					long numDays = (getStopDate().getTime() - getStartDate().getTime()) / 86400000;
					for ( long i = 1; i < numDays; i++)
					{
						for (int j = 0; j < repeatables.length; j++)
						{
							TimeSeriesDataItem repeatItem = (TimeSeriesDataItem)((java.util.Map.Entry)repeatables[j]).getValue();
							double v = repeatItem.getValue().doubleValue();
							tempCal.setTime((Date)repeatItem.getPeriod().getStart().clone());
							tempCal.add(Calendar.DATE, (int)i);
							Long timeKey = new Long(tempCal.getTimeInMillis());
							RegularTimePeriod tp = new Millisecond(new java.util.Date(timeKey.longValue()));
							
							dataItem = new TimeSeriesDataItem(tp,v);
							dataItemsMap.put(timeKey, dataItem);							
						}					
					}
				}
				
				if( trendSeries[serieIndex].getPointId().intValue() == lastPointId
					&& (trendSeries[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
				{
					trendSeries[serieIndex].setDataItemsMap((TreeMap)dataItemsMap.clone());
				}
				dataItemsMap.clear();
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
			pstmt.setTimestamp(1, new Timestamp( Long.valueOf(trendSeries[serieIndex].getMoreData()).longValue()));
			//pstmt.setTimestamp(2, new Timestamp( Long.valueOf(trendSeries[serieIndex].getMoreData()).longValue()));
			CTILogger.info("PEAK START DATE > " + new Timestamp(Long.valueOf(trendSeries[serieIndex].getMoreData()).longValue()));
		
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
public Dataset getDataset(int index)
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
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public JFreeChart refresh()
{
	//Need to check that at least one of the trendSeries is GraphType, otherwise return null
	for(int i = 0; i < getTrendSeries().length; i++)
	{
		TrendSerie serie = getTrendSeries()[i];
		if( serie != null && (GDSTypesFuncs.isGraphType(serie.getTypeMask())))
			break;
		if(i == getTrendSeries().length - 1)	//last iteration and we are still here
			return null;
	} 
		
	//Plot setup
	Plot plot = null;
	
	dataset = YukonDataSetFactory.createDataset( getTrendSeries(), getOptionsMaskSettings(), getViewType());
	int datasetCount = 0;
	if( getViewType() == GraphRenderers.LINE|| getViewType() == GraphRenderers.LINE_SHAPES || getViewType()== GraphRenderers.LINE_AREA || getViewType()== GraphRenderers.LINE_AREA_SHAPES| 
		getViewType() == GraphRenderers.STEP || getViewType()== GraphRenderers.STEP_SHAPES || getViewType()== GraphRenderers.STEP_AREA || getViewType()== GraphRenderers.STEP_AREA_SHAPES)
	{
//		  com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm mavg = new com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm();
//		  mavg.setPeriod(30);
//		  com.jrefinery.chart.data.PlotFit pf = new com.jrefinery.chart.data.PlotFit((com.jrefinery.data.XYDataset)dataset, mavg);
//		  dataset = (com.jrefinery.data.AbstractSeriesDataset)pf.getFit();

		XYItemRenderer rend = null;
		plot = new XYPlot(null, (ValueAxis)getDomainAxis(), null, null);

		//Weaken the alpha if this is an area type renderer, so we can see "through" it
		if ( GraphRenderers.isAreaGraph(getViewType()))
			plot.setForegroundAlpha(0.75f);
			
		///Currently we create (at most 2 renderers), one for each axis (L or R) 
		for (int d = 0; d < 2; d++)
		{
		    if( getDataset(d) != null)
		    {
				//Add the range axis, even if we aren't necessarily using it, this helps when adding Markers (thresholds)
				((XYPlot)plot).setRangeAxis(datasetCount, getRangeAxis(d));
				if( d == PRIMARY_AXIS)
					((XYPlot)plot).setRangeAxisLocation( datasetCount, AxisLocation.BOTTOM_OR_LEFT);
				else if (d == SECONDARY_AXIS)
					((XYPlot)plot).setRangeAxisLocation( datasetCount, AxisLocation.TOP_OR_RIGHT);
				
				rend = (XYItemRenderer)getRenderer(d);
				if( rend != null)
				{
					//TODO find out what urlGenerator does again.
					TimeSeriesURLGenerator urlg = new TimeSeriesURLGenerator(dateFormat, "user_trending.jsp", "gdefid", "startdate");
					rend.setURLGenerator(urlg);
					rend.setBaseLabelGenerator(new StandardXYLabelGenerator("", extendedTimeFormat, valueFormat));
					rend.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
					
					for(int i = 0, index = 0; i < getTrendSeries().length; i++)
					{
						TrendSerie serie = getTrendSeries()[i];
						if( serie.getAxis().equals(axisChars[d]))
						{					
							if( serie != null && (GDSTypesFuncs.isGraphType(serie.getTypeMask())))
							{
									rend.setSeriesPaint(index++, serie.getColor());
							}
						}
					}
					((XYPlot)plot).setRenderer(datasetCount, rend);
					
					if( getDataset(d) != null)
					{
						((XYPlot)plot).setDataset(datasetCount, (XYDataset)getDataset(d));
						((XYPlot)plot).mapDatasetToRangeAxis(datasetCount, datasetCount);
					}
					else
					{
						((XYPlot)plot).getRangeAxis(datasetCount).setVisible(false);
					}
					datasetCount++;
				}
		    }
		}
	}
	else if( getViewType()  == GraphRenderers.BAR || getViewType() == GraphRenderers.BAR_3D)
	{
		CategoryItemRenderer rend = null;
		plot = new CategoryPlot();//( null, (HorizontalSkipLabelsCategoryAxis)getDomainAxis(), null, null);
		((CategoryPlot)plot).setDomainAxis((HorizontalSkipLabelsCategoryAxis)getDomainAxis());
					
		///Currently we create (at most 2 renderers), one for each axis (L or R) 
		for (int a = 0; a < 2; a++)
		{
			//Add the range axis, even if we aren't necessarily using it, this helps when adding Markers (thresholds)
			((CategoryPlot)plot).setRangeAxis(a, getRangeAxis(a));
			if( a == PRIMARY_AXIS)
				((CategoryPlot)plot).setRangeAxisLocation( a, AxisLocation.BOTTOM_OR_LEFT);
			else if (a == SECONDARY_AXIS)
				((CategoryPlot)plot).setRangeAxisLocation( a, AxisLocation.TOP_OR_RIGHT);
			
			rend = (CategoryItemRenderer)getRenderer(a);
			if( rend != null)
			{
				//TODO find out what urlGenerator does again.
				rend.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
				rend.setBaseLabelGenerator(new StandardCategoryLabelGenerator());
				rend.setItemURLGenerator(new StandardCategoryURLGenerator());
				
				for(int i = 0, index = 0; i < getTrendSeries().length; i++)
				{
					TrendSerie serie = getTrendSeries()[i];
					if( serie.getAxis().equals(axisChars[a]))
					{					
						if( serie != null && (GDSTypesFuncs.isGraphType(serie.getTypeMask())))
						{
								rend.setSeriesPaint(index++, serie.getColor());
						}
					}
				}
				((CategoryPlot)plot).setRenderer(a, rend);
				
				if( getDataset(a) != null)
				{
					((CategoryPlot)plot).setDataset(a, (DefaultCategoryDataset)getDataset(a));
					((CategoryPlot)plot).mapDatasetToRangeAxis(a, a);
				}
				else
				{
					((CategoryPlot)plot).getRangeAxis(a).setVisible(false);
				}
			}
		}
	}	

	else if( getViewType() == GraphRenderers.TABULAR)
	{
		return null;
	}
	else if( getViewType()== GraphRenderers.SUMMARY)
	{
		return null;
	}

	addRangeMarkers(plot);

	JFreeChart fChart = null;
	fChart = new JFreeChart(plot);
	fChart.setLegend( getLegend() );
	fChart.setTitle(getTitle());
	fChart.setSubtitles(getSubtitles());
	fChart.setBackgroundPaint(java.awt.Color.white);    
	return fChart;
 }

	/**
	 * Returns the renderer for the graph based on the GraphRenderers type value
	 * @return XYItemRenderer
	 */ 

	private AbstractRenderer getRenderer(int axisIndex)
	{
		AbstractRenderer rend = null;
		switch (getViewType())
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
		
		if( trendSeries != null)
		{
			boolean useMult = false;
			if(( trendProps.getOptionsMaskSettings() & GraphRenderers.GRAPH_MULTIPLIER_MASK) == GraphRenderers.GRAPH_MULTIPLIER_MASK)
				useMult = true;
			
			for (int i = 0; i < trendSeries.length; i++)
			{
				trendSeries[i].setResolution(trendProps.getResolutionInMillis());
				trendSeries[i].setUseMultiplier(useMult);
			}
		}
		
	}

	/**
	 * @return
	 */
	public int getViewType()
	{
		return getTrendProps().getViewType();
	}


/*public JFreeChart refresh()
{
	//Plot setup
	Plot plot = null;
	Vector categories = new Vector();
	for (int i = 0; i < getTrendSeries().length; i++)
	{
		if( getTrendSeries()[i].getDataItemsMap() != null)
		{
			Object [] dataItemsArray = new Object[getTrendSeries()[i].getDataItemsMap().values().size()];
			getTrendSeries()[i].getDataItemsMap().values().toArray(dataItemsArray);
			Arrays.sort(dataItemsArray, timeSeriesDataItemPeriodComparator);
		
			for (int j = 0; j < dataItemsArray.length; j++)
			{
				Long time = new Long(((TimePeriodValue)dataItemsArray[j]).getPeriod().getStart().getTime());
				if( !categories.contains(time))
					categories.add(time);
			}
		}
	}
	
//	dataset = YukonDataSetFactory.createDataset( getTrendSeries(), getOptionsMaskSettings(), getViewType());
	for (int i = 0; i < getTrendSeries().length; i++)
	{
//		Object [] dataItemsArray = new Object[getTrendSeries()[i].getDataItemsMap().values().size()];
//		getTrendSeries()[i].getDataItemsMap().values().toArray(dataItemsArray);
//		Arrays.sort(dataItemsArray, timeSeriesDataItemPeriodComparator);
//		Vector categories = new Vector(dataItemsArray.length);
//		double categoryCount = dataItemsArray.length -1;// scale is from 0 start point not 1
//		for (int j = 0; j < dataItemsArray.length; j++)
//			categories.add(new Long(((TimePeriodValue)dataItemsArray[j]).getPeriod().getStart().getTime()));

//		YukonDataSetFactory.buildXYSeries_LD(getTrendSeries()[i], categories);
//		YukonDataSetFactory.buildXYSeries(getTrendSeries()[i], categories);
		YukonDataSetFactory.buildCategoryDataSet(getTrendSeries()[i], categories);
//		YukonDataSetFactory.buildTimePeriods(getTrendSeries()[i],categories);
//		YukonDataSetFactory.buildTimeSeries(getTrendSeries()[i]);
	}
		
	if( getViewType() == GraphRenderers.LINE|| getViewType() == GraphRenderers.SHAPES_LINE)
	{
		TimeSeriesToolTipGenerator generator = new TimeSeriesToolTipGenerator(extendedTimeFormat, valueFormat);

		XYItemRenderer rend = null;

		// Need to convert yukon GraphRenderers into StandardXYItemRenderer type
		if (getViewType() == GraphRenderers.BAR)
		{
			rend = new ClusteredXYBarRenderer();
		}
		else if( getViewType() == GraphRenderers.LINE)
		{
			if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
			{
				rend = new StandardXYItemRenderer_MinMax(StandardXYItemRenderer.LINES, generator);
				((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
			}
			else
				rend = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, generator);
		}
		else if( getViewType() == GraphRenderers.SHAPES_LINE)
		{
			if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
			{
				rend = new StandardXYItemRenderer_MinMax(StandardXYItemRenderer.SHAPES_AND_LINES, generator);
				((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
			}
			else
				rend = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES, generator);
		}
		rend = new ClusteredXYBarRenderer();
//		  TimeSeriesCollection
//		  com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm mavg = new com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm();
//		  mavg.setPeriod(30);
//		  com.jrefinery.chart.data.PlotFit pf = new com.jrefinery.chart.data.PlotFit((com.jrefinery.data.XYDataset)dataset, mavg);
//		  dataset = (com.jrefinery.data.AbstractSeriesDataset)pf.getFit();
		TimeSeriesURLGenerator urlg = new TimeSeriesURLGenerator
			(dateFormat, "user_trending.jsp", "gdefid", "startdate");
		rend.setURLGenerator(urlg);
		for(int i = 0, index = 0; i < getTrendSeries().length; i++)
		{
			if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
				if( getTrendSeries()[i].getAxis().equals(axisChars[PRIMARY_AXIS]))
					rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
		}
		plot = new XYPlot( (XYDataset)getDataset(PRIMARY_AXIS), (ValueAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		if(((XYDataset)getDataset(SECONDARY_AXIS)) != null)
		{
			if (getViewType() == GraphRenderers.BAR)
			{
				rend = new ClusteredXYBarRenderer();
			}
			else if( getViewType() == GraphRenderers.LINE)
			{
				if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
				{
					rend = new StandardXYItemRenderer_MinMax(StandardXYItemRenderer.LINES, generator);
					((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
				}
				else
					rend = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, generator);
			}
			else if( getViewType() == GraphRenderers.SHAPES_LINE)
			{
				if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
				{
					rend = new StandardXYItemRenderer_MinMax(StandardXYItemRenderer.SHAPES_AND_LINES, generator);
					((StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
				}
				else
					rend = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES, generator);
			}
			for(int i = 0, index = 0; i < getTrendSeries().length; i++)
			{
				if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
					if( getTrendSeries()[i].getAxis().equals(axisChars[SECONDARY_AXIS]))
						rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
			}
			((XYPlot)plot).setSecondaryRenderer(0, rend);
			((XYPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((XYPlot)plot).setSecondaryDataset(0, (XYDataset)getDataset(SECONDARY_AXIS));
			((XYPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
//		((XYPlot)plot).setSecondaryRenderer(0, new XYStepRenderer());
//		((XYPlot)plot).setSecondaryDataset(0, (XYDataset)getPrimaryDataset());
//		((XYPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
	}
	else if( getViewType()  == GraphRenderers.STEP)
	{
		XYItemRenderer rend = null;
		if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
			((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
		}
		else
		{
			rend = new XYStepRenderer();
		}
		TimeSeriesToolTipGenerator generator =
			 new TimeSeriesToolTipGenerator(extendedTimeFormat, valueFormat);

		rend.setToolTipGenerator(generator);
		
		for(int i = 0, index = 0; i < getTrendSeries().length; i++)
		{
			if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
				if( getTrendSeries()[i].getAxis().equals(axisChars[PRIMARY_AXIS]))
					rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
		}
		plot = new XYPlot( (XYDataset)getDataset(PRIMARY_AXIS), (ValueAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);
		((XYPlot)plot).setRangeAxisLocation( org.jfree.chart.axis.AxisLocation.BOTTOM_OR_LEFT);
		
		//Attempt to do multiple axis
		if(((XYDataset)getDataset(SECONDARY_AXIS)) != null)
		{
			if( (getOptionsMaskSettings()  & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
			{
				rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
				((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(SECONDARY_AXIS);
			}
			else
			{
				rend = new XYStepRenderer();
			}
			for(int i = 0, index = 0; i < getTrendSeries().length; i++)
			{
				if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
					if( getTrendSeries()[i].getAxis().equals(axisChars[SECONDARY_AXIS]))
						rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
			}
			((XYPlot)plot).setSecondaryRenderer(0, rend);
			((XYPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((XYPlot)plot).setSecondaryDataset(0, (XYDataset)getDataset(SECONDARY_AXIS));
			((XYPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
		
	}
	else if(getViewType() == GraphRenderers.BAR)
	{
		CategoryItemRenderer rend = new BarRenderer();		
		rend.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		rend.setItemURLGenerator(new org.jfree.chart.urls.StandardCategoryURLGenerator());
		
		for(int i = 0, index = 0; i < getTrendSeries().length; i++)
		{
			if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
				if( getTrendSeries()[i].getAxis().equals(axisChars[PRIMARY_AXIS]))
					rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
		}
//		rend.setSeriesPaint(i, serie.getColor());
		plot = new CategoryPlot( (DefaultCategoryDataset)getDataset(PRIMARY_AXIS), (HorizontalSkipLabelsCategoryAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)
		((CategoryPlot)plot).setRangeAxisLocation( org.jfree.chart.axis.AxisLocation.BOTTOM_OR_LEFT);
		if(((XYDataset)getDataset(SECONDARY_AXIS)) != null)
		{
//			rend = new LineAndShapeRenderer(LineAndShapeRenderer.LINES);
			rend = new BarRenderer();
			rend.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			rend.setItemURLGenerator(new org.jfree.chart.urls.StandardCategoryURLGenerator());
		
			for(int i = 0, index = 0; i < getTrendSeries().length; i++)
			{
				if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
					if( getTrendSeries()[i].getAxis().equals(axisChars[SECONDARY_AXIS]))
						rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
			}
//			rend.setSeriesPaint(i-1, serie.getColor());			

			((CategoryPlot)plot).setSecondaryRenderer(0, rend);
			((CategoryPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((CategoryPlot)plot).setSecondaryDataset(0, (DefaultCategoryDataset)getDataset(SECONDARY_AXIS));
			((CategoryPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
	}
	else if( getViewType() == GraphRenderers.BAR_3D)
	{
		CategoryItemRenderer rend = new BarRenderer3D(10, 10);
		
		for(int i = 0, index = 0; i < getTrendSeries().length; i++)
		{
			if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
				if( getTrendSeries()[i].getAxis().equals(axisChars[PRIMARY_AXIS]))
					rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
		}
		plot = new CategoryPlot( (DefaultCategoryDataset)getDataset(PRIMARY_AXIS), (HorizontalSkipLabelsCategoryAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)
		if(((XYDataset)getDataset(SECONDARY_AXIS)) != null)
		{
			rend = new LineAndShapeRenderer(LineAndShapeRenderer.LINES);
//rend = new BarRenderer3D(10,10);
			for(int i = 0, index = 0; i < getTrendSeries().length; i++)
			{
				if( getTrendSeries()[i] != null && (GDSTypesFuncs.isGraphType(getTrendSeries()[i].getTypeMask())))
					if( getTrendSeries()[i].getAxis().equals(axisChars[SECONDARY_AXIS]))
						rend.setSeriesPaint(index++, getTrendSeries()[i].getColor());
			}

			((CategoryPlot)plot).setSecondaryRenderer(0, rend);
			((CategoryPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((CategoryPlot)plot).setSecondaryDataset(0, (DefaultCategoryDataset)getDataset(SECONDARY_AXIS));
			((CategoryPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
	}
	else if( getViewType() == GraphRenderers.TABULAR)
	{
		return null;
	}
	else if( getViewType()== GraphRenderers.SUMMARY)
	{
		return null;
	}

	addRangeMarkers(plot);
	JFreeChart fChart = null;
	fChart = new JFreeChart(plot);
	fChart.setLegend( getLegend(fChart) );
	fChart.setTitle(getTitle());
	fChart.setSubtitles(getSubtitles());
	fChart.setBackgroundPaint(java.awt.Color.white);    
	return fChart;
 }*/

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
}
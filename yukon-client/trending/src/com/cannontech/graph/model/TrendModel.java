package com.cannontech.graph.model;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.TextTitle;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.TimeSeriesToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.BarRenderer;
import org.jfree.chart.renderer.BarRenderer3D;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.LineAndShapeRenderer;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.chart.urls.TimeSeriesURLGenerator;
import org.jfree.data.AbstractDataset;
import org.jfree.data.AbstractSeriesDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.Point;
import com.cannontech.jfreechart.chart.YukonStandardLegend;

public class TrendModel implements com.cannontech.graph.GraphDefines 
{
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
    
    private String chartName = "Yukon Trending";
    
	//Used for load duration to determine which point to reference all others by.  null is valid.
    private Integer primaryGDSPointID = null;

	// Multiple axis setup
    private Character autoScaleRight = new Character('Y');
    private Double rightScaleMin = new Double(0.0);
    private Double rightScaleMax = new Double(100.0);
    private Character autoScaleLeft = new Character('Y');
    private Double leftScaleMin = new Double(0.0);
    private Double leftScaleMax = new Double(100.0);
    
//	private final int PRIMARY = 0;		//LEFT yAxis
//	private final int SECONDARY = 1;	//RIGHT yAxis

/**
 * Constructor for TrendModel.
 * The startDate and stopDate values will default to the start/stop dates of newGraphDef.
 * setStartDate(...) and setStopDate(...) must be explicitely called otherwise.
 * @param graphDefinition the graphDefinition to graph
 * @param TrendProperties the properties to include
 */
public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, TrendProperties props)
{
	this(newGraphDef, newGraphDef.getGraphDefinition().getStartDate(), newGraphDef.getGraphDefinition().getStopDate(), props);
}

/**
 * Constructor for TrendModel.
 * @param graphDefinition the graphDefinition to graph
 * @param startDate the date to start the trend (exclusive)
 * @param stopDate the date to stop the trend (inclusive)
 * @param TrendProperties the properties to include
 */
public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, java.util.Date newStartDate, java.util.Date newStopDate, TrendProperties props)
{
	// Inititialize chart properties
	setStartDate(newStartDate);
	setStopDate(newStopDate);
	setChartName(newGraphDef.getGraphDefinition().getName());
	
	setAutoScaleLeft(newGraphDef.getGraphDefinition().getAutoScaleLeftAxis());
	setAutoScaleRight(newGraphDef.getGraphDefinition().getAutoScaleRightAxis());

	setRightScaleMax(newGraphDef.getGraphDefinition().getRightMax());
	setRightScaleMin(newGraphDef.getGraphDefinition().getRightMin());
	
	setLeftScaleMax(newGraphDef.getGraphDefinition().getLeftMax());
	setLeftScaleMin(newGraphDef.getGraphDefinition().getLeftMin());


	// Inititialize series properties
	java.util.Vector dsVector = new java.util.Vector(5);	//some small initial capactiy
	for (int i = 0; i < (newGraphDef.getGraphDataSeries().size()); i++)
	{
		GraphDataSeries gds = (GraphDataSeries)newGraphDef.getGraphDataSeries().get(i);
		TrendSerie serie = new TrendSerie();
		serie.setPointId(gds.getPointID());
		serie.setColor( com.cannontech.common.gui.util.Colors.getColor(gds.getColor().intValue()));
		serie.setLabel(gds.getLabel().toString());
		serie.setAxis(gds.getAxis());
		serie.setTypeMask(gds.getType().intValue());
		serie.setMultiplier(gds.getMultiplier());
		serie.setMoreData(gds.getMoreData());
		dsVector.add(serie);
	}

	if( !dsVector.isEmpty())
	{
		trendSeries = new TrendSerie[dsVector.size()];
		dsVector.toArray(trendSeries);
		hitDatabase_Basic(GraphDataSeries.BASIC_TYPE);
		hitDatabase_Basic(GraphDataSeries.YESTERDAY_TYPE);
		hitDatabase_Basic(GraphDataSeries.USAGE_TYPE);
		
		//Peak and (specific)Date types
		for (int i = 0; i < trendSeries.length; i++)
		{
			if (GraphDataSeries.isDateType(trendSeries[i].getTypeMask()) ||
				GraphDataSeries.isPeakType(trendSeries[i].getTypeMask()))
			{
				hitDatabase_Date(trendSeries[i].getTypeMask(), i);
			}
		}
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info(" GraphDefinition contains NO graphDataSeries Items");
	}
	setTrendProps(props);
	
}

public TrendModel(java.util.Date newStartDate, java.util.Date newStopDate, String newChartName, Point [] newPoints)
{
	// Inititialize chart properties
	setStartDate(newStartDate);
	setStopDate(newStopDate);
	setChartName(newChartName);

	// Inititialize series properties	
	trendSeries = new TrendSerie[newPoints.length];
	for (int i = 0; i < newPoints.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId(((Point)newPoints[i]).getPointID());
		tempSerie.setLabel(((Point)newPoints[i]).getPointName());
		tempSerie.setColor(com.cannontech.common.gui.util.Colors.getColor(i));	//some distinct color (valid 1-10 only)

		trendSeries[i] = tempSerie;
	}		
	hitDatabase_Basic(GraphDataSeries.BASIC_GRAPH_TYPE);
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

	// Inititialize series properties	
	trendSeries = new TrendSerie[ptID_.length];
	for (int i = 0; i < ptID_.length; i++)
	{
		TrendSerie tempSerie = new TrendSerie();
		tempSerie.setPointId( new Integer(ptID_[i]) );
		tempSerie.setTypeMask( GraphDataSeries.BASIC_GRAPH_TYPE );

		tempSerie.setLabel( ptNames_[i] );
		tempSerie.setColor(com.cannontech.common.gui.util.Colors.getColor(i));	//some distinct color (valid 1-10 only)

		trendSeries[i] = tempSerie;
	}		
	hitDatabase_Basic(GraphDataSeries.BASIC_GRAPH_TYPE);
}

public Character getAutoScaleLeft()
{
	return autoScaleLeft;
}
public Character getAutoScaleRight()
{
	return autoScaleRight;
}

public Double getLeftScaleMin()
{
	return leftScaleMin;
}
public Double getLeftScaleMax()
{
	return leftScaleMax;
}
public Double getRightScaleMin()
{
	return rightScaleMin;
}
public Double getRightScaleMax()
{
	return rightScaleMax;
}

public String getChartName()
{
	return chartName;
}
private AbstractDataset getDataset(int datatsetIndex)
{
	if( dataset != null)
	{
		return dataset[datatsetIndex];
	}
	return null;
}

private AbstractDataset getPrimaryDataset()
{
	if( dataset != null)
	{
		return dataset[PRIMARY_AXIS];
	}
	return null;
}
private AbstractDataset getSecondaryDataset()
{
	if( dataset != null)
	{
		return dataset[SECONDARY_AXIS];
	}
	return null;
}
private boolean isMultipleDataset()
{
	if( getPrimaryDataset() != null & getSecondaryDataset() != null)
		return true;
	return false;
}
private int getDatasetSeriesCount()
{
	int count = 0;
	count += getPrimaryDatasetCount();
	count += getSecondaryDatasetCount();
	return count;
}
private int getDatasetCount(int index)
{
	int count = 0;
	if( getDataset(index) != null)
	{
		if( getDataset(index) instanceof AbstractSeriesDataset)
		{
			count += ((AbstractSeriesDataset)getDataset(index)).getSeriesCount();
		}
		else if( getDataset(index) instanceof DefaultCategoryDataset)
		{
			count += ((DefaultCategoryDataset)getDataset(index)).getRowCount();	
		}
	}
	return count;
}

private int getPrimaryDatasetCount()
{
	int count = 0;
	if( getPrimaryDataset() != null)
	{
		if( getPrimaryDataset() instanceof AbstractSeriesDataset)
		{
			count += ((AbstractSeriesDataset)getPrimaryDataset()).getSeriesCount();
		}
		else if( getPrimaryDataset() instanceof DefaultCategoryDataset)
		{
			count += ((DefaultCategoryDataset)getPrimaryDataset()).getColumnCount();	
		}
	}
	return count;
}
private int getSecondaryDatasetCount()
{
	int count = 0;
	if( getSecondaryDataset() != null)
	{
		if( getSecondaryDataset() instanceof AbstractSeriesDataset)
		{
			count += ((AbstractSeriesDataset)getSecondaryDataset()).getSeriesCount();
		}
		else if( getSecondaryDataset() instanceof DefaultCategoryDataset)
		{
			count += ((DefaultCategoryDataset)getSecondaryDataset()).getColumnCount();	
		}
	}
	return count;
}

private void addRangeMarkers(Plot plot)
{
	for( int i = 0; i < getTrendSeries().length; i++)
	{
		TrendSerie serie = getTrendSeries()[i];
		if( GraphDataSeries.isThresholdType(serie.getTypeMask()))
		{
			// add a labelled marker for the bid start price...
			org.jfree.chart.Marker threshold = new org.jfree.chart.Marker(serie.getMultiplier().doubleValue(), serie.getColor());
			threshold.setLabel(serie.getMultiplier().toString());
			if( serie.getAxis().equals(axisChars[PRIMARY_AXIS]))
			{
				if( plot instanceof XYPlot)
					((XYPlot)plot).addRangeMarker(threshold);
				else if( plot instanceof CategoryPlot)
					((CategoryPlot)plot).addRangeMarker(threshold);
					
				threshold.setLabelPosition(org.jfree.chart.MarkerLabelPosition.TOP_LEFT);					
			}
			else
			{
				if( plot instanceof XYPlot)
					((XYPlot)plot).addSecondaryRangeMarker(threshold);
				else if( plot instanceof CategoryPlot)
					((CategoryPlot)plot).addSecondaryRangeMarker(threshold);
					
				threshold.setLabelPosition(org.jfree.chart.MarkerLabelPosition.TOP_RIGHT);					
			}
		}
	}
//	return plot;	 
}
private java.awt.Paint[] getSeriesPaint(int datasetIndex)
{
	java.awt.Paint[] paint = null;
	if( getTrendSeries() != null)
	{
		paint = new java.awt.Color[getDatasetCount(datasetIndex)];
		int count = 0;

		for( int i = 0; i < trendSeries.length; i++)
		{
			TrendSerie serie = trendSeries[i];
			if( serie != null && (GraphDataSeries.isGraphType(serie.getTypeMask())))
			{
				if( serie.getAxis().equals(axisChars[datasetIndex]))
				{
					if(serie.getColor() != null)
						paint[count++] = serie.getColor();
				}
			}
		}
	}
	return paint;
}

private com.cannontech.jfreechart.chart.Dataset_MinMaxValues[] getDataset_MinMaxValues(int index)
{
    com.cannontech.jfreechart.chart.Dataset_MinMaxValues[] minMaxValuesArray = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues[getDatasetCount(index)];
	int count =0;
	if( getTrendSeries() != null)
	{
		
		for( int j = 0; j < getTrendSeries().length; j++)
		{
			TrendSerie serie = getTrendSeries()[j];
			if( serie != null && (GraphDataSeries.isGraphType(serie.getTypeMask())))
			{
				if( serie.getAxis().equals(axisChars[index]))
				{
					if( serie.getMaximumValue() != null && serie.getMinimumValue() != null)
					{
						minMaxValuesArray[count++] = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues(serie.getMinimumValue().doubleValue(), serie.getMaximumValue().doubleValue());
					}
					else
					{
						minMaxValuesArray[count++] = null;//new com.cannontech.jfreechart.chart.Dataset_MinMaxValues();
					}
				}				
			}
		}
	}
	return minMaxValuesArray;
}
public void setAutoScaleLeft(Character newAutoScale)
{
	autoScaleLeft = newAutoScale;
}
public void setAutoScaleRight(Character newAutoScale)
{
	autoScaleRight = newAutoScale;
}

public void setLeftScaleMin(Double newMin)
{
	leftScaleMin = newMin;
}
public void setLeftScaleMax(Double newMax)
{
	leftScaleMax = newMax;
}
public void setRightScaleMin(Double newMin)
{
	rightScaleMin = newMin;
}
public void setRightScaleMax(Double newMax)
{
	rightScaleMax = newMax;
}

private Axis getDomainAxis()
{
	if( getViewType() == TrendModelType.LINE_VIEW || getViewType() == TrendModelType.STEP_VIEW || getViewType() == TrendModelType.SHAPES_LINE_VIEW )//|| rendererType == TrendModelType.BAR_VIEW)
	{
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
		{
			NumberAxis domainAxis = new NumberAxis(getTrendProps().getDomainLabel_LD());
//			NumberAxis domainAxis = new HorizontalNumberAxis("Percentage");
			domainAxis.setAutoRange(false);
			domainAxis.setUpperBound(100);
			domainAxis.setTickMarksVisible(true);
//			((HorizontalNumberAxis)domainAxis).setVerticalTickLabels(false);	
			((NumberAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
		else
		{
			DateAxis domainAxis = new DateAxis(getTrendProps().getDomainLabel());
			domainAxis.setAutoRange(false);
			domainAxis.setMaximumDate(getStopDate());
			domainAxis.setMinimumDate(getStartDate());
		
			domainAxis.setTickMarksVisible(true);	
			((DateAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
	}
	else if( getViewType() == TrendModelType.BAR_VIEW || getViewType() == TrendModelType.BAR_3D_VIEW)
	{
		CategoryAxis catAxis = new CategoryAxis(getTrendProps().getDomainLabel());
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
			catAxis.setLabel(getTrendProps().getDomainLabel_LD());

		((CategoryAxis)catAxis).setVerticalCategoryLabels(false);
		((CategoryAxis)catAxis).setSkipCategoryLabelsToFit(true);
		catAxis.setTickMarksVisible(true);
		return catAxis;
	}
	return null;
}	
private StringBuffer getSQLQueryString(int seriesTypeMask)
{
	
	java.util.Vector validIDs = new java.util.Vector(trendSeries.length);	//guess on max capacity
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
			validIDs.add(trendSeries[i].getPointId());
	}

	if( validIDs.isEmpty())
		return null;
			
	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE "
	+ "FROM RAWPOINTHISTORY WHERE POINTID IN (");

	sql.append( (Integer)validIDs.get(0));
	for ( int i = 1; i <validIDs.size(); i ++)
	{
		sql.append(", " + ( (Integer)validIDs.get(i)));
	}
	sql.append(")  AND ((TIMESTAMP > ? AND TIMESTAMP <= ? ) ");
	sql.append(" ) ORDER BY POINTID, TIMESTAMP");
	return sql;	
}

private Integer getPrimaryGDSPointId()
{
	if( primaryGDSPointID == null)
	{
		for (int i = 0; i < trendSeries.length; i++)
		{
			if( GraphDataSeries.isPrimaryType(trendSeries[i].getTypeMask()))
				primaryGDSPointID = trendSeries[i].getPointId();
		}
	}
	return primaryGDSPointID;
}			
private YukonStandardLegend getLegend(JFreeChart fChart)
{
	//Legend setup
	YukonStandardLegend legend = new YukonStandardLegend(fChart);
	legend.setAnchor(Legend.SOUTH);
	legend.setItemFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 10));
/*
	java.util.Vector stats = null;

	if( (getOptionsMaskSettings() & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK ||
		(getOptionsMaskSettings() & TrendModelType.LEGEND_MIN_MAX_MASK ) == TrendModelType.LEGEND_MIN_MAX_MASK)
	{
		stats = new java.util.Vector(getDatasetSeriesCount());
		for( int i = 0; i < trendSeries.length; i++)
		{
			TrendSerie serie = trendSeries[i];
			String stat = "";					
			if(GraphDataSeries.isGraphType(serie.getTypeMask()))
			{
				if ((getOptionsMaskSettings() & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK)
				{
					double lf = serie.getLoadFactor();
					if( lf < 0)
						stat += "Load Factor: n/a";
					else
						stat += "Load Factor: " + LF_FORMAT.format(lf);
				}

				if( (getOptionsMaskSettings() & TrendModelType.LEGEND_MIN_MAX_MASK) == TrendModelType.LEGEND_MIN_MAX_MASK)
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
private NumberAxis getPrimaryRangeAxis()	//LEFT
{
	NumberAxis primaryRangeAxis = null;
	if( getViewType() == TrendModelType.BAR_3D_VIEW)
		primaryRangeAxis = new NumberAxis3D(getTrendProps().getPrimaryRangeLabel());
	else
		primaryRangeAxis = new NumberAxis(getTrendProps().getPrimaryRangeLabel());
		

//	rangeAxis1.setLabel(TrendProperties.getRangeLabel_primary());
	
	if( getAutoScaleLeft().charValue() != 'Y')
	{
		primaryRangeAxis.setAutoRange(false);
		primaryRangeAxis.setUpperBound(getLeftScaleMax().doubleValue());
		primaryRangeAxis.setLowerBound(getLeftScaleMin().doubleValue());
	}
	primaryRangeAxis.setTickMarksVisible(true);
	primaryRangeAxis.setAutoRangeIncludesZero(false);

	return primaryRangeAxis;
}

private NumberAxis getSecondaryRangeAxis()	//RIGHT
{
	NumberAxis secondaryRangeAxis = null;
	if( getViewType() == TrendModelType.BAR_3D_VIEW)
		secondaryRangeAxis = new NumberAxis3D(getTrendProps().getSecondaryRangeLabel());
	else
		secondaryRangeAxis = new NumberAxis(getTrendProps().getSecondaryRangeLabel());

//		rangeAxis2.setLabel(TrendProperties.getRangeLabel_secondary());
	if( getAutoScaleRight().charValue() != 'Y')
	{
		secondaryRangeAxis.setAutoRange(false);
		secondaryRangeAxis.setUpperBound(getRightScaleMax().doubleValue());
		secondaryRangeAxis.setLowerBound(getRightScaleMin().doubleValue());
	}
		
	secondaryRangeAxis.setTickMarksVisible(true);
	secondaryRangeAxis.setAutoRangeIncludesZero(false);

	return secondaryRangeAxis;
}
/**
 * Returns the trendSeries value.
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * @param int seriesTypeMask
 * @return com.cannontech.graph.model.TrendSerie[]
 */
private TrendSerie[] hitDatabase_Basic(int seriesTypeMask) 
{
	java.util.Date timerStart = new java.util.Date();

	if( trendSeries == null)
		return null;

	StringBuffer sql = getSQLQueryString(seriesTypeMask);
	if( sql == null)
		return null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(":  Error getting database connection.");
			return null;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			long day = 0;			
			long startTS = 0;
			long stopTS = 0;

			if (GraphDataSeries.isYesterdayType(seriesTypeMask))
			{
				day = 86400000;
				startTS = getStartDate().getTime() - day;
				stopTS = getStopDate().getTime() - day;
				for (int i = 0; i < trendSeries.length; i++)
				{
					if (GraphDataSeries.isYesterdayType(trendSeries[i].getTypeMask()))
					{
						trendSeries[i].setLabel(trendSeries[i].getLabel() + " ["+ LEGEND_DATE_FORMAT.format(new java.util.Date(startTS))+"]");
					}
				}
			}
			else if (GraphDataSeries.isUsageType(seriesTypeMask))
			{
				day = 86400000;
				startTS = getStartDate().getTime() - day;
				stopTS = getStopDate().getTime() + day;
				day = 0;	// we set it back to 0 for our tp setup.
			}
			else
			{
				startTS = getStartDate().getTime();
				stopTS = getStopDate().getTime();
			}

			com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
			pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));
			pstmt.setTimestamp(2, new java.sql.Timestamp( stopTS ));

			rset = pstmt.executeQuery();
			 
			TimeSeriesDataItem dataItem = null;
			//contains org.jfree.data.time.TimeSeriesDataItem values.
			java.util.Vector dataItemVector = new java.util.Vector(0);			
			int lastPointId = -1;

			boolean addNext = true;
			boolean firstOne = true;
			java.util.Date start = getStartDate();
			java.util.Date stop = new java.util.Date (getStartDate().getTime() + 86400000);

			while( rset.next() )
			{
				int pointID = rset.getInt(1);

				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						//Save the data you've collected into the array of models (chartmodelsArray).
						TimeSeriesDataItem[] dataItemArray =
							new TimeSeriesDataItem[dataItemVector.size()];
						dataItemVector.toArray(dataItemArray);
						dataItemVector.clear();
						
						for (int i = 0; i < getTrendSeries().length; i++)
						{
							if( trendSeries[i].getPointId().intValue() == lastPointId 
								&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
								trendSeries[i].setDataItemArray(dataItemArray);
						}
		
					}
					lastPointId = pointID;
					
					if( GraphDataSeries.isUsageType(seriesTypeMask))
					{
						addNext = true;
						firstOne = true;
						start = getStartDate();
						stop = new java.util.Date (getStartDate().getTime() + 86400000);
					}
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				RegularTimePeriod tp = new Millisecond(new java.util.Date(ts.getTime() + day));

				double val = rset.getDouble(3);
			
				if( GraphDataSeries.isUsageType(seriesTypeMask))
				{
					java.util.Date tsDate = new java.util.Date(ts.getTime());

					if(tsDate.compareTo(start) < 0)
					{	
						dataItem = new TimeSeriesDataItem(tp, val);	
					}
					else if(tsDate.compareTo(start) == 0)
					{	
						dataItem = new TimeSeriesDataItem(tp, val);
						dataItemVector.add(dataItem);
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
							dataItemVector.add(dataItem);
							firstOne = false;
						}
						if( addNext )
						{
							dataItem = new TimeSeriesDataItem(tp, val);
							dataItemVector.add(dataItem);
							addNext = false;							
						}

						if(stop.getTime() < getStopDate().getTime())
						{
							start = stop;
							stop = new java.util.Date(start.getTime() + 86400000);
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
							dataItemVector.add(dataItem);
							firstOne = false;
						}
					}
				}
				else
				{
					dataItem = new TimeSeriesDataItem(tp, val);
					dataItemVector.add(dataItem);
				}
							
			}	//END WHILE
			
			
			if( !dataItemVector.isEmpty())
			{
				TimeSeriesDataItem[] dataItemArray = new TimeSeriesDataItem[dataItemVector.size()];
				dataItemVector.toArray(dataItemArray);
				dataItemVector.clear();			

				for (int i = 0; i < getTrendSeries().length; i++)
				{
					if( trendSeries[i].getPointId().intValue() == lastPointId
						&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
					{
						trendSeries[i].setDataItemArray(dataItemArray);
					}
				}
			}
			else 
			{
				return null;
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
			return null;
		}	
		java.util.Date timerStop = new java.util.Date();
		com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for Trend Data Load ( for type:" + GraphDataSeries.getType(seriesTypeMask) + " )");
	}
	return trendSeries;
}
private TrendSerie[] hitDatabase_Date(int seriesTypeMask, int serieIndex) 
{
	java.util.Date timerStart = new java.util.Date();

	if( trendSeries[serieIndex]== null)
		return null;

	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE " + 
	" FROM RAWPOINTHISTORY WHERE POINTID = " + trendSeries[serieIndex].getPointId().intValue() +
	" AND (TIMESTAMP > ? AND TIMESTAMP <= ? ) ORDER BY POINTID, TIMESTAMP");
	 
	if( sql == null)
		return null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(":  Error getting database connection.");
			return null;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			long day = 0;			
			long startTS = 0;
			long stopTS = 0;

			if (GraphDataSeries.isPeakType(seriesTypeMask))
			{
				if (GraphDataSeries.isPeakType(trendSeries[serieIndex].getTypeMask()))
				{ //TODO unfortunately, only able to do peak for one point right now
					day = retrievePeakIntervalTranslateMillis(trendSeries[serieIndex].getPointId().intValue());
					startTS = getStartDate().getTime() - day;
					stopTS = getStartDate().getTime() - day + 86400000;
					trendSeries[serieIndex].setLabel(trendSeries[serieIndex].getLabel() + " ["+ LEGEND_DATE_FORMAT.format(new java.util.Date(startTS))+"]");
				}
			}
			else if (GraphDataSeries.isDateType(seriesTypeMask))
			{
				if (GraphDataSeries.isDateType(trendSeries[serieIndex].getTypeMask()))
				{
					day = getStartDate().getTime() - Long.valueOf(trendSeries[serieIndex].getMoreData()).longValue();
					startTS = getStartDate().getTime() - day;
					stopTS = getStartDate().getTime() - day + 86400000;
					trendSeries[serieIndex].setLabel(trendSeries[serieIndex].getLabel() + " ["+ LEGEND_DATE_FORMAT.format(new java.util.Date(startTS))+"]");
				}
			}

			com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(startTS) + "  -  STOP DATE <= " + new java.sql.Timestamp(stopTS));
			pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));
			pstmt.setTimestamp(2, new java.sql.Timestamp( stopTS ));

			rset = pstmt.executeQuery();
			 
			TimeSeriesDataItem dataItem = null;
			//contains org.jfree.data.time.TimeSeriesDataItem values.
			java.util.Vector dataItemVector = new java.util.Vector(0);			
			int lastPointId = -1;

			boolean addNext = true;
			boolean firstOne = true;
			java.util.Date start = getStartDate();
			java.util.Date stop = new java.util.Date (getStartDate().getTime() + 86400000);

			while( rset.next() )
			{
				int pointID = rset.getInt(1);

				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						//Save the data you've collected into the array of models (chartmodelsArray).
						TimeSeriesDataItem[] dataItemArray = new TimeSeriesDataItem[dataItemVector.size()];
						dataItemVector.toArray(dataItemArray);
						dataItemVector.clear();
						
						//MAY NOT NEED THIS CAUSE ONLY HAVE ONE POINTID!
						if( trendSeries[serieIndex].getPointId().intValue() == lastPointId 
								&& (trendSeries[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
								trendSeries[serieIndex].setDataItemArray(dataItemArray);
		
					}
					lastPointId = pointID;
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				RegularTimePeriod tp = new Millisecond(new java.util.Date(ts.getTime() + day));

				double val = rset.getDouble(3);
			
				dataItem = new TimeSeriesDataItem(tp, val);
				dataItemVector.add(dataItem);
							
			}	//END WHILE
			
			
			if( !dataItemVector.isEmpty())
			{
				// Repeat the interval x # of days with Peak_interval data series.
				if (GraphDataSeries.isPeakType(seriesTypeMask ) || GraphDataSeries.isDateType(seriesTypeMask))
				{
					int size = dataItemVector.size();
					long numDays = (getStopDate().getTime() - getStartDate().getTime()) / 86400000;
					for ( long i = 1; i < numDays; i++)
					{
						for (int j = 0; j < size; j++)
						{
							double v = ((TimeSeriesDataItem)dataItemVector.get(j)).getValue().doubleValue();
							RegularTimePeriod tp = new Millisecond(new java.util.Date(((TimeSeriesDataItem)dataItemVector.get(j)).getPeriod().getStart().getTime() + (86400000*i)));
							dataItem = new TimeSeriesDataItem(tp,v);
							dataItemVector.add(dataItem);							
						}					
					}
				}
				
				TimeSeriesDataItem[] dataItemArray = new TimeSeriesDataItem[dataItemVector.size()];
				dataItemVector.toArray(dataItemArray);
				dataItemVector.clear();			

				if( trendSeries[serieIndex].getPointId().intValue() == lastPointId
					&& (trendSeries[serieIndex].getTypeMask() & seriesTypeMask) == seriesTypeMask)
				{
					trendSeries[serieIndex].setDataItemArray(dataItemArray);
				}
			}
			else 
			{
				return null;
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
			return null;
		}	
		java.util.Date timerStop = new java.util.Date();
		com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for Trend Data Load ( for type:" + GraphDataSeries.getType(seriesTypeMask) + " )");
	}
	return trendSeries;
}

private long retrievePeakIntervalTranslateMillis(int peakIntervalPointID)
{
	long translateMillis = 0;
	com.cannontech.database.cache.TimedDatabaseCache cache = com.cannontech.database.cache.TimedDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List peakPoints = cache.getAllPeakPointHistory();
		java.util.Iterator iter = peakPoints.iterator();
		while( iter.hasNext() )
		{
			com.cannontech.database.db.point.PeakPointHistory pt = (com.cannontech.database.db.point.PeakPointHistory) iter.next();
			if( pt.getPointID().intValue() == peakIntervalPointID)
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime(pt.getTimeStamp().getTime());
				String time = TRANSLATE_DATE.format(cal.getTime());
	
				if( Integer.valueOf(time).intValue() == 0)	//must have Day+1 00:00:00 instead of Day 00:00:01+
				{	
					cal.roll(java.util.Calendar.DAY_OF_YEAR, false);
				}
				else
				{
					cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
					cal.set(java.util.Calendar.MINUTE, 0);
					cal.set(java.util.Calendar.SECOND, 0);
					cal.set(java.util.Calendar.MILLISECOND, 0);
				}
				translateMillis = (getStartDate().getTime() - cal.getTime().getTime());
				com.cannontech.clientutils.CTILogger.info(" PEAK POINT TS/VALUE = " + pt.getPointID() + " | " + pt.getTimeStamp().getTime() + " | " + pt.getValue());
				break;
			}
		}
	}	

	return translateMillis;
}
/*
 * private long retrievePeakIntervalTranslateMillis(int peakIntervalPointID)
{
	long timerStart = System.currentTimeMillis();
	long translateMillis = 0;
	String sqlString = "SELECT TIMESTAMP FROM PEAKPOINTHISTORY_VIEW WHERE POINTID = " + peakIntervalPointID;

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try 
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			java.sql.Timestamp ts = rset.getTimestamp(1);

			java.util.Calendar cal = new java.util.GregorianCalendar();
			String time = TRANSLATE_DATE.format(new java.util.Date(ts.getTime()));

			if( Integer.valueOf(time).intValue() == 0)	//must have Day+1 00:00:00 instead of Day 00:00:01+
			{	
				cal.setTime(new java.util.Date(ts.getTime()));				
				cal.roll(java.util.Calendar.DAY_OF_YEAR, false);
			}
			else
			{
				cal.setTime(new java.util.Date(ts.getTime()));
				cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
				cal.set(java.util.Calendar.MINUTE, 0);
				cal.set(java.util.Calendar.SECOND, 0);
				cal.set(java.util.Calendar.MILLISECOND, 0);
			}
			ts.setTime(cal.getTime().getTime());
			translateMillis = (getStartDate().getTime() - ts.getTime());
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

	}
	return translateMillis;
}
*/
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
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public JFreeChart refresh()
{
	//Plot setup
	Plot plot = null;
	
	dataset = YukonDataSetFactory.createDataset( getTrendSeries(), getOptionsMaskSettings(), getViewType());
	
	if( getViewType() == TrendModelType.LINE_VIEW|| getViewType() == TrendModelType.SHAPES_LINE_VIEW)
	{
		TimeSeriesToolTipGenerator generator = new TimeSeriesToolTipGenerator(extendedTimeFormat, valueFormat);

		XYItemRenderer rend = null;

		// Need to convert yukon TrendModelType into StandardXYItemRenderer type
		int type = 0;
		if( getViewType() == TrendModelType.LINE_VIEW)
			type = StandardXYItemRenderer.LINES;
		else if( getViewType() == TrendModelType.SHAPES_LINE_VIEW)
			type = StandardXYItemRenderer.SHAPES_AND_LINES;
						
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax(type, generator);
			((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(PRIMARY_AXIS);
		}
		else
		{
			rend = new StandardXYItemRenderer(type, generator);
		}
//  	  TimeSeriesCollection
//        com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm mavg = new com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm();
//        mavg.setPeriod(30);
//        com.jrefinery.chart.data.PlotFit pf = new com.jrefinery.chart.data.PlotFit((com.jrefinery.data.XYDataset)dataset, mavg);
//        dataset = (com.jrefinery.data.AbstractSeriesDataset)pf.getFit();
		TimeSeriesURLGenerator urlg = new TimeSeriesURLGenerator
			(dateFormat, "user_trending.jsp", "gdefid", "startdate");
		rend.setURLGenerator(urlg);
		java.awt.Paint [] seriesPaint = getSeriesPaint(PRIMARY_AXIS);
		for(int i = 0; i < seriesPaint.length; i++)
			rend.setSeriesPaint(i, seriesPaint[i]);
		
		plot = new XYPlot( (XYDataset)getPrimaryDataset(), (ValueAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		if(getDatasetCount(SECONDARY_AXIS) > 0)
		{
			if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
			{
				rend = new com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax(type, generator);
				((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(SECONDARY_AXIS);
			}
			else
			{
				rend = new StandardXYItemRenderer(type, generator);
			}
			seriesPaint = getSeriesPaint(SECONDARY_AXIS);
			for(int i = 0; i < seriesPaint.length; i++)
				rend.setSeriesPaint(i, seriesPaint[i]);

			((XYPlot)plot).setSecondaryRenderer(0, rend);
			((XYPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((XYPlot)plot).setSecondaryDataset(0, (XYDataset)getSecondaryDataset());
			((XYPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
	}
	else if( getViewType()  == TrendModelType.STEP_VIEW)
	{
		XYItemRenderer rend = null;
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
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
		
		java.awt.Paint [] seriesPaint = getSeriesPaint(PRIMARY_AXIS);
		for(int i = 0; i < seriesPaint.length; i++)
			rend.setSeriesPaint(i, seriesPaint[i]);

		plot = new XYPlot( (XYDataset)getPrimaryDataset(), (ValueAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);
		((XYPlot)plot).setRangeAxisLocation( org.jfree.chart.axis.AxisLocation.BOTTOM_OR_LEFT);
		//Attempt to do multiple axis
		if( getDatasetCount(SECONDARY_AXIS) > 0)
		{
			if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
			{
				rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
				((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues(SECONDARY_AXIS);
			}
			else
			{
				rend = new XYStepRenderer();
			}
			seriesPaint = getSeriesPaint(SECONDARY_AXIS);
			for(int i = 0; i < seriesPaint.length; i++)
				rend.setSeriesPaint(i, seriesPaint[i]);			

			((XYPlot)plot).setSecondaryRenderer(0, rend);
			((XYPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((XYPlot)plot).setSecondaryDataset(0, (XYDataset)getSecondaryDataset());
			((XYPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
		
	}
	else if( getViewType()  == TrendModelType.BAR_VIEW)
	{
		CategoryItemRenderer rend = new BarRenderer();		
		rend.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		rend.setItemURLGenerator(new org.jfree.chart.urls.StandardCategoryURLGenerator());
		
		java.awt.Paint [] seriesPaint = getSeriesPaint(PRIMARY_AXIS);
		for(int i = 0; i < seriesPaint.length; i++)
			rend.setSeriesPaint(i, seriesPaint[i]);
		plot = new CategoryPlot( (DefaultCategoryDataset)getPrimaryDataset(), (CategoryAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)
		((CategoryPlot)plot).setRangeAxisLocation( org.jfree.chart.axis.AxisLocation.BOTTOM_OR_LEFT);
		if( getDatasetCount(SECONDARY_AXIS) > 0)
		{
			rend = new LineAndShapeRenderer(LineAndShapeRenderer.LINES);
//			rend = new BarRenderer();
			rend.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			rend.setItemURLGenerator(new org.jfree.chart.urls.StandardCategoryURLGenerator());
		
			seriesPaint = getSeriesPaint(SECONDARY_AXIS);
			for(int i = 0; i < seriesPaint.length; i++)
				rend.setSeriesPaint(i, seriesPaint[i]);			

			((CategoryPlot)plot).setSecondaryRenderer(0, rend);
			((CategoryPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((CategoryPlot)plot).setSecondaryDataset(0, (DefaultCategoryDataset)getSecondaryDataset());
			((CategoryPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
	}
	else if( getViewType() == TrendModelType.BAR_3D_VIEW)
	{
		CategoryItemRenderer rend = new BarRenderer3D(10, 10);
		
		java.awt.Paint [] seriesPaint = getSeriesPaint(PRIMARY_AXIS);
		for(int i = 0; i < seriesPaint.length; i++)
			rend.setSeriesPaint(i, seriesPaint[i]);

		plot = new CategoryPlot( (DefaultCategoryDataset)getPrimaryDataset(), (CategoryAxis)getDomainAxis(), getPrimaryRangeAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)
		if(getDatasetCount(SECONDARY_AXIS) > 0)
		{
			rend = new LineAndShapeRenderer(LineAndShapeRenderer.LINES);
//rend = new BarRenderer3D(10,10);
			seriesPaint = getSeriesPaint(SECONDARY_AXIS);
			for(int i = 0; i < seriesPaint.length; i++)
				rend.setSeriesPaint(i, seriesPaint[i]);			

			((CategoryPlot)plot).setSecondaryRenderer(0, rend);
			((CategoryPlot)plot).setSecondaryRangeAxis(0, getSecondaryRangeAxis());
			((CategoryPlot)plot).setSecondaryDataset(0, (DefaultCategoryDataset)getSecondaryDataset());
			((CategoryPlot)plot).mapSecondaryDatasetToRangeAxis(0, new Integer(0));
		}
	}
	else if( getViewType() == TrendModelType.TABULAR_VIEW)
	{
		return null;
	}
	else if( getViewType()== TrendModelType.SUMMARY_VIEW)
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
			if(( trendProps.getOptionsMaskSettings() & TrendModelType.GRAPH_MULTIPLIER_MASK) == TrendModelType.GRAPH_MULTIPLIER_MASK)
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

}
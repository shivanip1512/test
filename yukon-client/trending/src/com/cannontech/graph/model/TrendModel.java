package com.cannontech.graph.model;

import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.Point;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.DateAxis;
import com.jrefinery.chart.axis.HorizontalCategoryAxis;
import com.jrefinery.chart.axis.HorizontalDateAxis;
import com.jrefinery.chart.axis.HorizontalNumberAxis;
import com.jrefinery.chart.axis.NumberAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis;
import com.jrefinery.chart.axis.VerticalNumberAxis3D;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.renderer.VerticalBarRenderer;
import com.jrefinery.chart.renderer.VerticalBarRenderer3D;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.chart.renderer.XYStepRenderer;
import com.jrefinery.data.DefaultCategoryDataset;
//import com.cannontech.graph.model.TrendProperties;
public class TrendModel implements com.cannontech.graph.GraphDefines 
{
    private java.text.SimpleDateFormat TITLE_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMMMM dd, yyyy");
	private java.text.SimpleDateFormat TRANSLATE_DATE= new java.text.SimpleDateFormat("HHmmss");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
	
	private int optionsMaskSettings = 0x00;
	
	// dataset is an array of size 2.
	// Left and Right (axis) datasets.
	private com.jrefinery.data.AbstractDataset [] dataset = null;	
    private TrendSerie trendSeries[] = null;
    private java.util.Date startDate = null;
    private java.util.Date	stopDate = null;
    
    private String chartName = "Yukon Trending";
	private int rendererType = TrendModelType.LINE_VIEW;	// default
	    
    private Integer primaryGDSPointID = null;

	// Multiple axis setup
    private Character autoScaleRight = new Character('Y');
    private Double rightScaleMin = new Double(0.0);
    private Double rightScaleMax = new Double(100.0);
    private Character autoScaleLeft = new Character('Y');
    private Double leftScaleMin = new Double(0.0);
    private Double leftScaleMax = new Double(100.0);
    
	private NumberAxis rangeAxis1 = null;
	private NumberAxis rangeAxis2 = null;

	private static Character [] axisChars = new Character[]{new Character('L'), new Character('R')};
	private final int PRIMARY = 0;
	private final int SECONDARY = 1;

/**
 * Constructor for TrendModel.
 * The startDate and stopDate values will default to the start/stop dates of newGraphDef.
 * setStartDate(...) and setStopDate(...) must be explicitely called otherwise.
 * @param graphDefinition the graphDefinition to graph
 * @param optionsMask the options to include
 */
public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, int optionsMask)
{
	this(newGraphDef, newGraphDef.getGraphDefinition().getStartDate(), newGraphDef.getGraphDefinition().getStopDate(), optionsMask);
}

/**
 * Constructor for TrendModel.
 * @param graphDefinition the graphDefinition to graph
 * @param startDate the date to start the trend (exclusive)
 * @param stopDate the date to stop the trend (inclusive)
 * @param optionsMask the options to include
 */
public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, java.util.Date newStartDate, java.util.Date newStopDate, int optionsMask)
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

		dsVector.add(serie);
	}

	if( !dsVector.isEmpty())
	{
		trendSeries = new TrendSerie[dsVector.size()];
		dsVector.toArray(trendSeries);
		hitDatabase_Basic(GraphDataSeries.BASIC_TYPE);
		hitDatabase_Basic(GraphDataSeries.YESTERDAY_TYPE);
		hitDatabase_Basic(GraphDataSeries.PEAK_TYPE);
		hitDatabase_Basic(GraphDataSeries.USAGE_TYPE);
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info(" GraphDefinition contains NO graphDataSeries Items");
	}
	setOptionsMask(optionsMask);	
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
private com.jrefinery.data.AbstractDataset getDataset(int datatsetIndex)
{
	if( dataset != null)
	{
		return dataset[datatsetIndex];
	}
	return null;
}

private com.jrefinery.data.AbstractDataset getPrimaryDataset()
{
	if( dataset != null)
	{
		return dataset[PRIMARY];
	}
	return null;
}
private com.jrefinery.data.AbstractDataset getSecondaryDataset()
{
	if( dataset != null)
	{
		return dataset[SECONDARY];
	}
	return null;
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
		if( getDataset(index) instanceof com.jrefinery.data.AbstractSeriesDataset)
		{
			count += ((com.jrefinery.data.AbstractSeriesDataset)getDataset(index)).getSeriesCount();
		}
		else if( getDataset(index) instanceof DefaultCategoryDataset)
		{
			count += ((com.jrefinery.data.DefaultCategoryDataset)getDataset(index)).getRowCount();	
		}
	}
	return count;
}

private int getPrimaryDatasetCount()
{
	int count = 0;
	if( getPrimaryDataset() != null)
	{
		if( getPrimaryDataset() instanceof com.jrefinery.data.AbstractSeriesDataset)
		{
			count += ((com.jrefinery.data.AbstractSeriesDataset)getPrimaryDataset()).getSeriesCount();
		}
		else if( getPrimaryDataset() instanceof DefaultCategoryDataset)
		{
			count += ((com.jrefinery.data.DefaultCategoryDataset)getPrimaryDataset()).getColumnCount();	
		}
	}
	return count;
}
private int getSecondaryDatasetCount()
{
	int count = 0;
	if( getSecondaryDataset() != null)
	{
		if( getSecondaryDataset() instanceof com.jrefinery.data.AbstractSeriesDataset)
		{
			count += ((com.jrefinery.data.AbstractSeriesDataset)getSecondaryDataset()).getSeriesCount();
		}
		else if( getSecondaryDataset() instanceof DefaultCategoryDataset)
		{
			count += ((com.jrefinery.data.DefaultCategoryDataset)getSecondaryDataset()).getColumnCount();	
		}
	}
	return count;
}

private com.jrefinery.chart.renderer.DrawingSupplier getDrawingSupplier(int datasetIndex)
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
	com.jrefinery.chart.renderer.DrawingSupplier supplier = new com.jrefinery.chart.renderer.DefaultDrawingSupplier(paint, paint, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
	return supplier;
}

//private com.jrefinery.chart.PaintTable getPaintTable_BAR_Temporary()
private com.jrefinery.chart.renderer.DrawingSupplier getPaintTable_BAR_Temporary(int datasetIndex)
{
	java.awt.Paint [] paint = null;
	
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
					if(serie.getColor() != null)
						paint[count++] = serie.getColor();
			}
		}
	}
	
	com.jrefinery.chart.renderer.DrawingSupplier supplier = new com.jrefinery.chart.renderer.DefaultDrawingSupplier(paint, paint, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, com.jrefinery.chart.renderer.DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
	return supplier;
}

private com.cannontech.jfreechart.chart.Dataset_MinMaxValues[][] getDataset_MinMaxValues()
{
    com.cannontech.jfreechart.chart.Dataset_MinMaxValues[][] minMaxValuesArray = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues[2][];
	
	if( getTrendSeries() != null)
	{
		for ( int i = 0; i < 2; i++)
		{
			minMaxValuesArray[i] = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues[getDatasetCount(i)];
			int count =0;
			
			for( int j = 0; j < trendSeries.length; j++)
			{
				TrendSerie serie = trendSeries[j];
				if( serie != null && (GraphDataSeries.isGraphType(serie.getTypeMask())))
				{
					if( serie.getAxis().equals(axisChars[i]))
					{
						if( serie.getMaximumValue() != null && serie.getMinimumValue() != null)
						{
							minMaxValuesArray[i][count++] = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues(serie.getMinimumValue().doubleValue(), serie.getMaximumValue().doubleValue());
						}
						else
						{
							minMaxValuesArray[i][count++] = null;//new com.cannontech.jfreechart.chart.Dataset_MinMaxValues();
						}
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

private com.jrefinery.chart.axis.Axis getHorizontalAxis()
{
	if( rendererType == TrendModelType.LINE_VIEW || rendererType == TrendModelType.STEP_VIEW || rendererType == TrendModelType.SHAPES_LINE_VIEW )//|| rendererType == TrendModelType.BAR_VIEW)
	{
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
		{
			NumberAxis domainAxis = new HorizontalNumberAxis("Percentage");
			domainAxis.setAutoRange(false);
			domainAxis.setMaximumAxisValue(100);
			domainAxis.setTickMarksVisible(true);	
			((HorizontalNumberAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
		else
		{
			DateAxis domainAxis = new HorizontalDateAxis("Date/Time");
			domainAxis.setAutoRange(false);
			domainAxis.setMaximumDate(getStopDate());
			domainAxis.setMinimumDate(getStartDate());
		
			domainAxis.setTickMarksVisible(true);	
			((HorizontalDateAxis)domainAxis).setVerticalTickLabels(false);
			return domainAxis;
		}
	}
	else if( rendererType == TrendModelType.BAR_VIEW || rendererType == TrendModelType.BAR_3D_VIEW)
	{
		CategoryAxis catAxis = new HorizontalCategoryAxis("Date/Time");
		((HorizontalCategoryAxis)catAxis).setVerticalCategoryLabels(false);
		((HorizontalCategoryAxis)catAxis).setSkipCategoryLabelsToFit(true);
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
private com.jrefinery.chart.StandardLegend getLegend(JFreeChart fChart)
{
	//Legend setup
	com.jrefinery.chart.StandardLegend legend = new com.jrefinery.chart.StandardLegend(fChart);
	legend.setAnchor(com.jrefinery.chart.Legend.SOUTH);
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

private int getOptionsMaskSettings()
{
	return optionsMaskSettings;
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
	com.jrefinery.chart.TextTitle chartTitle = new com.jrefinery.chart.TextTitle(TITLE_DATE_FORMAT.format(getStartDate()) + " - " + TITLE_DATE_FORMAT.format(getStopDate()));	
    subtitleList.add(chartTitle);
    return subtitleList;
}

private com.jrefinery.chart.TextTitle getTitle()
{
	//Chart Title
	com.jrefinery.chart.TextTitle chartTitle = new com.jrefinery.chart.TextTitle( getChartName().toString());
	return chartTitle;
}

public TrendSerie[] getTrendSeries()
{
	return trendSeries;
}

//left side axis
private NumberAxis getPrimaryVerticalAxis()	//LEFT
{
	if( rendererType == TrendModelType.BAR_3D_VIEW)
		rangeAxis1 = new VerticalNumberAxis3D("Reading");
	else
		rangeAxis1 = new VerticalNumberAxis("Reading");
		

//	rangeAxis1.setLabel(TrendProperties.getRangeLabel_primary());
	
	if( getAutoScaleLeft().charValue() != 'Y')
	{
		rangeAxis1.setAutoRange(false);
		rangeAxis1.setMaximumAxisValue(getLeftScaleMax().doubleValue());
		rangeAxis1.setMinimumAxisValue(getLeftScaleMin().doubleValue());
	}

	rangeAxis1.setTickMarksVisible(true);
	rangeAxis1.setAutoRangeIncludesZero(false);

	return rangeAxis1;
}

private NumberAxis getSecondaryVerticalAxis()	//RIGHT
{
	if( rangeAxis2 == null || (rangeAxis2 instanceof VerticalNumberAxis3D))
	{
		if( rendererType == TrendModelType.BAR_3D_VIEW)
			rangeAxis2 = new VerticalNumberAxis3D("Reading");
		else
			rangeAxis2 = new VerticalNumberAxis("Reading");

//		rangeAxis2.setLabel(TrendProperties.getRangeLabel_secondary());
		if( getAutoScaleRight().charValue() != 'Y')
		{
			rangeAxis2.setAutoRange(false);
			rangeAxis2.setMaximumAxisValue(getRightScaleMax().doubleValue());
			rangeAxis2.setMinimumAxisValue(getRightScaleMin().doubleValue());
		}
			
		rangeAxis2.setTickMarksVisible(true);
		rangeAxis2.setAutoRangeIncludesZero(false);
	}
	return rangeAxis2;
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
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
			}
			else if (GraphDataSeries.isUsageType(seriesTypeMask))
			{
				day = 86400000;
				startTS = getStartDate().getTime() - day;
				stopTS = getStopDate().getTime() + day;
				day = 0;	// we set it back to 0 for our tp setup.
			}
			else if (GraphDataSeries.isPeakType(seriesTypeMask))
			{
				for (int i = 0; i < trendSeries.length; i++)
				{
					if (GraphDataSeries.isPeakType(trendSeries[i].getTypeMask()))
					{
						day = retrievePeakIntervalTranslateMillis(trendSeries[i].getPointId().intValue());
						startTS = getStartDate().getTime() - day;
						stopTS = getStartDate().getTime() - day + 86400000;
						break;
					}
				}
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
			
			com.jrefinery.data.TimeSeriesDataPair dataPair = null;
			java.util.Vector dataPairVector = new java.util.Vector(0);			
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
						com.jrefinery.data.TimeSeriesDataPair[] dataPairArray =
							new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
						dataPairVector.toArray(dataPairArray);
						dataPairVector.clear();
						
						for (int i = 0; i < getTrendSeries().length; i++)
						{
							if( trendSeries[i].getPointId().intValue() == lastPointId 
								&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
								trendSeries[i].setDataPairArray(dataPairArray);
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
				com.jrefinery.data.RegularTimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime() + day));

				double val = rset.getDouble(3);
			
				if( GraphDataSeries.isUsageType(seriesTypeMask))
				{
					java.util.Date tsDate = new java.util.Date(ts.getTime());

					if(tsDate.compareTo(start) < 0)
					{	
						dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);	
					}
					else if(tsDate.compareTo(start) == 0)
					{	
						dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
						dataPairVector.add(dataPair);
						firstOne = false;
					}
					else if( tsDate.compareTo(stop) >= 0)
					{
						if(firstOne)
						{
							if(dataPair == null)
							{
								dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);						
							}
							dataPairVector.add(dataPair);
							firstOne = false;
						}
						if( addNext )
						{
							dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
							dataPairVector.add(dataPair);
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
							if(dataPair == null)
							{
								dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);						
							}
							dataPairVector.add(dataPair);
							firstOne = false;
						}
					}
				}
				else
				{
					dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
					dataPairVector.add(dataPair);
				}
							
			}	//END WHILE
			
			
			if( !dataPairVector.isEmpty())
			{
				// Repeat the interval x # of days with Peak_interval data series.
				if (GraphDataSeries.isPeakType(seriesTypeMask ))
				{
					int size = dataPairVector.size();
					long numDays = (getStopDate().getTime() - getStartDate().getTime()) / 86400000;
					for ( long i = 1; i < numDays; i++)
					{
						for (int j = 0; j < size; j++)
						{
							double v = ((com.jrefinery.data.TimeSeriesDataPair)dataPairVector.get(j)).getValue().doubleValue();
							com.jrefinery.data.RegularTimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(((com.jrefinery.data.TimeSeriesDataPair)dataPairVector.get(j)).getPeriod().getStart().getTime() + (86400000*i)));
							dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp,v);
							dataPairVector.add(dataPair);							
						}					
					}
				}
				
				com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
					new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
				dataPairVector.toArray(dataPairArray);
				dataPairVector.clear();			

				for (int i = 0; i < getTrendSeries().length; i++)
				{
					if( trendSeries[i].getPointId().intValue() == lastPointId
							&& (trendSeries[i].getTypeMask() & seriesTypeMask) == seriesTypeMask)
							{
								trendSeries[i].setDataPairArray(dataPairArray);
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

public void setOptionsMask(int newOptionsMask)
{
	optionsMaskSettings = newOptionsMask;

	if( trendSeries != null)
	{
		for (int i = 0; i < trendSeries.length; i++)
		{
			if(( optionsMaskSettings & TrendModelType.GRAPH_MULTIPLIER) == TrendModelType.GRAPH_MULTIPLIER)
			{
				trendSeries[i].useMultiplier = true;
			}
			else
				trendSeries[i].useMultiplier = false;
		}
	}
			
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
public JFreeChart refresh(int newRendererType)
{
	//Plot setup
	com.jrefinery.chart.plot.Plot plot = null;
	rendererType = newRendererType;
	
	dataset = YukonDataSetFactory.createDataset( getTrendSeries(), getOptionsMaskSettings(), rendererType);
	
	if( rendererType == TrendModelType.LINE_VIEW|| rendererType == TrendModelType.SHAPES_LINE_VIEW)
	{
		com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator generator =
			 new com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator(dwellValuesDateTimeformat, valueFormat);

		XYItemRenderer rend = null;

		// Need to convert yukon TrendModelType into StandardXYItemRenderer type
		int type = 0;
		if( rendererType == TrendModelType.LINE_VIEW)
			type = StandardXYItemRenderer.LINES;
		else if( rendererType == TrendModelType.SHAPES_LINE_VIEW)
			type = StandardXYItemRenderer.SHAPES_AND_LINES;
		
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax(type, generator);
			((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues();
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
		
		rend.setDrawingSupplier(getDrawingSupplier(PRIMARY));
		
		plot = new com.jrefinery.chart.plot.XYPlot( (com.jrefinery.data.XYDataset)getPrimaryDataset(), (ValueAxis)getHorizontalAxis(), getPrimaryVerticalAxis(), rend);
		
		//Attempt to do multiple axis
		if(getDatasetCount(SECONDARY) > 0)
		{
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryDataset(getSecondaryDataset());
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());

			if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
			{
				rend = new com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax(type, generator);
				((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues();
			}
			else
			{
				rend = new StandardXYItemRenderer(type, generator);
			}
			rend.setDrawingSupplier(getDrawingSupplier(SECONDARY));
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRenderer(rend);
		}
	}
	else if( rendererType == TrendModelType.STEP_VIEW)
	{
		XYItemRenderer rend = null;
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
			((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues();
		}
		else
		{
			rend = new XYStepRenderer();
		}
		com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator generator =
			 new com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator(dwellValuesDateTimeformat, valueFormat);

		rend.setToolTipGenerator(generator);
		rend.setDrawingSupplier(getDrawingSupplier(PRIMARY));

		plot = new com.jrefinery.chart.plot.XYPlot( (com.jrefinery.data.XYDataset)getPrimaryDataset(), (ValueAxis)getHorizontalAxis(), getPrimaryVerticalAxis(), rend);

		//Attempt to do multiple axis
		if( getDatasetCount(SECONDARY) > 0)
		{
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryDataset(getSecondaryDataset());
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
			{
				rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
				((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = getDataset_MinMaxValues();
			}
			else
			{
				rend = new XYStepRenderer();
			}
			rend.setDrawingSupplier(getDrawingSupplier(SECONDARY));
			((com.jrefinery.chart.plot.XYPlot)plot).setSecondaryRenderer(rend);
		}
		
	}
	else if( rendererType == TrendModelType.BAR_VIEW)
	{
		CategoryItemRenderer rend = new VerticalBarRenderer(new com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator());
//		XYItemRenderer rend = new com.jrefinery.chart.renderer.ClusteredXYBarRenderer(.001, true);
		rend.setDrawingSupplier(getDrawingSupplier(PRIMARY));
//		plot = new com.jrefinery.chart.plot.XYPlot( (com.jrefinery.data.XYDataset)getPrimaryDataset(), (ValueAxis)getHorizontalAxis(), getPrimaryVerticalAxis(), rend);
		plot = new com.jrefinery.chart.plot.VerticalCategoryPlot( (DefaultCategoryDataset)getPrimaryDataset(), (CategoryAxis)getHorizontalAxis(), getPrimaryVerticalAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)

		if( getDatasetCount(SECONDARY) > 0)
		{
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryDataset(getSecondaryDataset());
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			rend = new com.jrefinery.chart.renderer.LineAndShapeRenderer(com.jrefinery.chart.renderer.LineAndShapeRenderer.LINES);
			rend.setDrawingSupplier(getDrawingSupplier(SECONDARY));
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRenderer(rend);
		}
	}
	else if( rendererType == TrendModelType.BAR_3D_VIEW)
	{

		CategoryItemRenderer rend = new VerticalBarRenderer3D(10, 10);

		rend.setDrawingSupplier(getPaintTable_BAR_Temporary(PRIMARY));
		plot = new com.jrefinery.chart.plot.VerticalCategoryPlot( (DefaultCategoryDataset)getPrimaryDataset(), (CategoryAxis)getHorizontalAxis(), getPrimaryVerticalAxis(), rend);

		//Attempt to do multiple axis
		//	FIX ME...Not able to do multiple bar axis, make lines instead (hopefully for not very long)
		if(getDatasetCount(SECONDARY) > 0)
		{
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryDataset(getSecondaryDataset());
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRangeAxis(getSecondaryVerticalAxis());
			rend = new com.jrefinery.chart.renderer.LineAndShapeRenderer(com.jrefinery.chart.renderer.LineAndShapeRenderer.LINES);
			rend.setDrawingSupplier(getDrawingSupplier(PRIMARY));
			((com.jrefinery.chart.plot.VerticalCategoryPlot)plot).setSecondaryRenderer(rend);
		}
	}
	else if( rendererType == TrendModelType.TABULAR_VIEW)
	{
		return null;
	}
	else if( rendererType == TrendModelType.SUMMARY_VIEW)
	{
		return null;
	}

	JFreeChart fChart = null;
	fChart = new JFreeChart(plot);

	fChart.setLegend( getLegend(fChart) );
	fChart.setTitle(getTitle());
	fChart.setSubtitles(getSubtitles());
	fChart.setBackgroundPaint(java.awt.Color.white);    
	return fChart;
 }

}

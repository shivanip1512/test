package com.cannontech.graph.model;

import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.Point;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.TimeSeriesCollection;

public class TrendModel implements com.cannontech.graph.GraphDataFormats
{
    private java.text.SimpleDateFormat TITLE_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMMMM dd, yyyy");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
	
	private int optionsMaskSettings = 0x00;
	private com.jrefinery.data.AbstractSeriesDataset dataset = null;
    private TrendSerie trendSeries[] = null;
    private java.util.Date startDate = null;
    private java.util.Date	stopDate = null;
    private Character autoScaleRight = new Character('Y');
    private Double rightScaleMin = new Double(0.0);
    private Double rightScaleMax = new Double(0.0);
    private Character autoScaleLeft = new Character('Y');
    private Double leftScaleMin = new Double(0.0);
    private Double leftScaleMax = new Double(0.0);
    
    private String chartName = "Yukon Trending";
    
    private Integer peakPointId = null;

/**
 * Constructor for TestFreeChart.
 * @param graphDefinition
 */

public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, int optionsMask)
{
	// Inititialize chart properties
	setStartDate(newGraphDef.getGraphDefinition().getStartDate());
	setStopDate(newGraphDef.getGraphDefinition().getStopDate());
	setChartName(newGraphDef.getGraphDefinition().getName());

	setAutoScaleRight(newGraphDef.getGraphDefinition().getAutoScaleRightAxis());
	setRightScaleMax(newGraphDef.getGraphDefinition().getRightMax());
	setRightScaleMin(newGraphDef.getGraphDefinition().getRightMin());

	setAutoScaleLeft(newGraphDef.getGraphDefinition().getAutoScaleLeftAxis());
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
		serie.setType(gds.getType().toString());
		serie.setTypeMask(gds.getTypeMask());
//		if( ((getOptionsMaskSettings() & TrendModelType.GRAPH_MULTIPLIER) == TrendModelType.GRAPH_MULTIPLIER))
//		{
			serie.setMultiplier(gds.getMultiplier());
//		}
		dsVector.add(serie);
	}

	if( !dsVector.isEmpty())
	{
		trendSeries = new TrendSerie[dsVector.size()];
		dsVector.toArray(trendSeries);
		hitDatabase_Basic(GraphDataSeries.NORMAL_QUERY_MASK);
		hitDatabase_Basic(GraphDataSeries.YESTERDAY_MASK);
		hitDatabase_Basic(GraphDataSeries.PEAK_VALUE_MASK);
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
	hitDatabase_Basic(GraphDataSeries.NORMAL_QUERY_MASK);
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

private java.awt.Color [] getDatasetColors(com.jrefinery.data.AbstractSeriesDataset dataset)
{
	java.awt.Color [] colors = null;
	if( getTrendSeries() != null)
	{
		colors = new java.awt.Color[dataset.getSeriesCount()];
		int colorCount = 0;
		for( int i = 0; i < trendSeries.length; i++)
		{
			TrendSerie serie = trendSeries[i];
			if( serie != null && ((serie.getTypeMask() & GraphDataSeries.VALID_INTERVAL_MASK) == serie.getTypeMask()))
			{
				if(serie.getColor() != null)
					colors[colorCount++] = serie.getColor();
			}
		}
	}
	return colors;
}

private com.jrefinery.chart.CategoryAxis getHorizontalCategoryAxis()
{
	com.jrefinery.chart.CategoryAxis catAxis = new com.jrefinery.chart.HorizontalCategoryAxis("Percent Duration");
	((com.jrefinery.chart.HorizontalCategoryAxis)catAxis).setVerticalCategoryLabels(false);
	((com.jrefinery.chart.HorizontalCategoryAxis)catAxis).setSkipCategoryLabelsToFit(true);
	catAxis.setTickMarksVisible(true);
	return catAxis;
}

private com.jrefinery.chart.DateAxis getHorizontalDateAxis()
{
	com.jrefinery.chart.DateAxis domainAxis = new com.jrefinery.chart.HorizontalDateAxis("Date/Time");
	domainAxis.setAutoRange(true);
	domainAxis.setAutoRange(false);
	domainAxis.setMaximumDate(getStopDate());
	domainAxis.setMinimumDate(getStartDate());
	domainAxis.setTickMarksVisible(true);	
	((com.jrefinery.chart.HorizontalDateAxis)domainAxis).setVerticalTickLabels(false);
	return domainAxis;
}
	
private com.jrefinery.chart.NumberAxis getHorizontalPercentAxis()
{
	com.jrefinery.chart.NumberAxis domainAxis = new com.jrefinery.chart.HorizontalNumberAxis("Percentage");
	domainAxis.setAutoRange(false);
	domainAxis.setMaximumAxisValue(100);
	domainAxis.setTickMarksVisible(true);	
	((com.jrefinery.chart.HorizontalNumberAxis)domainAxis).setVerticalTickLabels(false);
	return domainAxis;
}
	
private StringBuffer getSQLQueryString(int seriesTypeMask)
{
	
	java.util.Vector validIDs = new java.util.Vector(trendSeries.length);	//guess on max capacity
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( (trendSeries[i].getTypeMask() & seriesTypeMask) == trendSeries[i].getTypeMask())
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



private Integer getPeakPointId()
{
	if( peakPointId == null)
	{
		for (int i = 0; i < trendSeries.length; i++)
		{
			if( trendSeries[i].getType().equalsIgnoreCase(GraphDataSeries.PEAK_SERIES))
				peakPointId = trendSeries[i].getPointId();
		}
	}
	return peakPointId;
}			
private com.jrefinery.chart.StandardLegend getLegend(JFreeChart fChart)
{
	//Legend setup
	com.cannontech.jfreechart.chart.StandardLegend_VerticalItems legend = new com.cannontech.jfreechart.chart.StandardLegend_VerticalItems(fChart);
	legend.setAnchor(com.jrefinery.chart.Legend.SOUTH);
	legend.setItemFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 10));

	java.util.Vector stats = null;

	if( (getOptionsMaskSettings() & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK ||
		(getOptionsMaskSettings() & TrendModelType.LEGEND_MIN_MAX_MASK ) == TrendModelType.LEGEND_MIN_MAX_MASK)
	{
		int cnt = 0;
		stats = new java.util.Vector(dataset.getSeriesCount());
		for( int i = 0; i < trendSeries.length; i++)
		{
			String stat = "";					
			if(( trendSeries[i].getTypeMask() & GraphDataSeries.VALID_INTERVAL_MASK) == trendSeries[i].getTypeMask())
			{
				if ((getOptionsMaskSettings() & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK)
				{
					stat += "Load Factor: " + LF_FORMAT.format(trendSeries[i].getLoadFactor());
				}

				if( (getOptionsMaskSettings() & TrendModelType.LEGEND_MIN_MAX_MASK) == TrendModelType.LEGEND_MIN_MAX_MASK)
				{
					if( dataset.getMinValue(cnt).doubleValue() == Double.MAX_VALUE)
						stat += "    Min:  n/a";
					else
						stat += "    Min: " + MIN_MAX_FORMAT.format(dataset.getMinValue(cnt));
						
					if( dataset.getMaxValue(cnt).doubleValue() == Double.MIN_VALUE)
						stat += "    Max:  n/a";
					else
						stat += "    Max: " + MIN_MAX_FORMAT.format(dataset.getMaxValue(cnt));
					cnt++;
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
		legend.setStatsString(statsString);
	}	
	
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
private java.util.ArrayList getTitleList()
{
	//Chart Titles
	java.util.ArrayList titleList = new java.util.ArrayList();
	com.jrefinery.chart.TextTitle chartTitle = new com.jrefinery.chart.TextTitle( getChartName().toString());
	titleList.add(chartTitle);
	chartTitle = new com.jrefinery.chart.TextTitle(TITLE_DATE_FORMAT.format(getStartDate()) + " - " + TITLE_DATE_FORMAT.format(getStopDate()));	
    titleList.add(chartTitle);
    return titleList;
}

public TrendSerie[] getTrendSeries()
{
	return trendSeries;
}

private com.jrefinery.chart.NumberAxis getVerticalNumberAxis()
{
	//Vertical 'values' Axis setup
	com.jrefinery.chart.NumberAxis rangeAxis = new com.jrefinery.chart.VerticalNumberAxis("Reading");
	if( getAutoScaleLeft().equals(new Character('Y')))
		rangeAxis.setAutoRange(true);
	else
	{
		rangeAxis.setMaximumAxisValue(getLeftScaleMax().doubleValue());
		rangeAxis.setMinimumAxisValue(getLeftScaleMin().doubleValue());
	}
	rangeAxis.setTickMarksVisible(true);
	rangeAxis.setAutoRangeIncludesZero(false);
	return rangeAxis;
}

private com.jrefinery.chart.NumberAxis getVerticalNumberAxis3D()
{
	//Vertical 'values' Axis setup
	com.jrefinery.chart.NumberAxis rangeAxis = new com.jrefinery.chart.VerticalNumberAxis3D("Reading");
	if( getAutoScaleLeft().equals(new Character('Y')))
		rangeAxis.setAutoRange(true);
	else
	{
		rangeAxis.setMaximumAxisValue(getLeftScaleMax().doubleValue());
		rangeAxis.setMinimumAxisValue(getLeftScaleMin().doubleValue());
	}
	rangeAxis.setTickMarksVisible(true);
	rangeAxis.setAutoRangeIncludesZero(false);
	return rangeAxis;
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
private TrendSerie[] hitDatabase_Basic(int seriesTypeMask) 
{
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
			com.cannontech.clientutils.CTILogger.info("Executing:  " + sql.toString() );			
			pstmt = conn.prepareStatement(sql.toString());
			
			// YESTERDAY series type //
			long day = 0;			

			if ((seriesTypeMask & GraphDataSeries.YESTERDAY_MASK) == GraphDataSeries.YESTERDAY_MASK)
			{
				day = 86400000;
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() - day) );
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() - day) );
			}
			else if ((seriesTypeMask & GraphDataSeries.PEAK_VALUE_MASK) == GraphDataSeries.PEAK_VALUE_MASK)
			{
				for (int i = 0; i < trendSeries.length; i++)
				{
					if ((trendSeries[i].getTypeMask() & GraphDataSeries.PEAK_VALUE_MASK) ==  GraphDataSeries.PEAK_VALUE_MASK)
					{
						day = retrievePeakIntervalTranslateMillis(trendSeries[i].getPointId().intValue());
						pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() - day) );
						pstmt.setTimestamp(2, new java.sql.Timestamp( getStartDate().getTime() - day + 86400000) );
					}
				}
			}
			else
			{
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()) );
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );
			}
			
			rset = pstmt.executeQuery();
			
			com.jrefinery.data.TimeSeriesDataPair dataPair = null;
			java.util.Vector dataPairVector = new java.util.Vector(0);			
			int lastPointId = -1;
			
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
								&& (trendSeries[i].getTypeMask() & seriesTypeMask) == trendSeries[i].getTypeMask())
								trendSeries[i].setDataPairArray(dataPairArray);
						}
		
					}
					lastPointId = pointID;
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime() + day));
				double val = rset.getDouble(3);
				
				dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
				dataPairVector.add(dataPair);
								
			}
			if( !dataPairVector.isEmpty())
			{
				// Repeat the interval x # of days with Peak_interval data series.
				if ((seriesTypeMask & GraphDataSeries.PEAK_VALUE_MASK) == GraphDataSeries.PEAK_VALUE_MASK)
				{
					int size = dataPairVector.size();
					long numDays = (getStopDate().getTime() - getStartDate().getTime()) / 86400000;
					for ( long i = 1; i < numDays; i++)
					{
						for (int j = 0; j < size; j++)
						{
							double v = ((com.jrefinery.data.TimeSeriesDataPair)dataPairVector.get(j)).getValue().doubleValue();
							com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(((com.jrefinery.data.TimeSeriesDataPair)dataPairVector.get(j)).getPeriod().getStart()+ (86400000*i)));
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
							&& (trendSeries[i].getTypeMask() & seriesTypeMask) == trendSeries[i].getTypeMask())
						trendSeries[i].setDataPairArray(dataPairArray);
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
	}
	return trendSeries;
}

private long retrievePeakIntervalTranslateMillis(int peakIntervalPointID)
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
			if( ts.getTime() %86400 == 0)	//must have Day+1 00:00:00 instead of Day 00:00:01+
			{	
				cal.setTimeInMillis(ts.getTime());
				cal.roll(java.util.Calendar.DAY_OF_YEAR, false);
			}
			else
			{
				cal.setTimeInMillis(ts.getTime());
				cal.set(java.util.Calendar.HOUR, 0);
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

private void setAutoScaleLeft(Character newAutoScale)
{
	autoScaleLeft = newAutoScale;
}
private void setAutoScaleRight(Character newAutoScale)
{
	autoScaleRight = newAutoScale;
}
private void setLeftScaleMin(Double newMin)
{
	leftScaleMin = newMin;
}
private void setLeftScaleMax(Double newMax)
{
	leftScaleMax = newMax;
}
private void setRightScaleMin(Double newMin)
{
	rightScaleMin = newMin;
}
private void setRightScaleMax(Double newMax)
{
	rightScaleMax = newMax;
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
protected void setStartDate(java.util.Date newStartDate)
{
	startDate = newStartDate;
}

public void setOptionsMask(int newOptionsMask)
{
	optionsMaskSettings = newOptionsMask;

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
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
protected void setStopDate(java.util.Date newStopDate)
{
	stopDate = newStopDate;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public JFreeChart refresh(int rendererType)
{
	//Plot setup
	com.jrefinery.chart.Plot plot = null;
	
	if( rendererType == TrendModelType.LINE_VIEW|| rendererType == TrendModelType.SHAPES_LINE_VIEW)
	{
		com.jrefinery.chart.ValueAxis domainAxis = null;
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
		{
			dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries, getPeakPointId());
			domainAxis = getHorizontalPercentAxis();
		}
		else
		{
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);
			domainAxis = getHorizontalDateAxis();
		}

		com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator generator =
			 new com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator(com.cannontech.graph.GraphDataFormats.dwellValuesDateTimeformat, valueFormat);

		com.jrefinery.chart.XYItemRenderer rend = null;

		// Need to convert yukon TrendModelType into StandardXYItemRenderer type
		int type = 0;
		if( rendererType == TrendModelType.LINE_VIEW)
			type = com.jrefinery.chart.StandardXYItemRenderer.LINES;
		else if( rendererType == TrendModelType.SHAPES_LINE_VIEW)
			type = com.jrefinery.chart.StandardXYItemRenderer.SHAPES_AND_LINES;
		
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax(type, generator);
			((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues[dataset.getSeriesCount()];

			for ( int i = 0; i < dataset.getSeriesCount(); i++)
			{
				if(dataset.getMaxValue(i) != null && dataset.getMinValue(i) != null)
				{
					((com.cannontech.jfreechart.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues[i] = 
						new com.cannontech.jfreechart.chart.Dataset_MinMaxValues(dataset.getMinValue(i).doubleValue(), dataset.getMaxValue(i).doubleValue());
				}
			}
		}
		else
		{
			rend = new com.jrefinery.chart.StandardXYItemRenderer(type, generator);
		}
		//TimeSeriesCollection
//        com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm mavg = new com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm();
//        mavg.setPeriod(30);
//        com.jrefinery.chart.data.PlotFit pf = new com.jrefinery.chart.data.PlotFit((com.jrefinery.data.XYDataset)dataset, mavg);
//        dataset = (com.jrefinery.data.AbstractSeriesDataset)pf.getFit();
		
		plot = new com.jrefinery.chart.XYPlot( (com.jrefinery.data.XYDataset)dataset, domainAxis, getVerticalNumberAxis(), rend);
		
//        ((com.jrefinery.chart.XYPlot)plot).addRangeMarker(new com.jrefinery.chart.Marker(.64, java.awt.Color.blue));
//		
	}
	else if( rendererType == TrendModelType.STEP_VIEW)
	{
		com.jrefinery.chart.ValueAxis domainAxis = null;
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
		{
			dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries, getPeakPointId());
			domainAxis = getHorizontalPercentAxis();
		}
		else
		{
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);
			domainAxis = getHorizontalDateAxis();
		}

		com.jrefinery.chart.XYItemRenderer rend = null;
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.cannontech.jfreechart.chart.XYStepRenderer_MinMax(true);
			((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues = new com.cannontech.jfreechart.chart.Dataset_MinMaxValues[dataset.getSeriesCount()];			

			for ( int i = 0; i < dataset.getSeriesCount(); i++)
			{
				if( dataset.getMaxValue(i) != null && dataset.getMinValue(i) != null)
				{
					((com.cannontech.jfreechart.chart.XYStepRenderer_MinMax)rend).minMaxValues[i] = 
						new com.cannontech.jfreechart.chart.Dataset_MinMaxValues(dataset.getMinValue(i).doubleValue(), dataset.getMaxValue(i).doubleValue());
				}
			}
		}
		else
		{
			rend = new com.jrefinery.chart.XYStepRenderer();
		}

		plot = new com.jrefinery.chart.XYPlot( (com.jrefinery.data.XYDataset)dataset, domainAxis, getVerticalNumberAxis(), rend);	
	}
	else if( rendererType == TrendModelType.BAR_VIEW)
	{
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)		
			dataset = YukonDataSetFactory.createVerticalCategoryDataSet_LD(trendSeries, getPeakPointId());
		else
			dataset = YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);

		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.VerticalBarRenderer(new com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator());
		
		plot = new com.jrefinery.chart.VerticalCategoryPlot( (DefaultCategoryDataset)dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis(), rend);
	}
	else if( rendererType == TrendModelType.BAR_3D_VIEW)
	{
		if( (getOptionsMaskSettings()  & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)		
			dataset = YukonDataSetFactory.createVerticalCategoryDataSet_LD(trendSeries, getPeakPointId());
		else
			dataset = YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);

		
		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.VerticalBarRenderer3D(new com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator(), 10);
		plot = new com.jrefinery.chart.VerticalCategoryPlot( (DefaultCategoryDataset)dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis3D(), rend);
	}

	plot.setSeriesPaint(getDatasetColors(dataset));
//	plot.setSeriesPaint(2, java.awt.Color.black);
//	plot.setSeriesPaint(3, java.awt.Color.black);

		
	JFreeChart fChart = null;
	fChart = new JFreeChart(plot);//, com.jrefinery.chart.ChartFactory.createTimeSeriesChart("Yukon Trending Application", "Time", "Value", new com.jrefinery.data.TimeSeriesCollection(), true);
	
	fChart.setLegend( getLegend(fChart) );
	fChart.setTitles(getTitleList());
	fChart.setBackgroundPaint(java.awt.Color.white);    
	return fChart;
 }

}

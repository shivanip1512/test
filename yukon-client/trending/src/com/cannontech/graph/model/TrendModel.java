package com.cannontech.graph.model;

import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.point.Point;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.TimeSeriesCollection;

public class TrendModel implements com.cannontech.graph.GraphDataFormats
{
	private java.text.SimpleDateFormat minSecFormat = new java.text.SimpleDateFormat("mmss");
	private java.text.SimpleDateFormat hourFormat = new java.text.SimpleDateFormat("HH");
	private java.text.SimpleDateFormat dayInYearFormat = new java.text.SimpleDateFormat("D");
	private int optionsMaskSettings = 0x00;
	

	private com.jrefinery.data.AbstractSeriesDataset dataset = null;
    private TrendSerie trendSeries[] = null;
    private java.util.Date startDate = null;
    private java.util.Date	stopDate = null;
    private String chartName = "Yukon Trending";
    private java.text.SimpleDateFormat TITLE_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMMMM dd, yyyy");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
    
    private java.util.Date compareStartDate = null;
    private java.util.Date	compareStopDate = null;
    
    private Integer peakPointId = null;

/**
 * Constructor for TestFreeChart.
 * @param graphDefinition
 */

public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, int optionsMask)
{
	optionsMaskSettings = optionsMask;
	
	// Inititialize chart properties
	setStartDate(newGraphDef.getGraphDefinition().getStartDate());
	setStopDate(newGraphDef.getGraphDefinition().getStopDate());
	setChartName(newGraphDef.getGraphDefinition().getName());
	
	
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
		dsVector.add(serie);
	}

	if( !dsVector.isEmpty())
	{
		trendSeries = new TrendSerie[dsVector.size()];
		dsVector.toArray(trendSeries);
		hitDatabase_Basic();
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info(" GraphDefinition contains NO graphDataSeries Items");
	}
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
	hitDatabase_Basic();
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
			if( serie != null && serie.getType().equalsIgnoreCase("graph") && serie.getColor() != null)
			{
				if( (getOptionsMaskSettings() & TrendModelType.PLOT_YESTERDAY_MASK) == TrendModelType.PLOT_YESTERDAY_MASK)
				{
					java.awt.Color tempColor = new java.awt.Color(serie.getColor().getRed(),
									serie.getColor().getGreen(), 
									serie.getColor().getBlue(), 100);
					colors[colorCount++] = tempColor;
				}
//				if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_MINMAX_MASK) == TrendModelType.SHOW_MINMAX_MASK)
				{
//					java.awt.Color tempColor = java.awt.Color.black;
//					colors[colorCount++] = tempColor;
				}
				colors[colorCount++] = serie.getColor();
			}
		}
//		for( int i = 0; i < colors.length; i++)
//		{
//			if( colors[i] == null)
//				colors[i] = java.awt.Color.black;
//		}
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
	
private StringBuffer getSQLQueryString()
{
	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE "
	+ "FROM RAWPOINTHISTORY WHERE POINTID IN (");

	sql.append( trendSeries[0].getPointId() );
	for ( int i = 1; i < trendSeries.length; i ++)
	{
		sql.append(", " + ( trendSeries[i].getPointId()));

	}
	sql.append(")  AND ((TIMESTAMP > ? AND TIMESTAMP <= ? ) ");

	if( (getOptionsMaskSettings() & TrendModelType.PLOT_MULTIPLE_DAY_MASK) == TrendModelType.PLOT_MULTIPLE_DAY_MASK)
		sql.append(" OR ( TIMESTAMP > ? AND TIMESTAMP <= ? )");	

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
	com.jrefinery.chart.StandardLegend_VerticalItems legend = new com.jrefinery.chart.StandardLegend_VerticalItems(fChart);
	legend.setAnchor(com.jrefinery.chart.Legend.SOUTH);
	legend.setItemFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 10));

	java.util.Vector stats = null;
	if( (getOptionsMaskSettings() & TrendModelType.MULTIPLE_DAYS_LINE_MODEL) != TrendModelType.MULTIPLE_DAYS_LINE_MODEL)
	{
		if( (getOptionsMaskSettings() & TrendModelType.SHOW_LOAD_FACTOR_LEGEND_MASK) == TrendModelType.SHOW_LOAD_FACTOR_LEGEND_MASK ||
			(getOptionsMaskSettings() & TrendModelType.SHOW_MIN_MAX_LEGEND_MASK) == TrendModelType.SHOW_MIN_MAX_LEGEND_MASK )
		{
			if( stats == null)
			stats = new java.util.Vector(trendSeries.length);
			for( int i = 0; i < trendSeries.length; i++)
			{
				if( trendSeries[i].getType().equalsIgnoreCase("graph"))
				{
					String stat = "";
					if ((getOptionsMaskSettings() & TrendModelType.SHOW_LOAD_FACTOR_LEGEND_MASK) == TrendModelType.SHOW_LOAD_FACTOR_LEGEND_MASK)
					{
						stat += "Load Factor: " + LF_FORMAT.format(trendSeries[i].getLoadFactor());
					}
					if( (getOptionsMaskSettings() & TrendModelType.SHOW_MIN_MAX_LEGEND_MASK) == TrendModelType.SHOW_MIN_MAX_LEGEND_MASK)
					{
						stat += "    Min: " + MIN_MAX_FORMAT.format(trendSeries[i].getMinimumValue());
						stat += "    Max: " + MIN_MAX_FORMAT.format(trendSeries[i].getMaximumValue());
					}
					stats.add(stat);
				}
			}
		}
	}
//	if( (getOptionsMaskSettings() & TrendModelType.SHOW_MIN_MAX_LEGEND_MASK) == TrendModelType.SHOW_MIN_MAX_LEGEND_MASK)
//	{
//		if( stats == null)
//			stats = new java.util.Vector(trendSeries.length);
//		for( int i = 0; i < trendSeries.length; i++)
//		{
//			if( trendSeries[i].getType().equalsIgnoreCase("graph"))
//			{
//				String stat = "";
//				if( stats.get(i) == null)
//				{
//					stat = "   Min: " + MIN_MAX_FORMAT.format(trendSeries[i].getMinimumValue());
//					stat += "   Max: " + MIN_MAX_FORMAT.format(trendSeries[i].getMaximumValue());
//					stats.add(stat);					
//				}
//				else
//				{
//					stat = (String)stats.get(i);
//					stat += "   Min: " + MIN_MAX_FORMAT.format(trendSeries[i].getMinimumValue());
//					stat += "   Max: " + MIN_MAX_FORMAT.format(trendSeries[i].getMaximumValue());
//					stats.set(i, stat);
//				}
//			}
//		}
//	}

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

public java.util.Date getCompareStartDate()
{
	if( compareStartDate == null)
	{
		compareStartDate = com.cannontech.util.ServletUtil.getYesterday();
		return compareStartDate;
	}
	return compareStartDate;
//	return (new java.util.Date(startDate.getTime() - ( 86400000 * 5)));
}

public java.util.Date getCompareStopDate()
{
	if( compareStopDate == null)
	{
		compareStopDate = com.cannontech.util.ServletUtil.getToday();
		return compareStopDate;
	}
	return compareStopDate;
//	return (new java.util.Date(startDate.getTime() - ( 86400000 * 4)));
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
/*
public TrendSerie getTrendSerie(int pointId)
{
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( trendSeries[i].getPointId().intValue() == pointId)
			return trendSeries[i];
	}
	return null;	//failed...pointId not found!!!
}

public TrendSerie[] getTrendSeries(int pointId)
{
	java.util.Vector trendSerieVector = new java.util.Vector(trendSeries.length); 
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( trendSeries[i].getPointId().intValue() == pointId)
			trendSerieVector.add(trendSeries);
//			return trendSeries[i];
	}
	
	if( trendSerieVector != null)
	{
		TrendSerie [] commonTrendSeries = new TrendSerie[trendSerieVector.size()];
		for (int i = 0; i < trendSerieVector.size(); i++)
		{
			commonTrendSeries[i] = (TrendSerie)trendSerieVector.get(i);
		}
		return commonTrendSeries;
	}
	return null;	//failed...pointId not found!!!
}
*/

public TrendSerie[] getTrendSeries()
{
	return trendSeries;
}

private com.jrefinery.chart.NumberAxis getVerticalNumberAxis()
{
	//Vertical 'values' Axis setup
	com.jrefinery.chart.NumberAxis rangeAxis = new com.jrefinery.chart.VerticalNumberAxis("Reading");
	rangeAxis.setAutoRange(true);
	rangeAxis.setTickMarksVisible(true);
	rangeAxis.setAutoRangeIncludesZero(false);
	return rangeAxis;
}

private com.jrefinery.chart.NumberAxis getVerticalNumberAxis3D()
{
	//Vertical 'values' Axis setup
	com.jrefinery.chart.NumberAxis rangeAxis = new com.jrefinery.chart.VerticalNumberAxis3D("Reading");
	rangeAxis.setAutoRange(true);
	rangeAxis.setTickMarksVisible(true);
	rangeAxis.setAutoRangeIncludesZero(false);
	return rangeAxis;
}
/**
 * Get all data series with valid "graph" types.
 * Return type: Vector (of graphDataSeries)
 * Creation date: (10/3/00 5:53:52 PM)
 */
/*
private Vector getGraphDataSeriesVector()
{ 
	Vector dsVector = new Vector(5);	//some small initial capactiy

	for (int i = 0; i < (gDef.getGraphDataSeries().size()); i++)
	{
		GraphDataSeries gds = (GraphDataSeries)gDef.getGraphDataSeries().get(i);
		if( gds.getType().equalsIgnoreCase("graph"))
			dsVector.add(gds);
	}
	return dsVector;
}
*/
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
private TrendSerie[] hitDatabase_Basic() 
{
	if( trendSeries == null)
		return null;

	StringBuffer sql = getSQLQueryString();
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
			/* (Remove) MULTIPLIER setup //
			java.util.HashMap ptMultHashMap = null;
			if( (getOptionsMaskSettings() & TrendModelType.MULTIPLIER_MASK) == TrendModelType.MULTIPLIER_MASK)		
			{
				com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
				synchronized(cache)
				{
					ptMultHashMap = cache.getAllPointidMultiplierHashMap();
				}
			}	*/
			
			pstmt = conn.prepareStatement(sql.toString());
			
			// Show YESTERDAY setup //
			long day = 0;			
			if( (getOptionsMaskSettings() & TrendModelType.PLOT_YESTERDAY_MASK) == TrendModelType.PLOT_YESTERDAY_MASK)
			{
				day = 86400000;
				setCompareStartDate( new java.util.Date (getStartDate().getTime()  - day));
				setCompareStopDate( new java.util.Date (getStopDate().getTime()  - day));
				pstmt.setTimestamp(1, new java.sql.Timestamp( getCompareStartDate().getTime()) );
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );
			}
			else
			{
				setCompareStartDate( new java.util.Date (getStartDate().getTime()));
				setCompareStopDate( new java.util.Date (getStopDate().getTime()));
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()) );
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );
			}

			if( (getOptionsMaskSettings() & TrendModelType.PLOT_MULTIPLE_DAY_MASK) == TrendModelType.PLOT_MULTIPLE_DAY_MASK)
			{
				pstmt.setTimestamp(3, new java.sql.Timestamp( getCompareStartDate().getTime() ));
				pstmt.setTimestamp(4, new java.sql.Timestamp( getCompareStopDate().getTime()));
			}

			rset = pstmt.executeQuery();
			
			com.cannontech.clientutils.CTILogger.info("Executing:  " + sql.toString() );

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
							if( trendSeries[i].getPointId().intValue() == lastPointId)
								trendSeries[i].setDataPairArray(dataPairArray);
						}
					}
					lastPointId = pointID;
				}
				
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime()));
				double val = rset.getDouble(3);
				
				/* take out multiplier if necessary.//
				if( ptMultHashMap != null)
				{
					Double multiplier = (Double)ptMultHashMap.get(new Integer(pointID));
					if( multiplier != null)
					{
						val = val / multiplier.doubleValue();
					}
				}	*/
				
				dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
				dataPairVector.add(dataPair);
								
			}

			if( !dataPairVector.isEmpty())
			{
				com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
					new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
				dataPairVector.toArray(dataPairArray);
				dataPairVector.clear();			

				for (int i = 0; i < getTrendSeries().length; i++)
				{
					if( trendSeries[i].getPointId().intValue() == lastPointId)
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
protected void setStartDate(java.util.Date newStartDate)
{
	startDate = newStartDate;
}

/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
protected void setCompareStopDate(java.util.Date newStopDate)
{
	compareStopDate = newStopDate;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
protected void setCompareStartDate(java.util.Date newStartDate)
{
	compareStartDate = newStartDate;
}

public void setOptionsMask(int newOptionsMask)
{
	optionsMaskSettings = newOptionsMask;
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
	
	if( rendererType == TrendModelType.LINE_MODEL || rendererType == TrendModelType.SHAPES_LINE_MODEL)
	{
		if( (getOptionsMaskSettings() & TrendModelType.PLOT_YESTERDAY_MASK) == TrendModelType.PLOT_YESTERDAY_MASK)
			dataset = YukonDataSetFactory.createMultipleDaysDataSet(trendSeries, getStartDate(), getCompareStartDate());
		else
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);

		com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator generator = new com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator(TITLE_DATE_FORMAT, valueFormat);

		com.jrefinery.chart.XYItemRenderer rend = null;
		// Need to convert yukon TrendModelType into StandardXYItemRenderer type
		int type = 0;
		if( rendererType == TrendModelType.LINE_MODEL)
			type = com.jrefinery.chart.StandardXYItemRenderer.LINES;
		else if( rendererType == TrendModelType.SHAPES_LINE_MODEL)
			type = com.jrefinery.chart.StandardXYItemRenderer.SHAPES_AND_LINES;
		
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.jrefinery.chart.StandardXYItemRenderer_MinMax(type, generator);
			((com.jrefinery.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues = new com.jrefinery.chart.XYPlot_MinMaxValues[dataset.getSeriesCount()];
			int validCount= 0;
			for ( int i = 0; i < trendSeries.length; i++)
			{
				if( trendSeries[i] != null)
				{
					if( trendSeries[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
					{
						((com.jrefinery.chart.StandardXYItemRenderer_MinMax)rend).minMaxValues[validCount] = 
							new com.jrefinery.chart.XYPlot_MinMaxValues(trendSeries[i].getMinimumValue(), trendSeries[i].getMaximumValue());
						validCount++;
					}
				}
			}
		}
		else
		{
			rend = new com.jrefinery.chart.StandardXYItemRenderer(type, generator);
		}

		plot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), rend);		
	}
	else if( rendererType == TrendModelType.STEP_MODEL)
	{
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_YESTERDAY_MASK) == TrendModelType.PLOT_YESTERDAY_MASK)
			dataset = YukonDataSetFactory.createMultipleDaysDataSet(trendSeries, getStartDate(), getCompareStartDate());
		else
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);
		
		com.jrefinery.chart.XYStepRenderer rend = null;
		if( (getOptionsMaskSettings()  & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		{
			rend = new com.jrefinery.chart.XYStepRenderer_MinMax(true);
			((com.jrefinery.chart.XYStepRenderer_MinMax)rend).minMaxValues = new com.jrefinery.chart.XYPlot_MinMaxValues[dataset.getSeriesCount()];			
			int validCount= 0;
			for ( int i = 0; i < trendSeries.length; i++)
			{
				if( trendSeries[i] != null)
				{
					if( trendSeries[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
					{
						((com.jrefinery.chart.XYStepRenderer_MinMax)rend).minMaxValues[validCount] = 
							new com.jrefinery.chart.XYPlot_MinMaxValues(trendSeries[i].getMinimumValue(), trendSeries[i].getMaximumValue());
						validCount++;
					}
				}
			}
		}
		else
		{
			rend = new com.jrefinery.chart.XYStepRenderer();
		}

		plot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), rend);	
	}
	else if( rendererType == TrendModelType.BAR_MODEL)
	{
		dataset = YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);

		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.VerticalBarRenderer(new com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator());
		plot = new com.jrefinery.chart.VerticalCategoryPlot( (DefaultCategoryDataset)dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis(), rend);
	}
	else if( rendererType == TrendModelType.BAR_3D_MODEL)
	{
		dataset = YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);
		
		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.VerticalBarRenderer3D(new com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator(), 10);
		plot = new com.jrefinery.chart.VerticalCategoryPlot( (DefaultCategoryDataset)dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis3D(), rend);
	}
	else if( rendererType == TrendModelType.LOAD_DURATION_LINE_MODEL)
	{
		dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries, getPeakPointId());
		com.jrefinery.chart.StandardXYItemRenderer rend = new com.jrefinery.chart.StandardXYItemRenderer(com.jrefinery.chart.StandardXYItemRenderer.LINES);
		plot = new com.jrefinery.chart.XYPlot((com.jrefinery.data.XYDataset)dataset, getHorizontalPercentAxis(),getVerticalNumberAxis(), rend);
	}
	else if( rendererType == TrendModelType.LOAD_DURATION_STEP_MODEL)
	{
		dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries, getPeakPointId());
		com.jrefinery.chart.XYStepRenderer rend = new com.jrefinery.chart.XYStepRenderer();
		plot = new com.jrefinery.chart.XYPlot((com.jrefinery.data.XYDataset) dataset, getHorizontalPercentAxis(), getVerticalNumberAxis(), rend);
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

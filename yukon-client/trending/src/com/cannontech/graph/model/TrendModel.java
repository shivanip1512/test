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
	private int OPTIONS_MASK_SETTINGS = 0x00;
	

    private TrendSerie trendSeries[] = null;
    private java.util.Date startDate = null;
    private java.util.Date	stopDate = null;
    private String chartName = "Yukon Trending";
    private java.text.SimpleDateFormat TITLE_DATE_FORMAT = new java.text.SimpleDateFormat("EEE MMMMM dd, yyyy");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
    
    private java.util.Date compareStartDate = null;
    private java.util.Date	compareStopDate = null;

/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
private TrendSerie[] hitDatabase_LoadDuration() 
{
	if( trendSeries == null)
		return null;

	StringBuffer sql = getSQLQueryString();
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	long timer = System.currentTimeMillis();

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
			if( (OPTIONS_MASK_SETTINGS & TrendModelType.MULTIPLIER_MASK) == TrendModelType.MULTIPLIER_MASK)		
			{
				com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
				synchronized(cache)
				{
					ptMultHashMap = cache.getAllPointidMultiplierHashMap();
				}
			}	*/
			
			pstmt = conn.prepareStatement(sql.toString(), java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
			pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()) );
			pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );
			rset = pstmt.executeQuery();
			
			com.cannontech.clientutils.CTILogger.info("Executing:  " + sql.toString() );
				
			// Fields used in sorting out just the LD readings we are looking for.	
			double currentHour = 0; //The hour that we are currently looking for (0 - 23) 
			double tempHour = Double.MIN_VALUE; //the current hourly value of the current series timestamp.
			double tempMaxValue = 0;	//the current Maximum value of the hourly series.

			java.sql.Timestamp tempX = null;//the current timestamp of the series of tempMaxValue
			com.jrefinery.data.TimePeriod tempTP = null;//the current timestamp of the series of tempMaxValue


			int lastPointId = -1;
			int pointCount = 0;

			com.jrefinery.data.TimeSeriesDataPair dataPair = null;
			java.util.Vector dataPairVector = new java.util.Vector(0);			

			while( rset.next() )
			{
				int pointID = rset.getInt(1);
				if( pointID != lastPointId )
				{
					if( lastPointId != -1 )
					{
						com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
						dataPairVector.toArray(dataPairArray);
						dataPairVector.clear();
						
						getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
						pointCount++;
					}

					currentHour = 0; //The hour that we are currently looking for (0 - 23) 
					tempHour = 0; //keeps track of the current hourly value of the current series timestamp.
					tempMaxValue = Double.MIN_VALUE;
					tempX = null;

					lastPointId =  pointID;
					pointCount++;
// keep this!!!					currentDecimals = getDecimalPlaces( pointIndex);
				} 
			
				java.sql.Timestamp ts = rset.getTimestamp(2);
				com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime()));
				double val = rset.getDouble(3);

				/* take out multiplier if necessary.
				if( ptMultHashMap != null)
				{
					Double multiplier = (Double)ptMultHashMap.get(new Integer(pointID));
					if( multiplier != null)
					{
						val = val / multiplier.doubleValue();
					}
				}	*/
				
				tempTP = tp;
				tempX = ts;		// a way to init the tempX field for a non-normal query (no 0 hour existing or something like that)

				if (currentHour > 23)
					currentHour = 0; //reset for the next day (set to 00 hours)
						
				// Get the timestamp(xSeries) HOUR
				tempHour = (new Double(hourFormat.format(ts))).doubleValue();
				
				// Get the timestamp(xSeries) MINSEC combination
				double mmss = (new Double(minSecFormat.format(ts))).doubleValue();

				// Find the maximum value for the hour (00:00:01 - 01:00:00)
				if (tempHour == currentHour) // in the same hour timeframe still.
				{
					if (val > tempMaxValue)
					{
						tempMaxValue = val;
						tempX = ts;
						tempTP = tp;
					}
				}
				else if (tempHour == (currentHour + 1) ||
							tempHour == (currentHour - 23)) // the top of the hour (HH:00:00)
				{
					// check that the mins and secs are 00 too.
					if (mmss == 0)
					{
						if (val > tempMaxValue)
						{
							tempMaxValue = val;
							tempX = ts;
							tempTP =tp;
						}

						// Save values, this was the last chance for this hour.
						dataPair = new com.jrefinery.data.TimeSeriesDataPair(tempTP, val);
						dataPairVector.add(dataPair);
					}
					else // back up and loop through this one again with the new hour!
					{
						// Save values, this was the last chance for this hour.
						dataPair = new com.jrefinery.data.TimeSeriesDataPair(tempTP, val);
						dataPairVector.add(dataPair);

						// get this value again, didn't qualify this round!
						rset.previous();
					}
					
					// Will be getting a new MAX value and getting data for a new hour.
					tempMaxValue = 0;
					currentHour++; // move on to the next hour.
					
				}// end else if ( tempHour == (currentHour + 1) || tempHour == (currentHour - 23))
				else
				{
					// Save values, this was the last chance for this hour.
					dataPair = new com.jrefinery.data.TimeSeriesDataPair(tempTP, val);
					dataPairVector.add(dataPair);

					tempMaxValue = 0;
					currentHour = tempHour;
					
					// get this value again, didn't qualify this round!
					tempMaxValue = val;
					tempX = ts;
					tempTP = tp;
					
				}// end else
				
//				valueFormat.setMinimumFractionDigits(currentDecimals);
//				valueFormat.setMaximumFractionDigits(currentDecimals);
							
			}//end while( rset.next() )

			// If two(or more) point id's are equal, just copy all the data over
			//  to the next one, don't loop through the whole process.

			for  ( int i = 0; i + 1< trendSeries.length; i++)
			{
				if ( trendSeries[i].getPointId().intValue() == trendSeries[i + 1].getPointId().intValue() )
				{
					trendSeries[i + 1].setDataPairArray(trendSeries[i].getDataPairArray());
					pointCount++;
				}
			}
			
			if( !dataPairVector.isEmpty())
			{
				com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
					new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
				dataPairVector.toArray(dataPairArray);
				dataPairVector.clear();			
	
				getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
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
		}	
	}
	
//	valueFormat.setGroupingUsed(true);
//	com.cannontech.clientutils.CTILogger.info(" @HIT DATABASE: Took " + (System.currentTimeMillis() - timer) + " millis to update DataViewModel.");
return trendSeries;
}

private TrendSerie[] hitDatabase_OverlaidDays() 
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
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ) );
			pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );
			rset = pstmt.executeQuery();
			
			com.cannontech.clientutils.CTILogger.info("Executing:  " + sql.toString() );

			com.jrefinery.data.TimeSeriesDataPair dataPair = null;

			java.util.Vector dataPairVector = new java.util.Vector(0);			
			int lastPointId = -1;
			int pointCount = 0;
			
			int currentDayInYear = 0;
			int lastDayInYear = -1;
						
			while( rset.next() )
			{
				int pointID = rset.getInt(1);
				java.sql.Timestamp ts = rset.getTimestamp(2);
				currentDayInYear = new Integer( dayInYearFormat.format(new java.util.Date(ts.getTime()))).intValue();
				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						//Save the data you've collected into the array of models (chartmodelsArray).
						com.jrefinery.data.TimeSeriesDataPair[] dataPairArray =
							new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
						dataPairVector.toArray(dataPairArray);
						dataPairVector.clear();
						
						getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
						pointCount++;
						lastDayInYear = currentDayInYear;						
					}
					lastPointId = pointID;
				}
				
				else
				{
					if( lastDayInYear == -1)
						lastDayInYear = currentDayInYear;
						
					if( currentDayInYear == lastDayInYear)
					{
						com.cannontech.clientutils.CTILogger.info(" current = last");
						//good 
					}
					else if( currentDayInYear == lastDayInYear + 1)
					{
						int mmss = new Integer( minSecFormat.format(new java.util.Date(ts.getTime()))).intValue();
						if( mmss == 0)
						{
							//good
							com.cannontech.clientutils.CTILogger.info(" current = last + 1 and mmss");
						}
						else
						{
							com.cannontech.clientutils.CTILogger.info(" DONE WITH TODAY ");
							com.jrefinery.data.TimeSeriesDataPair[] dataPairArray =
								new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
							dataPairVector.toArray(dataPairArray);
							dataPairVector.clear();
							
							getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
							pointCount++;
							lastDayInYear = currentDayInYear;							
						}
					}
					else
					{
						com.cannontech.clientutils.CTILogger.info(" %%%%%%%ELSE%%%%%%%%%%%%");						
					}
//					lastDayInYear = currentDayInYear;
				}

				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime()));
				double val = rset.getDouble(3);
				dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
				dataPairVector.add(dataPair);
								
			}

			for  ( int i = 0; i + 1< trendSeries.length; i++)
			{
				if ( trendSeries[i].getPointId().intValue() == trendSeries[i + 1].getPointId().intValue() )
				{
					trendSeries[i + 1].setDataPairArray(trendSeries[i].getDataPairArray());
					pointCount++;
				}
			}

			if( !dataPairVector.isEmpty())
			{
				com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
					new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
				dataPairVector.toArray(dataPairArray);
				dataPairVector.clear();			
	
				getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
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
}/**
 * Constructor for TestFreeChart.
 * @param graphDefinition
 */

public TrendModel(com.cannontech.database.data.graph.GraphDefinition newGraphDef, int optionsMask)
{
	OPTIONS_MASK_SETTINGS = optionsMask;
	
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
				if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_YESTERDAY_MASK) == TrendModelType.SHOW_YESTERDAY_MASK)
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

	if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_MULTIPLE_DAY_MASK) == TrendModelType.SHOW_MULTIPLE_DAY_MASK)
		sql.append(" OR ( TIMESTAMP > ? AND TIMESTAMP <= ? )");	

	sql.append(" ) ORDER BY POINTID, TIMESTAMP");
	
	return sql;	
}

private com.jrefinery.chart.StandardLegend getLegend(JFreeChart fChart)
{
	//Legend setup
	com.jrefinery.chart.StandardLegend_VerticalItems legend = new com.jrefinery.chart.StandardLegend_VerticalItems(fChart);
	legend.setAnchor(com.jrefinery.chart.Legend.SOUTH);
	legend.setItemFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 10));
	
	if( (OPTIONS_MASK_SETTINGS & TrendModelType.LOAD_FACTOR_MASK) == TrendModelType.LOAD_FACTOR_MASK)
	{
		java.util.Vector stats = new java.util.Vector(trendSeries.length);
		for( int i = 0; i < trendSeries.length; i++)
		{
			if( trendSeries[i].getType().equalsIgnoreCase("graph"))
			{
				String stat = "Load Factor: " + LF_FORMAT.format(trendSeries[i].getLoadFactor());
				stat += "    Min: " + MIN_MAX_FORMAT.format(trendSeries[i].getMinimumValue());
				stat += "    Max: " + MIN_MAX_FORMAT.format(trendSeries[i].getMaximumValue());
				stats.add(stat);
			}
		}
		String []statsString = new String[stats.size()];
		for ( int i = 0; i < stats.size(); i++)
		{
			statsString[i] = (String)stats.get(i);
		}
		legend.setStatsString(statsString);
	}
	return legend;
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

public TrendSerie getTrendSerie(int pointId)
{
	for (int i = 0; i < trendSeries.length; i++)
	{
		if( trendSeries[i].getPointId().intValue() == pointId)
			return trendSeries[i];
	}
	return null;	//failed...pointId not found!!!
}

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
			if( (OPTIONS_MASK_SETTINGS & TrendModelType.MULTIPLIER_MASK) == TrendModelType.MULTIPLIER_MASK)		
			{
				com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
				synchronized(cache)
				{
					ptMultHashMap = cache.getAllPointidMultiplierHashMap();
				}
			}	*/
			
			// Show YESTERDAY setup //
			long day = 0;			
//			if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_YESTERDAY_MASK) == TrendModelType.SHOW_YESTERDAY_MASK)
//				day = 86400000;
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() - day) );
			pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()) );

			if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_MULTIPLE_DAY_MASK) == TrendModelType.SHOW_MULTIPLE_DAY_MASK)
			{
				pstmt.setTimestamp(3, new java.sql.Timestamp( getCompareStartDate().getTime() ));
				pstmt.setTimestamp(4, new java.sql.Timestamp( getCompareStopDate().getTime()));
			}

			rset = pstmt.executeQuery();
			
			com.cannontech.clientutils.CTILogger.info("Executing:  " + sql.toString() );

			com.jrefinery.data.TimeSeriesDataPair dataPair = null;

			java.util.Vector dataPairVector = new java.util.Vector(0);			
			int lastPointId = -1;
			int pointCount = 0;
			
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
						
						getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
						pointCount++;
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

			for  ( int i = 0; i + 1< trendSeries.length; i++)
			{
				if ( trendSeries[i].getPointId().intValue() == trendSeries[i + 1].getPointId().intValue() )
				{
					trendSeries[i + 1].setDataPairArray(trendSeries[i].getDataPairArray());
					pointCount++;
				}
			}

			if( !dataPairVector.isEmpty())
			{
				com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
					new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
				dataPairVector.toArray(dataPairArray);
				dataPairVector.clear();			
	
				getTrendSerie(lastPointId).setDataPairArray(dataPairArray);
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
public JFreeChart setupFreeChart(int rendererType)
{
	com.jrefinery.data.AbstractSeriesDataset dataset = null;
	
	//Plot setup
	com.jrefinery.chart.Plot plot = null;
	trendSeries = hitDatabase_Basic();	
	
	if( rendererType == TrendModelType.LINE_MODEL)
	{
		if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_YESTERDAY_MASK) == TrendModelType.SHOW_YESTERDAY_MASK)
			dataset = YukonDataSetFactory.createMultipleDaysDataSet(trendSeries, getStartDate(), getCompareStartDate());
		else
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);

		com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator generator = new com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator(TITLE_DATE_FORMAT, valueFormat);
		com.jrefinery.chart.XYItemRenderer rend = new com.jrefinery.chart.StandardXYItemRenderer(com.jrefinery.chart.StandardXYItemRenderer.LINES, generator);
		
		plot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), rend);		

//		com.jrefinery.chart.XYPlot subPlot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), rend);
//		plot = new com.jrefinery.chart.OverlaidXYPlot(getHorizontalDateAxis(), getVerticalNumberAxis());
//		((com.jrefinery.chart.OverlaidXYPlot)plot).add(subPlot);
//		subPlot.setSeriesPaint(getDatasetColors(dataset));
				
/*		dataset = YukonDataSetFactory.createMinMaxDataSetSeries(trendSeries);
		rend = new com.jrefinery.chart.StandardXYItemRenderer(com.jrefinery.chart.StandardXYItemRenderer.SHAPES, generator);
        ((com.jrefinery.chart.StandardXYItemRenderer)rend).setPlotShapes(true);
		((com.jrefinery.chart.StandardXYItemRenderer)rend).setDefaultShapeFilled(true);
		((com.jrefinery.chart.StandardXYItemRenderer)rend).setDefaultShapeScale(5.0);

		subPlot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), rend);
		subPlot.setSeriesPaint(getDatasetColors(dataset));
		((com.jrefinery.chart.OverlaidXYPlot)plot).add(subPlot);
*/		

	}
	else if( rendererType == TrendModelType.STEP_MODEL)
	{
		if( (OPTIONS_MASK_SETTINGS & TrendModelType.SHOW_YESTERDAY_MASK) == TrendModelType.SHOW_YESTERDAY_MASK)
			dataset = YukonDataSetFactory.createMultipleDaysDataSet(trendSeries, getStartDate(), getCompareStartDate());
		else
			dataset = YukonDataSetFactory.createBasicDataSet(trendSeries);
		plot = new com.jrefinery.chart.XYPlot( (TimeSeriesCollection)dataset, getHorizontalDateAxis(), getVerticalNumberAxis(), new com.jrefinery.chart.XYStepRenderer());	
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
		dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries);
		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.LineAndShapeRenderer(com.jrefinery.chart.LineAndShapeRenderer.LINES);
		plot = new com.jrefinery.chart.VerticalCategoryPlot( (DefaultCategoryDataset)dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis(), rend);
	}
	else if( rendererType == TrendModelType.LOAD_DURATION_STEP_MODEL)
	{
		dataset = YukonDataSetFactory.createLoadDurationDataSet(trendSeries);
		com.jrefinery.chart.CategoryItemRenderer rend = new com.jrefinery.chart.StepLineRenderer(com.jrefinery.chart.StepLineRenderer.STEPS);
		plot = new com.jrefinery.chart.VerticalCategoryPlot((DefaultCategoryDataset) dataset, getHorizontalCategoryAxis(), getVerticalNumberAxis(), rend);
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

package com.cannontech.graph.model;

/**
 * A ChartDataModel that will retrieve data for a number of points
 * over a time interval.
 *
 * Beyond the basic ChartDataModel it stores the quality, minimums,
 * maximums, and colors of each data series.
 *
 * See hitDatabase() for the interesting jdbc code.
 *
 * Creation date: (10/3/00 3:26:42 PM)
 * @author:  Aaron Lauinger 
 */
//import com.cannontech.graph.model.GraphModelType.*;
//import com.cannontech.graph.GraphDataFormats;
public class DataViewModel extends GraphModel
{
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 10:20:56 AM)
 */
public void addToYSeries() {}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 10:20:56 AM)
 */
public void addToYSeries(int series, int index, double newValue )
{
	double[]temp = new double[ySeries[series].length];
	ySeries[series] = new double[ySeries[series].length + 1];

	ySeries[series] = temp;
	ySeries[series][ySeries[series].length + 1] = newValue;
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
public void hitDatabase() 
{
	// There should be points.length number of series from the database
	initGraphModelFields(points.length);	//initialize all of the [][]arrays of GraphModel

	//Database call to retrieve the decimal places for each point.
	retrieveDecimalPlaces();

	long timer = System.currentTimeMillis();
	valueFormat.setGroupingUsed(false);
	
	StringBuffer sql = new StringBuffer("SELECT PointID,TimeStamp,Value,Quality FROM RawPointHistory WHERE ( TimeStamp > '" + toOracleDateString(startDate) + "' AND TimeStamp <= '" + toOracleDateString(endDate) + "' ) AND (");

	//Make sure the points array is sorted so that when the results come back
	//they are in the same order - will make it easier to retrieve the data
	//java.util.Arrays.sort(points);
	
	if( points.length > 0  )
	{
		sql.append(" PointID=");
		sql.append(points[0]);		
	}
		
	for( int i = 1; i < points.length; i++ )
	{
		sql.append(" OR PointID=");
		sql.append(points[i]);		
	}

	sql.append(" ) ORDER BY PointID, TimeStamp");
		
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			return;
		}
		else
		{
			System.out.println("Executing:  " + sql.toString() );

			long queryTimer = System.currentTimeMillis();	
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());

			System.out.println(" @HIT DATABASE: Took " + (System.currentTimeMillis() - queryTimer) + " millis to execute query.");
			
			// We don't yet know how many x and y values will come back
			//  for each series so store them in these lists and then
			//  we'll copy them into the permanent arrays when we know
			java.util.ArrayList xVals = new java.util.ArrayList();
			java.util.ArrayList yVals = new java.util.ArrayList();
			java.util.ArrayList yQual = new java.util.ArrayList();
			
			long currentPointID = Long.MIN_VALUE;
			boolean firstPass = true;

			int pointCount = -1;

			double currentMaxArea = 0;
			double currentAreaOfSet = 0;
			int currentDecimals = 0;
			int pointIndex = 0;
			
			double currentMin = Double.MAX_VALUE;
			double currentMax = Double.MIN_VALUE;
				
			while( rset.next() )
			{
				long pointID = rset.getLong("PointID");
				if( pointID != currentPointID )
				{
					if( pointCount != -1 )
					{
						// We've reached a new point so copy our lists
						//  into their more permanent arrays
						xSeries[pointIndex] = new double[xVals.size()];
						ySeries[pointIndex] = new double[yVals.size()];
						yQuality[pointIndex] = new int[yQual.size()];

						// Loop through each point and get the appropriate values;
						for( int i = 0; i < xVals.size(); i++ )
						{
							xSeries[pointIndex][i] = ((java.sql.Timestamp) xVals.get(i)).getTime()/1000.0;
							ySeries[pointIndex][i] = ((Double) yVals.get(i)).doubleValue();
							yQuality[pointIndex][i] = ((Integer) yQual.get(i)).intValue();
						}

						// Calculate the LF total area -> (max value * number of point readings)
						currentMaxArea = currentMax * xVals.size();

						// Store the current point's analysis values into the array model.
						maxArea[pointIndex] = currentMaxArea;
						areaOfSet[pointIndex] = currentAreaOfSet;
						minimums[pointIndex] = currentMin;
						maximums[pointIndex] = currentMax;

						// If two(or more) point id's are equal, just copy all the data over
						//  to the next one, don't loop through the whole process.
						for  ( int i = 0; i + 1< points.length; i++)
						{
							if ( points[i] == points[i + 1] )
							{
								copyAllFields(i);
								pointCount++;
							}
						}
					}

					// Re-inits done for every change in pointid.
					xVals.clear();
					yVals.clear();
					yQual.clear();

					currentMaxArea = 0;		// Re-init the temp LF maxArea for next point
					currentAreaOfSet = 0;	// Re-init the temp LF areaOfSet for next point
			
					currentMin = Double.MAX_VALUE;
					currentMax = Double.MIN_VALUE;

					currentPointID =  pointID;
					
					pointIndex = getPointIndex( pointID );	
					pointCount++;
					currentDecimals = getDecimalPlaces(pointIndex);
				} 
			
				// Going through every entry in rawpointhistory for the current pointID.
				java.sql.Timestamp ts = rset.getTimestamp("TimeStamp");
				double val = rset.getDouble("Value");
				
				int quality = rset.getInt("Quality");

				// Sum the reading's values to calculate the area of set (under the curve).
				// The currentMaxArea must wait to be calculated until the max value is found
				currentAreaOfSet += val;
			
				// Check if it is the minimum reading from each pointID, save it if min
				if( val < currentMin )
					currentMin = val;
				// Check if it is the maximum reading from each pointID, save it if max
				if( val > currentMax )
					currentMax = val;
						
				xVals.add( ts);
				valueFormat.setMinimumFractionDigits(currentDecimals);
				valueFormat.setMaximumFractionDigits(currentDecimals);
				
				yVals.add( new Double(valueFormat.format(val)) );
				yQual.add( new Integer(quality) );
				
			}

			// (The final point's data needs to be stored also.)
			// We're done so copy our lists into their more permanent arrays.
			if( pointCount == -1 )
				return;
			xSeries[pointIndex] = new double[xVals.size()];
			ySeries[pointIndex] = new double[yVals.size()];
			yQuality[pointIndex] = new int[yQual.size()];
				
			for( int i = 0; i < xVals.size(); i++ )
			{
				xSeries[pointIndex][i] = ((java.sql.Timestamp) xVals.get(i)).getTime()/1000.0;
				ySeries[pointIndex][i] = ((Double) yVals.get(i)).doubleValue();
				yQuality[pointIndex][i] = ((Integer) yQual.get(i)).intValue();
			}

			// Calculate the LF total area -> (max value * number of points)
			currentMaxArea = currentMax * xVals.size();
			
			// Store the current point's analysis values into the array model.
			maxArea[pointIndex] = currentMaxArea;
			areaOfSet[pointIndex] = currentAreaOfSet;						
			minimums[pointIndex] = currentMin;
			maximums[pointIndex] = currentMax;

			// If two(or more) point id's are equal, just copy all the data over
			//  to the next one, don't loop through the whole process.
			for  ( int i = 0; i +1 < points.length; i++)
			{
				if ( points[i] == points[i + 1] )
				{
					copyAllFields(i);
					pointCount++;
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
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
		}	
	}
	
	valueFormat.setGroupingUsed(true);
	System.out.println(" @HIT DATABASE: Took " + (System.currentTimeMillis() - timer) + " millis to update DataViewModel.");
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 6:29:28 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	long[] points = { 124, 125 };

	DataViewModel dvm = new DataViewModel();
	dvm.setPointIDs(points);

	dvm.setStartDate( com.cannontech.util.ServletUtil.getToday()); 
	dvm.setEndDate(com.cannontech.util.ServletUtil.getToday());
		
	dvm.setDatabaseAlias("yukon");
	
	while( true )
	{
	dvm.hitDatabase();

	System.out.println("Returned " + dvm.getNumSeries() + " series.");
	for( int i = 0; i < dvm.getNumSeries(); i++ )
	{
		System.out.println(i + " - length = " + dvm.getXSeries(i).length + ", min = " + dvm.getMinimum(i) + ", max = " + dvm.getMaximum(i));		
	}
	try { Thread.sleep(4000); } catch( InterruptedException e ) { }
	}
}
}

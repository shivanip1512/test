package com.cannontech.graph.model;

/**
 * A ChartDataModel that will retrieve data for a number of points
 * over a time interval.
 *
 * See hitDatabase() for the interesting jdbc code.
 *
 * Creation date: (06/19/01)
 * @author:  Stacey Nebben ( mocked from DataViewModel by AL) 
 */
//import com.cannontech.graph.model.GraphModelType.*;
//import com.cannontech.graph.GraphDataFormats;

public class LoadDurationCurveModel extends GraphModel
{
	java.text.SimpleDateFormat minSecFormat = new java.text.SimpleDateFormat("mmss");
	java.text.SimpleDateFormat hourFormat = new java.text.SimpleDateFormat("HH");

	private double[][] xHours = null;
/**
 * Insert the method's description here.
 * Creation date: (6/19/2001 4:21:38 PM)
 */
public LoadDurationCurveModel() {}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 4:25:53 PM)
 * @param a java.util.ArrayList
 * @param first int
 * @param length int
 */
public int findMaxIndex(java.util.ArrayList a, int first, int length)
{
	int maxIndex = first;

	for(int x = first + 1; x < length; x++)
		if( ((Double)a.get(x)).doubleValue() > ((Double)a.get(maxIndex)).doubleValue())
			maxIndex = x;

	return maxIndex;	
}
/**
 * Returns the x-values for for the series indexed.
 */
public double[] getXHours(int series)
{
	return xHours[series];
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
	xHours = new double[points.length][];
	
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
			java.util.ArrayList xHrs = new java.util.ArrayList();
			java.util.ArrayList yVals = new java.util.ArrayList();
			java.util.ArrayList yQual = new java.util.ArrayList();
			
			long currentPointID = Long.MIN_VALUE;
			int pointCount = -1;

			double currentMin = Double.MAX_VALUE;
			double currentMax = Double.MIN_VALUE;

			double currentMaxArea = 0;
			double currentAreaOfSet = 0;
			int allValuesCounter = 0;	// need to keep track of how many readings were read in order
			int pointIndex = 0;

			int currentDecimals = 0;
				
			// Fields used in sorting out just the LD readings we are looking for.	
			double currentHour = 0; //The hour that we are currently looking for (0 - 23) 
			double tempHour = Double.MIN_VALUE; //the current hourly value of the current series timestamp.
			double tempMaxValue = 0;	//the current Maximum value of the hourly series.
			int tempQual = 0;	//the current quality of the series of tempMaxValue
			java.sql.Timestamp tempX = null;//the current timestamp of the series of tempMaxValue

			java.sql.Timestamp ts = null;
			double val = 0;
			int quality = 0;
			
			while( rset.next() )
			{
				long pointID = rset.getLong("PointID");

				if( pointID != currentPointID )
				{
					if( pointCount != -1 )
					{
						//if( xVals.size() == 0)
						//{
							xSeries[pointIndex] = null;
							xHours[pointIndex] = null;
							ySeries[pointIndex] = null;
							yQuality[pointIndex] = null;
						//}
						//else
						//{
							
						// We've reached a new point so copy our lists
						//  into their more permanent arrays
						xSeries[pointIndex] = new double[xVals.size()];
						xHours[pointIndex] = new double[xHrs.size()];
						ySeries[pointIndex] = new double[yVals.size()];
						yQuality[pointIndex] = new int[yQual.size()];
				
						// Sort the arrays by tempX (xHour) for LD display
						sortValuesDescending( yVals, xHrs, yQual);
						
						// Loop through each point and get the appropriate values;
						for( int i = 0; i < xVals.size(); i++ )
						{
							xSeries[pointIndex][i] = ((java.sql.Timestamp) xVals.get(i)).getTime()/1000.0;
							xHours[pointIndex][i] = ((java.sql.Timestamp) xHrs.get(i)).getTime()/1000.0;
							ySeries[pointIndex][i] = ((Double) yVals.get(i)).doubleValue();
							yQuality[pointIndex][i] = ((Integer) yQual.get(i)).intValue();
						}
						//}
						// Calculate the LF total area -> (max value * number of point readings)
						currentMaxArea = currentMax * allValuesCounter;

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
								xHours[i+1] = xHours[i];
								copyAllFields(i);
								pointCount++;
							}
						}
					}

					// Starting with a new point, Re-inits fields for each point.
					xVals.clear();
					xHrs.clear();
					yVals.clear();
					yQual.clear();

					// Load Factor variables
					currentMaxArea = 0;		// Re-init the temp LF maxArea for next point
					currentAreaOfSet = 0;	// Re-init the temp LF areaOfSet for next point
					allValuesCounter = 0;	// Start the counter again.
						
					currentMin = Double.MAX_VALUE;
					currentMax = Double.MIN_VALUE;
					currentHour = 0; //The hour that we are currently looking for (0 - 23) 
					tempHour = 0; //keeps track of the current hourly value of the current series timestamp.
					tempMaxValue = Double.MIN_VALUE;
					tempQual = 0;
					tempX = null;

					currentPointID =  pointID;

					pointIndex = getPointIndex( pointID );	
					pointCount++;
					currentDecimals = getDecimalPlaces( pointIndex);
				} 
			
				// Going through every entry in rawpointhistory for the current pointID.
				ts = rset.getTimestamp("TimeStamp");
				val = rset.getDouble("Value");
				quality = rset.getInt("Quality");
				allValuesCounter++;	//count the number of readings (used for getting MaxArea)
				tempX = ts;		// a way to init the tempX field for a non-normal query (no 0 hour existing or something like that)
				
				// Sum the reading's values to calculate the area of set (under the curve).
				// The currentMaxArea must wait to be calculated until the max value is found
				currentAreaOfSet += val;
			
				// Check if it is the minimum reading from each pointID, save it if min
				if( val < currentMin )
					currentMin = val;
				// Check if it is the maximum reading from each pointID, save it if max
				if( val > currentMax )
					currentMax = val;

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
						tempQual = quality;
						tempX = ts;
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
							tempQual = quality;
							tempX = ts;
						}

						// Save values, this was the last chance for this hour.
						xVals.add(ts);
						xHrs.add(tempX);
						yVals.add( new Double(valueFormat.format(tempMaxValue)) );
						yQual.add(new Integer(tempQual));

					}
					else // back up and loop through this one again with the new hour!
					{
						// Save values, this was the last chance for this hour.
						xVals.add(ts);
						xHrs.add(tempX);
						yVals.add( new Double(valueFormat.format(tempMaxValue)) );
						yQual.add(new Integer(tempQual));
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
					xVals.add(ts);
					xHrs.add(tempX);
					yVals.add( new Double(valueFormat.format(tempMaxValue)) );
					yQual.add(new Integer(tempQual));

					tempMaxValue = 0;
					currentHour = tempHour;
					
					// get this value again, didn't qualify this round!
					tempMaxValue = val;
					tempQual = quality;
					tempX = ts;
					
				}// end else
				
				valueFormat.setMinimumFractionDigits(currentDecimals);
				valueFormat.setMaximumFractionDigits(currentDecimals);
							
			}//end while( rset.next() )

			// (The final point's data needs to be stored also.)
			// We're done so copy our lists into their more permanent arrays.
			if( pointCount == -1 )
				return;
			//if( xVals.size() == 0)
			//{
				xSeries[pointIndex] = null;
				xHours[pointIndex] = null;
				ySeries[pointIndex] = null;
				yQuality[pointIndex] = null;
			//}
			//else
			//{
							
			xSeries[pointIndex] = new double[xVals.size()];
			xHours[pointIndex] = new double[xVals.size()];
			ySeries[pointIndex] = new double[yVals.size()];
			yQuality[pointIndex] = new int[yQual.size()];

			sortValuesDescending( yVals, xHrs, yQual);
				
			for( int i = 0; i < xVals.size(); i++ )
			{
				xSeries[pointIndex][i] = ((java.sql.Timestamp) xVals.get(i)).getTime()/1000.0;
				xHours[pointIndex][i] = ((java.sql.Timestamp) xHrs.get(i)).getTime()/1000.0;
				ySeries[pointIndex][i] = ((Double) yVals.get(i)).doubleValue();
				yQuality[pointIndex][i] = ((Integer) yQual.get(i)).intValue();
			}
			//}

			// Calculate the LF total area -> (max value * number of points)
			currentMaxArea = currentMax * allValuesCounter;
			
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
					xHours[i+1] = xHours[i];
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
	// This isn't run as a standalone program...
	long[] points = { 124, 125 };

	LoadDurationCurveModel ldcm = new LoadDurationCurveModel();

	
	while( true )
	{
		System.out.println("Returned " + ldcm.getNumSeries() + " series.");
		for( int i = 0; i < ldcm.getNumSeries(); i++ )
		{
			System.out.println(i + " - length = " + ldcm.getXSeries(i).length);
		}
		try { Thread.sleep(4000); } catch( InterruptedException e ) { }
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 11:10:25 AM)
 * @param yVals java.util.ArrayList
 * @param xHrs java.util.ArrayList
 * @param yQual java.util.ArrayList
 */
public void sortValuesDescending(java.util.ArrayList yVals, java.util.ArrayList xHrs, java.util.ArrayList yQual)
{
	// Sort the values according to the readings (descending)
	int maxIndex = 0;
	for (int x = 0; x < yVals.size(); x++)
	{
		maxIndex = findMaxIndex(yVals, x, yVals.size());
		if (maxIndex != x)
		{
		 	// Don't sort the xVals because Chart uses them in the sorted order
			//  That's why we have the xHrs and xHours[][] to store them sorted for us.
			Object temp = new Double(((Double) yVals.get(x)).doubleValue());
			yVals.set(x, new Double(((Double) yVals.get(maxIndex)).doubleValue()));
			yVals.set(maxIndex, temp);

			temp = (java.sql.Timestamp) xHrs.get(x);
			xHrs.set(x, (java.sql.Timestamp) xHrs.get(maxIndex));
			xHrs.set(maxIndex, temp);

			temp = new Integer(((Integer) yQual.get(x)).intValue());
			yQual.set(x, new Integer(((Integer) yQual.get(maxIndex)).intValue()));
			yQual.set(maxIndex, temp);
		}
	}
}
}

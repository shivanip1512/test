package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (2/1/2002 9:52:28 AM)
 * @author: 
 */
import java.util.GregorianCalendar;
import java.util.Vector;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.message.dispatch.message.PointData;

public class Baseline
{
	private String databaseAlias = "yukon";
	public static java.text.SimpleDateFormat hhmmIntFormat = new java.text.SimpleDateFormat("HHmm");
	public static java.text.SimpleDateFormat hhIntFormat = new java.text.SimpleDateFormat("HH");
	public static java.text.SimpleDateFormat mmIntFormat = new java.text.SimpleDateFormat("mm");
	
	private java.util.GregorianCalendar nextBaselineCalcTime = null;
	
	private Integer baselineCalcTime = null;//time to start calcs in seconds from midnight (14400 = 4am)
	private Integer daysPreviousToCollect = null;//time to start calcs in seconds from midnight (14400 = 4am)
	private Vector calcDatesUsedForBaselineVector = null;

	private Integer [] dailyHoursArray = null;
	private Double [] dailyValuesArray = null;
	private java.util.TreeMap baselineTreeMap = null;

	private int[] skipDaysArray = {java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY};

	//DEFAULT VALUES!!!
	private int daysUsed = 30;
	private int percentWindow = 80;
	private int calcDays = 5;
	private char[] excludedWeekdays = {'N', 'N', 'N', 'N', 'N', 'Y', 'Y'};
	private int holidaysUsed = 0;

/**
 * Baseline constructor comment.
 */
public Baseline() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void figureNextBaselineCalcTime()
{
	if( nextBaselineCalcTime == null )
	{
		nextBaselineCalcTime = new GregorianCalendar();
		//start a week ago on start up
		nextBaselineCalcTime.set( java.util.Calendar.DAY_OF_YEAR, (nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_YEAR) - getDaysPreviousToCollect().intValue()));
		nextBaselineCalcTime.set( java.util.Calendar.MINUTE, 0);
		nextBaselineCalcTime.set( java.util.Calendar.SECOND, 0);
	}

	GregorianCalendar tempCal = nextBaselineCalcTime;	
	if ( nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_YEAR )  == 365)
	{
		//Check for leap year.
		if ( nextBaselineCalcTime.isLeapYear(nextBaselineCalcTime.get(java.util.Calendar.YEAR)))
		{	//just increment the day for leap year, need 366 days!
			tempCal.set( java.util.Calendar.DAY_OF_YEAR, nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_YEAR) + 1) ;
		}
		else
		{	// must set to begining of next year
			tempCal.set( java.util.Calendar.DAY_OF_YEAR, 1) ;
			tempCal.set( java.util.Calendar.YEAR, 1);
		}
	}
	else if ( nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR) == 366)
	{	// must set to begining of next year
		tempCal.set( java.util.Calendar.DAY_OF_YEAR, 1) ;
		tempCal.set( java.util.Calendar.YEAR, 1);
	}
	else
	{
		tempCal.set( java.util.Calendar.DAY_OF_YEAR, nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_YEAR) + 1) ;
	}

	while ( tempCal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY || 
			tempCal.get(java.util.Calendar.DAY_OF_WEEK)== java.util.Calendar.SATURDAY)
	{
		tempCal.set( java.util.Calendar.DAY_OF_YEAR, tempCal.get(java.util.Calendar.DAY_OF_YEAR) + 1) ;
	}


	tempCal.set( java.util.Calendar.HOUR_OF_DAY, getBaselineCalcTime().intValue());
	long nowInMilliSeconds = tempCal.getTime().getTime();

	long topOfTheHour = 3600000;	//number of millis in an hour
	long tempUpdateTime = nowInMilliSeconds - ( nowInMilliSeconds % topOfTheHour );

	nextBaselineCalcTime = new GregorianCalendar();
	nextBaselineCalcTime.setTime(new java.util.Date(tempUpdateTime));

	CalcHistorical.logEvent("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime(), com.cannontech.common.util.LogWriter.INFO);	
	System.out.println("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime());
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/2000 11:43:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getBaselineCalcTime()
{
	if( baselineCalcTime == null )
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			baselineCalcTime = new Integer( bundle.getString("calc_historical_baseline_calctime") );
			daysPreviousToCollect = new Integer( bundle.getString("calc_historical_daysprevioustocollect") );
			CalcHistorical.logEvent(" (config.prop) Baseline calculation time = " + baselineCalcTime + ":00", com.cannontech.common.util.LogWriter.INFO);
			System.out.println("[" + new java.util.Date() + "]  Baseline calculation time from config.properties is " + baselineCalcTime + ":00");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			baselineCalcTime = new Integer(14400);	//default to 4am.
			CalcHistorical.logEvent("Baseline calc start time was NOT found in config.properties, defaulted to " + baselineCalcTime + " seconds.", com.cannontech.common.util.LogWriter.INFO);
			System.out.println("[" + new java.util.Date() + "]  Baseline calc start time was NOT found in config.properties, defaulted to " + baselineCalcTime + " seconds.");
			CalcHistorical.logEvent("Add row named 'calc_historical_baseline_calctime' to config.properties. (ex. =4 (as 4am), =23 (as 11pm))", com.cannontech.common.util.LogWriter.DEBUG);
			System.out.println("[" + new java.util.Date() + "]  Add row named 'calc_historical_baseline_calctime' to config.properties. (ex. =4 (as 4am), =23 (as 11pm))");
			baselineCalcTime = new Integer(4);	//default this bad boy to run at 4am
			daysPreviousToCollect = new Integer( 7); //default to collect 7 days previous
		}
	}
	
	return baselineCalcTime;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 4:59:53 PM)
 * @param calcComponent com.cannontech.database.db.point.calculation.CalcComponent
 * @param baselineCalDatesVector java.util.Vector
 * @param databaseAlias java.lang.String
 */
public Vector getBaselinePointDataMsgVector(CalcComponent calcComponent)
{
	PointData pointDataMsg = null;
	Vector returnVector = new Vector();

	for (int i = 0; i < dailyValuesArray.length; i++)
	{
		// Must enter the baseline value into RPH twice in order for Graph to be able to draw it.
		// Begining Timestamp 00:00:01
		pointDataMsg = new com.cannontech.message.dispatch.message.PointData();
		pointDataMsg.setId(calcComponent.getPointID().intValue());

		pointDataMsg.setValue( dailyValuesArray[i].doubleValue());

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getNextBaselineCalcTime().getTime());

		cal.set(GregorianCalendar.HOUR_OF_DAY, dailyHoursArray[i].intValue() + 1);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		
		pointDataMsg.setTimeStamp(cal.getTime());
		pointDataMsg.setTime(cal.getTime());
		
		pointDataMsg.setQuality(PointQualities.NON_UPDATED_QUALITY);
		pointDataMsg.setType(com.cannontech.database.data.point.PointTypes.CALCULATED_POINT);
		pointDataMsg.setTags(0x00008000); //load profile tag setting
		pointDataMsg.setStr("Baseline Calc Historical");
		returnVector.addElement(pointDataMsg);
	}
	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/2002 2:46:47 PM)
 * @return java.util.TreeMap
 */
public java.util.TreeMap getBaselineTreeMap()
{
	return baselineTreeMap;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/2000 11:43:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDaysPreviousToCollect()
{
	if( daysPreviousToCollect == null )
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			daysPreviousToCollect = new Integer( bundle.getString("calc_historical_daysprevioustocollect") );
		}
		catch( Exception e)
		{
			e.printStackTrace();
			daysPreviousToCollect = new Integer( 7); //default to collect 7 days previous
		}
	}
	
	return daysPreviousToCollect;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public java.util.List getHistoricalBaselineCalcComponents(String databaseAlias)
{
	java.util.ArrayList returnList = new java.util.ArrayList();

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		preparedStatement = conn.prepareStatement("SELECT CC.POINTID, CC.COMPONENTORDER, "+
			" CC.COMPONENTTYPE, CC.COMPONENTPOINTID, CC.OPERATION, CC.CONSTANT, CC.FUNCTIONNAME "+
			" FROM CALCCOMPONENT CC, CALCBASE CB "+
			" WHERE CC.POINTID = CB.POINTID "+
			" AND FUNCTIONNAME = 'Baseline' "+
			" AND CB.UPDATETYPE = 'Historical' "+
			" ORDER BY CC.POINTID, CC.COMPONENTORDER");
		rset = preparedStatement.executeQuery();
		while (rset.next())
		{
			Integer pointID = new Integer(rset.getInt(1));
			Integer componentOrder = new Integer(rset.getInt(2));
			String componentType = rset.getString(3);
			Integer componentPointID = new Integer(rset.getInt(4));
			String operation = rset.getString(5);
			Double constant = new Double(rset.getDouble(6));
			String functionName = rset.getString(7);

			CalcComponent cc = new CalcComponent( pointID, componentOrder,
							componentType, componentPointID, operation, constant, functionName );

			returnList.add(cc);
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
			if( preparedStatement != null )
				preparedStatement.close();
			if (rset != null)
				rset.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}

	return returnList;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public GregorianCalendar getNextBaselineCalcTime()
{
	if( nextBaselineCalcTime == null)
		figureNextBaselineCalcTime();

	return nextBaselineCalcTime;
}
/**
 * Starts the application.
 */
public Vector main()
{
	java.util.Date now = null;
	now = new java.util.Date();

	Vector tempPointDataMsgVector = null;
	
	// Get a list of all 'Historical' & 'Baseline' CalcPoints and their fields from Point table in database. 
	CalcHistorical.allHistoricalCalcComponentsList = getHistoricalBaselineCalcComponents( databaseAlias );

	// Loop through each calcBase point(ID).	
	for (int i = 0; i < CalcHistorical.calcBasePoints.size(); i++)
	{
		CalcHistorical.setPointID(((Integer)CalcHistorical.calcBasePoints.get(i)).intValue());

		CalcComponent calcComponent = null;
		for (int j = 0; j < CalcHistorical.allHistoricalCalcComponentsList.size(); j++)
		{
			if( CalcHistorical.getPointID() == ((CalcComponent) CalcHistorical.allHistoricalCalcComponentsList.get(j)).getPointID().intValue() )
			{
				calcComponent = (CalcComponent) CalcHistorical.allHistoricalCalcComponentsList.get(j);
				j = CalcHistorical.allHistoricalCalcComponentsList.size();	//exit for loop
			}
		}
		if( calcComponent == null)
			break;

		// Calendar used to track/locate which days will be valid ones to search.	
		int daysUsedToCalcBaseline = 0;
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTime( getNextBaselineCalcTime().getTime());

		//start by looking at yesterday's date.
		tempCal.set( java.util.Calendar.DAY_OF_YEAR, nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR ) - 1 );
		tempCal.set( java.util.Calendar.HOUR_OF_DAY, 0);

		int dayofyear = tempCal.get(java.util.Calendar.DAY_OF_YEAR);
		
		calcDatesUsedForBaselineVector = new Vector();
		double baselineValue = 0;
		Vector validTimestampsVector = new Vector();

		// Parameters (attributes set per pointId (actually per customer).
		setCustomerBaselineAttributes(CalcHistorical.getPointID(), databaseAlias );
		setSkipDaysArray();
		
		while ( daysUsedToCalcBaseline < calcDays )
		{
			boolean skipMe = false;

			// Exclude specified days of the week.
			for ( int j = 0; j < skipDaysArray.length; j++)
			{
				if (tempCal.get( java.util.Calendar.DAY_OF_WEEK) == skipDaysArray[j])
				{
					dayofyear = tempCal.get(java.util.Calendar.DAY_OF_YEAR) - 1;
					skipMe = true;
					break;
				}
			}
				
			//else if( ( tempCal.get( java.util.Calendar.DAY_OF_WEEK) == HOLIDAY!!! )
			//{
				//tempCal.set( java.util.Calendar.DAY_OF_YEAR, calcHistorical.nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR) - 1);
			//}
			//else if( ( tempCal.get( java.util.Calendar.DAY_OF_WEEK) == PREVIOUSLY CURTAILED DAY!!! )
			//{
				//tempCal.set( java.util.Calendar.DAY_OF_YEAR, calcHistorical.nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR) - 1);					
			//}
			if (skipMe)
			{		
			}
			else if ( tempCal.get( java.util.Calendar.DAY_OF_YEAR) < 
				(getNextBaselineCalcTime().get(java.util.Calendar.DAY_OF_YEAR)) - daysUsed )
			{
				// Searched too far back in time.  Only going back up to daysUsed (Database, customerbaseline ).
				break;
			}
			else
			{
				// SUCCESSFUL day found to use in baseline calculation.
				// BAD ASSUMPTION....ASSUMING ALL DATA EXISTS ON A PARTICULAR DAY.

				validTimestampsVector.add( new Long( tempCal.getTime().getTime() ) );
				daysUsedToCalcBaseline++;	 //increment the number of average baseline values accumulated.

				dayofyear = tempCal.get(java.util.Calendar.DAY_OF_YEAR) - 1;
			}
			tempCal.set( java.util.Calendar.DAY_OF_YEAR, dayofyear);
		}

		if( daysUsedToCalcBaseline > 0)
		{

			retrieveDailyBaselineData(calcComponent,validTimestampsVector, databaseAlias);
			tempPointDataMsgVector = getBaselinePointDataMsgVector( calcComponent);
		}
	}		
	return tempPointDataMsgVector;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 4:59:53 PM)
 * @param calcComponent com.cannontech.database.db.point.calculation.CalcComponent
 * @param baselineCalDatesVector java.util.Vector
 * @param databaseAlias java.lang.String
 */
public int retrieveDailyBaselineData(CalcComponent calcComponent, Vector validTimestampsVector, String databaseAlias)
{
	long DAY = 86400000;
	com.cannontech.message.dispatch.message.PointData pointDataMsg = null;

	if (calcComponent == null)
		return 0;

	StringBuffer sql = new StringBuffer("SELECT VALUE, TIMESTAMP FROM RAWPOINTHISTORY WHERE ");

	sql.append(" POINTID = ");
	sql.append(calcComponent.getComponentPointID());

	// ...for each entry in the baselineCalDatesVector....
	// Timestamps give us values > 00:00:00 and <= 00:00:00 of the last day+1
	for (int i = 0; i < validTimestampsVector.size(); i++)
	{
		if (i == 0)
			sql.append(" AND (( TIMESTAMP > ?");
		else
			sql.append(" OR ( TIMESTAMP > ?");

		sql.append(" AND TIMESTAMP <= ?");
		sql.append(")");

		if (i == (validTimestampsVector.size() - 1))
		{
			sql.append(") ORDER BY TIMESTAMP");
		}
	}

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if (conn == null)
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			CalcHistorical.logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);			
			return 0;
		}
		else
		{

			pstmt = conn.prepareStatement(sql.toString());
			for (int i = 0; i < validTimestampsVector.size(); i++)
			{
				pstmt.setTimestamp(((i * 2) + 1), new java.sql.Timestamp((new Long(validTimestampsVector.get(i).toString()).longValue())));
				pstmt.setTimestamp(((i * 2) + 2), new java.sql.Timestamp((new Long(validTimestampsVector.get(i).toString()).longValue()) + DAY));
			}

			rset = pstmt.executeQuery();

			Vector values = new Vector();
			Vector timestamps = new Vector();

			int rowCount = 0;
			while (rset.next())
			{
				
				values.add( new Double(rset.getDouble(1)));
				java.sql.Timestamp ts = rset.getTimestamp(2);
				int min = (new Integer (mmIntFormat.format(ts).toString()).intValue());
				int hr = (new Integer (hhIntFormat.format(ts).toString()).intValue());
			
				// have to manipulate the hour associated with the value and timestamps.	
				int keyHour = hr;
				if( hr == 0)
				{
					if( min == 0)
					{
						keyHour = 23;
					}
				}
				else
				{
					if( min == 0)
					{
						keyHour = hr - 1;
					}
				}

					
				timestamps.add(new Integer (keyHour));
				rowCount++;
			}


			Double[] vals = new Double[values.size()];
			values.toArray(vals);
			
			Integer [] ts = new Integer[timestamps.size()];
			timestamps.toArray(ts);
			if (rowCount > 0)
			{
				setValuesAndTimestamps(vals, ts);
			}
			java.util.Set keySet = baselineTreeMap.keySet();
			dailyHoursArray = new Integer [keySet.size()];
			keySet.toArray(dailyHoursArray);

			java.util.Collection keyVals = baselineTreeMap.values();
			Object [] tempArray = new Double[keyVals.size()];
			dailyValuesArray = new Double[keyVals.size()];
			tempArray = keyVals.toArray();

			for (int i = 0; i < keyVals.size(); i++)
			{
				Double[] v = (Double[])tempArray[i];

				double counter = v[0].doubleValue();
				double totalVal = v[1].doubleValue();
				dailyValuesArray[i] = new Double(totalVal/counter);
			}				

		}
	}
	catch (java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if (pstmt != null)
				pstmt.close();
			if (rset != null)
				rset.close();
			if (conn != null)
				conn.close();
		}
		catch (java.sql.SQLException e2)
		{
			e2.printStackTrace(); //sometin is up
		}
	}

	return 0; // the average
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/2002 2:47:47 PM)
 * @param treeMap java.util.TreeMap
 */
public void setBaselineTreeMap(java.util.TreeMap treeMap)
{
	baselineTreeMap = treeMap;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 4:59:53 PM)
 * @param calcComponent com.cannontech.database.db.point.calculation.CalcComponent
 * @param baselineCalDatesVector java.util.Vector
 * @param databaseAlias java.lang.String
 */
public void setCustomerBaselineAttributes(int ptID, String databaseAlias)
{
	StringBuffer sql = new StringBuffer	("SELECT DAYSUSED, PERCENTWINDOW, CALCDAYS, EXCLUDEDWEEKDAYS, HOLIDAYSUSED ");
	sql.append("FROM CUSTOMERBASELINE CB, POINT PT, PAOOWNER PAOO ");
	sql.append("WHERE PT.PAOBJECTID = PAOO.CHILDID ");
	sql.append("AND CB.CUSTOMERID = PAOO.OWNERID");
		
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			CalcHistorical.logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);
			return;
		}
		else
		{
			stmt = conn.prepareStatement(sql.toString());
			rset = stmt.executeQuery();

			while( rset.next())
			{
				daysUsed = rset.getInt(1);
				percentWindow = rset.getInt(2);
				calcDays = rset.getInt(3);
				String days = rset.getString(4);
				excludedWeekdays = days.toCharArray();
				holidaysUsed = rset.getInt(5);
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
			if (rset != null)
				rset.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2001 11:08:29 AM)
 */
public void setSkipDaysArray()
{
	Vector skip = new java.util.Vector();
	
	int [] days =
		{java.util.Calendar.MONDAY,
		java.util.Calendar.TUESDAY,
		java.util.Calendar.WEDNESDAY,
		java.util.Calendar.THURSDAY,
		java.util.Calendar.FRIDAY,
		java.util.Calendar.SATURDAY,
		java.util.Calendar.SUNDAY };
		
	
	for (int i = 0; i < excludedWeekdays.length; i++)
		if (excludedWeekdays[i] == 'Y')
			skip.add( new Integer( days[i] ) );

	skipDaysArray = new int [skip.size()];
	for ( int i = 0; i < skip.size(); i++)
	{
		skipDaysArray[i] = ((Integer) skip.get(i)).intValue();
	}
	
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/2002 4:13:38 PM)
 * @param values double[]
 * @param timestamps int[]
 */
public void setValuesAndTimestamps(Double[] values, Integer[] timestamps)
{
	java.util.TreeMap tree = new java.util.TreeMap();

	for( int l = 0; timestamps != null && values != null &&  l < timestamps.length; l++ )
	{
 		Integer d = timestamps[l];
 		Double[] objectValues = (Double[]) tree.get(d);

 		
 		if( objectValues == null )
 		{
	 		//objectValues is not in the key already
	 		objectValues = new Double[ 2 ];	//1 for counter, 1 for value
	 		objectValues[0] = new Double(1);
	 		objectValues[1] = values[l];
	 		tree.put(d,objectValues);
 		}
 		else
 		{
	 		Double newVal = new Double(objectValues[1].doubleValue() + values[l].doubleValue());

	 		double counter = objectValues[0].doubleValue();
	 		objectValues[0] = new Double(objectValues[0].doubleValue() + 1);
	 		objectValues[1] = newVal;

	 		tree.put(d, objectValues);
 		}
	}
	//set up a treeMap of keys and values for Graph exporting.
	setBaselineTreeMap( tree );

}
}

package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (2/1/2002 9:52:28 AM)
 * @author: 
 */
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.message.dispatch.message.PointData;

public class Baseline
{
	//contains com.cannontech.database.db.point.calculation.CalcComponent values.
	public Vector returnPointDataMsgVector = new Vector();
	private Vector historicalCalcComponents = null;
	private java.util.GregorianCalendar nextBaselineCalcTime = null;
	private GregorianCalendar lastUpdateTimestamp = null;
	private Integer baselineCalcTime = null;//time to start calcs in seconds from midnight (14400 = 4am)
	private Integer daysPreviousToCollect = null;//time to start calcs in seconds from midnight (14400 = 4am)

//	private HoursAndValues baselineDataArray = null;
	private HoursAndValues averageDataArray = null;

	private int[] skipDaysArray = {java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY};
	
	private com.cannontech.database.db.baseline.BaseLine baselineProps = null;
	
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
			nextBaselineCalcTime.add(java.util.Calendar.DAY_OF_YEAR, - getDaysPreviousToCollect().intValue());
			nextBaselineCalcTime.set(java.util.Calendar.HOUR_OF_DAY, getBaselineCalcTime().intValue());
			nextBaselineCalcTime.set( java.util.Calendar.MINUTE, 0);
			nextBaselineCalcTime.set( java.util.Calendar.SECOND, 0);
			nextBaselineCalcTime.set( java.util.Calendar.MILLISECOND, 0);
		}
	
//		nextBaselineCalcTime.add(java.util.Calendar.DAY_OF_YEAR, 1);
		nextBaselineCalcTime.add(java.util.Calendar.HOUR_OF_DAY, 1);
		while(true)
		{
			if(isSkipDay(nextBaselineCalcTime)) 
			{
				System.out.println("** DAY " + nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_WEEK));
				nextBaselineCalcTime.add(java.util.Calendar.DAY_OF_YEAR, 1);
			}
			else
				break;		
		}
//		nextBaselineCalcTime.set(java.util.Calendar.HOUR_OF_DAY, getBaselineCalcTime().intValue());
	
		CalcHistorical.logEvent("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime(), com.cannontech.common.util.LogWriter.INFO);	
		com.cannontech.clientutils.CTILogger.info("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime());
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
				com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Baseline calculation time from config.properties is " + baselineCalcTime + ":00");
			}
			catch( Exception e)
			{
				e.printStackTrace();
				baselineCalcTime = new Integer(14400);	//default to 4am.
				CalcHistorical.logEvent("Baseline calc start time was NOT found in config.properties, defaulted to " + baselineCalcTime + " seconds.", com.cannontech.common.util.LogWriter.INFO);
				com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Baseline calc start time was NOT found in config.properties, defaulted to " + baselineCalcTime + " seconds.");
				CalcHistorical.logEvent("Add row named 'calc_historical_baseline_calctime' to config.properties. (ex. =4 (as 4am), =23 (as 11pm))", com.cannontech.common.util.LogWriter.DEBUG);
				com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Add row named 'calc_historical_baseline_calctime' to config.properties. (ex. =4 (as 4am), =23 (as 11pm))");
				baselineCalcTime = new Integer(4);	//default this bad boy to run at 4am
				daysPreviousToCollect = new Integer( 7); //default to collect 7 days previous
			}
		}
		
		return baselineCalcTime;
	}
	
	/*private void print30DayAverageValues(Integer pointID)
	{
		if( averageDataArray != null)
		{			
			for (int i = 0; i < averageDataArray.length(); i++)
			{
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(lastUpdateTimestamp.getTime());
	
				cal.set(GregorianCalendar.HOUR_OF_DAY, averageDataArray.getHours()[i].intValue() + 1);
				cal.set(GregorianCalendar.MINUTE, 0);
				cal.set(GregorianCalendar.SECOND, 0);
			
				System.out.println("AVG TIME : " + cal.getTime() + "   -   VALUE : " + averageDataArray.getValues()[i].doubleValue());
			}
		}
	}*/
	/**
	 * Insert the method's description here.
	 * Creation date: (8/9/2001 4:59:53 PM)
	 * @param calcComponent com.cannontech.database.db.point.calculation.CalcComponent
	 * @param baselineCalDatesVector java.util.Vector
	 */
	public Vector getBaselinePointDataMsgVector(Integer pointID, HoursAndValues dataArray)
	{
		Vector pointDataVector= new Vector();
		
		if( dataArray != null)
		{
			PointData pointDataMsg = null;
			for (int i = 0; i < dataArray.length(); i++)
			{
				// Must enter the baseline value into RPH twice in order for Graph to be able to draw it.
				// Begining Timestamp 00:00:01
				pointDataMsg = new com.cannontech.message.dispatch.message.PointData();
				pointDataMsg.setId(pointID.intValue());
		
				pointDataMsg.setValue( dataArray.getValues()[i].doubleValue());
		
				GregorianCalendar cal = new GregorianCalendar();
//				cal.setTime(getNextBaselineCalcTime().getTime());
//				cal.setTime(CalcHistorical.getCalcHistoricalLastUpdateTimeStamp(pointID.intValue()).getTime());
				cal.setTime(lastUpdateTimestamp.getTime());
		
				cal.set(GregorianCalendar.HOUR_OF_DAY, dataArray.getHours()[i].intValue() + 1);
				cal.set(GregorianCalendar.MINUTE, 0);
				cal.set(GregorianCalendar.SECOND, 0);
				
				pointDataMsg.setTimeStamp(cal.getTime());
				pointDataMsg.setTime(cal.getTime());
				
				pointDataMsg.setQuality(PointQualities.NON_UPDATED_QUALITY);
				pointDataMsg.setType(com.cannontech.database.data.point.PointTypes.CALCULATED_POINT);
				pointDataMsg.setTags(0x00008000); //load profile tag setting
				pointDataMsg.setStr("Baseline Calc Historical");
				pointDataVector.addElement(pointDataMsg);
				CalcHistorical.updateDynamicCalcHistorical(pointDataMsg.getTimeStamp(), pointID.intValue());
			}
		}
		return pointDataVector;
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
	public GregorianCalendar getNextBaselineCalcTime()
	{
		if( nextBaselineCalcTime == null)
			figureNextBaselineCalcTime();
	
		return nextBaselineCalcTime;
	}
	/**
	 * Starts the application.
	 */
	public Vector main(Integer calcBasePointID)
	{
		averageDataArray = null;	//reset for each point.
			
		CalcComponent calcComponent = null;
		CalcComponent percentCalcComponent = null;
		for (int j = 0; j < getHistoricalCalcComponents().size(); j++)
		{
			if( calcBasePointID.intValue() == ((CalcComponent)getHistoricalCalcComponents().get(j)).getPointID().intValue() )
			{
				if(  ((CalcComponent) getHistoricalCalcComponents().get(j)).getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION))
					calcComponent = (CalcComponent) getHistoricalCalcComponents().get(j);
				else if( ((CalcComponent)getHistoricalCalcComponents().get(j)).getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_PERCENT_FUNCTION))
					percentCalcComponent = (CalcComponent)getHistoricalCalcComponents().get(j);
			}
		}


		if( calcComponent != null)
		{
			lastUpdateTimestamp = CalcHistorical.getCalcHistoricalLastUpdateTimeStamp(calcBasePointID.intValue());
			System.out.println("LastUpdateTimestamp = " + lastUpdateTimestamp.getTime() + " : " + calcBasePointID);				
			if( nextBaselineCalcTime.getTime().compareTo(lastUpdateTimestamp.getTime()) <= 0)
			{
				//The lastUpdate time is greater than our current calculation time.
				//We already have this data!
				System.out.println( " BREAK BASELINETIMESTAMP TO " + lastUpdateTimestamp.getTime());				
			}		
			else
			{	
				while(true)
				{
					if(isSkipDay(lastUpdateTimestamp)) 
					{
						lastUpdateTimestamp.add(java.util.Calendar.DAY_OF_YEAR, 1);
					}
					else
						break;		
				}

				HoursAndValues dataArray = retrieveHoursAndValues(calcComponent, percentCalcComponent);
				if( dataArray != null)
					returnPointDataMsgVector.addAll(getBaselinePointDataMsgVector( calcBasePointID, dataArray));
			}				
		}
		return returnPointDataMsgVector;
	}
	
	/**
	 * @param calcComponent
	 */
	private HoursAndValues retrieveHoursAndValues(CalcComponent calcComponent, CalcComponent percentCalcComponent)
	{
		CTILogger.info("PROCESSING POINTID " + calcComponent.getPointID());
		HoursAndValues dataArray = null;
		//contains java.util.Date values
		Vector validTimestampsVector = new Vector();
		
		// Parameters (attributes set per pointId.
		retrieveBaselineAttributes(calcComponent.getPointID().intValue());
		setSkipDaysArray();
					
		GregorianCalendar tempCal = (GregorianCalendar)lastUpdateTimestamp.clone();
		tempCal.add( java.util.Calendar.DAY_OF_YEAR, - 1 );
	  
		while ( validTimestampsVector.size() < getBaselineProperties().getCalcDays().intValue() )
		{
			boolean validData = true;
						
			if ( tempCal.get( java.util.Calendar.DAY_OF_YEAR) <  
				(lastUpdateTimestamp.get(java.util.Calendar.DAY_OF_YEAR)) - getBaselineProperties().getDaysUsed().intValue() )
			{
				// Searched too far back in time.  Only going back up to Baseline.DaysUsed.
				CTILogger.info("STOP SEARCHING - too far back: " + tempCal.getTime());
				break;
			}
			else if( isSkipDay(tempCal))
			{	//Invalid.  Baseline.ExcludedWeekDays
				CTILogger.info("EXCLUDED WEEKDAYS: " + tempCal.getTime());
				validData = false;
			}
			else if( isHoliday(tempCal))
			{	//Invalid.  Baseline.HolidaysUsed
				CTILogger.info("HOLIDAYS USED: " + tempCal.getTime());
				validData = false;
			}
			//else if( ( tempCal.get( java.util.Calendar.DAY_OF_WEEK) == PREVIOUSLY CURTAILED DAY!!! )
			//{
				//tempCal.set( java.util.Calendar.DAY_OF_YEAR, calcHistorical.nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR) - 1);					
			//}
			else 
			{	//Checks on actual data, not just date.
				Vector validTimestamps = new Vector(1);
				validTimestamps.add(tempCal.getTime());
				HoursAndValues currentData = retrieveData(calcComponent.getComponentPointID(), validTimestamps);
				if( currentData == null)
				{	//Invalid.  No data
					validData = false;
					if( validTimestampsVector.size() == 0)
					{
						CTILogger.info("First day to check and we don't have data.  Going to skip and wait longer!");
						return null;
					}
				}
				else if(currentData.length() < 24)
				{	//Invalid.  Not enough data.
					CTILogger.info("NOT ENOUGH DATA: Only " + currentData.length() + " values found.");
					validData = false;
					if( validTimestampsVector.size() == 0)
					{
						CTILogger.info("First day to check and we don't have a full days worth of data.  Going to skip and wait longer!");
//						return null;
					}
				}
				else if(baselineProps.getPercentWindow().intValue() > 0 
						&& percentCalcComponent != null)
				{	
					//0 is default for of % for x days comparison.  Defalut x to 30.
					if( averageDataArray == null)
					{
						Vector tempTimestamp = new Vector(1);
						tempTimestamp.add(lastUpdateTimestamp.getTime());
						//Attempt to first load data from the database.
						averageDataArray = retrieveData(percentCalcComponent.getComponentPointID(), tempTimestamp);
						if( averageDataArray == null)
							main(percentCalcComponent.getComponentPointID());											

						// Get the data values for the percent componenet.
						// if data values are empty call retrieveHoursAndValues on 
//						load30Average(calcComponent.getComponentPointID());
//						print30DayAverageValues(calcComponent.getComponentPointID());
					}
					if ( isLessThanAverage( tempCal, currentData))
					{								
						//Invalid.  Baseline.PercentWindow
						CTILogger.info("PERCENT WINDOW: " + tempCal.getTime());
						validData = false;
					}
				}
			}			
						
			if( validData)
			{
				// SUCCESSFUL date/data found to use in baseline calculation.
				validTimestampsVector.add( tempCal.getTime());
			}
			tempCal.add(java.util.Calendar.DAY_OF_YEAR, -1);
		}
		if( validTimestampsVector != null && validTimestampsVector.size() > 0)
		{
			for (int i = 0; i < validTimestampsVector.size(); i++)
				System.out.println((java.util.Date)validTimestampsVector.get(i));
			
			dataArray = retrieveData(calcComponent.getComponentPointID(), validTimestampsVector);
		}
		return dataArray;
	}
	
	
	private boolean isHoliday(GregorianCalendar cal)
	{
		com.cannontech.database.data.holiday.HolidaySchedule schedule = new com.cannontech.database.data.holiday.HolidaySchedule(getBaselineProperties().getHolidaysUsed());
		try
		{
			com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, schedule);
			schedule = (com.cannontech.database.data.holiday.HolidaySchedule)t.execute();
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		}
		
		for ( int j = 0; j < schedule.getHolidayDatesVector().size(); j++)
		{
			if( ( ( cal.get(java.util.Calendar.MONTH) == 
				((com.cannontech.database.db.holiday.DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayMonth().intValue())
				&& (cal.get(java.util.Calendar.DAY_OF_MONTH) ==
				((com.cannontech.database.db.holiday.DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayDay().intValue())) )
			{
				if(((com.cannontech.database.db.holiday.DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayYear().intValue() > 0)
				{
					if((cal.get(java.util.Calendar.YEAR) ==
						((com.cannontech.database.db.holiday.DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayYear().intValue()))
						return true;
				}
				else
				{
					return true;
				}
			}				
		}
		return false;
	}
	private boolean isSkipDay(GregorianCalendar cal)
	{
		for ( int i = 0; i < skipDaysArray.length; i++)
		{
			if (cal.get( java.util.Calendar.DAY_OF_WEEK) == skipDaysArray[i])
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isLessThanAverage(GregorianCalendar cal, HoursAndValues data)
	{
		if( data != null && averageDataArray != null)
		{
			for (int i = 0; i < data.length(); i ++)
			{
				if( ((Integer)data.getHours()[i]).intValue() == ((Integer)averageDataArray.getHours()[i]).intValue())
				{
					if( ((Double)data.getValues()[i]).doubleValue() <  ((Double)averageDataArray.getValues()[i]).doubleValue() * .75d)
					{
						CTILogger.info("DATA IS < 75% for: " + cal.getTime() + " - " + data.getValues()[i] + " < " +averageDataArray.getValues()[i]);
						return true;
					}
				}
				else
				{
					CTILogger.info("BAD HOUR/MISSING DATA for: " + cal.getTime());
					return true;
				}
			}
		}
		return false;
	}
	/*private void load30Average(Integer pointId)
	{
		Vector valid30DayTimestamps = new Vector(30);
		final int DAYS_AVERAGE = 30;
		int daysCount = 0;
		
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal = (GregorianCalendar)lastUpdateTimestamp.clone();
		//start by looking at yesterday's date.
		tempCal.add( java.util.Calendar.DAY_OF_YEAR, - 1 );
		tempCal.set( java.util.Calendar.HOUR_OF_DAY, 0);
		int totalDaysSearched = 0;		
		while ( daysCount < DAYS_AVERAGE && totalDaysSearched < 60)
		{
			if( isSkipDay(tempCal))
			{	//Invalid.
			}
			else if( isHoliday(tempCal))
			{	//Invalid.
			}
			//else if( ( tempCal.get( java.util.Calendar.DAY_OF_WEEK) == PREVIOUSLY CURTAILED DAY!!! )
			//{
				//tempCal.set( java.util.Calendar.DAY_OF_YEAR, calcHistorical.nextBaselineCalcTime.get( java.util.Calendar.DAY_OF_YEAR) - 1);					
			//}
			else
			{
				// SUCCESSFUL day found to use in baseline calculation.
				valid30DayTimestamps.add( tempCal.getTime() );
				daysCount++;	 //increment the number of valid timestamps found.
			}
			tempCal.add(java.util.Calendar.DAY_OF_YEAR, -1);
			totalDaysSearched++;
		}
		System.out.println(" *30 DAY LOAD* VALID TIMESTAMPS FOR POINTID " + pointId);
		for (int i = 0; i < valid30DayTimestamps.size(); i++)
			System.out.println((java.util.Date)valid30DayTimestamps.get(i));
			
		averageDataArray = retrieveData(pointId, valid30DayTimestamps);
	}
	/*	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/9/2001 4:59:53 PM)
	 * @param pointID java.util.Integer
	 * @param validTimestampsVector java.util.Vector contains java.util.Date values.
	 */
	public HoursAndValues retrieveData(Integer pointID, Vector validTimestampsVector)
	{
		HoursAndValues hoursAndValues = null;
		long DAY = 86400000;
		com.cannontech.message.dispatch.message.PointData pointDataMsg = null;
	
		StringBuffer sql = new StringBuffer("SELECT VALUE, TIMESTAMP FROM RAWPOINTHISTORY WHERE ");
	
		sql.append(" POINTID = ");
		sql.append(pointID);
	
		// ...for each entry in the baselineCalDatesVector....
		// Timestamps give us values > 00:00:00 and <= 00:00:00 of the last day+1
		//contains java.util.Date values.
		Vector sqlQueryDates = new Vector();
		sqlQueryDates = appendTimestamps(sql, validTimestampsVector);	
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if (conn == null)
			{
				com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
				CalcHistorical.logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);			
				return null;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				for (int i = 0; i < sqlQueryDates.size(); i++)
				{
					pstmt.setTimestamp(i+1, new java.sql.Timestamp(((java.util.Date)sqlQueryDates.get(i)).getTime()));
				}
				rset = pstmt.executeQuery();
	
				//contains Double values (rawPointHistory values).
				Vector values = new Vector();
				//contains Integer values (rawPointHistory timestamps.hour).
				Vector hours = new Vector();

				while (rset.next())
				{
					values.add( new Double(rset.getDouble(1)));

					java.sql.Timestamp ts = rset.getTimestamp(2);
					int keyHour = getAdjustedHour(ts);
					hours.add(new Integer (keyHour));
				}
	
				if (!values.isEmpty() && !hours.isEmpty())	//hours and values we always both have data or both be empty.
				{
					java.util.TreeMap treeMap = buildTreeMap(values, hours);					
					java.util.Set keySet = treeMap.keySet();
					java.util.Collection keyVals = treeMap.values();

					Object [] tempArray = new Double[keyVals.size()];
					hoursAndValues = new HoursAndValues(keySet.size(), keyVals.size());
					keySet.toArray(hoursAndValues.getHours());
					tempArray = keyVals.toArray();
		
					for (int i = 0; i < keyVals.size(); i++)
					{
						Double[] v = (Double[])tempArray[i];
						double counter = v[0].doubleValue();
						double totalVal = v[1].doubleValue();
						hoursAndValues.values[i] = new Double(totalVal/counter);
					}
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
	
		return hoursAndValues; //hours and values of the retreived data.
	}
	
	private int getAdjustedHour(java.sql.Timestamp ts)
	{
		int min = (new Integer ( new java.text.SimpleDateFormat("mm").format(ts).toString()).intValue());
		int hr = (new Integer (new java.text.SimpleDateFormat("HH").format(ts).toString()).intValue());
			
		// have to manipulate the hour associated with the value and timestamps.	
		if( hr == 0)
		{
			if( min == 0)
			{
				return 23;
			}
		}
		else
		{
			if( min == 0)
			{
				return (hr - 1);
			}
		}
		return hr; 
	}
	/**
	 * @param sql
	 * @param validTimestampsVector
	 * @return Vector of java.util.Date values that will need to be set for the sql statement.
	 */
	private Vector appendTimestamps(StringBuffer sql, Vector validTimestampsVector)
	{
		Vector vectorOfQueryDates = new Vector();
		boolean newLine = false; 
		for (int i = 0; i < validTimestampsVector.size(); i++)
			{
				GregorianCalendar endDate = new GregorianCalendar();
				endDate.setTime((java.util.Date)validTimestampsVector.get(i));
				endDate.add(java.util.Calendar.DATE, 1);		
				if (i == 0 )
				{
					sql.append(" AND (( TIMESTAMP <= ?");
					vectorOfQueryDates.add(endDate.getTime());
//					System.out.println(" AND (( TIMESTAMP <= " + endDate.getTime() );
				}
				else if(newLine)
				{
					sql.append(" OR ( TIMESTAMP <= ?");
					vectorOfQueryDates.add(endDate.getTime());
//					System.out.println(" OR ( TIMESTAMP <= " + endDate.getTime());
					newLine = false;
				}
			
				//The last timestamp to check so we need to close the statement too.
				if( i+1 == validTimestampsVector.size())
				{
					sql.append(" AND TIMESTAMP > ?)");
					sql.append(") ORDER BY TIMESTAMP");
					vectorOfQueryDates.add(validTimestampsVector.get(i));
//					System.out.println("  AND TIMESTAMP > " + validTimestampsVector.get(i) + ") ORDER BY TIMESTAMP");
					break;			
				}
				//Compare i and i+1 values incremental dates.  If they are in order we may skip the date by making it inclusive in the where clause
				GregorianCalendar yesterday = new GregorianCalendar();
				yesterday.setTime((java.util.Date)validTimestampsVector.get(i));
				yesterday.add(java.util.Calendar.DATE, -1);
				if( yesterday.getTime().compareTo( (java.util.Date)validTimestampsVector.get(i+1)) == 0)
				{
					//SKIP
				}
				else
				{
					sql.append(" AND TIMESTAMP > ?)");
					vectorOfQueryDates.add(validTimestampsVector.get(i));
//					System.out.println(" AND TIMESTAMP > " + validTimestampsVector.get(i));					
					newLine = true;			
				}
			}	
		return vectorOfQueryDates;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/28/2002 2:47:47 PM)
	 * values contains java.util.Double values.
	 * hours contains java.util.Integer values.
	 * @param treeMap java.util.TreeMap
	 */
	public static java.util.TreeMap buildTreeMap(Vector values, Vector hours)
	{
		java.util.TreeMap tree = new java.util.TreeMap();
		if( !hours.isEmpty() && !values.isEmpty())
		{
			for( int i = 0; i < hours.size(); i++ )
			{
				Integer d = (Integer)hours.get(i);
				Double[] objectValues = (Double[]) tree.get(d);
				 		
				if( objectValues == null )
				{
					//objectValues is not in the key already
					objectValues = new Double[ 2 ];	//1 for counter, 1 for value
					objectValues[0] = new Double(1);
					objectValues[1] = (Double)values.get(i);
					tree.put(d,objectValues);
				}
				else
				{
					Double newVal = new Double(objectValues[1].doubleValue() + ((Double)values.get(i)).doubleValue());
		
					double counter = objectValues[0].doubleValue();
					objectValues[0] = new Double(objectValues[0].doubleValue() + 1);
					objectValues[1] = newVal;
		
					tree.put(d, objectValues);
				}
			}
		}
		return tree;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/9/2001 4:59:53 PM)
	 * @param calcComponent com.cannontech.database.db.point.calculation.CalcComponent
	 * @param baselineCalDatesVector java.util.Vector
	 */
	public void retrieveBaselineAttributes(int ptID)
	{
		StringBuffer sql = new StringBuffer	("SELECT DAYSUSED, PERCENTWINDOW, CALCDAYS, EXCLUDEDWEEKDAYS, HOLIDAYSUSED, BASELINENAME ");
		sql.append("FROM BASELINE BASE, CALCPOINTBASELINE CPB ");
		sql.append("WHERE BASE.BASELINEID = CPB.BASELINEID ");
		sql.append("AND CPB.POINTID = " + ptID);
			
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
				CalcHistorical.logEvent(getClass() + ":  Error getting database connection.", com.cannontech.common.util.LogWriter.ERROR);
				return;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
	
				baselineProps = new com.cannontech.database.db.baseline.BaseLine();
				while( rset.next())
				{
					baselineProps.setDaysUsed(new Integer(rset.getInt(1)));
					baselineProps.setPercentWindow(new Integer(rset.getInt(2)));
					baselineProps.setCalcDays(new Integer(rset.getInt(3)));
					baselineProps.setExcludedWeekDays(rset.getString(4));
					baselineProps.setHolidaysUsed(new Integer(rset.getInt(5)));
					baselineProps.setBaselineName(rset.getString(6));
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
			{java.util.Calendar.SUNDAY,
			java.util.Calendar.MONDAY,
			java.util.Calendar.TUESDAY,
			java.util.Calendar.WEDNESDAY,
			java.util.Calendar.THURSDAY,
			java.util.Calendar.FRIDAY,
			java.util.Calendar.SATURDAY };
			
		for (int i = 0; i < baselineProps.getExcludedWeekDays().toString().length(); i++)
		{
			if(baselineProps.getExcludedWeekDays().charAt(i) == 'Y')
				skip.add( new Integer( days[i] ) );
		}
	
		skipDaysArray = new int [skip.size()];
		for ( int i = 0; i < skip.size(); i++)
		{
			skipDaysArray[i] = ((Integer) skip.get(i)).intValue();
		}
		
		
	}
	/**
	 * @return
	 */
	public com.cannontech.database.db.baseline.BaseLine getBaselineProperties()
	{
		if( baselineProps == null)
		{
			baselineProps = new com.cannontech.database.db.baseline.BaseLine();
			baselineProps.setDaysUsed(new Integer(30));
			baselineProps.setPercentWindow(new Integer(75));
			baselineProps.setCalcDays(new Integer(5));
			baselineProps.setExcludedWeekDays("YNNNNNY");
			baselineProps.setHolidaysUsed(new Integer(0));
			baselineProps.setBaselineName("Baseline");
		}
		return baselineProps;
	}
	/**
	 * @return
	 */
	public Vector getHistoricalCalcComponents()
	{
		return historicalCalcComponents;
	}

	/**
	 * @param vector
	 */
	public void setHistoricalCalcComponents(Vector vector)
	{
		historicalCalcComponents = vector;
	}

}

package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (2/1/2002 9:52:28 AM)
 * @author: 
 */
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.holiday.HolidaySchedule;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.holiday.DateOfHoliday;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.roles.application.CalcHistoricalRole;

public class Baseline implements Serializable
{
	//contains com.cannontech.database.db.point.calculation.CalcComponent values.
	public Vector returnPointDataMsgVector = new Vector();
	private Vector historicalCalcComponents = null;
	private java.util.GregorianCalendar nextBaselineCalcTime = null;
	private GregorianCalendar lastUpdateTimestamp = null;
	private Integer baselineCalcTime = null;//time to start calcs in seconds from midnight (14400 = 4am)
	private Integer daysPreviousToCollect = null;//time to start calcs in seconds from midnight (14400 = 4am)

	private HoursAndValues baselineHoursAndValues = null;
	private HoursAndValues percentHoursAndValues = null;

	private int[] skipDaysArray = {java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY};
	
	private com.cannontech.database.db.baseline.Baseline baselineProps = null;
	//contains java.util.Date objects.
	private Vector curtailedDates = null;
	
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
				CTILogger.info("** DAY " + nextBaselineCalcTime.get(java.util.Calendar.DAY_OF_WEEK));
				nextBaselineCalcTime.add(java.util.Calendar.DAY_OF_YEAR, 1);
			}
			else
				break;		
		}
//		nextBaselineCalcTime.set(java.util.Calendar.HOUR_OF_DAY, getBaselineCalcTime().intValue());
	
		CalcHistorical.logEvent("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime(), LogWriter.INFO);	
		CTILogger.info("...Next Baseline Calculation to occur at: " + nextBaselineCalcTime.getTime());
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
			String propValue = RoleFuncs.getGlobalPropertyValue(CalcHistoricalRole.BASELINE_CALCTIME);
			baselineCalcTime = Integer.valueOf(propValue);
			
			CalcHistorical.logEvent("Baseline calculation time = " + baselineCalcTime + ":00", LogWriter.INFO);
			CTILogger.info("Baseline calculation time from Global Properties is " + baselineCalcTime + ":00");
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
	public Vector getBaselinePointDataMsgVector(Integer pointID, HoursAndValues data)
	{
		Vector pointDataVector= new Vector();
		
		if( data != null)
		{
			PointData pointDataMsg = null;
			for (int i = 0; i < data.length(); i++)
			{
				// Must enter the baseline value into RPH twice in order for Graph to be able to draw it.
				// Begining Timestamp 00:00:01
				pointDataMsg = new PointData();
				pointDataMsg.setId(pointID.intValue());
		
				pointDataMsg.setValue( data.getValues()[i].doubleValue());
		
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(lastUpdateTimestamp.getTime());
		
				cal.set(GregorianCalendar.HOUR_OF_DAY, data.getHours()[i].intValue() + 1);
				cal.set(GregorianCalendar.MINUTE, 0);
				cal.set(GregorianCalendar.SECOND, 0);
				
				pointDataMsg.setTimeStamp(cal.getTime());
				pointDataMsg.setTime(cal.getTime());
				
				pointDataMsg.setQuality(PointQualities.NON_UPDATED_QUALITY);
				pointDataMsg.setType(PointTypes.CALCULATED_POINT);
				if( i+1 >= data.length() )
					pointDataMsg.setTags(0x00000000); //represent the last PointData message in the group
				else
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
			String propValue = RoleFuncs.getGlobalPropertyValue(CalcHistoricalRole.DAYS_PREVIOUS_TO_COLLECT);
			daysPreviousToCollect = Integer.valueOf(propValue);
			CalcHistorical.logEvent("Baseline days previous to collect is " + daysPreviousToCollect, LogWriter.INFO);
			CTILogger.info("Baseline days previous to collect is " + daysPreviousToCollect);
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
		percentHoursAndValues = null;	//reset for each point.
			
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
			CTILogger.info("LastUpdateTimestamp = " + lastUpdateTimestamp.getTime() + " : " + calcBasePointID);				
			if( nextBaselineCalcTime.getTime().compareTo(lastUpdateTimestamp.getTime()) <= 0)
			{
				//The lastUpdate time is greater than our current calculation time.
				//We already have this data!
				CTILogger.info( " BREAK BASELINETIMESTAMP TO " + lastUpdateTimestamp.getTime());				
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

				baselineHoursAndValues  = retrieveHoursAndValues(calcComponent, percentCalcComponent);
				if( baselineHoursAndValues != null)
					returnPointDataMsgVector.addAll(getBaselinePointDataMsgVector( calcBasePointID, baselineHoursAndValues ));
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
		HoursAndValues returnData = null;
		//contains java.util.Date values
		Vector validTimestampsVector = new Vector();
		
		// Parameters (attributes set per pointId.
		retrieveBaselineAttributes(calcComponent.getPointID().intValue());
		retrieveCurtailedDays(calcComponent.getPointID().intValue());
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
			else if( isCurtailedDate(tempCal))
			{	//Invalid. Previously curtailed date.  Point.pointID ->DeviceCustomerList.Deviceid ->Customer accepted offer.
				CTILogger.info("PREVIOUSLY CURTAILED: " + tempCal.getTime());
				validData = false;
			}
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
						CTILogger.info("First day to check and we don't have data.  Going to skip and wait longer! " + tempCal.getTime());
						return null;
					}
				}
				else if(currentData.length() < 24)
				{	//Invalid.  Not enough data.
					CTILogger.info("NOT ENOUGH DATA: Only " + currentData.length() + " values found.");
					validData = false;
					if( validTimestampsVector.size() == 0)
					{
						/* TODO  - Add check for, do we have any data greater than yesterday?  If so, we must skip yesterday and carry on with our life!  David/Stacey choice.*/						
						CTILogger.info("First day to check and we don't have a full days worth of data.  Going to skip and wait longer! " + tempCal.getTime());
						return null;
					}
				}
				else if(baselineProps.getPercentWindow().intValue() > 0 
						&& percentCalcComponent != null)
				{	
					if( percentHoursAndValues == null)
					{
						Vector tempTimestamp = new Vector(1);
						tempTimestamp.add(lastUpdateTimestamp.getTime());

						//Attempt to first load data from the database.
						percentHoursAndValues = retrieveData(percentCalcComponent.getComponentPointID(), tempTimestamp);
						if( percentHoursAndValues == null)
						{
							try
							{
								Baseline baselineClass = (Baseline)CtiUtilities.copyObject(this);
								returnPointDataMsgVector.addAll(baselineClass.main(percentCalcComponent.getComponentPointID()));
								percentHoursAndValues = baselineClass.baselineHoursAndValues;
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}											

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
				CTILogger.info((java.util.Date)validTimestampsVector.get(i));
			
			returnData = retrieveData(calcComponent.getComponentPointID(), validTimestampsVector);
		}
		return returnData;
	}
	
	
	private boolean isHoliday(GregorianCalendar cal)
	{
		HolidaySchedule schedule = new HolidaySchedule(getBaselineProperties().getHolidaysUsed());
		try
		{
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, schedule);
			schedule = (HolidaySchedule)t.execute();
		}
		catch(Exception e)
		{
			CTILogger.error(e.getMessage(), e);
		}
		
		for ( int j = 0; j < schedule.getHolidayDatesVector().size(); j++)
		{
			if( ( ( cal.get(java.util.Calendar.MONTH) == 
				((DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayMonth().intValue())
				&& (cal.get(java.util.Calendar.DAY_OF_MONTH) ==
				((DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayDay().intValue())) )
			{
				if(((DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayYear().intValue() > 0)
				{
					if((cal.get(java.util.Calendar.YEAR) ==
						((DateOfHoliday)schedule.getHolidayDatesVector().get(j)).getHolidayYear().intValue()))
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
		if( data != null && percentHoursAndValues != null)
		{
			for (int i = 0; i < data.length(); i ++)
			{
				if( ((Integer)data.getHours()[i]).intValue() == ((Integer)percentHoursAndValues.getHours()[i]).intValue())
				{
					if( ((Double)data.getValues()[i]).doubleValue() <  ((Double)percentHoursAndValues.getValues()[i]).doubleValue() * .75d)
					{
						CTILogger.info("DATA IS < 75% for: " + cal.getTime() + " - " + data.getValues()[i] + " < " + percentHoursAndValues.getValues()[i]);
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
	
	private boolean isCurtailedDate(GregorianCalendar cal)
	{
		if( getCurtailedDates() != null)
		{
			for (int i = 0; i < getCurtailedDates().size(); i++)
			{
				if( ((Date)getCurtailedDates().get(i)).compareTo(cal.getTime()) == 0)
					return true;
			}
		}
		return false;	
	}
	private void retrieveCurtailedDays(int pointID)
	{

		java.sql.PreparedStatement preparedStatement = null;
		java.sql.Connection conn = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			String sql = "SELECT DISTINCT LMEEPO.OFFERDATE FROM LMENERGYEXCHANGEPROGRAMOFFER LMEEPO, "+
							"DEVICECUSTOMERLIST DCL, " +
							"LMENERGYEXCHANGECUSTOMERREPLY LMEECR, "+
							"POINT P " +
							"WHERE DCL.CUSTOMERID = LMEECR.CUSTOMERID " +
							"AND LMEECR.OFFERID = LMEEPO.OFFERID " +
							"AND DCL.DEVICEID = P.PAOBJECTID " +
							"AND LMEECR.ACCEPTSTATUS = 'Accepted' " + 
							"AND P.POINTID = " + pointID;
			preparedStatement = conn.prepareStatement(sql);
			rset = preparedStatement.executeQuery();
		
			curtailedDates = new Vector();
			while (rset.next())
			{
				Timestamp ts = rset.getTimestamp(1);
				Date date = new Date();
				date.setTime(ts.getTime());
				curtailedDates.add(date);
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
	public static HoursAndValues retrieveData(Integer pointID, Vector validTimestampsVector)
	{
		HoursAndValues returnData = null;
		long DAY = 86400000;
		PointData pointDataMsg = null;
	
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
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if (conn == null)
			{
				CTILogger.info("Error getting database connection.");
				CalcHistorical.logEvent("Error getting database connection.", LogWriter.ERROR);			
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
					PointUnit pUnit = new PointUnit();
					pUnit.setPointID(pointID);
					pUnit.setDbConnection(conn);
					pUnit.retrieve();
					
					java.util.TreeMap treeMap = buildTreeMap(values, hours);					
					java.util.Set keySet = treeMap.keySet();
					java.util.Collection keyVals = treeMap.values();

					Object [] tempArray = new Double[keyVals.size()];
					returnData = new HoursAndValues(keySet.size(), keyVals.size());
					keySet.toArray(returnData.getHours());
					tempArray = keyVals.toArray();
		
					for (int i = 0; i < keyVals.size(); i++)
					{
						Double[] v = (Double[])tempArray[i];
						double counter = v[0].doubleValue();
						double totalVal = v[1].doubleValue();
						
						if( pUnit.getUomID().intValue() == PointUnits.UOMID_KW)
							returnData.values[i] = new Double(totalVal/counter);	//kW values.
						else if( pUnit.getUomID().intValue() == PointUnits.UOMID_KWH)
							returnData.values[i] = new Double(totalVal/validTimestampsVector.size());	//kWh values
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
	
		return returnData; //hours and values of the retreived data.
	}
	
	private static int getAdjustedHour(java.sql.Timestamp ts)
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
	private static Vector appendTimestamps(StringBuffer sql, Vector validTimestampsVector)
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
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.info(getClass() + ":  Error getting database connection.");
				CalcHistorical.logEvent(getClass() + ":  Error getting database connection.", LogWriter.ERROR);
				return;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
	
				baselineProps = new com.cannontech.database.db.baseline.Baseline();
				while( rset.next())
				{
					baselineProps.setDaysUsed(new Integer(rset.getInt(1)));
					baselineProps.setPercentWindow(new Integer(rset.getInt(2)));
					baselineProps.setCalcDays(new Integer(rset.getInt(3)));
					baselineProps.setExcludedWeekdays(rset.getString(4));
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
			
		for (int i = 0; i < baselineProps.getExcludedWeekdays().toString().length(); i++)
		{
			if(baselineProps.getExcludedWeekdays().charAt(i) == 'Y')
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
	public com.cannontech.database.db.baseline.Baseline getBaselineProperties()
	{
		if( baselineProps == null)
		{
			baselineProps = new com.cannontech.database.db.baseline.Baseline();
			baselineProps.setDaysUsed(new Integer(30));
			baselineProps.setPercentWindow(new Integer(75));
			baselineProps.setCalcDays(new Integer(5));
			baselineProps.setExcludedWeekdays("YNNNNNY");
			baselineProps.setHolidaysUsed(new Integer(0));
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

	/**
	 * @return
	 */
	public Vector getCurtailedDates()
	{
		return curtailedDates;
	}

}

package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (12/4/2000 2:04:30 PM)
 * @author: 
 */

import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import java.util.Vector;
import java.util.GregorianCalendar;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.data.lite.LitePoint;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.database.data.point.PointQualities;
//import com.cannontech.database.data.point.PointTypes;

public final class CalcHistorical
{
	private String databaseAlias = "yukon";
	private static final String VERSION = "2.1.11";

	private Integer aggregationInterval = null;//interval in seconds between calculations
	private static com.cannontech.message.dispatch.ClientConnection dispatchConnection = null;
	private GregorianCalendar nextCalcTime = null;

	//public static LitePoint litePoint = null;
	public static int pointID = 0;
	public static com.cannontech.message.dispatch.message.Multi multiMsg = null;
	public static Vector calcComponentVector = null;


	public static Vector calcBasePoints;
	public static java.util.List allHistoricalCalcComponentsList;

	public static boolean isService = true;
	public static Thread sleepThread = null;
	private static com.cannontech.common.util.LogWriter logger = null;

	//private GregorianCalendar nextBaselineCalcTime = null;
	
	//private Integer baselineCalcTime = null;//time to start calcs in seconds from midnight (14400 = 4am)
	//private Integer daysPreviousToCollect = null;//time to start calcs in seconds from midnight (14400 = 4am)
	//private Vector calcDatesUsedForBaselineVector = null;

	//private Integer [] dailyHoursArray = null;
	//private Double [] dailyValuesArray = null;
	//private java.util.TreeMap baselineTreeMap = null;

	//private int[] skipDaysArray = {java.util.Calendar.SATURDAY, java.util.Calendar.SUNDAY};

	////DEFAULT VALUES!!!
	//private int daysUsed = 30;
	//private int percentWindow = 80;
	//private int calcDays = 5;
	//private char[] excludedWeekdays = {'N', 'N', 'N', 'N', 'N', 'Y', 'Y'};
	//private int holidaysUsed = 0;
/**
 * PointCalculationModule constructor comment.
 */
public CalcHistorical() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void figureNextCalcTime()
{
	if( this.nextCalcTime == null )
	{
		this.nextCalcTime = new GregorianCalendar();
	}
	else
	{
		GregorianCalendar tempCal = new GregorianCalendar();
		long nowInMilliSeconds = tempCal.getTime().getTime();
		long aggIntInMilliSeconds = getAggregationInterval().longValue() * 1000;
		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;

		/* if it hasn't been at least one full aggregation interval since we last did a
			 calculation, wait until next scheduled calculation time */
		if( tempSeconds < (this.nextCalcTime.getTime().getTime()+aggIntInMilliSeconds) )
		{
			tempSeconds += aggIntInMilliSeconds;
		}
		
		this.nextCalcTime = new GregorianCalendar();
		this.nextCalcTime.setTime(new java.util.Date(tempSeconds));
	}
	logEvent("...Next Historical Calculation to occur at: " + nextCalcTime.getTime(),com.cannontech.common.util.LogWriter.INFO );
	System.out.println("...Next Historical Calculation to occur at: " + nextCalcTime.getTime());
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public double figurePointDataMsgValue(Vector calcComponentVector, Vector currentRawPointHistoryVector)
{
	double returnValue = 0;
System.out.println("ENTERING figurePointDataMsgValue");
	for(int i=0;i<calcComponentVector.size();i++)
	{
		if( ((CalcComponent)calcComponentVector.get(i)).getComponentType().equalsIgnoreCase(CalcComponentTypes.OPERATION_COMP_TYPE) )
		{
			for(int j=0;j<currentRawPointHistoryVector.size();j++)
			{
				if( ((CalcComponent)calcComponentVector.get(i)).getComponentPointID().intValue() == ((RawPointHistory)currentRawPointHistoryVector.get(j)).getPointID().intValue() )
				{
					if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION) )
					{
						returnValue = returnValue + ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
					}
					else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.SUBTRACTION_OPERATION) )
					{
						returnValue = returnValue - ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
					}
					else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.MULTIPLICATION_OPERATION) )
					{
						returnValue = returnValue * ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
					}
					else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION) )
					{
						if( ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue() != 0.0 )
						{
							returnValue = returnValue / ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
						}
						else
						{
							logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()", com.cannontech.common.util.LogWriter.ERROR);
							System.out.println("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
						}
					}
					else
					{
						logEvent("Can not determine the Operation in CalcHistorical::figurePointDataMsgValue()", com.cannontech.common.util.LogWriter.ERROR);
						System.out.println("Can not determine the Operation in CalcHistorical::figurePointDataMsgValue()");
					}
				}
			}
		}
		else if( ((CalcComponent)calcComponentVector.get(i)).getComponentType().equalsIgnoreCase(CalcComponentTypes.CONSTANT_COMP_TYPE) )
		{
			if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION) )
			{
				returnValue = returnValue + ((CalcComponent)calcComponentVector.get(i)).getConstant().doubleValue();
			}
			else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.SUBTRACTION_OPERATION) )
			{
				returnValue = returnValue - ((CalcComponent)calcComponentVector.get(i)).getConstant().doubleValue();
			}
			else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.MULTIPLICATION_OPERATION) )
			{
				returnValue = returnValue * ((CalcComponent)calcComponentVector.get(i)).getConstant().doubleValue();
			}
			else if( ((CalcComponent)calcComponentVector.get(i)).getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION) )
			{
				if( ((CalcComponent)calcComponentVector.get(i)).getConstant().doubleValue() != 0.0 )
				{
					returnValue = returnValue / ((CalcComponent)calcComponentVector.get(i)).getConstant().doubleValue();
				}
				else
				{
					logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()", com.cannontech.common.util.LogWriter.ERROR);
					System.out.println("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
				}
			}
			else
			{
				logEvent("Can not determine the Operation in CalcHistorical::figurePointDataMsgValue()", com.cannontech.common.util.LogWriter.ERROR);
				System.out.println("Can not determine the Operation in CalcHistorical::figurePointDataMsgValue()");
			}
		}
		else if( ((CalcComponent)calcComponentVector.get(i)).getComponentType().equalsIgnoreCase(CalcComponentTypes.FUNCTION_COMP_TYPE) )
		{
			//For this to ever work the query in getCalcComponentPoints(...)
			// must be changed at the spot of != 'Function'
//			System.out.println("Can not handle ComponentType of Function yet CalcHistorical::figurePointDataMsgValue()");
		}
		else
		{
			System.out.println("Can not determine the ComponentType in CalcHistorical::figurePointDataMsgValue()");
		}
	}
	return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/2000 11:43:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAggregationInterval()
{
	if( aggregationInterval == null )
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			aggregationInterval = new Integer( bundle.getString("calc_historical_interval") );
			logEvent(" (config.prop)Aggregation interval = " + aggregationInterval + " seconds.", com.cannontech.common.util.LogWriter.INFO);
			System.out.println("[" + new java.util.Date() + "]  Aggregation interval was found in config.properties is " + aggregationInterval + " seconds.");
		}
		catch( Exception e)
		{
			e.printStackTrace();
			aggregationInterval = new Integer(900);
			logEvent("Aggregation interval NOT found in config.properties, defaulted to " + aggregationInterval + " seconds.", com.cannontech.common.util.LogWriter.INFO);
			System.out.println("[" + new java.util.Date() + "]  Aggregation interval was NOT found in config.properties, defaulted too " + aggregationInterval + " seconds.");
			logEvent("Add row named 'calc_historical_interval' to config.properties with the time between calculations in seconds", com.cannontech.common.util.LogWriter.DEBUG);
			System.out.println("[" + new java.util.Date() + "]  Add row named 'calc_historical_interval' to config.properties with the time between calculations in seconds");
		}
	}
	
	return aggregationInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public GregorianCalendar getCalcHistoricalLastUpdateTimeStamp(int calcPointID, String databaseAlias)
{
	//January 1, 1980
	GregorianCalendar returnTimeStamp = new GregorianCalendar(1980, 0, 1, 0, 0);

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		preparedStatement = conn.prepareStatement("SELECT LASTUPDATE FROM DYNAMICCALCHISTORICAL WHERE POINTID = " + String.valueOf(calcPointID));
		rset = preparedStatement.executeQuery();
		
		if( rset == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			returnTimeStamp.set(java.util.Calendar.YEAR, tempCal.get(java.util.Calendar.YEAR));
			returnTimeStamp.set(java.util.Calendar.DAY_OF_YEAR, tempCal.get(java.util.Calendar.DAY_OF_YEAR));

			logEvent(" ####### UPDATE DYNAMICCALCHISTORICAL WITH POINTID = "+ String.valueOf(calcPointID), com.cannontech.common.util.LogWriter.INFO);
			logEvent(" ####### USING LASTUPDATE = " + returnTimeStamp, com.cannontech.common.util.LogWriter.INFO);
	
			System.out.println(" ####### UPDATE DYNAMICCALCHISTORICAL WITH POINTID = "+ String.valueOf(calcPointID));
			System.out.println(" ####### USING LASTUPDATE = " + returnTimeStamp);
		}
		
		java.sql.Timestamp tempTimestamp = null;
		while (rset.next())
		{
			tempTimestamp = rset.getTimestamp(1);
			if( tempTimestamp != null )
			{
				returnTimeStamp.setTime(tempTimestamp);
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
			if( preparedStatement != null )
				preparedStatement.close();			
			if (rset != null)
				rset.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			System.out.println(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL");
			logEvent(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL", com.cannontech.common.util.LogWriter.DEBUG);
			e.printStackTrace();
		}
	}
	return returnTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public Vector getCalcHistoricalPointDataMsgVector(int ptID,
	Vector calcComponentVector,
	GregorianCalendar lastCalcPointRawPointHistoryTimeStamp,
	String databaseAlias)
{
	//System.out.println("[" + new java.util.Date() + "] ENTER getCalcHistoricalPointDataMsgVector");
	
	Vector returnVector = null;

	Vector tempRawPointHistoryVector = null;
	Vector rawPointHistoryVectorOfVectors = new Vector();

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
		preparedStatement =	conn.prepareStatement("SELECT CHANGEID, POINTID, TIMESTAMP, QUALITY, VALUE FROM RAWPOINTHISTORY WHERE POINTID = ? AND TIMESTAMP > ? ORDER BY TIMESTAMP");
		for (int i = 0; i < calcComponentVector.size(); i++)
		{
			if (((CalcComponent) calcComponentVector.get(i)).getComponentPointID().intValue() > 0)
			{
				preparedStatement.setInt(1, ((CalcComponent) calcComponentVector.get(i)).getComponentPointID().intValue());
				preparedStatement.setTimestamp(2, new java.sql.Timestamp(lastCalcPointRawPointHistoryTimeStamp.getTime().getTime()));
				
				rset = preparedStatement.executeQuery();
				tempRawPointHistoryVector = new Vector();

				int count = 0;
				//System.out.println("[" + new java.util.Date() + "] ** RSET RETRIEVED * getCalcHistoricalPointDataMsgVector");
				while (rset.next())
				{
					Integer changeID = new Integer(rset.getInt(1));
					Integer pointID = new Integer(rset.getInt(2));
					GregorianCalendar timestamp = new GregorianCalendar();
					timestamp.setTime(rset.getTimestamp(3));

					Integer quality = new Integer(rset.getInt(4));
					Double value = new Double(rset.getDouble(5));

					RawPointHistory rph = new RawPointHistory(changeID, pointID, timestamp, quality, value);

					tempRawPointHistoryVector.add(rph);
					count++;
				}
				//System.out.println("[" + new java.util.Date() + "] ** RSET FINISHED *" + count + "* getCalcHistoricalPointDataMsgVector");
				if (tempRawPointHistoryVector != null && tempRawPointHistoryVector.size() > 0)
				{
					rawPointHistoryVectorOfVectors.add(tempRawPointHistoryVector);
				}
				else
				{
					rawPointHistoryVectorOfVectors.add(new Vector(0)); //add vector with size == 0
				}

				tempRawPointHistoryVector = null;
			}
		}

		//MOVED TO END OF FUNCTION.. hoping to solve a dispatch block.
//		returnVector =
		//	parseAndCalculateRawPointHistories(rawPointHistoryVectorOfVectors, litePoint, calcComponentVector, lastCalcPointRawPointHistoryTimeStamp);
	}
	catch (java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if (preparedStatement != null)
				preparedStatement.close();
			if (rset != null)
				rset.close();
			if (conn != null)
				conn.close();
		}
		catch (java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	returnVector = parseAndCalculateRawPointHistories(rawPointHistoryVectorOfVectors);
	//System.out.println("[" + new java.util.Date() + "'] EXIT getCalcHistoricalPointDataMsgVector");
	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public com.cannontech.message.dispatch.ClientConnection getDispatchConnection()
{
	if( dispatchConnection == null || !dispatchConnection.isValid() )
	{
		String host = null;
		int port;
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			host = bundle.getString("dispatch_machine");
			port = (new Integer(bundle.getString("dispatch_port"))).intValue();
		}
		catch( java.util.MissingResourceException mre)
		{
			mre.printStackTrace();
			host = "127.0.0.1";
			port = 1510;
		}
		catch(NumberFormatException nfe)
		{
			nfe.printStackTrace();
			port = 1510;
		}
		dispatchConnection = new com.cannontech.message.dispatch.ClientConnection();

		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("Calc Historical");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 3600 );  // 1 hour should be OK

		//dispatchConnection.addObserver(this);
		dispatchConnection.setHost(host);
		dispatchConnection.setPort(port);
		dispatchConnection.setAutoReconnect(true);
		dispatchConnection.setRegistrationMsg(reg);
		
		try
		{
			dispatchConnection.connect();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	return dispatchConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public java.util.List getHistoricalCalcComponents(String databaseAlias)
{
	java.util.ArrayList returnList = new java.util.ArrayList();

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		// don't collect componenttype = 'Function'


		String statement = ("SELECT CC.POINTID, CC.COMPONENTORDER, CC.COMPONENTTYPE, CC.COMPONENTPOINTID, CC.OPERATION, CC.CONSTANT, CC.FUNCTIONNAME "+
							"FROM CALCCOMPONENT CC, POINT P, YUKONPAOBJECT PAO, CALCBASE CB "+
							"WHERE CC.COMPONENTPOINTID = P.POINTID "+
							"AND CC.POINTID = CB.POINTID "+
							"AND P.PAOBJECTID = PAO.PAOBJECTID "+
							"AND PAO.DISABLEFLAG = 'N' "+
//							"AND CC.COMPONENTTYPE != 'Function' "+
							"AND CB.UPDATETYPE = 'Historical' "+
							"ORDER BY CC.POINTID, CC.COMPONENTORDER");

		//SELECT POINTID, COMPONENTORDER, COMPONENTTYPE, COMPONENTPOINTID, OPERATION, CONSTANT, FUNCTIONNAME FROM CALCCOMPONENT WHERE POINTID IN (SELECT POINTID FROM CALCBASE WHERE UPDATETYPE = 'Historical') AND COMPONENTTYPE != 'Function' ORDER BY POINTID, COMPONENTORDER
		preparedStatement = conn.prepareStatement( statement );
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


		//Store just the distinct CalcPoint ID's
		calcBasePoints = new Vector();
		for ( int i = 0; i < returnList.size(); i++)
		{
			Integer ptID = ((CalcComponent)returnList.get(i)).getPointID();
			if ( !calcBasePoints.contains(ptID))
				calcBasePoints.add(ptID);		
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
public GregorianCalendar getNextCalcTime()
{
	return this.nextCalcTime;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 11:13:14 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public static int getPointID()
{
	return pointID;
}
public static void logEvent(String event, int severity)
{
	if (logger == null)
	{
		try
		{
			String dataDir = "../log/";
			java.io.File file = new java.io.File( dataDir );
			file.mkdirs();

			String className = "calchist";
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			String filename = dataDir + className + ".log";
			java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter(className, com.cannontech.common.util.LogWriter.DEBUG, writer);

			logger.log("Starting up " + className, com.cannontech.common.util.LogWriter.INFO );
			logger.log("Version: " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() +VERSION+ ".", com.cannontech.common.util.LogWriter.INFO );

		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}
	logger.log( event, severity);
}
/**
 * Starts the application.
 */
public static void main(java.lang.String[] args)
{
	CalcHistorical calcHistorical = new CalcHistorical();
	Baseline baseLine = new Baseline();

	
	String databaseAlias;
	if (args.length > 0)
	{
		databaseAlias = args[0];
	}
	else
	{
		databaseAlias = new String("yukon");
		calcHistorical.setDatabaseAlias(databaseAlias);
	}

	GregorianCalendar lastCalcPointRawPointHistoryTimeStamp;
	int calcComponentIndex = 0;

	Vector tempPointDataMsgVector = null;
	java.util.Date now = null;

	
	calcHistorical.figureNextCalcTime();
	baseLine.figureNextBaselineCalcTime();
	
	System.out.println("[" + new java.util.Date() + "]  Calc Historical (Version: " + VERSION + ") Started.");
	System.out.println(" DISCLAIMER:  Percent Window is not yet functional in determining the Baseline for a point.");

	logEvent("Calc Historical (Version: " + VERSION + ") Started.", com.cannontech.common.util.LogWriter.INFO);
	logEvent(" DISCLAIMER:  Percent Window is not yet functional in determining the Baseline for a point.", com.cannontech.common.util.LogWriter.INFO);

	calcHistorical.sleepThread = new Thread();

	do
	{
		now = new java.util.Date();
		
		if (calcHistorical.getNextCalcTime().getTime().compareTo(now) <= 0)
		{
			System.out.println("[" + new java.util.Date() + "]  Starting period calculating of historical points.");
			logEvent("Starting period calculating of historical points.", com.cannontech.common.util.LogWriter.INFO);
			
			// Get a list of all 'Historical' CalcPoints and their fields from Point table in database. 
			// Get a list of all CalcComponents and their fields from calcComponent table in database
			allHistoricalCalcComponentsList = calcHistorical.getHistoricalCalcComponents(databaseAlias);
			
			for (int i = 0; i < calcBasePoints.size(); i++)
			{
				if( calcHistorical.isService)	//Check for service exit
				{
					calcHistorical.setPointID(((Integer)calcBasePoints.get(i)).intValue());
		
					calcComponentVector = new Vector();
					while (calcComponentIndex < allHistoricalCalcComponentsList.size())
					{
						// Find the calcComponents entries with pointIds matching the LitePoint (current CalcBase pointId).
						if ( getPointID() == ((CalcComponent) allHistoricalCalcComponentsList.get(calcComponentIndex)).getPointID().intValue())
						{
							calcComponentVector.add(allHistoricalCalcComponentsList.get(calcComponentIndex));
							calcComponentIndex++;
						}
						else
						{
							break;
						}
					}
					if ( !calcComponentVector.isEmpty() )
					{
						lastCalcPointRawPointHistoryTimeStamp = calcHistorical.getCalcHistoricalLastUpdateTimeStamp(getPointID(), databaseAlias);
						//lastCalcPointRawPointHistoryTimeStamp = calcHistorical.getLastCalcPointRawPointHistoryTimeStamp(litePoint.getPointID(), databaseAlias);
		
						tempPointDataMsgVector = calcHistorical.getCalcHistoricalPointDataMsgVector(getPointID(), calcComponentVector, lastCalcPointRawPointHistoryTimeStamp, databaseAlias);
						calcHistorical.writeMultiMessage( tempPointDataMsgVector );
					}
					//else
						//System.out.println("Skipping point "+ litePoint.getPointID());
				}
				else
				{
					//Forcing the for loop exit
					i = calcBasePoints.size();
					break;
				}
			}

			System.out.println("[" + new java.util.Date() + "]  Done with period calculating of historical points.");
			logEvent("Done with period calculating of historical points.", com.cannontech.common.util.LogWriter.INFO);
			calcHistorical.figureNextCalcTime();

			// Clear out the lists.
			allHistoricalCalcComponentsList.clear();
			calcComponentIndex = 0;
		}

		// CALCULATE BASELINE TOTALS.
		if (baseLine.getNextBaselineCalcTime().getTime().compareTo(now) <= 0)
		{
			System.out.println("[" + new java.util.Date() + "]  Starting baseline calculation of baseline calc points.");
			logEvent("Starting baseline calculation of baseline calc points.", com.cannontech.common.util.LogWriter.INFO);

			tempPointDataMsgVector = baseLine.main();
			
			calcHistorical.writeMultiMessage(tempPointDataMsgVector);

			System.out.println("[" + new java.util.Date() + "]  Done with baseline calculation of baseline calc points.");
			logEvent("Done with baseline calculation of baseline calc points.", com.cannontech.common.util.LogWriter.INFO);
			baseLine.figureNextBaselineCalcTime();
			
			// Clear out the lists.
			allHistoricalCalcComponentsList.clear();
			calcComponentIndex = 0;
		}

		try
		{
			System.gc();
			sleepThread.sleep(2000);
		}
		catch (InterruptedException ie)
		{
			System.out.println("Exiting Calc Historical");
			logEvent("Exiting Calc Historical", com.cannontech.common.util.LogWriter.ERROR);
			if (calcHistorical.getDispatchConnection().isValid())
			{
				try
				{
					calcHistorical.getDispatchConnection().disconnect();
				}
				catch (java.io.IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
			break;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (calcHistorical.getDispatchConnection().isValid())
		{
			Object msg = calcHistorical.getDispatchConnection().read(0);
			if (msg != null)
			{
				if (msg instanceof Command)
				{
					if (((Command) msg).getOperation() == Command.ARE_YOU_THERE)
					{
						System.out.println("[" + new java.util.Date() + "]  Echoing -Are You There- message back to Dispatch.");
						logEvent("Echoing -Are You There- message back to Dispatch.", com.cannontech.common.util.LogWriter.INFO);
						calcHistorical.getDispatchConnection().write(msg);
					}
				}
			}
		}
	}while (isService);

	try
	{
		dispatchConnection.disconnect();
		dispatchConnection = null;
	}
	catch(java.io.IOException ioe)
	{
		logEvent("An exception occured disconnecting from load control", com.cannontech.common.util.LogWriter.ERROR);
		System.out.println("An exception occured disconnecting from load control");
	}

	logger.getPrintWriter().close();
	logger = null;
	
	System.out.println("Exiting Calc Historical...at end");
	logEvent("Exiting Calc Historical...at end", com.cannontech.common.util.LogWriter.INFO);
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public Vector parseAndCalculateRawPointHistories(Vector rawPointHistoryVectorOfVectors )
{
	//System.out.println("[" + new java.util.Date() + "] ENTER parseAndCalculateRawPointHistories");
	if (rawPointHistoryVectorOfVectors.size() == 0)
	{
		return null;
	}

	for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++)
	{
		if (((Vector) rawPointHistoryVectorOfVectors.get(i)).size() == 0)
		{
			//if one of the rawpointhistory vectors is empty we can not matchup the timestamps
			return null;
		}
	}

	int arrayOfIndexes[] = new int[rawPointHistoryVectorOfVectors.size()];
	for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++)
	{
		arrayOfIndexes[i] = 0;
	}

	Vector tempRawPointHistoryVector = null;
	Vector returnVector = new Vector();

	boolean done = false;
	com.cannontech.message.dispatch.message.PointData pointDataMsg = null;
	GregorianCalendar targetRawPointHistoryTimeStamp = null;

	if (arrayOfIndexes[0] < ((Vector) rawPointHistoryVectorOfVectors.get(0)).size())
	{
		targetRawPointHistoryTimeStamp = ((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(0)).get(arrayOfIndexes[0]))).getTimeStamp();
	}
	else
	{
		done = true;
	}

	while (!done)
	{
		tempRawPointHistoryVector = new Vector(rawPointHistoryVectorOfVectors.size());

		while (tempRawPointHistoryVector.size() < rawPointHistoryVectorOfVectors.size() && !done)
		{
			for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++)
			{
				if (arrayOfIndexes[i] < ((Vector) rawPointHistoryVectorOfVectors.get(i)).size())
				{
					if (targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) > 0)
					{
						while (arrayOfIndexes[i] < ((Vector) rawPointHistoryVectorOfVectors.get(i)).size()
							&& targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) > 0)
						{
							arrayOfIndexes[i]++;
						}

						if (arrayOfIndexes[i] >= ((Vector) rawPointHistoryVectorOfVectors.get(i)).size())
						{
							if( tempRawPointHistoryVector != null)
								tempRawPointHistoryVector.clear();
								
							done = true;
						}
						break;
					}
					else if ( targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) < 0)
					{
						if( tempRawPointHistoryVector != null)
							tempRawPointHistoryVector.clear();
							
						targetRawPointHistoryTimeStamp = ((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp();
						break;
					}
					else if (targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) == 0)
					{
						tempRawPointHistoryVector.addElement((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i])));
					}
				}
				else
				{
					if( tempRawPointHistoryVector != null)
						tempRawPointHistoryVector.clear();
					done = true;
					break;
				}
			}
		}

		if (tempRawPointHistoryVector != null
			&& tempRawPointHistoryVector.size() > 0
			&& tempRawPointHistoryVector.size() == rawPointHistoryVectorOfVectors.size())
		{
			pointDataMsg = new com.cannontech.message.dispatch.message.PointData();
			pointDataMsg.setId(getPointID());
			pointDataMsg.setValue(figurePointDataMsgValue(calcComponentVector, tempRawPointHistoryVector));
			pointDataMsg.setTimeStamp(((RawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp().getTime());
			pointDataMsg.setTime(((RawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp().getTime());
			pointDataMsg.setQuality(PointQualities.NON_UPDATED_QUALITY);
			pointDataMsg.setType(com.cannontech.database.data.point.PointTypes.CALCULATED_POINT);
			pointDataMsg.setTags(0x00008000); //load profile tag setting
			pointDataMsg.setStr("Calc Historical");

			returnVector.addElement(pointDataMsg);

			for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++)
			{
				arrayOfIndexes[i]++;
			}
		}

		//( (RawPointHistory) tempRawPointHistoryVector.get(0) ).getTimeStamp()
		tempRawPointHistoryVector = null;
	}
	updateDynamicCalcHistorical(pointDataMsg.getTimeStamp());
	//DONE HERE>>>>
	//System.out.println("[" + new java.util.Date() + "] EXIT parseAndCalculateRawPointHistories");
	return returnVector;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 11:05:54 AM)
 * @param calcCompVector java.util.Vector
 */
public static void setCalcComponentVector(Vector newCalcCompVector)
{
	calcComponentVector = newCalcCompVector;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 10:35:23 AM)
 * @param dbAlias java.lang.String
 */
public void setDatabaseAlias(String dbAlias)
{
	databaseAlias = dbAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 11:05:20 AM)
 * @param msg com.cannontech.message.dispatch.message.Multi
 */
public static void setMultiMsg(com.cannontech.message.dispatch.message.Multi newMultiMsg)
{
	multiMsg = newMultiMsg;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 11:04:48 AM)
 * @param ltPt com.cannontech.database.data.lite.LitePoint
 */
public static void setPointID(int newPointID)
{
	pointID = newPointID;
}
public static void stopApplication()
{
	logEvent("Forced stopApplication.", com.cannontech.common.util.LogWriter.INFO);
	isService = false;
	try
	{
		dispatchConnection.disconnect();
	}
	catch(java.io.IOException ioe)
	{
		logEvent("Disconnecting dispatch failed.", com.cannontech.common.util.LogWriter.INFO);
		ioe.printStackTrace();
	}
	
	sleepThread.interrupt();

	//System.exit(0);
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2002 3:53:38 PM)
 * @param lastUpdate java.util.Date
 */
public void updateDynamicCalcHistorical(java.util.Date lastUpdate)
{
	//System.out.println("[" + new java.util.Date() + "] ENTER updateDynamicCalcHistorica");
	StringBuffer pSql = new StringBuffer("UPDATE DYNAMICCALCHISTORICAL SET LASTUPDATE =? WHERE POINTID = " + getPointID());
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		pstmt = conn.prepareStatement(pSql.toString());
		pstmt.setTimestamp(1, new java.sql.Timestamp(lastUpdate.getTime()));
		pstmt.executeUpdate();
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
		//throw e;
	}
	finally
	{	
		try
		{
			if( pstmt != null )
				pstmt.close();
			if( conn != null )
				conn.close();			
		}
		catch( java.sql.SQLException e )
		{
			System.out.println(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL");
			logEvent(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL", com.cannontech.common.util.LogWriter.DEBUG);
			e.printStackTrace();
			
		}
	}
	//System.out.println("[" + new java.util.Date() + "] EXIT updateDynamicCalcHistorica");
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 10:50:47 AM)
 */
public void writeMultiMessage(Vector pointDataMsgVector)
{
	//System.out.println("[" + new java.util.Date() + "] ENTER writeMultiMessage");
	if (pointDataMsgVector != null)
	{
		multiMsg = new com.cannontech.message.dispatch.message.Multi();
		multiMsg.getVector().addAll(pointDataMsgVector);
		if( pointDataMsgVector != null)
			pointDataMsgVector.clear();
		
		if (getDispatchConnection().isValid())
		{
			if (multiMsg.getVector().size() > 0)
			{
				System.out.println("[" + new java.util.Date() + "]  Sending " + multiMsg.getVector().size() + " point changes to Dispatch for pointID: " + getPointID());
				logEvent("Sending " + multiMsg.getVector().size() + " point changes to Dispatch for pointID: " + getPointID(), com.cannontech.common.util.LogWriter.INFO);
				getDispatchConnection().write(multiMsg);
			}
			else
			{
				System.out.println( "[" + new java.util.Date() + "]  Dispatch connection valid, but no Point Changes to send at this time for pointID: " + getPointID() );
				logEvent( "Dispatch connection valid, but no Point Changes to send at this time for pointID: " + getPointID(), com.cannontech.common.util.LogWriter.INFO);
			}
		}
		else
		{
			System.out.println("[" + new java.util.Date() + "]  Dispatch connection is not valid couldn't send point changes.");
			logEvent("Dispatch connection is not valid couldn't send point changes.", com.cannontech.common.util.LogWriter.DEBUG);
		}
	}

	if( calcComponentVector != null)	
		calcComponentVector.clear();

	if (getDispatchConnection().isValid())
	{
		Object msg = getDispatchConnection().read(0);
		if (msg != null)
		{
			if (msg instanceof Command)
			{
				if (((Command) msg).getOperation() == Command.ARE_YOU_THERE)
				{
					System.out.println("[" + new java.util.Date() + "]  Echoing -Are You There- message back to Dispatch.");
					logEvent("Echoing -Are You There- message back to Dispatch.", com.cannontech.common.util.LogWriter.INFO);
					getDispatchConnection().write(msg);
				}
			}
		}
	}
	//System.out.println("[" + new java.util.Date() + "] EXIT writeMultiMsg");
}
}

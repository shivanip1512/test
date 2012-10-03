package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (12/4/2000 2:04:30 PM)
 * @author: 
 */

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.application.CalcHistoricalRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.impl.YukonSettingsDaoImpl;

public final class CalcHistorical
{
	private Thread starter = null;

	private Integer aggregationInterval = null;//interval in seconds between calculations
	private ClientConnection dispatchConnection = null;
	private GregorianCalendar nextCalcTime = null;

	public static boolean isService = true;
	public static Thread sleepThread = null;
	private static LogWriter logger = null;
	
	private final int KW_UNITMEASURE = UnitOfMeasure.KW.getId();
	private final int KVA_UNITMEASURE = UnitOfMeasure.KVA.getId();
	private final int KVAR_UNITMEASURE = UnitOfMeasure.KVAR.getId();
	private final int KQ_UNITMEASURE = UnitOfMeasure.KQ.getId();
	
	private final int KW_KVAR_PFTYPE = 0;
	private final int KW_KQ_PFTYPE = 1;
	private final int KW_KVA_PFTYPE = 2;
	
	private class PF
	{
		final double SQRT3 = 1.7320508075688772935274463415059;		
		private int pfType = 0;
		private double kw_value = 0.0;
		private double kvar_value = 0.0;
		private double kq_value = 0.0;
		private double kva_value = 0.0;
	}

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
	logEvent(" ... Next Historical Calculation to occur at: " + nextCalcTime.getTime(),LogWriter.INFO );
	CTILogger.info(" ... Next Historical Calculation to occur at: " + nextCalcTime.getTime());
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public Double figurePointDataMsgValue(Vector calcComponentVector, Vector currentRawPointHistoryVector)
{
	double returnValue = 0;
//	double kwValue = 0; 
//	double kvarValue = 0;
	PF powerFactor = null;

//	Vector pushVector = null;	//calc-logic way of processing power factor.
	
	for(int i=0;i<calcComponentVector.size();i++)
	{
	    CalcComponent calcComponent = (CalcComponent)calcComponentVector.get(i);
		if( calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.OPERATION_COMP_TYPE) )
		{
			for(int j=0;j<currentRawPointHistoryVector.size();j++)
			{
				if( calcComponent.getComponentPointID().intValue() == ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getPointID() )
				{
					if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION) )
					{
						returnValue = returnValue + ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
					}
					else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.SUBTRACTION_OPERATION) )
					{
						returnValue = returnValue - ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
					}
					else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.MULTIPLICATION_OPERATION) )
					{
						returnValue = returnValue * ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
					}
					else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION) )
					{
						if( ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue() != 0.0 )
						{
							returnValue = returnValue / ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
						}
						else
						{
							logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
							CTILogger.info("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
							return null;
						}
					}
/*				//Calc-logic way of processing power factor.  CalcHistorical uses the setup of powerfactor_xx_xx_function.
					else if( calcComponent.getOperation().equalsIgnoreCase("push") )
					{
						if( powerFactor == null)
						{
							powerFactor = new PF();
						}
						
						LitePointUnit ltPU = new LitePointUnit( calcComponent.getComponentPointID().intValue());
						ltPU.retrieve(CtiUtilities.getDatabaseAlias());
					
					
						if( ltPU.getUomID() == KW_UNITMEASURE)
						{
							powerFactor.kw_value = ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
						}
						else if( ltPU.getUomID() == KVAR_UNITMEASURE)
						{
							powerFactor.kvar_value = ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
						}
						else if( ltPU.getUomID() == KQ_UNITMEASURE)
						{
							powerFactor.kq_value = ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
						}
						else if( ltPU.getUomID() == KVA_UNITMEASURE)
						{
							powerFactor.kva_value = ((RawPointHistory)currentRawPointHistoryVector.get(j)).getValue().doubleValue();
						}
					}	*/
					else
					{
						logEvent("Can not determine the Operation " + calcComponent.getOperation().toString()+ " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
						CTILogger.info("Can not determine the Operation  " + calcComponent.getOperation().toString() + " in CalcHistorical::figurePointDataMsgValue()");
						return null;
					}
				}
			}
		}
		else if( calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.CONSTANT_COMP_TYPE) )
		{
			if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION) )
			{
				returnValue = returnValue + calcComponent.getConstant().doubleValue();
			}
			else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.SUBTRACTION_OPERATION) )
			{
				returnValue = returnValue - calcComponent.getConstant().doubleValue();
			}
			else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.MULTIPLICATION_OPERATION) )
			{
				returnValue = returnValue * calcComponent.getConstant().doubleValue();
			}
			else if( calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION) )
			{
				if( calcComponent.getConstant().doubleValue() != 0.0 )
				{
					returnValue = returnValue / calcComponent.getConstant().doubleValue();
				}
				else
				{
					logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
					CTILogger.info("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
					return null;
				}
			}
			else
			{
				logEvent("Can not determine the Operation " + calcComponent.getOperation().toString() + " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
				CTILogger.info("Can not determine the Operation " + calcComponent.getOperation().toString() + " in CalcHistorical::figurePointDataMsgValue()");
				return null;
			}
		}
		else if( calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.FUNCTION_COMP_TYPE) )
		{

			if( calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION) )
			{
				if( powerFactor == null)
				{
					powerFactor = new PF();
				}
				powerFactor.pfType = KW_KVAR_PFTYPE;				

				//Original way of processing Power Fail.  It is done using push in calc-logic.				
				LitePointUnit ltPU = new LitePointUnit( calcComponent.getComponentPointID().intValue());
				ltPU.retrieve(CtiUtilities.getDatabaseAlias());

				for(int j = 0; j < currentRawPointHistoryVector.size(); j++)
				{
					if( calcComponent.getComponentPointID().intValue() == ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getPointID() )
					{
					
						if( ltPU.getUomID() == KW_UNITMEASURE)
						{
							powerFactor.kw_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
						}
						else if( ltPU.getUomID() == KVAR_UNITMEASURE)
						{
							powerFactor.kvar_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
						}
					}
				}				
			}

			else if( calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION) )
			{
				if( powerFactor == null)
				{
					powerFactor = new PF();
				}
				powerFactor.pfType = KW_KQ_PFTYPE;
				
				LitePointUnit ltPU = new LitePointUnit( calcComponent.getPointID().intValue());
				ltPU.retrieve(CtiUtilities.getDatabaseAlias());
				
				for(int j = 0; j < currentRawPointHistoryVector.size(); j++)
				{
					CTILogger.info(" Current RawPointHistoryVector.size() = " + currentRawPointHistoryVector.size());
					if( ltPU.getUomID() == KW_UNITMEASURE)
						powerFactor.kw_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
					else if( ltPU.getUomID() == KQ_UNITMEASURE)
						powerFactor.kq_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
				}				
			}
			else if( calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVA_FUNCTION) )
			{
				if( powerFactor == null)
				{
					powerFactor = new PF();
				}
				powerFactor.pfType = KW_KVA_PFTYPE;
				
				LitePointUnit ltPU = new LitePointUnit( calcComponent.getPointID().intValue());
				ltPU.retrieve(CtiUtilities.getDatabaseAlias());
				
				for(int j = 0; j < currentRawPointHistoryVector.size(); j++)
				{
					CTILogger.info(" Current RawPointHistoryVector.size() = " + currentRawPointHistoryVector.size());
					if( ltPU.getUomID() == KW_UNITMEASURE)
						powerFactor.kw_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
					else if( ltPU.getUomID() == KVA_UNITMEASURE)
						powerFactor.kva_value = ((LiteRawPointHistory)currentRawPointHistoryVector.get(j)).getValue();
				}
			}
			//For this to ever work the query in getCalcComponentPoints(...)
			// must be changed at the spot of != 'Function'
//			CTILogger.info("Can not handle ComponentType of Function yet CalcHistorical::figurePointDataMsgValue()");
			else if( calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) )
			{
				//This is handled in main, not here!
				return null;
			}
			else
			{
				logEvent("Can not determine the Function " + calcComponent.getFunctionName().toString() + " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
				CTILogger.info("Can not determine the Function " + calcComponent.getFunctionName().toString() + " in CalcHistorical::figurePointDataMsgValue()");
				return null;
			}
		}
		else
		{
			CTILogger.info("Can not determine the ComponentType " + calcComponent.getComponentType().toString() + " in CalcHistorical::figurePointDataMsgValue()");
			return null;
		}
	}
	if( powerFactor != null)	//aka power factor being computed here, using !null as a flag
	{
		returnValue = calculatePowerFactor(powerFactor);
	}
	return new Double(returnValue);
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
	    aggregationInterval= YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).getSettingIntegerValue(YukonSetting.INTERVAL);
			
		logEvent(" Aggregation interval = " + aggregationInterval + " seconds.", LogWriter.INFO);
		CTILogger.info("Aggregation interval from Global Properties is " + aggregationInterval + " seconds.");
	}
	return aggregationInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public static GregorianCalendar getCalcHistoricalLastUpdateTimeStamp(int calcPointID)
{
	//January 1, 1980
	GregorianCalendar returnTimeStamp = new GregorianCalendar(1980, 0, 1, 0, 0);

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		preparedStatement = conn.prepareStatement("SELECT LASTUPDATE FROM DYNAMICCALCHISTORICAL WHERE POINTID = " + String.valueOf(calcPointID));
		rset = preparedStatement.executeQuery();
		
		if( rset != null) 
	    {
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
		else
		{
		    //Default lastUpdate to TODAY, insert lastupdate and pointid into DynamicCalcHistorical table for future use.
			GregorianCalendar tempCal = new GregorianCalendar();
			returnTimeStamp.set(java.util.Calendar.YEAR, tempCal.get(java.util.Calendar.YEAR));
			returnTimeStamp.set(java.util.Calendar.DAY_OF_YEAR, tempCal.get(java.util.Calendar.DAY_OF_YEAR));
			
			if( preparedStatement != null)
			    preparedStatement.close();
			
			preparedStatement = conn.prepareStatement("INSERT INTO DYNAMICCALCHISTORICAL VALUES ( ?, ?)");
			preparedStatement.setInt(1, calcPointID);
			preparedStatement.setTimestamp(2, new java.sql.Timestamp(returnTimeStamp.getTime().getTime()));
			preparedStatement.executeUpdate();
			
			logEvent("POINTID: " + calcPointID + "   Missing from DynamicCalcHistorical.  PointID was added with LastUpdate= " + returnTimeStamp.getTime(), LogWriter.INFO);
			CTILogger.info("POINTID: " + calcPointID + "   Missing from DynamicCalcHistorical.  PointID was added with LastUpdate= " + returnTimeStamp.getTime());
		}
		
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		CTILogger.debug("CalcPointID:  " + calcPointID + "    Timestamp: " + returnTimeStamp.getTime() + "  Found in DynamicCalcHistorical");
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
			CTILogger.info(" *** DYNAMICCALCHISTORICAL TABLE may be missing:  DYNAMICCALCHISTORICAL");
			logEvent(" *** DYNAMICCALCHISTORICAL TABLE may be missing:  DYNAMICCALCHISTORICAL", LogWriter.ERROR);
			e.printStackTrace();
		}
	}
	return returnTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public Vector getRawPointHistoryVectorOfVectors(Vector calcComponentVector, GregorianCalendar lastCalcPointRawPointHistoryTimeStamp)
{
	//CTILogger.info("ENTER getCalcHistoricalPointDataMsgVector");
	
	Vector tempRawPointHistoryVector = null;
	Vector rawPointHistoryVectorOfVectors = new Vector();

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		for (int i = 0; i < calcComponentVector.size(); i++)
		{
			if (((CalcComponent) calcComponentVector.get(i)).getComponentPointID().intValue() > 0)
			{
			    int compPointID = ((CalcComponent) calcComponentVector.get(i)).getComponentPointID().intValue();
			    
			    CTILogger.debug("Starting RPH Retrieve loop for PointID: " + compPointID + " - Timestamp: " + lastCalcPointRawPointHistoryTimeStamp.getTime());
				preparedStatement =	conn.prepareStatement("SELECT DISTINCT POINTID, TIMESTAMP, QUALITY, VALUE FROM RAWPOINTHISTORY WHERE POINTID = ? AND TIMESTAMP > ? ORDER BY TIMESTAMP");			    
				preparedStatement.setInt(1, compPointID);
				preparedStatement.setTimestamp(2, new java.sql.Timestamp(lastCalcPointRawPointHistoryTimeStamp.getTime().getTime()));
				
				rset = preparedStatement.executeQuery();
				tempRawPointHistoryVector = new Vector();

				int count = 0;
				CTILogger.debug(" ** RSET RETRIEVED * getCalcHistoricalPointDataMsgVector");
				while (rset.next())
				{
//					int changeID = rset.getInt(1);
				    int changeID = -1;	//HACK, we just need some value here, and we wanted to do distinct in the query.
					int pointID = rset.getInt(1);
					long time = rset.getTimestamp(2).getTime(); 
					int quality = rset.getInt(3);
					double value = rset.getDouble(4);

					LiteRawPointHistory lrph = new LiteRawPointHistory(changeID, pointID, time, quality, value);

					tempRawPointHistoryVector.add(lrph);
					count++;
				}
				CTILogger.debug(" ** RSET FINISHED *" + count + "* getCalcHistoricalPointDataMsgVector");
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
			if(preparedStatement != null)
			    preparedStatement.close();
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

	//CTILogger.info(" EXIT getCalcHistoricalPointDataMsgVector");
	return rawPointHistoryVectorOfVectors;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public ClientConnection getDispatchConnection()
{
	if( dispatchConnection == null || !dispatchConnection.isValid() )
	{
		String host = "127.0.0.1";
		int port = 1510;
		try {
		    host = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).getSettingStringValue(YukonSetting.DISPATCH_MACHINE);
            port = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).getSettingIntegerValue(YukonSetting.DISPATCH_PORT);
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}		
		dispatchConnection = new ClientConnection();

		Registration reg = new Registration();
		reg.setAppName( CtiUtilities.getAppRegistration() );
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
public Vector retrieveHistoricalCalcComponents()
{
	//contains com.cannontech.database.db.point.calculation.CalcComponent values.
	Vector calcComponents = new Vector();

	java.sql.PreparedStatement preparedStatement = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
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

			calcComponents.add(cc);
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

	return calcComponents;
}
	
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public GregorianCalendar getNextCalcTime()
{
	return this.nextCalcTime;
}

private double calculatePowerFactor(PF pFactor)
{
	double pfValue = 1.0;
	double kva = pFactor.kva_value;
	double kw = pFactor.kw_value;
	double kvar = pFactor.kvar_value;
	
	if( pFactor.pfType == KW_KVAR_PFTYPE)
	{
		// KVA = sqrt( KW^2 + KVAR^2 )
		kva = Math.sqrt( (kw*kw) + (kvar*kvar));
	}
	else if( pFactor.pfType == KW_KQ_PFTYPE)
	{
		// KVAR = ((2 * KQ) - KW) / SQRT3)
		kvar = (((2 * pFactor.kq_value) - kw) / pFactor.SQRT3);
		kva = Math.sqrt( (kw*kw) + (kvar*kvar));
	}
	else if( pFactor.pfType == KW_KVA_PFTYPE)
	{
		// all values already stored from database.
	}

	//Calc PF=KW/KVA (based on DSM2 Calculations)
	if( kvar == 0.0)
	{
		pfValue = 1.0;
	}
	else if( kva != 0 )
	{
		pfValue = kw / kva;
		// Check if this is leading
		if( kvar < 0.0 && pfValue != 1.0)
		{
			pfValue = - pfValue;
		}
	}
	else
	{
		CTILogger.info(" ERROR IN CALCULATEPOWERFACTOR::Cannot devide by 0");
	}
	return pfValue;
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
			logger = new LogWriter(className, LogWriter.DEBUG, writer);

			logger.log("Starting up " + className, LogWriter.INFO );
			logger.log("Version: " + VersionTools.getYUKON_VERSION() + ".", LogWriter.INFO );

		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}
	logger.log( event, severity);
}

public void start()
{
	Runnable runner = new Runnable()
	{
		public void run()
		{
			Baseline baseLine = new Baseline();
		
			GregorianCalendar lastCalcPointRawPointHistoryTimeStamp;
			int calcComponentIndex = 0;
		
			Vector tempPointDataMsgVector = null;
			java.util.Date now = null;
			
			figureNextCalcTime();
			baseLine.figureNextBaselineCalcTime();
			
			CTILogger.info("Calc Historical Version: " + VersionTools.getYUKON_VERSION()+ " Started.");
			logEvent("Calc Historical (Version: " + VersionTools.getYUKON_VERSION()+ ") Started.", LogWriter.INFO);
		
			sleepThread = new Thread();
		
			do
			{
				now = new java.util.Date();
				
				if( getNextCalcTime().getTime().compareTo(now) <= 0 )
				{
					CTILogger.info("Starting period calculating of historical points.");
					logEvent("Starting period calculating of historical points.", LogWriter.INFO);
					
					// Get a list of all 'Historical' CalcPoints and their fields from Point table in database. 
					// Get a list of all CalcComponents and their fields from calcComponent table in database
					//contains com.cannontech.database.db.point.calculation.CalcComponent values.
					Vector allHistoricalCalcComponentsList = retrieveHistoricalCalcComponents();
					//contains 
					Vector calcBasePoints = getCalcBasePoints(allHistoricalCalcComponentsList);
								
					//calcBasePoints, Vector of Integer pointIds for distinct calcBase points.
					 
					for (int i = 0; i < calcBasePoints.size(); i++)
					{
						if( isService)	//Check for service exit
						{
							int pointID = ((Integer)calcBasePoints.get(i)).intValue();
							
							//contains CalcComponent values.
							Vector currentCalcComponents = new Vector();
							while (calcComponentIndex < allHistoricalCalcComponentsList.size())
							{
								// Find the calcComponents entries with pointIds matching the LitePoint (current CalcBase pointId).
								if ( pointID == ((CalcComponent) allHistoricalCalcComponentsList.get(calcComponentIndex)).getPointID().intValue())
								{
									currentCalcComponents.add(allHistoricalCalcComponentsList.get(calcComponentIndex));
									calcComponentIndex++;
								}
								else
								{
									break;
								}
							}
							if ( !currentCalcComponents.isEmpty() )
							{
								lastCalcPointRawPointHistoryTimeStamp = getCalcHistoricalLastUpdateTimeStamp(pointID);
				
								Vector rphDataVectorOfVectors = getRawPointHistoryVectorOfVectors(currentCalcComponents, lastCalcPointRawPointHistoryTimeStamp);
								tempPointDataMsgVector = parseAndCalculateRawPointHistories(rphDataVectorOfVectors, pointID, currentCalcComponents);
								writeMultiMessage( tempPointDataMsgVector, pointID);
							}
							//else
								//CTILogger.info("Skipping point "+ litePoint.getPointID());
						}
						else
						{
							//Forcing the for loop exit
							i = calcBasePoints.size();
							break;
						}
					}
		
					CTILogger.info("Done with period calculating of historical points.");
					logEvent("Done with period calculating of historical points.",LogWriter.INFO);
					figureNextCalcTime();
		
					// Clear out the lists.
					allHistoricalCalcComponentsList.clear();
					calcComponentIndex = 0;
				}
		
				// CALCULATE BASELINE TOTALS.
				if (baseLine.getNextBaselineCalcTime().getTime().compareTo(now) <= 0)
				{
					CTILogger.info("Starting baseline calculation of baseline calc points.");
					logEvent("Starting baseline calculation of baseline calc points.",LogWriter.INFO);
		
					// Get a list of all 'Historical' & 'Baseline' CalcPoints and their fields from Point table in database.
					//contains com.cannontech.database.db.point.calculation.CalcComponent values. 
					Vector allBaselineCalcComponents = retrieveHistoricalCalcComponents();
					Vector calcBasePoints = getCalcBasePoints(allBaselineCalcComponents);
					baseLine.setHistoricalCalcComponents(allBaselineCalcComponents);
					
					// Loop through each calcBase point(ID).	
					for (int i = 0; i < calcBasePoints.size(); i++)
					{
						//Kind of hackery to init this thing here...but it works for now.
						baseLine.returnPointDataMsgVector = new Vector();
						Integer pointID = ((Integer)calcBasePoints.get(i));				
						tempPointDataMsgVector = baseLine.main(pointID);
						writeMultiMessage(tempPointDataMsgVector, pointID.intValue());
					}
		
					CTILogger.info("[" + new java.util.Date() + "]  Done with baseline calculation of baseline calc points.");
					logEvent("Done with baseline calculation of baseline calc points.",LogWriter.INFO);
					baseLine.figureNextBaselineCalcTime();
				}
		
				try
				{
					System.gc();
					sleepThread.sleep(2000);
				}
				catch (InterruptedException ie)
				{
					CTILogger.info("Exiting Calc Historical");
					logEvent("Exiting Calc Historical", LogWriter.ERROR);
					if (getDispatchConnection().isValid())
					{
						getDispatchConnection().disconnect();
					}
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
			} while (isService);

		
			getDispatchConnection().disconnect();
            dispatchConnection = null;
		
			logger.getPrintWriter().close();
			logger = null;
			
			CTILogger.info("Exiting Calc Historical...at end");
			logEvent("Exiting Calc Historical...at end", LogWriter.INFO);

			//be sure the runner thread is NULL
			starter = null;		
		}
	};

	if( starter == null )
	{
		starter = new Thread( runner, "CalcHistorical" );
		starter.start();
	}

}

/** 
 * Stop us
 */
public void stop()
{
	try
	{
		Thread t = starter;
		starter = null;
		t.interrupt();
	}
	catch (Exception e)
	{}
}

public boolean isRunning()
{
	return starter != null;
}

/**
 * Starts the application.
 */
public static void main(java.lang.String[] args)
{
	ClientSession session = ClientSession.getInstance(); 
	if(!session.establishSession()){
		System.exit(-1);			
	}
	  	
	if(session == null) 		
		System.exit(-1);
				
	if(!session.checkRole(CalcHistoricalRole.ROLEID)) 
	{
	  CTILogger.info("User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.");
	  System.exit(-1);				
	}
		
	System.setProperty("cti.app.name", "CalcHistorical");
	CalcHistorical calcHistorical = new CalcHistorical();
	
	calcHistorical.start();	
}


/**
 * @param allHistoricalCalcComponentsList
 * @return
 */
private Vector getCalcBasePoints(Vector calcComponents)
{
	//Store just the distinct CalcPoint ID's
	Vector calcBasePoints = new Vector();
	for ( int i = 0; i < calcComponents.size(); i++)
	{
		Integer ptID = ((CalcComponent)calcComponents.get(i)).getPointID();
		if ( !calcBasePoints.contains(ptID))
			calcBasePoints.add(ptID);		
	}
	return calcBasePoints;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public Vector parseAndCalculateRawPointHistories(Vector rawPointHistoryVectorOfVectors, int pointID, Vector calcComponents)
{
	//CTILogger.info("ENTER parseAndCalculateRawPointHistories");
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
	PointData pointDataMsg = null;
//	GregorianCalendar targetRawPointHistoryTimeStamp = null;
	long targetRPHTimeInMillis = 0;

	if (arrayOfIndexes[0] < ((Vector) rawPointHistoryVectorOfVectors.get(0)).size())
	{
	    targetRPHTimeInMillis = ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(0)).get(arrayOfIndexes[0]))).getTimeStamp();
//		targetRawPointHistoryTimeStamp = ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(0)).get(arrayOfIndexes[0]))).getTimeStamp();
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
//					if (targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) > 0)
				    if (targetRPHTimeInMillis > ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp())
					{
						while (arrayOfIndexes[i] < ((Vector) rawPointHistoryVectorOfVectors.get(i)).size()
//							&& targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) > 0)
								&& targetRPHTimeInMillis > ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp())
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
//					else if ( targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) < 0)
					else if ( targetRPHTimeInMillis < ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp())					    
					{
						if( tempRawPointHistoryVector != null)
							tempRawPointHistoryVector.clear();
							
//						targetRawPointHistoryTimeStamp = ((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp();
						targetRPHTimeInMillis = ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp();
						break;
					}
//					else if (targetRawPointHistoryTimeStamp.getTime().compareTo(((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp().getTime()) == 0)					
					else if (targetRPHTimeInMillis == ((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i]))).getTimeStamp())
					{
//						tempRawPointHistoryVector.addElement((RawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i])));
						tempRawPointHistoryVector.addElement((LiteRawPointHistory) (((Vector) rawPointHistoryVectorOfVectors.get(i)).get(arrayOfIndexes[i])));
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
			Double value = figurePointDataMsgValue(calcComponents, tempRawPointHistoryVector);
			if( value != null)
			{
				pointDataMsg = new PointData();
				pointDataMsg.setId(pointID);
				pointDataMsg.setValue(value.doubleValue());
				pointDataMsg.setTimeStamp(new Date(((LiteRawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp()));
				pointDataMsg.setTime(new Date(((LiteRawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp()));
				pointDataMsg.setPointQuality(PointQuality.Normal);
				pointDataMsg.setType(PointTypes.CALCULATED_POINT);
				pointDataMsg.setTags(PointData.TAG_POINT_LP_NO_REPORT); //load profile tag setting
				pointDataMsg.setStr("Calc Historical");

				returnVector.addElement(pointDataMsg);
				updateDynamicCalcHistorical(pointDataMsg.getTimeStamp(), pointID);
				
			}	
			for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++)
			{
				arrayOfIndexes[i]++;
			}
		}

		//( (RawPointHistory) tempRawPointHistoryVector.get(0) ).getTimeStamp()
		tempRawPointHistoryVector = null;
	}
	//DONE HERE>>>>
	//CTILogger.info("EXIT parseAndCalculateRawPointHistories");
	//Make the last PointData in the vector have the load profile tag only on it....this should NOW route to all listeners! (Per Corey)
	if( pointDataMsg != null)
		pointDataMsg.setTags(PointData.TAG_POINT_LOAD_PROFILE_DATA);
	
	for (int i = 0; i < returnVector.size(); i++)
	{
		PointData tempPD = (PointData)returnVector.get(i);
		CTILogger.debug("POINTID: " + tempPD.getId() + " -  TAG: " + Long.toHexString(tempPD.getTags()));
	}	
	return returnVector;
}

public void stopApplication()
{
	logEvent("Forced stopApplication.", LogWriter.INFO);
	isService = false;
	getDispatchConnection().disconnect();
	
	sleepThread.interrupt();

	//System.exit(0);
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2002 3:53:38 PM)
 * @param lastUpdate java.util.Date
 */
public static void updateDynamicCalcHistorical(java.util.Date lastUpdate, int pointID)
{
	//CTILogger.info("ENTER updateDynamicCalcHistorica");
	StringBuffer pSql = new StringBuffer("UPDATE DYNAMICCALCHISTORICAL SET LASTUPDATE =? WHERE POINTID = " + pointID);
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.Connection conn = null;
	try
	{
		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias());
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
			CTILogger.info(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL");
			logEvent(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL", LogWriter.DEBUG);
			e.printStackTrace();
			
		}
	}
	//CTILogger.info("EXIT updateDynamicCalcHistorica");
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2002 10:50:47 AM)
 */
public void writeMultiMessage(Vector pointDataMsgVector, int pointID)
{
	//CTILogger.info("ENTER writeMultiMessage");
	if (pointDataMsgVector != null)
	{
		Multi multiMsg = new Multi();
		multiMsg.getVector().addAll(pointDataMsgVector);
		
		if( pointDataMsgVector != null)
			pointDataMsgVector.clear();
		
		if (getDispatchConnection().isValid())
		{
			if (multiMsg.getVector().size() > 0)
			{
				CTILogger.info("Sending " + multiMsg.getVector().size() + " point changes to Dispatch for pointID: " + pointID);
				logEvent("Sending " + multiMsg.getVector().size() + " point changes to Dispatch for pointID: " + pointID, LogWriter.INFO);
				getDispatchConnection().write(multiMsg);
			}
			else
			{
				CTILogger.info("Dispatch connection valid, but no Point Changes to send at this time for pointID: " + pointID );
				logEvent( "Dispatch connection valid, but no Point Changes to send at this time for pointID: " + pointID, LogWriter.INFO);
			}
		}
		else
		{
			CTILogger.info("Dispatch connection is not valid couldn't send point changes.");
			logEvent("Dispatch connection is not valid couldn't send point changes.", LogWriter.DEBUG);
		}
	}

	//CTILogger.info("EXIT writeMultiMsg");
}
}

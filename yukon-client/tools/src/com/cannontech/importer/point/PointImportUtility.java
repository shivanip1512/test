/*
 * Created on Nov 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.importer.point;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.Point;


//THIS ENTIRE CLASS SHOULD BE REDONE AT SOME POINT.  DO NOT HANDLE CSV FILES LIKE THIS!
//IT UTILIZES STRING TOKENS FOR MANIPULATING THE INPUT VALUES.  BAD BAD BAD!  
//INSTEAD, USE A CSV-READING JAVA CLASS.
public class PointImportUtility
{

	private static String analogPointFileName;
	private static String statusPointFileName;
	public static final int STATUS_PT_TOKEN_COUNT = 16;
	public static final int ANALOG_PT_TOKEN_COUNT = 23;

	public static boolean processAnalogPoints(String fileLocation) 
	{
		CTILogger.info("Starting analog point file process...");
			
		java.util.ArrayList lines = preprocessTokenStrings(readFile(fileLocation), ANALOG_PT_TOKEN_COUNT);

		if( lines == null )
			return true; //continue the process


		//create an object to hold all of our DBPersistent objects
		com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();

		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
			
		for( int i = 0; i < lines.size(); i++ )
		{
			int tokenCounter = 0;

			CTILogger.info("ANALOG_PT line: " + lines.get(i).toString());
			
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
			
			if( tokenizer.countTokens() < ANALOG_PT_TOKEN_COUNT )
			{
				CTILogger.info("** Analog point line #" + i + " has less than " + ANALOG_PT_TOKEN_COUNT + " tokens, EXITING.");
				return false;
			}
			/*
			 * PointName,PointType,DeviceName,PointOffset,UOM_Text,Multiplier,DataOffset,DecimalPlaces,DeadBand,HiLimit1,
			 * LowLimit1,Duration1,HiLimit2,LowLimit2,Duration2,Archivetype,ArchInterval,NonUpdate,
			 * RateOfChange,LimitSet1,LimitSet2,HighReasonablility,LowReasonability
			 */
			Integer pointID = new Integer(Point.getNextPointID() + addCount);
			
			String pointName = tokenizer.nextElement().toString();
			if(emptyField(pointName))
			{
				missingRequiredAnalogField(i, tokenCounter);
				return false;
			}
			
			String pointType = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(pointType))
			{
				missingRequiredAnalogField(i, tokenCounter);
				return false;
			}

			com.cannontech.database.data.point.ScalarPoint analogPoint = null;
			
			//if pointType
			if (pointType.equals(new String("CalcAnalog")))
			{
				// This is a Calculated Analog point
				analogPoint = new com.cannontech.database.data.point.CalculatedPoint();
		
				// default state group ID
				analogPoint.getPoint().setStateGroupID( new Integer(-3) );
			}
			else
			{
				analogPoint = new com.cannontech.database.data.point.AnalogPoint();
			
				// default state group ID
				analogPoint.getPoint().setStateGroupID( new Integer(-1) );
			}
			
			analogPoint.getPoint().setPointID(pointID);
			analogPoint.setPointID(pointID);
			analogPoint.getPoint().setPointType(pointType);
			analogPoint.getPoint().setPointName( pointName );

			String deviceName = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(deviceName))
			{
				missingRequiredAnalogField(i, tokenCounter);
				return false;
			}
			
			Integer deviceID = findDeviceID(deviceName);
			analogPoint.getPoint().setPaoID( deviceID );

			//Character wastedToken = new Character(tokenizer.nextElement().toString().charAt(0));
			//analogPoint.getPoint().setPseudoFlag(new Character(tokenizer.nextElement().toString().charAt(0)));
			String temp = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(temp))
			{
				missingRequiredAnalogField(i, tokenCounter);
				return false;
			}
			analogPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(temp)));	
			
			// set Point Units
			analogPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			String comparer = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(comparer))
			{
				comparer = "kW";
			}

			comparer = comparer.toUpperCase();
			if( comparer.compareTo( "KW" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 0 ) );
			else if( comparer.compareTo( "KWH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 1 ) );
			else if( comparer.compareTo( "KVA" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 2) );
			else if( comparer.compareTo( "KVAR" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 3 ) );
			else if( comparer.compareTo( "KVAH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer (4) );
			else if( comparer.compareTo( "KVARH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 5 ) );
			else if( comparer.compareTo( "KVOLTS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 6 ) );
			else if( comparer.compareTo( "KQ" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 7 ) );
			else if( comparer.compareTo( "AMPS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 8 ) );
			else if( comparer.compareTo( "COUNTS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 9 ) );
			else if( comparer.compareTo( "DEGREES" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 10 ) );
			else if( comparer.compareTo( "DOLLARS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 11 ) );
			else if( comparer.compareTo( "$" ) == 0 || comparer.compareTo( "DOLLAR CHAR" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 12 ) );
			else if( comparer.compareTo( "FEET" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 13 ) );
			else if( comparer.compareTo( "GALLONS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 14 ) );
			else if( comparer.compareTo( "GAL/PM"  ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 15 ) );
			else if( comparer.compareTo( "GAS-CFT" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 16 ) );
			else if( comparer.compareTo( "HOURS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 17 ) );
			else if( comparer.compareTo( "LEVEL") == 0 || comparer.compareTo( "LEVELS") == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 18 ) );
			else if( comparer.compareTo( "MINUTES") == 0 || comparer.compareTo( "MIN") == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 19 ) );
			else if( comparer.compareTo( "MW" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 20 ) );
			else if( comparer.compareTo( "MWH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 21 ) );
			else if( comparer.compareTo( "MVA" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 22 ) );
			else if( comparer.compareTo( "MVAR" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 23 ) );
			else if( comparer.compareTo( "MVAH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 24 ) );	
			else if( comparer.compareTo( "MVARH" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 25 ) );
			else if( comparer.compareTo( "OPS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 26 ) );
			else if( comparer.compareTo( "PF" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 27 ) );
			else if( comparer.compareTo( "PERCENT" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 28 ) );
			else if( comparer.compareTo( "%" ) == 0 || comparer.compareTo( "PERCENT CHAR" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 29 ) );
			else if( comparer.compareTo( "PSI") == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 30 ) );
			else if( comparer.compareTo( "SECONDS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 31 ) );
			else if( comparer.compareTo( "F" ) == 0 || comparer.compareTo( "TEMP-F" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 32 ) );
			else if( comparer.compareTo( "C" ) == 0 || comparer.compareTo( "TEMP-C" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 33 ) );
			else if( comparer.compareTo( "VARS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 34 ) );
			else if( comparer.compareTo( "VOLTS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 35 ) );
			else if( comparer.compareTo( "VOLTAMPS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 36 ) );
			else if( comparer.compareTo( "VA" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 37 ) );
			else if( comparer.compareTo( "WATR-CFT" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 38 ) );
			else if( comparer.compareTo( "WATTS" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 39 ) );
			else if( comparer.compareTo( "HERTZ" ) == 0 || comparer.compareTo( "HZ" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 40 ) );
			else if( comparer.compareTo( "VOLTS FROM V2H" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 41 ) );
			else if( comparer.compareTo( "AMPS FROM V2H" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 42 ) );
			else if( comparer.compareTo( "LTC TAP POSITION" ) == 0 || comparer.compareTo( "TAP" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 43 ) );
			else if( comparer.compareTo( "MILES" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 44 ) );
			else if( comparer.compareTo( "MS" ) == 0 || comparer.compareTo("MILLISECONDS") == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 45 ) );
			else if( comparer.compareTo( "LTC TAP POSITION" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 45 ) );
			else if( comparer.compareTo( "PARTS PER MILLION" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 46 ) );
			else if( comparer.compareTo( "MILES PER HOUR" ) == 0 || comparer.compareTo( "MPH" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 47 ) );
			else if( comparer.compareTo( "INCHES" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 48 ) );
			else if( comparer.compareTo( "KILOMETERS PER HOUR" ) == 0 || comparer.compareTo( "KPH" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 49 ) );
			else if( comparer.compareTo( "MILIBARS" ) == 0 || comparer.compareTo( "MILLIBARS" ) == 0)
				analogPoint.getPointUnit().setUomID( new Integer( 50 ) );
			else if( comparer.compareTo( "INCHES" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 51 ) );
			else if( comparer.compareTo( "METERS PER SECOND" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 52 ) );
			else if( comparer.compareTo( "UNDEFINED" ) == 0 )
				analogPoint.getPointUnit().setUomID( new Integer( 54 ) );
			else 
				analogPoint.getPointUnit().setUomID( new Integer( 0 ) );
			
			// set default settings for BASE point
			analogPoint.getPoint().setLogicalGroup(new String("Default"));
			analogPoint.getPoint().setServiceFlag(new Character('N'));
			analogPoint.getPoint().setAlarmInhibit(new Character('N'));

			// set default settings for point ALARMING
			analogPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			analogPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );

			if (pointType.equals(new String("CalcAnalog")))
			{
				// move the token up 3
				tokenizer.nextElement().toString();
				tokenizer.nextElement().toString();
				tokenizer.nextElement().toString();
			
				((com.cannontech.database.data.point.CalculatedPoint)analogPoint).getCalcBase().setUpdateType(new String("On All Change") );
				((com.cannontech.database.data.point.CalculatedPoint)analogPoint).getCalcBase().setPeriodicRate(new Integer(1) );
			}
			else
			{
				// analog points have these values
				String tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setMultiplier(new Double(1));
				}
				
				((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setMultiplier(new Double(tokenHolder));
				
				tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDataOffset(new Double(0));
				}
				((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDataOffset(new Double(tokenHolder));
				
				//this is a little out of place...one last pointunit field that needs to be grabbed
				String decimalPlaces = tokenizer.nextElement().toString();
				if(emptyField(decimalPlaces))
				{
					decimalPlaces = "2";
				}
				analogPoint.getPointUnit().setDecimalPlaces(new Integer(decimalPlaces));
				
				tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDeadband(new Double(-1));
				}
				((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDeadband(new Double(tokenHolder));
				
				((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setTransducerType( new String("none") );
			}
			
			// make a vector for the repeaters
			java.util.Vector pointLimitVector = new java.util.Vector();
				
			int limitCount = 0;
			com.cannontech.database.db.point.PointLimit myPointLimit = null;

			// set RPT stuff
			for(int count = 0; count < 2; count++)
			{
				Double myHighLimit, myLowLimit;
				Integer myDuration;
				
				String tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					tokenHolder = "0";
				}
				myHighLimit = new Double( tokenHolder );
				
				tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					tokenHolder = "0";
				}
				myLowLimit = new Double( tokenHolder );
				
				tokenHolder = tokenizer.nextElement().toString();
				tokenCounter++;
				if(emptyField(tokenHolder))
				{
					tokenHolder = "0";
				}
				myDuration = new Integer(tokenHolder);

				if(myHighLimit.intValue() != 0 && myLowLimit.intValue() != 0)
				{
					++limitCount;
					myPointLimit = new com.cannontech.database.db.point.PointLimit();
		
					myPointLimit.setPointID(analogPoint.getPoint().getPointID());
					myPointLimit.setLowLimit(myLowLimit);
					myPointLimit.setHighLimit(myHighLimit);
					myPointLimit.setLimitDuration(myDuration) ;
					myPointLimit.setLimitNumber( new Integer(limitCount) );
	
					pointLimitVector.addElement( myPointLimit );

					myPointLimit = null;
				}
			}
			if (limitCount > 0)
			{
				// stuff the repeaters into the CCU Route
				analogPoint.setPointLimitsVector(pointLimitVector);
			}
		
			//archiving settings
			String tokenHolder2 = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder2))
			{
				tokenHolder2 = "None";
			}
			analogPoint.getPoint().setArchiveType(tokenHolder2);
			
			tokenHolder2 = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder2) || tokenHolder2.compareTo("0") == 0)
			{
				tokenHolder2 = "0 second";
			}
			analogPoint.getPoint().setArchiveInterval( getArchiveIntervalSeconds(tokenHolder2) );
			
			//alarm categories
			//grab all the alarm chars
			String alarmCategory = new String();
			String alarmStates = new String();
			int inputCounter = 0;
				
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )	
			{
				java.util.List liteAlarms = cache.getAllAlarmCategories();
				
				int alarmCategoryID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
				
				while(tokenizer.hasMoreTokens())
				{
					inputCounter++;
					
					alarmCategory = tokenizer.nextElement().toString();
					tokenCounter++;
					if(emptyField(alarmCategory))
					{
						alarmCategory = "(none)";
					}
					if(alarmCategory.compareTo("none") == 0)
						alarmCategory = "(none)";
										
					for( int j = 0; j < liteAlarms.size(); j++ )
					{
						if(((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarms.get(j)).getCategoryName().compareTo(alarmCategory) == 0)
						{
							alarmCategoryID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarms.get(j)).getAlarmStateID();
							break;
						}
					}
					
					//get the char value to put into the alarm state string for the database
					char generate = (char)alarmCategoryID;
					alarmStates += generate;
									
				}	
					
				//fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
				alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(inputCounter);
				//System.out.println(alarmStates);
				analogPoint.getPointAlarming().setAlarmStates(alarmStates);
				analogPoint.getPointAlarming().setExcludeNotifyStates(com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY);
				analogPoint.getPointAlarming().setNotifyOnAcknowledge("N");	
				analogPoint.getPointAlarming().setPointID(pointID);
			
			
			multi.getDBPersistentVector().add( analogPoint );
			++addCount;
			System.out.println("POINT #: " + (addCount) + " ADDED TO THE MULTI OBJECT.");
			}
		}
		boolean success = writeToSQLDatabase(multi);

		if( success )
		{
			
			CTILogger.info(" analog point file was processed and inserted successfully");
			
			CTILogger.info(" " + addCount + " analog points were added to the database");
		}
		else
			CTILogger.info(" analog points could NOT be added to the database");
		
	
		return success;
	}

	public static boolean processStatusPoints(String fileLocation)
	{
		CTILogger.info("Starting status point file process...");
			
		java.util.ArrayList lines = preprocessTokenStrings(readFile(fileLocation), STATUS_PT_TOKEN_COUNT);

		if( lines == null )
			return true; //continue the process
	
		//create an object to hold all of our DBPersistant objects
		com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();

		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
		
		int addCount = 0;
		
		for( int i = 0; i < lines.size(); i++ )
		{
			int tokenCounter = 0;
			
			CTILogger.info("STATUS_PT line: " + lines.get(i).toString());
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
			if( tokenizer.countTokens() < STATUS_PT_TOKEN_COUNT )
			{
				CTILogger.info("** Status point line #" + i + " has less than " + STATUS_PT_TOKEN_COUNT + " tokens, EXITING.");
				return false;
			}
		
			/*
			 *PointName,PointType,DeviceName,PointOffset,StateGroupName,CtrlType,Ctrloffset,time1,time2,
			 *Archive,State1,State2,NonUpdate,Abnormal,UnCommanded,CommandFail
			 */

			com.cannontech.database.data.point.StatusPoint statusPoint = new com.cannontech.database.data.point.StatusPoint();
			
			Integer pointID = new Integer(Point.getNextPointID() + addCount);
				
			String pointName = tokenizer.nextElement().toString();
			if(emptyField(pointName))
			{
				missingRequiredStatusField(i, tokenCounter);
				return false;
			}
			
			String pointType = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(pointType))
			{
				missingRequiredStatusField(i, tokenCounter);
				return false;
			}
			
			statusPoint.getPoint().setPointID(pointID);
			statusPoint.getPoint().setPointName( pointName );
			statusPoint.getPoint().setPointType(pointType);

			String deviceName = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(deviceName))
			{
				missingRequiredStatusField(i, tokenCounter);
				return false;
			}
			Integer deviceID = findDeviceID(deviceName);
			statusPoint.getPoint().setPaoID( deviceID );

			//Character wastedToken = new Character(tokenizer.nextElement().toString().charAt(0));
			//statusPoint.getPoint().setPseudoFlag(new Character(tokenizer.nextElement().toString().charAt(0)));
			String holder = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(holder))
			{
				missingRequiredStatusField(i, tokenCounter);
				return false;
			}
			statusPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(holder)));	

			// state group next
			String stateGroupName = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(stateGroupName))
			{
				statusPoint.getPoint().setStateGroupID(new Integer(0));
			}
			else
			{
				Integer stateGroupID = findStateGroupID(stateGroupName);
				statusPoint.getPoint().setStateGroupID(stateGroupID);
			}
			
			// set default settings for BASE point
			statusPoint.getPoint().setLogicalGroup(new String("Default"));
			statusPoint.getPoint().setServiceFlag(new Character('N'));
			statusPoint.getPoint().setAlarmInhibit(new Character('N'));

			statusPoint.getPointStatus().setInitialState(new Integer(1));
			statusPoint.getPointStatus().setControlInhibit(new Character('N'));
			statusPoint.getPointStatus().setPointID(pointID);

			// Control point settings
			String tokenHolder = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder))
				tokenHolder = "None";
							
			statusPoint.getPointStatus().setControlType(tokenHolder);
		
			tokenHolder = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder))
				tokenHolder = "0";
			Integer controlOffset = new Integer( Integer.parseInt(tokenHolder));
			
			tokenHolder = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder))
				tokenHolder = "0";
			Integer closeTime1 = new Integer( Integer.parseInt(tokenHolder));	
			
			tokenHolder = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(tokenHolder))
				tokenHolder = "0";
			Integer closeTime2 = new Integer( Integer.parseInt(tokenHolder));
		
			if (!statusPoint.getPointStatus().getControlType().equals(new String("None")) )
			{
				// there is control
				statusPoint.getPointStatus().setControlOffset(controlOffset);
			
				statusPoint.getPointStatus().setCloseTime1(closeTime1);
				statusPoint.getPointStatus().setCloseTime2(closeTime2);
			}
		
			//archiving settings
			String yesNo = tokenizer.nextElement().toString();
			tokenCounter++;
			if(emptyField(yesNo))
				yesNo = "N";
			if( yesNo == "Y" )
				statusPoint.getPoint().setArchiveType("On Change");
			else
				statusPoint.getPoint().setArchiveType("None");
			
			statusPoint.getPoint().setArchiveInterval( new Integer( 0) );
			
			//alarm categories
			//grab all the alarm chars
			String alarmCategory = new String();
			String alarmStates = new String();
			int inputCounter = 0;
				
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )	
			{
				java.util.List liteAlarms = cache.getAllAlarmCategories();
				
				int alarmCategoryID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
				
				while(tokenizer.hasMoreTokens())
				{
					inputCounter++;
					
					alarmCategory = tokenizer.nextElement().toString();
					tokenCounter++;
					if(emptyField(alarmCategory))
					{
						alarmCategory = "(none)";
					}
					for( int j = 0; j < liteAlarms.size(); j++ )
					{
						alarmCategoryID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
						
						if(((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarms.get(j)).getCategoryName().compareTo(alarmCategory) == 0)
						{
							alarmCategoryID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarms.get(j)).getAlarmStateID();
							break;
						}
					}
					
					//get the char value to put into the alarm state string for the database
					char generate = (char)alarmCategoryID;
					alarmStates += generate;
									
				}	
			
			//fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
			alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(inputCounter);
			statusPoint.getPointAlarming().setAlarmStates(alarmStates);
			statusPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			statusPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			statusPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
			statusPoint.getPointAlarming().setPointID(pointID);
			
			multi.getDBPersistentVector().add( statusPoint );
		
			++addCount;
			System.out.println("POINT #: " + (addCount) + " ADDED TO THE MULTI OBJECT.");

			}
		}

		boolean success = writeToSQLDatabase(multi);

		if( success )
		{
			
			CTILogger.info(" Status point file was processed and inserted successfully");
			
			CTILogger.info(" " + addCount + " Status points were added to the database");
		}
		else
			CTILogger.info(" Status points could NOT be added to the database");
		
		return success;
	}
	
	
	
	private static java.util.ArrayList readFile(String fileName) 
	{
		java.io.File file = new java.io.File(fileName);

		if( file.exists() )
		{
			try
			{
				java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
				java.util.ArrayList lines = new java.util.ArrayList();

				while( fileReader.getFilePointer() < fileReader.length() )
				{
					String line = fileReader.readLine();

					if( line != null && line.length() > 0 )
						lines.add( line );
				}	

				fileReader.close();
				return lines;  //file open/closed and read successfully
			}
			catch( java.io.IOException e)
			{
				CTILogger.error( e.getMessage(), e );
				return null;
			}
		}
		else
		{
			CTILogger.info( "Unable to find file '" + fileName +"'" );
			javax.swing.JOptionPane.showMessageDialog(
			PointImportWarningBox.getWarningFrame(), 
				"Unable to find file '" + fileName + "'" ,"File Not Found",
				javax.swing.JOptionPane.WARNING_MESSAGE );
		}
		return null;
	}
	
	private static boolean writeToSQLDatabase(com.cannontech.database.data.multi.MultiDBPersistent multi) 
	{
		//write all the collected data to the SQL database
		try
		{
		  multi = (com.cannontech.database.data.multi.MultiDBPersistent)
			com.cannontech.database.Transaction.createTransaction(
				   com.cannontech.database.Transaction.INSERT, 
				   multi).execute();

			return true;
		}
		catch( com.cannontech.database.TransactionException t )
		{
			CTILogger.error( t.getMessage(), t );
			return false;
		}
	}
	
	public static Integer findDeviceID(String dName)
	{
		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
			stmt = conn.createStatement();
			rset = stmt.executeQuery( "SELECT PAObjectID FROM YukonPAObject WHERE PAOName = '" + dName + "'");	
				
			//get the first returned result
			rset.next();
			return new Integer( rset.getInt(1) );
		}
			
		catch (java.sql.SQLException e) 
		{
			e.printStackTrace();
		}
		
		finally 
		{
			try 
			{
				if ( stmt != null) stmt.close();
			}
			catch (java.sql.SQLException e2) 
			{
				e2.printStackTrace();
			}
			try {
					conn.close();
				} catch(java.sql.SQLException e) { }
		}
		//strange, should not get here
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	}

	public static Integer findStateGroupID(String sgName)
	{
		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try 
		{		
			stmt = conn.createStatement();
			rset = stmt.executeQuery( "SELECT STATEGROUPID FROM STATEGROUP WHERE NAME = '" + sgName + "'");	
				
			//get the first returned result
			rset.next();
			return new Integer( rset.getInt(1) );
		}
		
		catch (java.sql.SQLException e) 
		{
			e.printStackTrace();
		}
	
		finally 
		{
			try 
			{
				if ( stmt != null) stmt.close();
			}
			catch (java.sql.SQLException e2) 
			{
				e2.printStackTrace();
			}
			try {
					conn.close();
				} catch(java.sql.SQLException e) { }
		}
		//strange, should not get here
		return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	}
	
	//Oh the hackishness...
	private static java.util.ArrayList preprocessTokenStrings(java.util.ArrayList inputLines, int pointTypeTotalFields)
	{
		java.util.ArrayList alteredInput = new java.util.ArrayList();		
		for( int i = 0; i < inputLines.size(); i++ )
		{
					
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(inputLines.get(i).toString(), ",", true);
			String currentNewLine = new String();
			String nextToken = tokenizer.nextElement().toString();
			int totalRealTokens = 0;
			while(tokenizer.hasMoreElements())
			{
				totalRealTokens++;
				if(nextToken.equals(","))
				{
					currentNewLine += "@,";
					nextToken = tokenizer.nextElement().toString();
					
				}
				else
				{
					currentNewLine += nextToken + ",";
					//move past that extra token (the delimiter itself)
					Object holder = tokenizer.nextElement();
					if(tokenizer.countTokens() != 0)
						nextToken = tokenizer.nextElement().toString();
					else
						break;
					
				}
				
			}
			//with delimiter flag set to true so that commas can be seen, things
			//get a little more interesting...
			if(totalRealTokens < pointTypeTotalFields)
			{
				int diff = pointTypeTotalFields - totalRealTokens;
				for(int y = diff; y > 0; y--)
				{
					currentNewLine += "@,";
				}
			}
			
			//System.out.println(currentNewLine);
			alteredInput.add(i, currentNewLine);
		}
		
		return alteredInput;
	}
	
	private static boolean emptyField(String input)
	{
		if(input.equals("@"))
			return true;
		else
			return false;
	}
	
	private static void missingRequiredAnalogField(int lineNumber, int fieldNumber)
	{
		String analogFields[] = new String[ANALOG_PT_TOKEN_COUNT];
			analogFields[0] = "PointName";
			analogFields[1] = "PointType";
			analogFields[2] = "DeviceName";
			analogFields[3] = "PointOffset";
			analogFields[4] = "UOM_Text";
			analogFields[5] = "Multiplier";
			analogFields[6] = "DataOffset";
			analogFields[7] = "Deadband";
			analogFields[8] = "HiLimit1";
			analogFields[9] = "LowLimit1";
			analogFields[10] = "Duration1";
			analogFields[11] = "Limit2";
			analogFields[12] = "HiLimit2";
			analogFields[13] = "LowLimit2";
			analogFields[14] = "Duration2";
			analogFields[15] = "Archivetype";
			analogFields[16] = "ArchInterval";
			analogFields[17] = "NonUpdate";
			analogFields[18] = "RateOfChange";
			analogFields[19] = "LimitSet1";
			analogFields[20] = "LimitSet2";
			analogFields[21] = "HighReasonablility";
			analogFields[22] = "LowReasonability";
		
		javax.swing.JOptionPane.showMessageDialog(
		PointImportWarningBox.getWarningFrame(), 
			"You are missing a required field on line number " + lineNumber + "\n The missing field is " + analogFields[fieldNumber]    ,"Item Not Found",
			javax.swing.JOptionPane.WARNING_MESSAGE );
	
	}
	
	private static void missingRequiredStatusField(int lineNumber, int fieldNumber)
	{
		String statusFields[] = new String[STATUS_PT_TOKEN_COUNT];
			statusFields[0] = "PointName";
			statusFields[1] = "PointType";
			statusFields[2] = "DeviceName";
			statusFields[3] = "PointOffset";
			statusFields[4] = "StateGroupName";
			statusFields[5] = "CtrlType";
			statusFields[6] = "CtrlOffset";
			statusFields[7] = "Time1";
			statusFields[8] = "Time2";
			statusFields[9] = "Archive";
			statusFields[10] = "State1";
			statusFields[11] = "State2";
			statusFields[12] = "NonUpdate";
			statusFields[13] = "Abnormal";
			statusFields[14] = "Uncommanded";
			statusFields[15] = "CommandFail";
			
		
		javax.swing.JOptionPane.showMessageDialog(
		PointImportWarningBox.getWarningFrame(), 
			"You are missing a required field on line number " + lineNumber + "\n The missing field is " + statusFields[fieldNumber]    ,"Item Not Found",
			javax.swing.JOptionPane.WARNING_MESSAGE );
	}
	
	public final static Integer getArchiveIntervalSeconds(String selectedString) 
	{
		Integer retVal = null;
		int multiplier = 1;

		if( selectedString == null)
		{
			retVal = new Integer(0);  //we have no idea, just use zero
		}
		else if( selectedString.toLowerCase().indexOf("second") != -1 || selectedString.toLowerCase().indexOf("sec") != -1 )
		{
			multiplier = 1;
		}
		else if( selectedString.toLowerCase().indexOf("minute") != -1 || selectedString.toLowerCase().indexOf("min") != -1 )
		{
			multiplier = 60;
		}
		else if( selectedString.toLowerCase().indexOf("hour") != -1 || selectedString.toLowerCase().indexOf("hrs") != -1 )
		{
			multiplier = 3600;
		}
		else if( selectedString.toLowerCase().indexOf("day") != -1 || selectedString.toLowerCase().indexOf("days") != -1 )
		{
			multiplier = 86400;
		}
		else
			multiplier = 0;  //we have no idea, just use zero
		
		try
		{
			int loc = selectedString.toLowerCase().indexOf(" ");
			retVal = new Integer( 
				multiplier * Integer.parseInt(
							  selectedString.toLowerCase().substring( 0, loc ) ));
		}
		catch( Exception e )
		{
			CTILogger.error( "Unable to parse archive interval string into seconds, using ZERO", e );
			retVal = new Integer(0);
		}

		return retVal;
	}
	
}
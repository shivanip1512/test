package com.cannontech.dbtools.dbcreator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
public class PowerFailPointCreate 
{
	//a mutable lite point used for comparisons
	private static final LitePoint DUMMY_LITE_POINT = 
					new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public PowerFailPointCreate() 
	{
		super();
	}
	
	/**
	 * Main. Start the Power Fail Point creation/insertion process.
	 * Creation date: (1/10/2001 11:18:55 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) 
	{
		PowerFailPointCreate creater = new PowerFailPointCreate();
		creater.createPowerFailPoints();
		System.exit(0);
	}

	/**
	 * Returns true when the deviceDevID does not already have 
	 * a POWER FAIL point attached to it.  
	 * The list points contains all points to search through.
	 * Creation date: (2/27/2002 10:37:56 AM)
	 * @param points java.util.List
	 * @param deviceDevID int
	 * @return boolean
	 */
	private boolean addPowerFailPointToDevice(java.util.List points, int deviceDevID )
	{
		java.util.List pointTempList = new java.util.Vector(10);
		
		DUMMY_LITE_POINT.setPaobjectID(deviceDevID);	//needed for binarySearchRepetition
		
		//searches and sorts the list!
		com.cannontech.common.util.CtiUtilities.binarySearchRepetition( 
						points,
						DUMMY_LITE_POINT, //must have the needed DeviceID set!!
						com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator,
						pointTempList );
					
		for (int i = 0; i < pointTempList.size(); i++)
		{
			com.cannontech.database.data.lite.LitePoint lp = (LitePoint)pointTempList.get(i);
			if( lp.getPointOffset() == 20)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns true if the Device is a valid container of Power Fail points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid( int type_ )
	{
		// All MCT's are valid (with the exception of LMT2 and DCT501 //
		return com.cannontech.database.data.device.DeviceTypesFuncs.isMCTOnly(type_);
	}

	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	public java.util.Vector getPowerFailPointsDeviceVector()
	{
		java.util.Vector powerFailPointDevices = new java.util.Vector(20);
		
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			java.util.List devices = cache.getAllDevices();
			java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
			
			com.cannontech.clientutils.CTILogger.info("Adding POWER FAIL points to the following devices:");
			for (int i = 0; i < devices.size(); i++)
			{
				com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i));
				if( isDeviceValid(litePaobject.getType()) )
				{
					int deviceDevID = litePaobject.getLiteID();
					
					//makes a list of points devices to add power fail points to
					if( addPowerFailPointToDevice( points, deviceDevID ))
					{
						powerFailPointDevices.addElement(litePaobject);
						com.cannontech.clientutils.CTILogger.info("\t"+litePaobject.getPaoName());
					}
				}
			}
			com.cannontech.clientutils.CTILogger.info(powerFailPointDevices.size() + " Total MCT Devices needing POWER FAIL points added.");
		} //synch
		return powerFailPointDevices;
	}
	
	/**
	 * Parses through the powerFailPointsDeviceList and creates a mutiDBPersistent
	 *  of PulseAccumulator points to be inserted as Power Fail points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	public boolean createPowerFailPoints() 
	{
		com.cannontech.clientutils.CTILogger.info("Starting Power Fail Point creation process...");
	
		java.util.Vector powerFailDevices = new java.util.Vector(20);
		powerFailDevices = getPowerFailPointsDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		for( int i = 0; i < powerFailDevices.size(); i++)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = 
				(com.cannontech.database.data.lite.LiteYukonPAObject)powerFailDevices.get(i);
	
			Integer pointID = new Integer( com.cannontech.database.db.point.Point.getNextPointID() );
			String pointType = "PulseAccumulator";
	
			// This is an Accumulator point		
			AccumulatorPoint accumPoint = (AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);
			accumPoint.setPointID(pointID);
			
			// set default settings for BASE point
			accumPoint.getPoint().setPointID(pointID);
		    accumPoint.getPoint().setPointName("POWER FAIL");
	
			Integer deviceID = new Integer( litePaobject.getLiteID());
			accumPoint.getPoint().setPaoID( deviceID );
	//		accumPoint.getPoint().setPseudoFlag(new Character('N'));	//derived attribute
			accumPoint.getPoint().setLogicalGroup(new String("Default"));
			accumPoint.getPoint().setStateGroupID( new Integer(-2) );	//default stateGroupId
			accumPoint.getPoint().setServiceFlag(new Character('N'));
			accumPoint.getPoint().setAlarmInhibit(new Character('N'));
			accumPoint.getPoint().setPointOffset(new Integer(20));	//DEFAULT FOR POWER FAIL POINTS
			accumPoint.getPoint().setArchiveType("None");	//default?
			accumPoint.getPoint().setArchiveInterval(new Integer(1));
			
			// set default settings for point POINTALARMING
			accumPoint.getPointAlarming().setPointID(pointID);
			accumPoint.getPointAlarming().setAlarmStates( accumPoint.getPointAlarming().DEFAULT_ALARM_STATES );
			accumPoint.getPointAlarming().setExcludeNotifyStates( accumPoint.getPointAlarming().DEFAULT_EXCLUDE_NOTIFY );
			accumPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			accumPoint.getPointAlarming().setNotificationGroupID(  new Integer(accumPoint.getPointAlarming().NONE_NOTIFICATIONID) );
			accumPoint.getPointAlarming().setRecipientID(new Integer(accumPoint.getPointAlarming().NONE_LOCATIONID));
	
			// set default settings for point POINTUNIT
			accumPoint.getPointUnit().setPointID(pointID);
			accumPoint.getPointUnit().setUomID(new Integer(com.cannontech.database.data.point.PointUnits.UOMID_PF));
			accumPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			
			// set default settings for point POINTACCUMULATOR
			accumPoint.getPointAccumulator().setMultiplier(new Double(1.0));
			accumPoint.getPointAccumulator().setDataOffset(new Double(0.0));
				
			multi.getDBPersistentVector().add( accumPoint );
			
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
		{
			com.cannontech.clientutils.CTILogger.info(addCount + " Power Fail Points were processed and inserted Successfully");
		}
		else
			com.cannontech.clientutils.CTILogger.info("Power Fail Points failed insertion");
			
		return success;
	}
	
	
	/**
	 * Returns true if multi is successfully inserted into the Database.
	 * Creation date: (3/31/2001 12:07:17 PM)
	 * @param multi com.cannontech.database.data.multi.MultiDBPersistent
	 */
	private boolean writeToSQLDatabase(com.cannontech.database.data.multi.MultiDBPersistent multi) 
	{
		//write all the collected data to the SQL database
		try
		{
	      multi = (com.cannontech.database.data.multi.MultiDBPersistent)com.cannontech.database.Transaction.createTransaction( com.cannontech.database.Transaction.INSERT, multi).execute();
	
			return true;
		}
		catch( com.cannontech.database.TransactionException t )
		{
			com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
			return false;
		}
	}
}
package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
public class OneDevice_AnalogPointCreate extends PointCreate
{
	int deviceID = -1;
	int createCount = 0;
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public OneDevice_AnalogPointCreate() 
	{
		super();
	}

	public OneDevice_AnalogPointCreate(int devID, int count) 
	{
		super();
		deviceID = devID;
		createCount = count;
	}
	
	/**
	 * Parses through the powerFailPointsDeviceList and creates a mutiDBPersistent
	 *  of PulseAccumulator points to be inserted as Power Fail points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	public boolean create() 
	{
		com.cannontech.clientutils.CTILogger.info("Starting One Device Analog Point creation process...");

		com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO(deviceID);
	
		//create an object to hold all of our DBPersistant objects
		com.cannontech.database.data.multi.SmartMultiDBPersistent multi = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		int pointID = com.cannontech.database.db.point.Point.getNextPointID();
		for (int i = 1; i < createCount+1; i++)
		{
			com.cannontech.clientutils.CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
			String pointType = "Analog";
			
			com.cannontech.database.data.point.AnalogPoint analogPoint = new com.cannontech.database.data.point.AnalogPoint();
			analogPoint.setPointID(new Integer(pointID));
			analogPoint.getPoint().setPointType(pointType);
			analogPoint.getPoint().setPointName( pointType + i);
			analogPoint.getPoint().setPaoID(new Integer(deviceID));
			analogPoint.getPoint().setStateGroupID( new Integer(-1) );
			analogPoint.getPoint().setServiceFlag(new Character('N'));
			analogPoint.getPoint().setAlarmInhibit(new Character('N'));
//			analogPoint.getPoint().setPseudoFlag(new Character('N'));	//derive attribute
			analogPoint.getPoint().setPointOffset(new Integer( i ));	
			analogPoint.getPoint().setArchiveType("None");	//default?
			analogPoint.getPoint().setArchiveInterval(new Integer(0));
			
			// set POINTALARMING defaults
			analogPoint.getPointAlarming().setPointID(new Integer(pointID));
			analogPoint.getPointAlarming().setAlarmStates( analogPoint.getPointAlarming().DEFAULT_ALARM_STATES );
			analogPoint.getPointAlarming().setExcludeNotifyStates( analogPoint.getPointAlarming().DEFAULT_EXCLUDE_NOTIFY );
			analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(analogPoint.getPointAlarming().NONE_NOTIFICATIONID) );
			analogPoint.getPointAlarming().setRecipientID(new Integer(analogPoint.getPointAlarming().NONE_NOTIFICATIONID));
			
	
			// set POINTUNIT defaults
			analogPoint.getPointUnit().setPointID(new Integer(pointID));
			analogPoint.getPointUnit().setUomID(new Integer(com.cannontech.database.data.point.PointUnits.UOMID_KW));
			analogPoint.getPointUnit().setDecimalPlaces(new Integer(2));
	
			// set POINTANALOG defuaults
			analogPoint.getPointAnalog().setPointID(new Integer(pointID));
			analogPoint.getPointAnalog().setDeadband(new Double(10.0));
			analogPoint.getPointAnalog().setTransducerType(new String("None"));
			analogPoint.getPointAnalog().setMultiplier(new Double(1.0));
			analogPoint.getPointAnalog().setDataOffset(new Double(0.0));
	
			multi.addDBPersistent( analogPoint );
			
			++addCount;
			pointID++;
		}
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
		{
			com.cannontech.clientutils.CTILogger.info(addCount + " One Device Analog Points were processed and inserted Successfully");
		}
		else
			com.cannontech.clientutils.CTILogger.info("One Device Analog Points failed insertion");
			
		return success;
	}
	
	
}
package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
public class PowerFailPointCreate extends PointCreate
{
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public PowerFailPointCreate() 
	{
		super();
	}
	
	/**
	 * Returns true if the Device is a valid container of Power Fail points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid( com.cannontech.database.data.lite.LiteYukonPAObject litePaobject_ )
	{
		// All MCT's are valid (with the exception of LMT2 and DCT501 //
		return com.cannontech.database.data.device.DeviceTypesFuncs.isMCTOnly(litePaobject_.getType());
	}

	/**
	 * Returns true if the Device is a valid container of Power Fail points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isPointCreated( LitePoint lp )
	{
		return ((lp.getPointOffset() == 20) && (lp.getPointType() == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT));
	}
	/**
	 * Parses through the powerFailPointsDeviceList and creates a mutiDBPersistent
	 *  of PulseAccumulator points to be inserted as Power Fail points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	public boolean create() 
	{
		com.cannontech.clientutils.CTILogger.info("Starting Power Fail Point creation process...");
	
		java.util.Vector powerFailDevices = new java.util.Vector(20);
		getDeviceVector(powerFailDevices);
	
		//create an object to hold all of our DBPersistant objects
		com.cannontech.database.data.multi.SmartMultiDBPersistent multi = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		int pointID = com.cannontech.database.db.point.Point.getNextPointID();
		for( int i = 0; i < powerFailDevices.size(); i++)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = 
				(com.cannontech.database.data.lite.LiteYukonPAObject)powerFailDevices.get(i);

			String pointType = "PulseAccumulator";

			com.cannontech.clientutils.CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
	
			// This is an Accumulator point		
			AccumulatorPoint accumPoint = (AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);
			accumPoint.setPointID(new Integer(pointID));
			
			// set default settings for BASE point
			accumPoint.getPoint().setPointID(new Integer(pointID));
		    accumPoint.getPoint().setPointName("POWER FAIL");
	
			Integer deviceID = new Integer( litePaobject.getLiteID());
			accumPoint.getPoint().setPaoID( deviceID );
	//		accumPoint.getPoint().setPseudoFlag(new Character('N'));	//derived attribute
			accumPoint.getPoint().setStateGroupID( new Integer(-2) );	//default stateGroupId
			accumPoint.getPoint().setServiceFlag(new Character('N'));
			accumPoint.getPoint().setAlarmInhibit(new Character('N'));
			accumPoint.getPoint().setPointOffset(new Integer(20));	//DEFAULT FOR POWER FAIL POINTS
			accumPoint.getPoint().setArchiveType("None");	//default?
			accumPoint.getPoint().setArchiveInterval(new Integer(1));
			
			// set default settings for point POINTALARMING
			accumPoint.getPointAlarming().setPointID(new Integer(pointID));
			accumPoint.getPointAlarming().setAlarmStates( accumPoint.getPointAlarming().DEFAULT_ALARM_STATES );
			accumPoint.getPointAlarming().setExcludeNotifyStates( accumPoint.getPointAlarming().DEFAULT_EXCLUDE_NOTIFY );
			accumPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			accumPoint.getPointAlarming().setNotificationGroupID(  new Integer(accumPoint.getPointAlarming().NONE_NOTIFICATIONID) );
			accumPoint.getPointAlarming().setRecipientID( new Integer(CtiUtilities.NONE_ZERO_ID) );
	
			// set default settings for point POINTUNIT
			accumPoint.getPointUnit().setPointID(new Integer(pointID));
			accumPoint.getPointUnit().setUomID(new Integer(com.cannontech.database.data.point.PointUnits.UOMID_PF));
			accumPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			
			// set default settings for point POINTACCUMULATOR
			accumPoint.getPointAccumulator().setMultiplier(new Double(1.0));
			accumPoint.getPointAccumulator().setDataOffset(new Double(0.0));
				
			multi.addDBPersistent( accumPoint );
			
			++addCount;
			pointID++;
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
	
	
}
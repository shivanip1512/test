package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.StatusPoint;
public class DisconnectPointCreate extends PointCreate
{
	/**
	 * DisconnectPointCreate constructor comment.
	 */
	public DisconnectPointCreate() 
	{
		super();
	}
	
	/**
	 * Returns true if the Device is a valid container of Disconnect points.
	 * A valid type_ is of type MCT310ID or MCT 213.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid(com.cannontech.database.data.lite.LiteYukonPAObject litePaobject_ )
	{
		int type = litePaobject_.getType();
		return ( DeviceTypesFuncs.isDisconnectMCT(type) );
	}

	/**
	 * Returns true if the Device is a valid container of Disconnect points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param type int
	 * @return boolean
	 */
	public boolean isPointCreated(LitePoint lp)
	{
		return (( lp.getPointOffset() == 1) && (lp.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT));
	}
	/**
	 * Parses through the DisconnectPointsDeviceList and creates a mutiDBPersistent
	 *  of Status points to be inserted as Disconnect points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	public boolean create() 
	{
		com.cannontech.clientutils.CTILogger.info("Starting Disconnect Point creation process...");
	
		java.util.Vector disconnectDevices = new java.util.Vector(20);
		getDeviceVector(disconnectDevices);
	
		//create an object to hold all of our DBPersistant objects
		com.cannontech.database.data.multi.SmartMultiDBPersistent multi = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
		
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		int pointID = com.cannontech.database.db.point.Point.getNextPointID();
		for( int i = 0; i < disconnectDevices.size(); i++)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject litePaobject = 
				(com.cannontech.database.data.lite.LiteYukonPAObject)disconnectDevices.get(i);

			String pointType = "Status";

			com.cannontech.clientutils.CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
	
			// This is an Status point		
			StatusPoint statusPoint = (StatusPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.STATUS_POINT);
			statusPoint.setPointID(new Integer(pointID));
			
			// set default settings for BASE point
			statusPoint.getPoint().setPointID(new Integer(pointID));
		    statusPoint.getPoint().setPointName("DISCONNECT STATUS");
	
			Integer deviceID = new Integer( litePaobject.getLiteID());
			statusPoint.getPoint().setPaoID( deviceID );
	//		statusPoint.getPoint().setPseudoFlag(new Character('N'));	//derived attribute
			statusPoint.getPoint().setStateGroupID( new Integer(2) );	//default stateGroupId
			statusPoint.getPoint().setServiceFlag(new Character('N'));
			statusPoint.getPoint().setAlarmInhibit(new Character('N'));
			statusPoint.getPoint().setPointOffset(new Integer(1));	//DEFAULT FOR DISCONNECT
			statusPoint.getPoint().setArchiveType("None");	//default?
			statusPoint.getPoint().setArchiveInterval(new Integer(1));
			
			// set default settings for point POINTALARMING
			statusPoint.getPointAlarming().setPointID(new Integer(pointID));
			statusPoint.getPointAlarming().setAlarmStates( statusPoint.getPointAlarming().DEFAULT_ALARM_STATES );
			statusPoint.getPointAlarming().setExcludeNotifyStates( statusPoint.getPointAlarming().DEFAULT_EXCLUDE_NOTIFY );
			statusPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			statusPoint.getPointAlarming().setNotificationGroupID(  new Integer(statusPoint.getPointAlarming().NONE_NOTIFICATIONID) );
			statusPoint.getPointAlarming().setRecipientID(new Integer(statusPoint.getPointAlarming().NONE_NOTIFICATIONID));
	
			// set default settings for point POINTSTATUS
			statusPoint.getPointStatus().setPointID(new Integer(pointID));
			statusPoint.getPointStatus().setInitialState(new Integer(0));
			statusPoint.getPointStatus().setControlType(com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NONE));
			statusPoint.getPointStatus().setControlInhibit(new Character('N'));
			statusPoint.getPointStatus().setControlOffset(new Integer(0));
			statusPoint.getPointStatus().setCloseTime1(new Integer(0));
			statusPoint.getPointStatus().setCloseTime2(new Integer(0));
			statusPoint.getPointStatus().setStateZeroControl("control open");
			statusPoint.getPointStatus().setStateOneControl("control close");
			statusPoint.getPointStatus().setCommandTimeOut(new Integer(com.cannontech.database.db.point.PointStatus.DEFAULT_CMD_TIMEOUT));
			
			multi.addDBPersistent( statusPoint );
			
			++addCount;
			pointID++;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
		{
			com.cannontech.clientutils.CTILogger.info(addCount + " Disconnect Points were processed and inserted Successfully");
		}
		else
			com.cannontech.clientutils.CTILogger.info("Disconnect Points failed insertion");
			
		return success;
	}
	
	
}
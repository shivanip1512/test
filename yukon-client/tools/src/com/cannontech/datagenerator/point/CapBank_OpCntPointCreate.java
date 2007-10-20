/*
 * Created on Apr 13, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.datagenerator.point;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointUnit;

public class CapBank_OpCntPointCreate extends PointCreate
{
	/**
	 * Returns true if the Cap Bank is a valid container of Op Count points.
	 * A valid type is a Switched Cap Bank
	 * Creation date: (4/13/2005 9:11:23 AM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid( LiteYukonPAObject litePaobject_ ) {
		// All Switched Cap Banks are valid
		if(DeviceTypesFuncs.CAPBANK == litePaobject_.getType())
			return CapBank.isSwitchedBank(new Integer(litePaobject_.getLiteID()));
			
		return false;
	}

	/**
	 * Returns true if the Point already exists for the Device
	 */
	public boolean isPointCreated( LitePoint lp ) {
		return ((lp.getPointOffset() == 1) && 
				(lp.getPointType() == PointTypes.ANALOG_POINT));
	}
	
	/**
	 * Parses through the deviceList and creates a multiDBPersistent
	 *  of Analog points to be inserted as Operations Count points for Cap Banks.
	 */
	public boolean create()  {
		CTILogger.info("Starting Operations Count Point creation process...");
	
		Vector<LiteYukonPAObject> opCountDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = DaoFactory.getPointDao();
		int addCount = 0;
		for (LiteYukonPAObject litePaobject: opCountDevices) {
			
			int pointID = pointDao.getNextPointId();
			
			CTILogger.info("Adding PointId " + pointID + " to Cap Bank " + litePaobject.getPaoName());
	
			// This is an Analog point		
			AnalogPoint analogPoint = (AnalogPoint)PointFactory.createPoint(PointTypes.ANALOG_POINT);
			analogPoint.setPointID(new Integer(pointID));
			
			// set default settings for BASE point
			analogPoint.getPoint().setPointID(new Integer(pointID));
			analogPoint.getPoint().setPointName("OPERATION");
	
			Integer deviceID = new Integer( litePaobject.getLiteID());
			analogPoint.getPoint().setPaoID( deviceID );

			analogPoint.getPoint().setStateGroupID( new Integer(-2) );	//default stateGroupId
			analogPoint.getPoint().setServiceFlag(new Character('N'));
			analogPoint.getPoint().setAlarmInhibit(new Character('N'));
			analogPoint.getPoint().setPointOffset(new Integer(1));	//DEFAULT FOR OP COUNT POINTS
			analogPoint.getPoint().setArchiveType("None");	//default?
			analogPoint.getPoint().setArchiveInterval(new Integer(1));
			
			// set default settings for point POINTALARMING
			analogPoint.getPointAlarming().setPointID(new Integer(pointID));
			analogPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			analogPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
			analogPoint.getPointAlarming().setRecipientID( new Integer(CtiUtilities.NONE_ZERO_ID) );
	
			// set default settings for point POINTUNIT
			analogPoint.getPointUnit().setPointID(new Integer(pointID));
			analogPoint.getPointUnit().setUomID(new Integer(PointUnits.UOMID_COUNTS));
			analogPoint.getPointUnit().setDecimalPlaces(new Integer(PointUnit.DEFAULT_DECIMAL_PLACES));
			
			// set POINTANALOG defaults
			analogPoint.getPointAnalog().setPointID(new Integer(pointID));
				
			multi.addDBPersistent( analogPoint );
			
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Op Count Points were processed and inserted successfully");
		else
			CTILogger.info("Op Count Points failed insertion");
			
		return success;
	}
}
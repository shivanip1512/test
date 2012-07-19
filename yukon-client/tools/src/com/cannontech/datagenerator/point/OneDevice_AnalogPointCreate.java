package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.PointAlarming;
public class OneDevice_AnalogPointCreate extends PointCreate
{
	int deviceID = -1;
	int createCount = 0;

	public OneDevice_AnalogPointCreate(int devID, int count) {
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
	public boolean create()  {
		CTILogger.info("Starting One Device Analog Point creation process...");

		LiteYukonPAObject litePaobject = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		PointDao pointDao = DaoFactory.getPointDao();
		for (int i = 1; i < createCount+1; i++)
		{
		    int pointID = pointDao.getNextPointId();
			CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
			String pointType = "Analog";
			
			AnalogPoint analogPoint = new AnalogPoint();
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
			analogPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			analogPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
	
			// set POINTUNIT defaults
			analogPoint.getPointUnit().setUomID(new Integer(PointUnits.UOMID_KW));
			analogPoint.getPointUnit().setDecimalPlaces(new Integer(2));
	
			// set POINTANALOG defuaults
			analogPoint.getPointAnalog().setDeadband(new Double(10.0));
	
			multi.addDBPersistent( analogPoint );
			
			++addCount;
		}
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " One Device Analog Points were processed and inserted Successfully");
		else
			CTILogger.info("One Device Analog Points failed insertion");
			
		return success;
	}

	@Override
	boolean isDeviceValid(LiteYukonPAObject litePaobject_) {
		// this method is only implemented for completeness
		return true;
	}

	@Override
	boolean isPointCreated(LitePoint lp) {
		// this method is only implemented for completeness
		return false;
	}
}
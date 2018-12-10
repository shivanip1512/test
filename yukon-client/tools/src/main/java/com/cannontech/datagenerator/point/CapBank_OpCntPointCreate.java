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

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.spring.YukonSpringHook;

public class CapBank_OpCntPointCreate extends PointCreate
{
	/**
	 * Returns true if the Cap Bank is a valid container of Op Count points.
	 * A valid type is a Switched Cap Bank
	 * Creation date: (4/13/2005 9:11:23 AM)
	 * @param _type int
	 * @return boolean
	 */
	@Override
    public boolean isDeviceValid( LiteYukonPAObject litePaobject_ ) {
		// All Switched Cap Banks are valid
        CapbankDao dao = YukonSpringHook.getBean("capbankDao",CapbankDao.class);
		if(PaoType.CAPBANK == litePaobject_.getPaoType())
			return dao.isSwitchedBank(new Integer(litePaobject_.getLiteID()));
			
		return false;
	}

	/**
	 * Returns true if the Point already exists for the Device
	 */
	@Override
    public boolean isPointCreated( LitePoint lp ) {
		return ((lp.getPointOffset() == 1) && 
				(lp.getPointTypeEnum() == PointType.Analog));
	}
	
	/**
	 * Parses through the deviceList and creates a multiDBPersistent
	 *  of Analog points to be inserted as Operations Count points for Cap Banks.
	 */
	@Override
    public boolean create()  {
		CTILogger.info("Starting Operations Count Point creation process...");
	
		Vector<LiteYukonPAObject> opCountDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		int addCount = 0;
		for (LiteYukonPAObject litePaobject: opCountDevices) {
			
			int pointID = pointDao.getNextPointId();
			
			CTILogger.info("Adding PointId " + pointID + " to Cap Bank " + litePaobject.getPaoName());
	
			// This is an Analog point		
			AnalogPoint analogPoint = (AnalogPoint)PointFactory.createPoint(PointType.Analog.getPointTypeId());
			analogPoint.setPointID(new Integer(pointID));
			
			// set default settings for BASE point
			analogPoint.getPoint().setPointName("OPERATION");
	
			Integer deviceID = new Integer( litePaobject.getLiteID());
			analogPoint.getPoint().setPaoID( deviceID );

			analogPoint.getPoint().setStateGroupID( new Integer(-2) );	//default stateGroupId
			analogPoint.getPoint().setServiceFlag(new Character('N'));
			analogPoint.getPoint().setAlarmInhibit(new Character('N'));
			analogPoint.getPoint().setPointOffset(new Integer(1));	//DEFAULT FOR OP COUNT POINTS
			analogPoint.getPoint().setArchiveType(PointArchiveType.NONE);
			analogPoint.getPoint().setArchiveInterval(new Integer(1));
			
			// set default settings for point POINTALARMING
			analogPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			analogPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
	
			// set default settings for point POINTUNIT
			analogPoint.getPointUnit().setUomID(UnitOfMeasure.COUNTS.getId());
			analogPoint.getPointUnit().setDecimalPlaces(new Integer(PointUnit.DEFAULT_DECIMAL_PLACES));
			
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
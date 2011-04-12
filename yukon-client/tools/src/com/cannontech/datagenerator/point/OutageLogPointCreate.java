package com.cannontech.datagenerator.point;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;
public class OutageLogPointCreate extends PointCreate
{
	/**
	 * Returns true if the Device is a valid container of OutageLog points.
	 * A valid type_ is of type isMCT4XX
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid(LiteYukonPAObject litePaobject_ )  {
		int type = litePaobject_.getPaoType().getDeviceTypeId();
	    return DeviceTypesFuncs.isMCT4XX(type);
	}

	/**
	 * Returns true if the Device has the OutageLog point created.
	 * Checks for PointOffset = 100 and PointType = Analog
	 * @param type int
	 * @return boolean
	 */
	public boolean isPointCreated(LitePoint lp) {
		return (( lp.getPointOffset() == 100) && 
				(lp.getPointType() == PointTypes.ANALOG_POINT));
	}
	/**
	 * Parses through the OutageLogPointsDeviceList and creates a mutiDBPersistent
	 *  of Analog points to be inserted as OutageLog points.
	 * @return boolean
	 */
	public boolean create() {
		
		CTILogger.info("Starting OutageLog Point creation process...");
	
		Vector<LiteYukonPAObject> outageLogDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = DaoFactory.getPointDao();
		int addCount = 0;

		for (LiteYukonPAObject litePaobject : outageLogDevices) {
		    int pointID = pointDao.getNextPointId();

			CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
	
		    multi.addDBPersistent(
			        PointFactory.createAnalogPoint(
			        	"Outages", 
						new Integer(litePaobject.getYukonID()), 
                        new Integer(pointID), 
                        PointTypes.PT_OFFSET_OUTAGE, 
                        PointUnits.UOMID_SECONDS,
						StateGroupUtils.STATEGROUP_ANALOG) );
		    
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Outage Log Points were processed and inserted Successfully");
		else
			CTILogger.info("Outage Log Points failed insertion");
			
		return success;
	}
	
	
}
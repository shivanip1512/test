package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.state.StateGroupUtils;
public class ZeroUsageFlagPointCreate extends PointCreate
{
	private static final int ZERO_USAGE_FLAG_POINT_OFFSET = 20;

	/**
	 * Returns true if the Device is a valid container of Zero Usage Flag points.
	 * A valid type_ is of type MCT410XX
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid(LiteYukonPAObject litePaobject_ ) {
		int type = litePaobject_.getPaoType().getDeviceTypeId();
		return DeviceTypesFuncs.isMCT410(type);
	}

	/**
	 * Returns true if the Device has the Zero Usage Flag point created.
	 * Checks for PointOffset = ZERO_USAGE_FLAG_POINT_OFFSET and PointType = Status
	 * @param type int
	 * @return boolean
	 */
	public boolean isPointCreated(LitePoint lp) {
		return (( lp.getPointOffset() == ZERO_USAGE_FLAG_POINT_OFFSET) && 
				(lp.getPointType() == PointTypes.STATUS_POINT));
	}
	
	/**
	 * Parses through the ZeroUsageFlagPointsDeviceList and creates a mutiDBPersistent
	 *  of Status points to be inserted as ZeroUsageFlagFlag points.
	 * @return boolean
	 */
	public boolean create() {
		CTILogger.info("Starting Zero Usage Flag Point creation process...");
	
		Vector<LiteYukonPAObject> zeroUsageFlagDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = DaoFactory.getPointDao();
		int addCount = 0;
		for (LiteYukonPAObject litePaobject: zeroUsageFlagDevices) {
			
		    int pointID = pointDao.getNextPointId();
			
		    PointBase pointBase = PointFactory.createNewPoint(pointID,
                    PointTypes.STATUS_POINT,
                    "Zero Usage",
                    litePaobject.getYukonID(),
                    ZERO_USAGE_FLAG_POINT_OFFSET);

		    pointBase.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TRUEFALSE));
            ((StatusPoint) pointBase).setPointStatus(new PointStatus(pointBase.getPoint().getPointID()));

            multi.addDBPersistent(pointBase);
 
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Zero Usage Flag Points were processed and inserted Successfully");
		else
			CTILogger.info("Zero Usage Flag Points failed insertion");
			
		return success;
	}
}

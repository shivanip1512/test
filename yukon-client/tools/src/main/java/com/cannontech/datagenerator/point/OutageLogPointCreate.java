package com.cannontech.datagenerator.point;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
public class OutageLogPointCreate extends PointCreate
{
	/**
	 * Returns true if the Device is a valid container of OutageLog points.
	 * A valid type_ is of type isMCT4XX
	 * @param _type int
	 * @return boolean
	 */
	@Override
    public boolean isDeviceValid(LiteYukonPAObject litePaobject_ )  {
		PaoType paoType = litePaobject_.getPaoType();
	    return DeviceTypesFuncs.isMCT4XX(paoType);
	}

	/**
	 * Returns true if the Device has the OutageLog point created.
	 * Checks for PointOffset = 100 and PointType = Analog
	 * @param type int
	 * @return boolean
	 */
	@Override
    public boolean isPointCreated(LitePoint lp) {
		return (( lp.getPointOffset() == 100) && 
				(lp.getPointTypeEnum() == PointType.Analog));
	}
	/**
	 * Parses through the OutageLogPointsDeviceList and creates a mutiDBPersistent
	 *  of Analog points to be inserted as OutageLog points.
	 * @return boolean
	 */
	@Override
    public boolean create() {
		
		CTILogger.info("Starting OutageLog Point creation process...");
	
		Vector<LiteYukonPAObject> outageLogDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		int addCount = 0;

		for (LiteYukonPAObject litePaobject : outageLogDevices) {
		    int pointID = pointDao.getNextPointId();

			CTILogger.info("Adding PointId " + pointID + " to Device " + litePaobject.getPaoName());
	
		    multi.addDBPersistent(
			        PointFactory.createAnalogPoint(
			        	"Outages", 
						new Integer(litePaobject.getYukonID()), 
                        new Integer(pointID), 
                        PointOffsets.PT_OFFSET_OUTAGE, 
                        UnitOfMeasure.SECONDS.getId(),
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
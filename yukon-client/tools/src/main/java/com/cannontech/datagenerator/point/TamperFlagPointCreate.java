package com.cannontech.datagenerator.point;
/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
public class TamperFlagPointCreate extends PointCreate
{
	private static final int TAMPER_FLAG_POINT_OFFSET = 17;

	/**
	 * Returns true if the Device is a valid container of TamperFlag points.
	 * A valid type_ is of type MCT410XX
	 * @param _type int
	 * @return boolean
	 */
	@Override
    public boolean isDeviceValid(LiteYukonPAObject litePaobject_ ) {
		PaoType paoType = litePaobject_.getPaoType();
		return DeviceTypesFuncs.isMCT410(paoType);
	}

	/**
	 * Returns true if the Device has the TamperFlag point created.
	 * Checks for PointOffset = TAMPER_FLAG_POINT_OFFSET and PointType = Status
	 * @param type int
	 * @return boolean
	 */
	@Override
    public boolean isPointCreated(LitePoint lp) {
		return (( lp.getPointOffset() == TAMPER_FLAG_POINT_OFFSET) && 
				(lp.getPointTypeEnum() == PointType.Status));
	}
	
	/**
	 * Parses through the TamperFlagPointsDeviceList and creates a mutiDBPersistent
	 *  of Status points to be inserted as TamperFlag points.
	 * @return boolean
	 */
	@Override
    public boolean create() {
		CTILogger.info("Starting TamperFlag Point creation process...");
	
		Vector<LiteYukonPAObject> tamperFlagDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		int addCount = 0;
		for (LiteYukonPAObject litePaobject: tamperFlagDevices) {
			
		    int pointID = pointDao.getNextPointId();
			
		    PointBase pointBase = PointFactory.createNewPoint(pointID,
                    PointType.Status.getPointTypeId(),
                    "Tamper Flag",
                    litePaobject.getYukonID(),
                    TAMPER_FLAG_POINT_OFFSET);

		    pointBase.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TRUEFALSE));

            multi.addDBPersistent(pointBase);
 
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Tamper Flag Points were processed and inserted Successfully");
		else
			CTILogger.info("Tamper Flag Points failed insertion");
			
		return success;
	}
}

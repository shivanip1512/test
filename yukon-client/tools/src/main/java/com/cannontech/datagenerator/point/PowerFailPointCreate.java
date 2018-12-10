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
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
public class PowerFailPointCreate extends PointCreate
{
	/**
	 * Returns true if the Device is a valid container of Power Fail points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	@Override
    public boolean isDeviceValid( LiteYukonPAObject litePaobject_ ) {
		// All MCT's are valid (with the exception of LMT2 and DCT501 //
	    return litePaobject_.getPaoType().isMct() && 
	            !(litePaobject_.getPaoType() == PaoType.LMT_2 || litePaobject_.getPaoType() == PaoType.DCT_501);
	}

	/**
	 * Returns true if the Device is a valid container of Power Fail points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	@Override
    public boolean isPointCreated( LitePoint lp ) {
		return ((lp.getPointOffset() == 20) && 
				(lp.getPointTypeEnum() == PointType.PulseAccumulator));
	}
	/**
	 * Parses through the powerFailPointsDeviceList and creates a mutiDBPersistent
	 *  of PulseAccumulator points to be inserted as Power Fail points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	@Override
    public boolean create() {
		CTILogger.info("Starting Power Fail Point creation process...");
	
		Vector<LiteYukonPAObject> powerFailDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		for (LiteYukonPAObject litePaobject : powerFailDevices) {
			
			int pointID = pointDao.getNextPointId();
			
		    multi.addDBPersistent(
			        PointFactory.createPulseAccumPoint(
					   "Blink Count",
					   new Integer(litePaobject.getYukonID()),
					   new Integer(pointID),
					   PointOffsets.PT_OFFSET_BLINK_COUNT,
					   UnitOfMeasure.COUNTS.getId(),
					   1.0,
					   StateGroupUtils.STATEGROUP_ANALOG,
                       PointUnit.DEFAULT_DECIMAL_PLACES,
                       PointArchiveType.NONE,
                       PointArchiveInterval.ZERO) );
			CTILogger.info("Adding Blink Count: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
			
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Power Fail Points were processed and inserted Successfully");
		else
			CTILogger.info("Power Fail Points failed insertion");
			
		return success;
	}	
}
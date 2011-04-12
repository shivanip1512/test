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
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.state.StateGroupUtils;
public class DisconnectPointCreate extends PointCreate
{
	/**
	 * Returns true if the Device is a valid container of Disconnect points.
	 * A valid type_ is of type MCT310ID or MCT 213.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid(LiteYukonPAObject litePaobject_ ) {
		int type = litePaobject_.getPaoType().getDeviceTypeId();
	    if( DeviceTypesFuncs.isMCT4XX(type)) {
	        MCT400SeriesBase mct = (MCT400SeriesBase)DaoFactory.getDbPersistentDao().retrieveDBPersistent(litePaobject_);
	        if( mct.getDeviceMCT400Series().getDisconnectAddress().intValue() > -1)
	            return true;
	    }
		return ( DeviceTypesFuncs.isDisconnectMCT(type) );
	}

	/**
	 * Returns true if the Device is a valid container of Disconnect points.
	 * A valid type_ is of type MCT (Not including LMT2 or DCT501
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param type int
	 * @return boolean
	 */
	public boolean isPointCreated(LitePoint lp) {
		return (( lp.getPointOffset() == 1) && 
				(lp.getPointType() == PointTypes.STATUS_POINT));
	}
	
	/**
	 * Parses through the DisconnectPointsDeviceList and creates a mutiDBPersistent
	 *  of Status points to be inserted as Disconnect points.
	 * Creation date: (5/29/2001 9:13:14 AM)
	 * @return boolean
	 */
	public boolean create() {
		CTILogger.info("Starting Disconnect Point creation process...");
	
		Vector<LiteYukonPAObject> disconnectDevices = getDeviceVector();
	
		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
        PointDao pointDao = DaoFactory.getPointDao();
		int addCount = 0;
		for (LiteYukonPAObject litePaobject: disconnectDevices) {
			
		    int pointID = pointDao.getNextPointId();
			
		    PointBase newPoint2 = PointFactory.createNewPoint(pointID,
                    PointTypes.STATUS_POINT,
                    "Disconnect Status",
                    litePaobject.getYukonID(),
                    new Integer(PointTypes.PT_OFFSET_TOTAL_KWH));

            newPoint2.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_MCT410DISC));
            ((StatusPoint) newPoint2).setPointStatus(new PointStatus(newPoint2.getPoint().getPointID()));

            multi.addDBPersistent(newPoint2);
 
			++addCount;
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " Disconnect Points were processed and inserted Successfully");
		else
			CTILogger.info("Disconnect Points failed insertion");
			
		return success;
	}
}
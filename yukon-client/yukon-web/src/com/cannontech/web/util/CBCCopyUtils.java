package com.cannontech.web.util;

import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;


public class CBCCopyUtils {

	public CBCCopyUtils() {
		super();

	}

	public static void copyAllPointsForPAO(Integer fromID, Integer toID) {
        List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(fromID);
		MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        
        for (LitePoint point : points) {
			DBPersistent pointCopy = copy(DaoFactory.getDbPersistentDao()
					.retrieveDBPersistent(point));
			((PointBase) pointCopy).getPoint().setPaoID(toID);
			dbPersistentVector.getDBPersistentVector().add(pointCopy);
		}
		try {
			PointUtil.insertIntoDB(dbPersistentVector);
		} catch (TransactionException e) {
			CTILogger.error("Could not copy points. copyAllPointsForPAO () "
					+ e.getMessage());
		}
	}

	public static DBPersistent copy(DBPersistent dbObj)
			throws IllegalArgumentException {
		if (dbObj instanceof ICapBankController)
			return copyCBC((ICapBankController) dbObj);
		if (dbObj instanceof PointBase)
			return copyPoint((PointBase) dbObj);
		else
			throw new IllegalArgumentException(
					"Only copies Cap Bank Controllers and Points");

	}

	private static DBPersistent copyPoint(PointBase base) {
		PointBase point = base;
			
		PointBase point1 = PointFactory.createPoint(PointTypes.getType(base
				.getPoint().getPointType()));
		point.setPointID(point1.getPoint().getPointID());
		
        //REMOVED seperate point value settings.  Replaced with above reference copying.  
		//Watch out.  This is no longer the original point.  Use as such.  
		
		return point;
	}

	private static DBPersistent copyCBC(ICapBankController controller) {
		if (controller instanceof CapBankController702x)
			return handleCBC702x((CapBankController702x) controller);
		if (controller instanceof CapBankController)
			return handleCBC((CapBankController) controller);
		return null;
	}

	private static DBPersistent handleCBC(CapBankController controller) {
		CapBankController cbc = null;
		cbc = (CapBankController) DeviceFactory.createDevice(PAOGroups
				.getDeviceType(controller.getPAOType()));
		cbc.setAddress(0);
		cbc.setCommID(controller.getCommID());
		cbc.setSchedules(controller.getSchedules());
		cbc.setDisabled(controller.isDisabled());
		cbc.setPAOName(controller.getPAOName());
		return cbc;
	}

	private static DBPersistent handleCBC702x(CapBankController702x controller) {
		CapBankController702x cbc702 = null;
		cbc702 = (CapBankController702x) DeviceFactory.createDevice(PAOGroups
				.getDeviceType(controller.getPAOType()));
		cbc702.setAddress(controller.getAddress());
		cbc702.setCommID(controller.getCommID());
		cbc702.setSchedules(controller.getSchedules());
		cbc702.setDisabled(controller.isDisabled());
		cbc702.setPAOName(controller.getPAOName());
		return cbc702;
	}

	public static boolean isPoint(DBPersistent origObject) {
		if (origObject instanceof PointBase)
			return true;
		else
			return false;
	}

	public static DBPersistent getDBPersistentByID(int copyObjectID, int type) {
		DBPersistent originalDbPers = null;
		if (type == 0 || type == 1)
        {
            LiteBase  lite= DaoFactory.getPaoDao().getLiteYukonPAO(copyObjectID);
           if(lite != null)
           {
               originalDbPers = DaoFactory.getDbPersistentDao().retrieveDBPersistent(lite);
           }
        }
         else  if (type  == 2){
			LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(copyObjectID);
			if (litePoint != null) {
				originalDbPers = DaoFactory.getDbPersistentDao().retrieveDBPersistent(litePoint);
			}
		}
		return originalDbPers;
	}

}

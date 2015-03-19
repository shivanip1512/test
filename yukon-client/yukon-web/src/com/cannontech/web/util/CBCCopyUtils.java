package com.cannontech.web.util;

import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


public class CBCCopyUtils {

	public CBCCopyUtils() {
		super();

	}

	public static void copyAllPointsForPAO(Integer fromID, Integer toID) {
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(fromID);
		MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
        
        for (LitePoint point : points) {
			DBPersistent pointCopy = copy(YukonSpringHook.getBean(DBPersistentDao.class)
					.retrieveDBPersistent(point));
			((PointBase) pointCopy).getPoint().setPaoID(toID);
			dbPersistentVector.getDBPersistentVector().add(pointCopy);
		}
		try {
			PointUtil.insertIntoDB(dbPersistentVector);
		} catch (PersistenceException e) {
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
        if (controller instanceof CapBankControllerDNP)
            return handleCBCDNP((CapBankControllerDNP) controller);
        if (controller instanceof CapBankController)
			return handleCBC((CapBankController) controller);
		return null;
	}

	private static DBPersistent handleCBC(CapBankController controller) {
		CapBankController cbc = null;
		PaoType paoType = controller.getPaoType();
		cbc = (CapBankController) DeviceFactory.createDevice(paoType);
		cbc.setAddress(0);
		cbc.setCommID(controller.getCommID());
		cbc.setSchedules(controller.getSchedules());
		cbc.setDisabled(controller.isDisabled());
		cbc.setPAOName(controller.getPAOName());
		return cbc;
	}

	private static DBPersistent handleCBC702x(CapBankController702x controller) {
		CapBankController702x cbc702 = null;
		PaoType paoType = controller.getPaoType();
		cbc702 = (CapBankController702x) DeviceFactory.createDevice(paoType);
		cbc702.setAddress(controller.getAddress());
		cbc702.setCommID(controller.getCommID());
		cbc702.setSchedules(controller.getSchedules());
		cbc702.setDisabled(controller.isDisabled());
		cbc702.setPAOName(controller.getPAOName());
		cbc702.setDnpConfiguration(controller.getDnpConfiguration());
		cbc702.setDeviceScanRateMap(controller.getDeviceScanRateMap());
		return cbc702;
	}
	
    private static DBPersistent handleCBCDNP(CapBankControllerDNP controller) {
        CapBankControllerDNP cbcDNP = null;
        PaoType paoType = controller.getPaoType();
        cbcDNP = (CapBankControllerDNP) DeviceFactory.createDevice(paoType);
        cbcDNP.setAddress(controller.getAddress());
        cbcDNP.setCommID(controller.getCommID());
        cbcDNP.setSchedules(controller.getSchedules());
        cbcDNP.setDisabled(controller.isDisabled());
        cbcDNP.setPAOName(controller.getPAOName());
        cbcDNP.setDnpConfiguration(controller.getDnpConfiguration());
        return cbcDNP;
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
            LiteBase  lite= YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(copyObjectID);
           if(lite != null)
           {
               originalDbPers = YukonSpringHook.getBean(DBPersistentDao.class).retrieveDBPersistent(lite);
           }
        }
         else  if (type  == 2){
			LitePoint litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(copyObjectID);
			if (litePoint != null) {
				originalDbPers = YukonSpringHook.getBean(DBPersistentDao.class).retrieveDBPersistent(litePoint);
			}
		}
		return originalDbPers;
	}

}

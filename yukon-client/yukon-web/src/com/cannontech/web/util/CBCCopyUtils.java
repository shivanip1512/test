package com.cannontech.web.util;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;

public class CBCCopyUtils {

	public CBCCopyUtils() {
		super();

	}

	public static void copyAllPointsForPAO(Integer fromID, Integer toID) {
		LitePoint[] points = PAOFuncs.getLitePointsForPAObject(fromID
				.intValue());
		MultiDBPersistent dbPersistentVector = new MultiDBPersistent();
		for (int i = 0; i < points.length; i++) {
			DBPersistent pointCopy = copy(DBPersistentFuncs
					.retrieveDBPersistent(points[i]));
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
		PointBase point = PointFactory.createPoint(PointTypes.getType(base
				.getPoint().getPointType()));
		Point pointComposite = point.getPoint();
		// copy the composite
		pointComposite.setAlarmInhibit(base.getPoint().getAlarmInhibit());
		pointComposite.setAlarmsDisabled(base.getPoint().isAlarmsDisabled());
		pointComposite.setArchiveInterval(base.getPoint().getArchiveInterval());
		pointComposite.setArchiveType(base.getPoint().getArchiveType());
		pointComposite.setLogicalGroup(base.getPoint().getLogicalGroup());
		pointComposite.setOutOfService(base.getPoint().isOutOfService());
		pointComposite.setPaoID(base.getPoint().getPaoID());
		pointComposite.setPointName("Copy of " + base.getPoint().getPointName()
				+ " " + System.currentTimeMillis());
		pointComposite.setPointOffset(base.getPoint().getPointOffset());
		pointComposite.setServiceFlag(base.getPoint().getServiceFlag());
		pointComposite.setStateGroupID(base.getPoint().getStateGroupID());
		// copy the component
		point.setPointAlarming(base.getPointAlarming());
		point.setPointFDRVector(base.getPointFDRVector());

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
		cbc.setAddress(controller.getAddress());
		cbc.setCommID(controller.getCommID());
		cbc.setSchedules(controller.getSchedules());
		cbc.setDisabled(controller.isDisabled());
		cbc.setPAOName("Copy of " + controller.getPAOName() + " "
				+ System.currentTimeMillis());
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
		cbc702.setPAOName("Copy of " + controller.getPAOName() + " "
				+ System.currentTimeMillis());
		return cbc702;
	}

	public static boolean isPoint(DBPersistent origObject) {
		if (origObject instanceof PointBase)
			return true;
		else
			return false;
	}

	public static DBPersistent getDBPersistentByID(int copyObjectID) {
		LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO(copyObjectID);
		DBPersistent originalDbPers = null;
		if (litePAO != null) {
			originalDbPers = DBPersistentFuncs.retrieveDBPersistent(litePAO);
		} else {
			LitePoint litePoint = PointFuncs.getLitePoint(copyObjectID);
			if (litePoint != null) {
				originalDbPers = DBPersistentFuncs
						.retrieveDBPersistent(litePoint);
			}
		}
		return originalDbPers;
	}

}

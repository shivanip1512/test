package com.cannontech.web.db;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapBank;

public class PointFuncsWrapper {

	public PointFuncsWrapper() {
		super();
	}

	public static LitePoint getLitePoint(int pointId, DBPersistent obj) {
		PointNullHelper pointNullHelper = new PointNullHelper();
        LitePoint litePoint = pointNullHelper.getLitePoint(pointId, obj);
        return litePoint;
	}

}

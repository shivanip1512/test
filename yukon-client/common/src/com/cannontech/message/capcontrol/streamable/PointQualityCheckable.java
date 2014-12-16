package com.cannontech.message.capcontrol.streamable;

import com.cannontech.database.data.point.UnitOfMeasure;

public interface PointQualityCheckable {
	public int getCurrentPtQuality(UnitOfMeasure uom);
}

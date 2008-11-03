package com.cannontech.yukon.api.pointsnapshot;

import java.util.List;

import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface SnapshotResult {
    public long getAsOf();
    public List<PointValueQualityHolder> getValues();
}

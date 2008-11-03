package com.cannontech.yukon.api.pointsnapshot;

import java.util.Collection;
import java.util.List;

import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface PointDataSnapshotService {
    public PointValueQualityHolder getSnapshot(int pointId);
    
    public List<PointValueQualityHolder> getSnapshots(Collection<Integer> pointIds);
    public SnapshotResult getUpdatedSnapshots(Collection<Integer> pointIds, Long after);
}

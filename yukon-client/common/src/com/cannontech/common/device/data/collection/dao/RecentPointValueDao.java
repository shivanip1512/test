package com.cannontech.common.device.data.collection.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface RecentPointValueDao {
    
    /**
     * Deletes all the data from RecentPointValue.
     * Inserts recent values.
     * @param recentValues - values to insert
     */
    void collectData(Map<PaoIdentifier, PointValueQualityHolder> recentValues);

    /**
     * Returns reading results for a date range and a group
     */
    List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2, boolean includeDisabled, Range<Instant> range);

    int getDeviceCount(DeviceGroup group, boolean includeDisabled, Range<Instant> range);
}

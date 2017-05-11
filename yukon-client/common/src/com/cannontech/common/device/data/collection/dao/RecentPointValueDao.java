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
     * Returns the device collection detail.
     * @param group1 - finds only devices within the group and its subgroups
     * @param group2 - can be null, otherwise finds devices that are in group1 and group2
     * @param includeDisabled - if false only enabled devices will be returned
     * @param ranges - time period
     */
    List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2, boolean includeDisabled, Range<Instant> range);

    /**
     * Returns device count for range and group.
     */
    int getDeviceCount(DeviceGroup group, boolean includeDisabled, Range<Instant> range);

    /**
     * Returns the device collection detail for devices that are not in RecentPointValue table
     * @param group1 - finds only devices within the group and its subgroups
     * @param group2 - can be null, otherwise finds devices that are in group1 and group2
     * @param includeDisabled - if false only enabled devices will be returned
     */
    List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, DeviceGroup subGroup,
            boolean includeDisabled);
}

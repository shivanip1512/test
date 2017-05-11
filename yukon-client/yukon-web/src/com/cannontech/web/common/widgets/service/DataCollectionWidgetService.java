
package com.cannontech.web.common.widgets.service;

import java.util.List;

import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;

public interface DataCollectionWidgetService {

    enum RangeType {
        AVAILABLE, EXPECTED, OUTDATED, UNAVAILABLE
    }
    /**
     * Returns cached collection summary for Device Group.
     *
     * @param includeDisabled - if true includes disabled devices otherwise only enabled devices will be
     *        included
     */
    DataCollectionSummary getDataCollectionSummary(DeviceGroup group, boolean includeDisabled);
    
    /**
     * Sends request to PointDataCollectionService (SM) to initiate data collection.
     */
    void collectData();

    /**
     * Returns the device collection detail.
     * @param group1 - finds only devices within the group and its subgroups
     * @param group2 - can be null, otherwise finds devices that are in group1 and group2
     * @param includeDisabled - if false only enabled devices will be returned
     * @param ranges - time period
     */
    List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2,
            boolean includeDisabled, RangeType... ranges);
}

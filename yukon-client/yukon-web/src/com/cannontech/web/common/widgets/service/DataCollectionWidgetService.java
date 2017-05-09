
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
     * Sends request to PointDataCollectionService to initiate data collection.
     */
    void collectData();

    /**
     * Returns collection result
     * 
     * @param includeDisabled - if true includes disabled devices otherwise only enabled devices will be
     *        included
     */
    List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2,
            boolean includeDisabled, RangeType... ranges);
}

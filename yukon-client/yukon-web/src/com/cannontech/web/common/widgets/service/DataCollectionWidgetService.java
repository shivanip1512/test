
package com.cannontech.web.common.widgets.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;

public interface DataCollectionWidgetService {

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
     * 
     * Returns the device collection detail.
     * 
     * @param group - finds only devices within the group and its subgroups
     * @param groups - can be null, otherwise finds devices that are in "group" and in "groups"
     * @param includeDisabled - if false only enabled devices will be returned
     * @param selectedGatewayIds - can be null, otherwise contains list of gatewayId's to filter with 
     * @param ranges - time periods,  can't be null
     * @param paging - paging information, can't be null
     * @param sortBy - used by order by, can't be null
     * @param direction - direction (asc/desc) for the order by, can't be null
     * @return
     */
    SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, Integer[] selectedGatewayIds, List<RangeType> ranges, PagingParameters paging, SortBy sortBy,
            Direction direction);

    /**
     * If nextRunTime is true returns next time refresh can be attempted, otherwise return time the data was collected last.
     */
    Instant getRunTime(boolean nextRunTime);
}

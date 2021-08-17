package com.cannontech.common.device.port.dao;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.service.impl.PortServiceImpl.CommChannelSortBy;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;

public interface PortDao {

    /**
     * Find Port Id based on IP Address and Port number.
     */
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port);

    /**
     * Retrieves object with sorting, paging, direction of sorting and list of all devices using portId. Returns empty list when no devices found.
     */
    public SearchResults<DeviceBaseModel> getDevicesAssignedPort(Integer portId, CommChannelSortBy sortby, PagingParameters paging, Direction direction);
}

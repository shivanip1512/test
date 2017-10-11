package com.cannontech.web.tools.device.config.dao;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistory;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;

public interface DeviceConfigSummaryDao {

    /**
     * Returns filtered device configuration summary detail.
     */
    SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter, PagingParameters paging,
            SortBy sortBy, Direction direction);

    /**
     * Returns action (READ/SEND/VERIFY) history for device.
     */
    DeviceConfigActionHistory getDeviceConfigActionHistory(int deviceId);

}

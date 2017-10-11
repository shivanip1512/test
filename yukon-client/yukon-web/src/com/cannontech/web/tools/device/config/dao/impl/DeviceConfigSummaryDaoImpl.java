package com.cannontech.web.tools.device.config.dao.impl;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistory;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;

public class DeviceConfigSummaryDaoImpl implements DeviceConfigSummaryDao {

    private static final Logger log = YukonLogManager.getLogger(DeviceConfigSummaryDaoImpl.class);
    
    @Override
    public SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction) {
        log.debug("Getting summary for filter="+ filter);
        return new SearchResults<>();
    }

    @Override
    public DeviceConfigActionHistory getDeviceConfigActionHistory(int deviceId) {
        return new DeviceConfigActionHistory();
    }
}

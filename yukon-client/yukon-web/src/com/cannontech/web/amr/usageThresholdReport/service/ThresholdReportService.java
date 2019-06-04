package com.cannontech.web.amr.usageThresholdReport.service;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;

public interface ThresholdReportService {

    /**
     * Returns filtered report.
     */
    ThresholdReport getReportDetail(int reportId, ThresholdReportFilter filter, Integer[] selectedGatewayIds,
            PagingParameters paging, SortBy sortBy, Direction direction);

    /**
     * Creates report.
     */
    int createThresholdReport(ThresholdReportCriteria criteria, List<SimpleDevice> devices);
}

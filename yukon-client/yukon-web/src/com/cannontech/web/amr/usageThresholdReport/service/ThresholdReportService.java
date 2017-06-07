package com.cannontech.web.amr.usageThresholdReport.service;

import java.util.List;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;

public interface ThresholdReportService {

    SearchResults<ThresholdReportDetail> getReportDetail(int reportId, ThresholdReportFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction);

    int createThresholdReport(ThresholdReportCriteria criteria, List<SimpleDevice> devices);
}

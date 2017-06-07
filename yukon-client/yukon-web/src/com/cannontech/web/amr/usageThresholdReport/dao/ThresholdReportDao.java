package com.cannontech.web.amr.usageThresholdReport.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;

public interface ThresholdReportDao {

    void deleteReport(int reportId);

    ThresholdReportCriteria getReport(int reportId);

    int createReport(ThresholdReportCriteria criteria);

    void createReportDetail(int reportId, List<ThresholdReportDetail> details);

    int getDeviceCount(int reportId, ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy,
            Direction direction);

    SearchResults<ThresholdReportDetail> getReportDetail(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy, Direction direction);

}

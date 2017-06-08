package com.cannontech.web.amr.usageThresholdReport.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;

public interface ThresholdReportDao {
    
    public enum SortBy{
        DEVICE_NAME("ypo.PAOName"),
        METER_NUMBER("dmg.MeterNumber"),
        DEVICE_TYPE("ypo.Type"),
        SERIAL_NUMBER_ADDRESS("SerialNumberAddress"),
        DELTA("Delta");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }

    void deleteReport(int reportId);

    ThresholdReportCriteria getReport(int reportId);

    int createReport(ThresholdReportCriteria criteria);

    void createReportDetail(int reportId, List<ThresholdReportDetail> details);

    SearchResults<ThresholdReportDetail> getReportDetail(int reportId, Range<Instant> criteriaRange,
            ThresholdReportFilter filter, PagingParameters paging, SortBy sortBy, Direction direction);

    int getDeviceCount(int reportId, Range<Instant> criteriaRange, ThresholdReportFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction);

}

package com.cannontech.web.amr.usageThresholdReport.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;

public interface ThresholdReportDao {
    
    public enum SortBy{
        DEVICE_NAME("ypo.PAOName"),
        METER_NUMBER("dmg.MeterNumber"),
        DEVICE_TYPE("ypo.Type"),
        SERIAL_NUMBER_ADDRESS("SerialNumberAddress"),
        PRIMARY_GATEWAY("GatewayName"),
        DELTA("Delta"),
        EARLIEST_READING("FirstTimestamp"),
        LATEST_READING("LastTimestamp");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }

    /**
     * Deletes report
     */
    void deleteReport(int reportId);

    /**
     * Returns report criteria.
     */
    ThresholdReportCriteria getReport(int reportId);

    /**
     * Creates report based on criteria.
     */
    int createReport(ThresholdReportCriteria criteria);

    /**
     * Creates report details.
     */
    void createReportDetail(int reportId, List<ThresholdReportDetail> details);

    /**
     * Returns report.
     */
    ThresholdReport getReportDetail(int reportId, ThresholdReportFilter filter, Integer[] selectedGatewayIds, PagingParameters paging, SortBy sortBy,
            Direction direction);
    /**
     * Returns the RfnGateway List
     * 
     * @param reportId - UsageThresholdReportId
     * @return
     */
    List<RfnGateway> getRfnGatewayList(int reportId);
}

package com.cannontech.web.tools.reports.service;

import java.time.Instant;
import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;

public interface AggregateIntervalReportService {

    enum IncompleteIntervalData implements DisplayableEnum {
        SKIP,
        BLANK,
        PARTIAL,
        FIXED_VALUE;

        private final static String keyPrefix = "";

        @Override
        public String getFormatKey() {
            return keyPrefix + "incompleteIntervalData." + name();
        }
    }
    
    enum Aggregation implements DisplayableEnum {
        ADD,
        MAX;

        private final static String keyPrefix = "";

        @Override
        public String getFormatKey() {
            return keyPrefix + "aggregation." + name();
        }
    }

    class AggregateIntervalReportFilter {
        private List<Integer> deviceIds;
        private String deviceGroup;
        private Attribute attribute;
        private Instant startDate;
        private Instant endDate;
        private TimeIntervals interval;
        private IncompleteIntervalData incompleteIntervalData;
        private Integer incompleteIntervalDataValue;
        private Aggregation aggregation;

        public List<Integer> getDeviceIds() {
            return deviceIds;
        }

        public void setDeviceIds(List<Integer> deviceIds) {
            this.deviceIds = deviceIds;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }

        public Instant getStartDate() {
            return startDate;
        }

        public void setStartDate(Instant startDate) {
            this.startDate = startDate;
        }

        public Instant getEndDate() {
            return endDate;
        }

        public void setEndDate(Instant endDate) {
            this.endDate = endDate;
        }

        public TimeIntervals getInterval() {
            return interval;
        }

        public void setInterval(TimeIntervals interval) {
            this.interval = interval;
        }

        public IncompleteIntervalData getIncompleteIntervalData() {
            return incompleteIntervalData;
        }

        public void setIncompleteIntervalData(IncompleteIntervalData incompleteIntervalData) {
            this.incompleteIntervalData = incompleteIntervalData;
        }

        public Integer getIncompleteIntervalDataValue() {
            return incompleteIntervalDataValue;
        }

        public void setIncompleteIntervalDataValue(Integer incompleteIntervalDataValue) {
            this.incompleteIntervalDataValue = incompleteIntervalDataValue;
        }

        public Aggregation getAggregation() {
            return aggregation;
        }

        public void setAggregation(Aggregation aggregation) {
            this.aggregation = aggregation;
        }

        public String getDeviceGroup() {
            return deviceGroup;
        }

        public void setDeviceGroup(String deviceGroup) {
            this.deviceGroup = deviceGroup;
        }
    }

    default List<TimeIntervals> getIntervals() {
        return List.of(TimeIntervals.HOURS_1, TimeIntervals.MINUTES_15, TimeIntervals.MINUTES_30, TimeIntervals.DAYS_1);
    }
    
    /**
     * Returns the list if rows that represent a row in CSV file. The data is formatted and ready to be written to CSV file.
     * 
     * @throws NotFoundException - if there is no data to return
     */
    List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context);
}

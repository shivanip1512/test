package com.cannontech.web.tools.reports.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;
import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;

public interface AggregateIntervalReportService {

    enum MissingIntervalData implements DisplayableEnum {
        SKIP,
        BLANK,
        PARTIAL,
        FIXED_VALUE;

        private final static String keyPrefix = "";

        @Override
        public String getFormatKey() {
            return keyPrefix + "missingIntervalData." + name();
        }
    }
    
    enum Operation implements DisplayableEnum {
        ADD,
        MAX;

        private final static String keyPrefix = "";

        @Override
        public String getFormatKey() {
            return keyPrefix + "operation." + name();
        }
    }

    class AggregateIntervalReportFilter {
        private List<PaoIdentifier> devices;
        private String deviceGroup;
        private Attribute attribute;
        private Instant startDate;
        private Instant endDate;
        private TimeIntervals interval;
        private MissingIntervalData missingIntervalData;
        private String missingIntervalDataValue;
        private Operation operation;

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

        public String getDeviceGroup() {
            return deviceGroup;
        }

        public void setDeviceGroup(String deviceGroup) {
            this.deviceGroup = deviceGroup;
        }

        public MissingIntervalData getMissingIntervalData() {
            return missingIntervalData;
        }

        public void setMissingIntervalData(MissingIntervalData missingIntervalData) {
            this.missingIntervalData = missingIntervalData;
        }

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            this.operation = operation;
        }

        public List<PaoIdentifier> getDevices() {
            return devices;
        }

        public void setDevices(List<PaoIdentifier> devices) {
            this.devices = devices;
        }

        public String getMissingIntervalDataValue() {
            return missingIntervalDataValue;
        }

        public void setMissingIntervalDataValue(String missingIntervalDataValue) {
            this.missingIntervalDataValue = missingIntervalDataValue;
        }
        
        @Override    
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
        } 
    }

    /**
     * Returns the list if rows that represent a row in CSV file. The data is formatted and ready to be written to CSV file.
     * 
     * @throws NotFoundException - if there is no data to return
     */
    List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context);
}

package com.cannontech.web.tools.reports.service;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeDeserializer;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface AggregateIntervalReportService {

    enum MissingIntervalData implements DisplayableEnum {
        SKIP,
        BLANK,
        PARTIAL,
        FIXED_VALUE;

        private final static String keyPrefix = "yukon.web.modules.tools.aggregateIntervalReport.";

        @Override
        public String getFormatKey() {
            return keyPrefix + "missingIntervalData." + name();
        }
        
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static MissingIntervalData getMissingIntervalData(String missingIntervalDataJsonString) {
            try {
                return MissingIntervalData.valueOf(missingIntervalDataJsonString);
            } catch (IllegalArgumentException e) {
                throw new TypeNotSupportedException(missingIntervalDataJsonString + " missing interval data is not valid.");
            }
        }
    }
    
    enum Operation implements DisplayableEnum {
        ADD,
        MAX;

        private final static String keyPrefix = "yukon.web.modules.tools.aggregateIntervalReport.";

        @Override
        public String getFormatKey() {
            return keyPrefix + "operation." + name();
        }
        
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Operation getOperation(String operationJsonString) {
            try {
                return Operation.valueOf(operationJsonString);
            } catch (IllegalArgumentException e) {
                throw new TypeNotSupportedException(operationJsonString + " operation is not valid.");
            }
        }
    }

    class AggregateIntervalReportFilter {
        private List<Integer> devices;
        private String deviceGroup;
        private Attribute attribute;
        private Instant startDate;
        private Instant endDate;
        private TimeIntervals interval;
        private MissingIntervalData missingIntervalData;
        private String missingIntervalDataValue;
        private Operation operation;

        @JsonDeserialize(using = AttributeDeserializer.class)
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

        public List<Integer> getDevices() {
            return devices;
        }

        public void setDevices(List<Integer> devices) {
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
     * Returns the list of rows that represent a row in CSV file. Empty list is returned if no data found.
     */
    List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context);
}

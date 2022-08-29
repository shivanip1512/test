package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

import com.cannontech.common.i18n.DisplayableEnum;

public class SimulatorSettings implements Serializable {

    // lcr data simulator
    private int lcr6200serialFrom;
    private int lcr6200serialTo;
    private int lcr6600serialFrom;
    private int lcr6600serialTo;
    private int lcr6700serialFrom;
    private int lcr6700serialTo;
    private int tlvVersion;
    
    // meter data simulator
    private String paoType;

    // % of duplicates to generate
    private int percentOfDuplicates;

    // used for testing simulator
    private int deviceId;

    private RecordingInterval recordingInterval = RecordingInterval.RECORDING_INTERVAL_60_MINUTES;
    private ReportingInterval reportingInterval = ReportingInterval.REPORTING_INTERVAL_24_HOURS;

    public enum RecordingInterval implements DisplayableEnum {
        RECORDING_INTERVAL_15_MINUTES(Duration.standardMinutes(15)),
        RECORDING_INTERVAL_30_MINUTES(Duration.standardMinutes(30)),
        RECORDING_INTERVAL_60_MINUTES(Duration.standardMinutes(60));
        private static final String prefix = "yukon.web.modules.dev.rfnMeterSimulator.recordingInterval.types.";
        private Duration duration;

        RecordingInterval(Duration duration) {
            this.duration = duration;
        }

        public int getSeconds() {
            return duration.toStandardSeconds().getSeconds();
        }

        public Duration getDuration() {
            return duration;
        }

        @Override
        public String getFormatKey() {
            return prefix + name();
        }
    }

    public enum ReportingInterval implements DisplayableEnum {
        REPORTING_INTERVAL_1_HOURS(Duration.standardHours(1)),
        REPORTING_INTERVAL_4_HOURS(Duration.standardHours(4)),
        REPORTING_INTERVAL_6_HOURS(Duration.standardHours(6)),
        REPORTING_INTERVAL_24_HOURS(Duration.standardDays(1)),;
        private static final String prefix = "yukon.web.modules.dev.rfnMeterSimulator.reportingInterval.types.";
        private Duration duration;

        ReportingInterval(Duration duration) {
            this.duration = duration;
        }

        public int getSeconds() {
            return duration.toStandardSeconds().getSeconds();
        }

        public Duration getDuration() {
            return duration;
        }

        public int getDailyIntervals() {
            return DateTimeConstants.HOURS_PER_DAY / duration.toStandardHours().getHours();
        }

        @Override
        public String getFormatKey() {
            return prefix + name();
        }
    }

    public SimulatorSettings(
                int lcr6200serialFrom, int lcr6200serialTo, 
                int lcr6600serialFrom, int lcr6600serialTo, 
                int lcr6700serialFrom, int lcr6700serialTo, 
                int percentOfDuplicates, 
                RecordingInterval recordingInterval, 
                ReportingInterval reportingInterval, 
                int tlvVersion) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
        this.lcr6700serialFrom = lcr6700serialFrom;
        this.lcr6700serialTo = lcr6700serialTo;

        this.percentOfDuplicates = percentOfDuplicates;
        this.recordingInterval = recordingInterval;
        this.reportingInterval = reportingInterval;
        this.tlvVersion = tlvVersion;
    }

    public SimulatorSettings(String paoType, int percentOfDuplicates, ReportingInterval reportingInterval) {
        this.paoType = paoType;
        this.percentOfDuplicates = percentOfDuplicates;
        this.reportingInterval = reportingInterval;
    }

    public SimulatorSettings(int deviceId) {
        setDeviceId(deviceId);
    }

    public SimulatorSettings() {
    }

    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }

    public void setLcr6200serialFrom(int lcr6200serialFrom) {
        this.lcr6200serialFrom = lcr6200serialFrom;
    }

    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }

    public void setLcr6200serialTo(int lcr6200serialTo) {
        this.lcr6200serialTo = lcr6200serialTo;
    }

    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }

    public void setLcr6600serialFrom(int lcr6600serialFrom) {
        this.lcr6600serialFrom = lcr6600serialFrom;
    }

    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }

    public void setLcr6600serialTo(int lcr6600serialTo) {
        this.lcr6600serialTo = lcr6600serialTo;
    }

    public int getPercentOfDuplicates() {
        return percentOfDuplicates;
    }

    public int getLcr6700serialFrom() {
        return lcr6700serialFrom;
    }

    public void setLcr6700serialFrom(int lcr6700serialFrom) {
        this.lcr6700serialFrom = lcr6700serialFrom;
    }

    public int getLcr6700serialTo() {
        return lcr6700serialTo;
    }

    public void setLcr6700serialTo(int lcr6700serialTo) {
        this.lcr6700serialTo = lcr6700serialTo;
    }

    public int getTlvVersion() {
        return tlvVersion;
    }

    public void setTlvVersion(int tlvVersion) {
        this.tlvVersion = tlvVersion;
    }

    public void setPercentOfDuplicates(int percentOfDuplicates) {
        this.percentOfDuplicates = percentOfDuplicates;
    }

    public String getPaoType() {
        return paoType;
    }

    public void setPaoType(String paoType) {
        this.paoType = paoType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public RecordingInterval getRecordingInterval() {
        return recordingInterval;
    }

    public void setRecordingInterval(RecordingInterval recordingInterval) {
        this.recordingInterval = recordingInterval;
    }

    public ReportingInterval getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(ReportingInterval reportingInterval) {
        this.reportingInterval = reportingInterval;
    }
}

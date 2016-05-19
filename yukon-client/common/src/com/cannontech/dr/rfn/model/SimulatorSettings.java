package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;

public class SimulatorSettings implements Serializable{
    
    //lcr data simulator
    private int lcr6200serialFrom;
    private int lcr6200serialTo;
    private int lcr6600serialFrom;
    private int lcr6600serialTo;
    
    //meter data simulator
    private String paoType;
    
    //% of duplicates to generate
    private int percentOfDuplicates;
    
    //used for testing simulator
    private int deviceId;
    
    private int reportingInterval = ReportingIntervalEnum.REPORTING_INTERVAL_24_HOURS.getSeconds();
    
    public enum ReportingIntervalEnum {
        REPORTING_INTERVAL_1_HOURS(Duration.standardHours(1)),
        REPORTING_INTERVAL_4_HOURS(Duration.standardHours(4)),
        REPORTING_INTERVAL_24_HOURS(Duration.standardDays(1)), 
        ;

        private Duration duration;

        ReportingIntervalEnum(Duration duration) {
            this.duration = duration;
        }

        public int getSeconds() {
            return duration.toStandardSeconds().getSeconds();
        }

        public Duration getDuration() {
            return duration;
        }

        public static ReportingIntervalEnum fromSeconds(int seconds) {
            for (ReportingIntervalEnum interval : values()) {
                if (interval.getSeconds() == seconds)
                    return interval;
            }
            return null;
        }
    }
    public SimulatorSettings(int lcr6200serialFrom, int lcr6200serialTo, int lcr6600serialFrom, int lcr6600serialTo, int percentOfDuplicates, int reportingInterval) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
        this.percentOfDuplicates =  percentOfDuplicates;
        this.reportingInterval = reportingInterval;
    }
    
    public SimulatorSettings(String paoType, int percentOfDuplicates, int reportingInterval) {
        this.paoType = paoType;
        this.percentOfDuplicates =  percentOfDuplicates;
        this.reportingInterval = reportingInterval;
    }
    public SimulatorSettings(int deviceId) {
        this.setDeviceId(deviceId);
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

    public int getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(int reportingInterval) {
        this.reportingInterval = reportingInterval;
    }
    
    public ReportingIntervalEnum getReportingIntervalEnum() {
        if(reportingInterval == 3600){
            return ReportingIntervalEnum.REPORTING_INTERVAL_1_HOURS;
        } else if (reportingInterval == 14400){
            return ReportingIntervalEnum.REPORTING_INTERVAL_4_HOURS;
        } else{
            return ReportingIntervalEnum.REPORTING_INTERVAL_24_HOURS;
        }
    }
}

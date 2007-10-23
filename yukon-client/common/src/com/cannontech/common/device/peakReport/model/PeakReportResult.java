package com.cannontech.common.device.peakReport.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class PeakReportResult {

    // db values
    private int deviceId = -1;
    private int channel = -1;
    private PeakReportPeakType peakType = null;
    private PeakReportRunType runType = null;
    private Date runDate = null;
    private String resultString = "";
    
    // parsed values
    private Date rangeStartDate = null;
    private Date rangeStopDate = null;
    private Date peakStartDate = null;
    private Date peakStopDate = null;
    private String periodStartDateDisplay = "";
    private String periodStopDateDisplay = "";
    private String runDateDisplay = "";
    private double usage = 0.0;
    private double demand = 0.0;
    private double averageDailyUsage = 0.0;
    private double totalUsage = 0.0;
    private String peakValue = "";
    private String peakTypeDisplayName = "";
    private String peakTypeReportDisplayName = "";
    
    // error values
    private boolean noData = true;
    private String deviceError = "";
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>(); 
    
    
    // db
    public int getChannel() {
        return channel;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public PeakReportPeakType getPeakType() {
        return peakType;
    }
    public void setPeakType(PeakReportPeakType peakType) {
        this.peakType = peakType;
        setPeakTypeDisplayName(this.peakType.displayName());
        setPeakTypeReportDisplayName(this.peakType.reportTypeDisplayName());
    }
    public void setPeakType(String peakTypeStr) {
        this.peakType = PeakReportPeakType.valueOf(peakTypeStr.toUpperCase());
        setPeakTypeDisplayName(this.peakType.displayName());
        setPeakTypeReportDisplayName(this.peakType.reportTypeDisplayName());
    }
    public String getResultString() {
        return resultString;
    }
    public void setResultString(String resultString) {
        this.resultString = resultString;
    }
    public Date getRunDate() {
        return runDate;
    }
    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }
    public PeakReportRunType getRunType() {
        return runType;
    }
    public void setRunType(PeakReportRunType runType) {
        this.runType = runType;
    }
    public void setRunType(String runTypeStr) {
        this.runType = PeakReportRunType.valueOf(runTypeStr.toUpperCase());
    }
    
    // parsed 
    public Date getPeakStartDate() {
        return peakStartDate;
    }
    public void setPeakStartDate(Date peakStartDate) {
        this.peakStartDate = peakStartDate;
    }
    public Date getPeakStopDate() {
        return peakStopDate;
    }
    public void setPeakStopDate(Date peakStopDate) {
        this.peakStopDate = peakStopDate;
    }
    public Date getRangeStartDate() {
        return rangeStartDate;
    }
    public void setRangeStartDate(Date rangeStartDate) {
        this.rangeStartDate = rangeStartDate;
    }
    public Date getRangeStopDate() {
        return rangeStopDate;
    }
    public void setRangeStopDate(Date rangeStopDate) {
        this.rangeStopDate = rangeStopDate;
    }
    public String getPeriodStopDateDisplay() {
        return periodStopDateDisplay;
    }
    public void setPeriodStopDateDisplay(String periodStopDateDisplay) {
        this.periodStopDateDisplay = periodStopDateDisplay;
    }
    
    public double getAverageDailyUsage() {
        return averageDailyUsage;
    }
    public void setAverageDailyUsage(double averageDailyUsage) {
        this.averageDailyUsage = averageDailyUsage;
    }
    public double getDemand() {
        return demand;
    }
    public void setDemand(double demand) {
        this.demand = demand;
    }
    public String getPeakValue() {
        return peakValue;
    }
    public void setPeakValue(String peakValue) {
        this.peakValue = peakValue;
    }
    public double getTotalUsage() {
        return totalUsage;
    }
    public void setTotalUsage(double totalUsage) {
        this.totalUsage = totalUsage;
    }
    public double getUsage() {
        return usage;
    }
    public void setUsage(double usage) {
        this.usage = usage;
    }
    public String getPeriodStartDateDisplay() {
        return periodStartDateDisplay;
    }
    public void setPeriodStartDateDisplay(String periodStartDateDisplay) {
        this.periodStartDateDisplay = periodStartDateDisplay;
    }
    public String getPeakTypeDisplayName() {
        return peakTypeDisplayName;
    }
    public void setPeakTypeDisplayName(String peakTypeDisplayName) {
        this.peakTypeDisplayName = peakTypeDisplayName;
    }
    public String getRunDateDisplay() {
        return runDateDisplay;
    }
    public void setRunDateDisplay(String runDateDisplay) {
        this.runDateDisplay = runDateDisplay;
    }
    public String getPeakTypeReportDisplayName() {
        return peakTypeReportDisplayName;
    }
    public void setPeakTypeReportDisplayName(String peakTypeReportDisplayName) {
        this.peakTypeReportDisplayName = peakTypeReportDisplayName;
    }   
    
    // error
    public String getDeviceError() {
        return deviceError;
    }
    public void setDeviceError(String deviceError) {
        this.deviceError = deviceError;
    }
    public boolean isNoData() {
        return noData;
    }
    public void setNoData(boolean noData) {
        this.noData = noData;
    }
    public List<DeviceErrorDescription> getErrors() {
        return errors;
    }
    public void setErrors(List<DeviceErrorDescription> errors) {
        this.errors = errors;
    }
     
}

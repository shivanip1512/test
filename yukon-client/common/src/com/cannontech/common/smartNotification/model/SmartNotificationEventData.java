package com.cannontech.common.smartNotification.model;

import org.joda.time.Instant;

public class SmartNotificationEventData {
    private Instant timestamp;
    private int eventId;
    private int monitorId;
    private int deviceId;
    private int fileSuccessCount;
    private int fileErrorCount;
    private int jobGroupId;
    private String jobName;
    private String status;
    private String severity;
    private String argument1;
    private String argument2;
    private String argument3;
    private String type;
    private String deviceName;
    private String warningType;
    
    public int getMonitorId() {
        return monitorId;
    }
    
    public void setMonitorId(int monitorId) {
        this.monitorId = monitorId;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getArgument1() {
        return argument1;
    }

    public void setArgument1(String argument1) {
        this.argument1 = argument1;
    }

    public String getArgument2() {
        return argument2;
    }

    public void setArgument2(String argument2) {
        this.argument2 = argument2;
    }

    public String getArgument3() {
        return argument3;
    }

    public void setArgument3(String argument3) {
        this.argument3 = argument3;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public int getFileSuccessCount() {
        return fileSuccessCount;
    }

    public void setFileSuccessCount(int fileSuccessCount) {
        this.fileSuccessCount = fileSuccessCount;
    }

    public int getFileErrorCount() {
        return fileErrorCount;
    }

    public void setFileErrorCount(int fileErrorCount) {
        this.fileErrorCount = fileErrorCount;
    }

    public int getJobGroupId() {
        return jobGroupId;
    }

    public void setJobGroupId(int jobGroupId) {
        this.jobGroupId = jobGroupId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}

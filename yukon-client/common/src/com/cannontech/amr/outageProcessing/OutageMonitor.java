package com.cannontech.amr.outageProcessing;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;

public class OutageMonitor implements PointMonitor, Comparable<OutageMonitor> {

    private String groupName;
    private MonitorEvaluatorStatus evaluatorStatus;
    
    private Integer outageMonitorId;
    private String outageMonitorName;
    private int timePeriodDays;
    private int numberOfOutages;
    
    public Integer getOutageMonitorId() {
        return outageMonitorId;
    }

    public void setOutageMonitorId(Integer outageMonitorId) {
        this.outageMonitorId = outageMonitorId;
    }

    public String getOutageMonitorName() {
        return outageMonitorName;
    }
    
    public void setOutageMonitorName(String outageMonitorName) {
        this.outageMonitorName = outageMonitorName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getTimePeriodDays() {
        return timePeriodDays;
    }

    public void setTimePeriodDays(int timePeriodDays) {
        this.timePeriodDays = timePeriodDays;
    }

    public int getNumberOfOutages() {
        return numberOfOutages;
    }

    public void setNumberOfOutages(int numberOfOutages) {
        this.numberOfOutages = numberOfOutages;
    }

    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }
    
    public void setEvaluatorStatus(MonitorEvaluatorStatus status) {
        this.evaluatorStatus = status;
    }
    
    public boolean isEnabled() {
        return evaluatorStatus == MonitorEvaluatorStatus.ENABLED;
    }
    
    @Override
    public int compareTo(OutageMonitor o) {
        return this.getOutageMonitorName().compareToIgnoreCase(o.getOutageMonitorName());
    }
    
    @Override
    public String toString() {
        return String
                .format("OutageMonitor [outageMonitorId=%s, outageMonitorName=%s, groupName=%s, timePeriodDays=%s, numberOfOutages=%s, evaluatorStatus=%s]",
                        outageMonitorId, outageMonitorName, groupName,
                        timePeriodDays, numberOfOutages, evaluatorStatus);
    }
    
}
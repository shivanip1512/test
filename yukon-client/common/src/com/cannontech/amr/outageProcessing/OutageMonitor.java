package com.cannontech.amr.outageProcessing;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;

public class OutageMonitor implements PointMonitor, Comparable<OutageMonitor> {

    private Integer outageMonitorId;
    private String outageMonitorName;
    private String groupName;
    private int timePeriodDays;
    private int numberOfOutages;
    private MonitorEvaluatorStatus evaluatorStatus;
    
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
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("outageMonitorId", getOutageMonitorId());
        tsc.append("outageMonitorName", getOutageMonitorName());
        tsc.append("groupName", getGroupName());
        tsc.append("timePeriod", getTimePeriodDays());
        tsc.append("numberOfOutages", getNumberOfOutages());
        tsc.append("evaluatorStatus", getEvaluatorStatus());
        return tsc.toString();
    }
    
    @Override
    public int compareTo(OutageMonitor o) {
        return this.getOutageMonitorName().compareToIgnoreCase(o.getOutageMonitorName());
    }
    
}
package com.cannontech.amr.tamperFlagProcessing;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;

public class TamperFlagMonitor implements PointMonitor, Comparable<TamperFlagMonitor> {

    private String groupName;
    private MonitorEvaluatorStatus evaluatorStatus;
    
    private Integer tamperFlagMonitorId;
    private String tamperFlagMonitorName;
    
    public Integer getTamperFlagMonitorId() {
        return tamperFlagMonitorId;
    }
    public void setTamperFlagMonitorId(Integer tamperFlagMonitorId) {
        this.tamperFlagMonitorId = tamperFlagMonitorId;
    }
    @Override
    public String getName() {
        return tamperFlagMonitorName;
    }
    public void setName(String name) {
        this.tamperFlagMonitorName = name.trim();
    }
    @Override
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    @Override
    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }
    public void setEvaluatorStatus(MonitorEvaluatorStatus evaluatorStatus) {
        this.evaluatorStatus = evaluatorStatus;
    }
    
    public boolean isEnabled() {
        return evaluatorStatus == MonitorEvaluatorStatus.ENABLED;
    }
    
    @Override
    public int compareTo(TamperFlagMonitor o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
    
    @Override
    public String toString() {
        return String
                .format("TamperFlagMonitor [tamperFlagMonitorId=%s, tamperFlagMonitorName=%s, groupName=%s, evaluatorStatus=%s]",
                        tamperFlagMonitorId, tamperFlagMonitorName, groupName,
                        evaluatorStatus);
    }
    
}

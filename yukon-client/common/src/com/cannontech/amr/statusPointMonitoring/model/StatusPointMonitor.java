package com.cannontech.amr.statusPointMonitoring.model;

import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.lite.LiteStateGroup;

public class StatusPointMonitor implements PointMonitor, Comparable<StatusPointMonitor> {

    private String groupName;
    private MonitorEvaluatorStatus evaluatorStatus;
    
    private Integer statusPointMonitorId;
    private String statusPointMonitorName;
    private Attribute attribute;
    private LiteStateGroup stateGroup;
    private List<StatusPointMonitorProcessor> processors = LazyList.ofInstance(StatusPointMonitorProcessor.class);
    
    public StatusPointMonitor() {
        setGroupName(DeviceGroupService.ROOT);
        setAttribute(BuiltInAttribute.FAULT_STATUS);
        setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
    }
    
    public Integer getStatusPointMonitorId() {
        return statusPointMonitorId;
    }
    
    public void setStatusPointMonitorId(Integer statusPointMonitorId) {
        this.statusPointMonitorId = statusPointMonitorId;
    }
    
    public String getStatusPointMonitorName() {
        return statusPointMonitorName;
    }
    
    public void setStatusPointMonitorName(String name) {
        this.statusPointMonitorName = name;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setStateGroup(LiteStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }
    
    public LiteStateGroup getStateGroup() {
        return stateGroup;
    }
    
    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }
    
    public void setEvaluatorStatus(MonitorEvaluatorStatus evaluatorStatus) {
        this.evaluatorStatus = evaluatorStatus;
    }
    
    public void setProcessors(List<StatusPointMonitorProcessor> processors) {
        this.processors = processors;
    }
    
    public List<StatusPointMonitorProcessor> getProcessors() {
        return processors;
    }
    
    public boolean isEnabled() {
        return evaluatorStatus == MonitorEvaluatorStatus.ENABLED;
    }
    
    @Override
    public String toString() {
        return String.format("StatusPointMonitor [attribute=%s, evaluatorStatus=%s, groupName=%s, stateGroup=%s, statusPointMonitorId=%s, statusPointMonitorProcessors=%s, statusPointMonitorName=%s]",
                             attribute, evaluatorStatus, groupName, stateGroup,
                             statusPointMonitorId,
                             processors,
                             statusPointMonitorName);
    }
    
    @Override
    public int compareTo(StatusPointMonitor o) {
        return this.getStatusPointMonitorName().compareToIgnoreCase(o.getStatusPointMonitorName());
    }
    
}
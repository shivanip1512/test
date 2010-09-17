package com.cannontech.amr.statusPointProcessing.model;

import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.google.common.collect.ImmutableList;

public class StatusPointMonitor implements PointMonitor, Comparable<StatusPointMonitor> {

	private Integer statusPointMonitorId;
	private String statusPointMonitorName;
	private String groupName;
	private Attribute attribute;
	private LiteStateGroup stateGroup;
	private MonitorEvaluatorStatus evaluatorStatus;
	private List<StatusPointMonitorMessageProcessor> messageProcessors = ImmutableList.of();
	
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
	
	public void setStatusPointMonitorMessageProcessors(List<StatusPointMonitorMessageProcessor> statusPointMonitorMessageProcessors) {
        this.messageProcessors = statusPointMonitorMessageProcessors;
    }
	
    public List<StatusPointMonitorMessageProcessor> getStatusPointMonitorMessageProcessors() {
        return messageProcessors;
    }
    
    @Override
    public String toString() {
        return String.format("StatusPointMonitor [attribute=%s, evaluatorStatus=%s, groupName=%s, stateGroup=%s, statusPointMonitorId=%s, statusPointMonitorMessageProcessors=%s, statusPointMonitorName=%s]",
                             attribute, evaluatorStatus, groupName, stateGroup,
                             statusPointMonitorId,
                             messageProcessors,
                             statusPointMonitorName);
    }

    @Override
	public int compareTo(StatusPointMonitor o) {
		return this.getStatusPointMonitorName().compareToIgnoreCase(o.getStatusPointMonitorName());
	}
}

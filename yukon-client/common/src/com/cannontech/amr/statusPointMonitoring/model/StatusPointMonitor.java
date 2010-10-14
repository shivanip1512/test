package com.cannontech.amr.statusPointMonitoring.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.SimpleSupplier;
import com.cannontech.database.data.lite.LiteStateGroup;

public class StatusPointMonitor implements PointMonitor, Comparable<StatusPointMonitor> {

	private Integer statusPointMonitorId;
	private String statusPointMonitorName;
	private String groupName;
	private Attribute attribute;
	private LiteStateGroup stateGroup;
	private MonitorEvaluatorStatus evaluatorStatus;
	private List<StatusPointMonitorProcessor> processors = new LazyList<StatusPointMonitorProcessor>(new ArrayList<StatusPointMonitorProcessor>(), 
                                                                            new SimpleSupplier<StatusPointMonitorProcessor>(StatusPointMonitorProcessor.class));
	
	public StatusPointMonitor() {
	    setGroupName(SystemGroupEnum.ROOT.getFullPath());
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

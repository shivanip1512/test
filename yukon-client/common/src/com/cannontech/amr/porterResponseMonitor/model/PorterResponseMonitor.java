package com.cannontech.amr.porterResponseMonitor.model;

import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;
import com.cannontech.database.data.lite.LiteStateGroup;

public class PorterResponseMonitor implements Comparable<PorterResponseMonitor> {
	private Integer monitorId;
	private String name;
	private LiteStateGroup stateGroup;
	private Attribute attribute = BuiltInAttribute.FAULT_STATUS;
	private MonitorEvaluatorStatus evaluatorStatus = MonitorEvaluatorStatus.ENABLED;
	private List<PorterResponseMonitorRule> rules = LazyList.ofInstance(PorterResponseMonitorRule.class);

	public Integer getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LiteStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(LiteStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public MonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}

	public void setEvaluatorStatus(MonitorEvaluatorStatus evaluatorStatus) {
		this.evaluatorStatus = evaluatorStatus;
	}

	public List<PorterResponseMonitorRule> getRules() {
		return rules;
	}

	public void setRules(List<PorterResponseMonitorRule> rules) {
		this.rules = rules;
	}

	@Override
	public int compareTo(PorterResponseMonitor o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}
}

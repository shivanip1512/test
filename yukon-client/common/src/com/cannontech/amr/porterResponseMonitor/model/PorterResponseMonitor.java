package com.cannontech.amr.porterResponseMonitor.model;

import java.util.List;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.util.LazyList;

public class PorterResponseMonitor implements Comparable<PorterResponseMonitor> {
	private Integer monitorId;
	private String name;
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

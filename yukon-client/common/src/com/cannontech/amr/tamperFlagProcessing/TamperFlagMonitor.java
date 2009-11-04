package com.cannontech.amr.tamperFlagProcessing;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;



public class TamperFlagMonitor implements PointMonitor, Comparable<TamperFlagMonitor> {

	private Integer tamperFlagMonitorId;
	private String tamperFlagMonitorName;
	private String groupName;
	private MonitorEvaluatorStatus evaluatorStatus;
	
	public Integer getTamperFlagMonitorId() {
		return tamperFlagMonitorId;
	}
	public void setTamperFlagMonitorId(Integer tamperFlagMonitorId) {
		this.tamperFlagMonitorId = tamperFlagMonitorId;
	}
	public String getTamperFlagMonitorName() {
		return tamperFlagMonitorName;
	}
	public void setTamperFlagMonitorName(String name) {
		this.tamperFlagMonitorName = name;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public MonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}
	public void setEvaluatorStatus(MonitorEvaluatorStatus evaluatorStatus) {
		this.evaluatorStatus = evaluatorStatus;
	}
	
	@Override
	public int compareTo(TamperFlagMonitor o) {
		return this.getTamperFlagMonitorName().compareToIgnoreCase(o.getTamperFlagMonitorName());
	}
}

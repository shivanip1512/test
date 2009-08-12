package com.cannontech.amr.tamperFlagProcessing;



public class TamperFlagMonitor {

	private Integer tamperFlagMonitorId;
	private String name;
	private String groupName;
	private TamperFlagMonitorEvaluatorStatus evaluatorStatus;
	
	public Integer getTamperFlagMonitorId() {
		return tamperFlagMonitorId;
	}
	public void setTamperFlagMonitorId(Integer tamperFlagMonitorId) {
		this.tamperFlagMonitorId = tamperFlagMonitorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public TamperFlagMonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}
	public void setEvaluatorStatus(TamperFlagMonitorEvaluatorStatus evaluatorStatus) {
		this.evaluatorStatus = evaluatorStatus;
	}
	
	
}

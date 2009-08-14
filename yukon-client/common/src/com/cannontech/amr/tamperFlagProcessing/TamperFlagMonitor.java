package com.cannontech.amr.tamperFlagProcessing;



public class TamperFlagMonitor {

	private Integer tamperFlagMonitorId;
	private String tamperFlagMonitorName;
	private String groupName;
	private TamperFlagMonitorEvaluatorStatus evaluatorStatus;
	
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
	public TamperFlagMonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}
	public void setEvaluatorStatus(TamperFlagMonitorEvaluatorStatus evaluatorStatus) {
		this.evaluatorStatus = evaluatorStatus;
	}
	
	
}

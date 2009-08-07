package com.cannontech.amr.outageProcessing;

import org.springframework.core.style.ToStringCreator;

public class OutageMonitor {

	private Integer outageMonitorId;
	private String name;
	private String groupName;
	private int timePeriod;
	private int numberOfOutages;
	private OutageMonitorEvaluatorStatus evaluatorStatus;
	
	
	public Integer getOutageMonitorId() {
		return outageMonitorId;
	}

	public void setOutageMonitorId(Integer outageMonitorId) {
		this.outageMonitorId = outageMonitorId;
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

	public int getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
	}

	public int getNumberOfOutages() {
		return numberOfOutages;
	}

	public void setNumberOfOutages(int numberOfOutages) {
		this.numberOfOutages = numberOfOutages;
	}

	public OutageMonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}
	
	public void setEvaluatorStatus(OutageMonitorEvaluatorStatus status) {
		this.evaluatorStatus = status;
	}
	
	@Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("outageMonitorId", getOutageMonitorId());
        tsc.append("name", getName());
        tsc.append("groupName", getGroupName());
        tsc.append("timePeriod", getTimePeriod());
        tsc.append("numberOfOutages", getNumberOfOutages());
        tsc.append("evaluatorStatus", getEvaluatorStatus());
        return tsc.toString();
    }
}

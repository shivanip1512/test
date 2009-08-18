package com.cannontech.amr.outageProcessing;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.MonitorEvaluatorStatus;

public class OutageMonitor {

	private Integer outageMonitorId;
	private String outageMonitorName;
	private String groupName;
	private int timePeriod;
	private int numberOfOutages;
	private MonitorEvaluatorStatus evaluatorStatus;
	
	
	public Integer getOutageMonitorId() {
		return outageMonitorId;
	}

	public void setOutageMonitorId(Integer outageMonitorId) {
		this.outageMonitorId = outageMonitorId;
	}

	public String getOutageMonitorName() {
		return outageMonitorName;
	}
	
	public void setOutageMonitorName(String outageMonitorName) {
		this.outageMonitorName = outageMonitorName;
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

	public MonitorEvaluatorStatus getEvaluatorStatus() {
		return evaluatorStatus;
	}
	
	public void setEvaluatorStatus(MonitorEvaluatorStatus status) {
		this.evaluatorStatus = status;
	}
	
	@Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("outageMonitorId", getOutageMonitorId());
        tsc.append("outageMonitorName", getOutageMonitorName());
        tsc.append("groupName", getGroupName());
        tsc.append("timePeriod", getTimePeriod());
        tsc.append("numberOfOutages", getNumberOfOutages());
        tsc.append("evaluatorStatus", getEvaluatorStatus());
        return tsc.toString();
    }
}

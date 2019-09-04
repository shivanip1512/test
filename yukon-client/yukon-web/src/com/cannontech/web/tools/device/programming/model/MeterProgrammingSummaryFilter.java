package com.cannontech.web.tools.device.programming.model;

import java.util.List;

public class MeterProgrammingSummaryFilter {
	public enum DisplayableStatus {
		//I will map to statuses in database
		SUCCESS, FAILURE, IN_PROGRESS, CONFIRMING
	}
	
	private List<MeterProgramInfo> programs;
	private List<DisplayableStatus> statuses;
	public List<MeterProgramInfo> getPrograms() {
		return programs;
	}
	public void setPrograms(List<MeterProgramInfo> programs) {
		this.programs = programs;
	}
	public List<DisplayableStatus> getStatuses() {
		return statuses;
	}
	public void setStatuses(List<DisplayableStatus> statuses) {
		this.statuses = statuses;
	}
	
}

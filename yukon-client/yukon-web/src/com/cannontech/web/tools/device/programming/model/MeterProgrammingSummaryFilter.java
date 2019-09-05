package com.cannontech.web.tools.device.programming.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.google.common.collect.Lists;

public class MeterProgrammingSummaryFilter {
	public enum DisplayableStatus {
		SUCCESS(ProgrammingStatus.IDLE), 
		FAILURE(ProgrammingStatus.FAILED, ProgrammingStatus.CANCELED, ProgrammingStatus.MISMATCHED), 
		IN_PROGRESS(ProgrammingStatus.INITIATING,ProgrammingStatus.UPLOADING), 
		CONFIRMING(ProgrammingStatus.CONFIRMING);
		private List<ProgrammingStatus> programStatuses= null;
		private DisplayableStatus(ProgrammingStatus... statuses) {
			  programStatuses = Collections.unmodifiableList(Lists.newArrayList(statuses));
		}
		public List<ProgrammingStatus> getProgramStatuses() {
			return programStatuses;
		}
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

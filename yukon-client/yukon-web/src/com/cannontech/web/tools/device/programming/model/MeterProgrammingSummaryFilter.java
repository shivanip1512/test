package com.cannontech.web.tools.device.programming.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.programming.model.ProgramStatus;
import com.google.common.collect.Lists;

public class MeterProgrammingSummaryFilter {
	public enum DisplayableStatus {
		SUCCESS(ProgramStatus.IDLE), 
		FAILURE(ProgramStatus.FAILED, ProgramStatus.CANCELED, ProgramStatus.MISMATCHED), 
		IN_PROGRESS(ProgramStatus.INITIATING,ProgramStatus.UPLOADING), 
		CONFIRMING(ProgramStatus.CONFIRMING);
		private List<ProgramStatus> programStatuses= null;
		private DisplayableStatus(ProgramStatus... statuses) {
			  programStatuses = Collections.unmodifiableList(Lists.newArrayList(statuses));
		}
		public List<ProgramStatus> getProgramStatuses() {
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

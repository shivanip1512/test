package com.cannontech.web.tools.device.programming.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.google.common.collect.Lists;

public class MeterProgrammingSummaryFilter {
    public enum DisplayableStatus {
        SUCCESS(ProgrammingStatus.IDLE), 
        FAILURE(ProgrammingStatus.FAILED, ProgrammingStatus.CANCELED, ProgrammingStatus.MISMATCHED), 
        IN_PROGRESS(ProgrammingStatus.INITIATING, ProgrammingStatus.UPLOADING), 
        CONFIRMING(ProgrammingStatus.CONFIRMING);
        private List<ProgrammingStatus> programStatuses = null;

        private DisplayableStatus(ProgrammingStatus... statuses) {
            programStatuses = Collections.unmodifiableList(Lists.newArrayList(statuses));
        }

        public List<ProgrammingStatus> getProgramStatuses() {
            return programStatuses;
        }

        public static DisplayableStatus getDisplayableStatus(ProgrammingStatus status) {
            return Arrays.asList(DisplayableStatus.values())
                .stream().filter(displayableStatus -> displayableStatus.getProgramStatuses().contains(status))
                .findFirst().get();  
        }
    }

    private List<MeterProgramInfo> programs;
    private List<DisplayableStatus> statuses;
    private List<DeviceGroup> groups;

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

    public List<DeviceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<DeviceGroup> groups) {
        this.groups = groups;
    }
}

package com.cannontech.common.dr.program.setup.model;

import java.util.List;

import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.pao.PaoType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value={ "programId"}, allowGetters= true, ignoreUnknown = true)
public class LoadProgram {

    private Integer programId;
    private String name;

    private PaoType type;
    private OperationalState operationalState;
    private ProgramConstraint constraint;

    private Double triggerOffset;
    private Double restoreOffset;

    private List<ProgramGear> gears;
    private ProgramControlWindow controlWindow;
    private List<ProgramGroup> assignedGroups;
    private Notification notification;

    private List<ProgramDirectMemberControl> memberControl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public Double getTriggerOffset() {
        return triggerOffset;
    }

    public void setTriggerOffset(Double triggerOffset) {
        this.triggerOffset = triggerOffset;
    }

    public Double getRestoreOffset() {
        return restoreOffset;
    }

    public void setRestoreOffset(Double restoreOffset) {
        this.restoreOffset = restoreOffset;
    }

    public OperationalState getOperationalState() {
        return operationalState;
    }

    public void setOperationalState(OperationalState operationalState) {
        this.operationalState = operationalState;
    }

    public List<ProgramGear> getGears() {
        return gears;
    }

    public void setGears(List<ProgramGear> gears) {
        this.gears = gears;
    }

    public ProgramControlWindow getControlWindow() {
        return controlWindow;
    }

    public void setControlWindow(ProgramControlWindow controlWindow) {
        this.controlWindow = controlWindow;
    }

    public ProgramConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(ProgramConstraint constraint) {
        this.constraint = constraint;
    }

    public List<ProgramDirectMemberControl> getMemberControl() {
        return memberControl;
    }

    public void setMemberControl(List<ProgramDirectMemberControl> memberControl) {
        this.memberControl = memberControl;
    }

    public List<ProgramGroup> getAssignedGroups() {
        return assignedGroups;
    }

    public void setAssignedGroups(List<ProgramGroup> assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}

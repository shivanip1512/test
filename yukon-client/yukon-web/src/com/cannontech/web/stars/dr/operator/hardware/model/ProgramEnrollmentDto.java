package com.cannontech.web.stars.dr.operator.hardware.model;

public class ProgramEnrollmentDto {
    private int assignedProgramId;
    private int loadGroupId;
    private int relay;

    public int getAssignedProgramId() {
        return assignedProgramId;
    }

    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }

    public int getLoadGroupId() {
        return loadGroupId;
    }

    public void setLoadGroupId(int loadGroupId) {
        this.loadGroupId = loadGroupId;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }
}

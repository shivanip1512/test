package com.cannontech.stars.dr.displayable.model;

import com.cannontech.stars.dr.appliance.model.AssignedProgramName;

/**
 * This class represents an enrollment for a specific piece of inventory.
 */
public class DisplayableInventoryEnrollment {
    private int assignedProgramId;
    private int programId;
    private AssignedProgramName programName;
    private int loadGroupId;
    private String loadGroupName;
    private int relay;

    public DisplayableInventoryEnrollment() {
    }

    public DisplayableInventoryEnrollment(int assignedProgramId, int programId,
            AssignedProgramName programName) {
        this.assignedProgramId = assignedProgramId;
        this.programId = programId;
        this.programName = programName;
    }

    public DisplayableInventoryEnrollment(int assignedProgramId, int programId,
            AssignedProgramName programName, int loadGroupId,
            String loadGroupName, int relay) {
        this.assignedProgramId = assignedProgramId;
        this.programId = programId;
        this.programName = programName;
        this.loadGroupId = loadGroupId;
        this.loadGroupName = loadGroupName;
        this.relay = relay;
    }

    public int getAssignedProgramId() {
        return assignedProgramId;
    }

    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public AssignedProgramName getProgramName() {
        return programName;
    }

    public void setProgramName(AssignedProgramName programName) {
        this.programName = programName;
    }

    public int getLoadGroupId() {
        return loadGroupId;
    }

    public void setLoadGroupId(int loadGroupId) {
        this.loadGroupId = loadGroupId;
    }

    public String getLoadGroupName() {
        return loadGroupName;
    }

    public void setLoadGroupName(String loadGroupName) {
        this.loadGroupName = loadGroupName;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }
}

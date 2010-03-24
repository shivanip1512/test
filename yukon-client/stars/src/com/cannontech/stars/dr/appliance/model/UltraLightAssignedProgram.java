package com.cannontech.stars.dr.appliance.model;

public class UltraLightAssignedProgram {
    private int assignedProgramId;
    private int programId;
    private String programName;
    private String displayName;

    private int applianceCategoryId;
    private String applianceCategoryName;

    public UltraLightAssignedProgram(int assignedProgramId, int programId,
            String programName, String displayName, int applianceCategoryId,
            String applianceCategoryName) {
        this.assignedProgramId = assignedProgramId;
        this.programId = programId;
        this.programName = programName;
        this.displayName = displayName;
        this.applianceCategoryId = applianceCategoryId;
        this.applianceCategoryName = applianceCategoryName;
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

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }

    public String getApplianceCategoryName() {
        return applianceCategoryName;
    }

    public void setApplianceCategoryName(String applianceCategoryName) {
        this.applianceCategoryName = applianceCategoryName;
    }
}

package com.cannontech.stars.dr.program.service;

public class ProgramEnrollmentRequest {
    private int applianceCategoryId;
    private int programId;
    private int inventoryId;
    private boolean enroll; 
    
    public ProgramEnrollmentRequest() {

    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int categoryId) {
        this.applianceCategoryId = categoryId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public boolean isEnroll() {
        return enroll;
    }

    public void setEnroll(boolean enroll) {
        this.enroll = enroll;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + applianceCategoryId;
        result = prime * result + (enroll ? 1231 : 1237);
        result = prime * result + inventoryId;
        result = prime * result + programId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProgramEnrollmentRequest other = (ProgramEnrollmentRequest) obj;
        if (applianceCategoryId != other.applianceCategoryId)
            return false;
        if (enroll != other.enroll)
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (programId != other.programId)
            return false;
        return true;
    }
    
}

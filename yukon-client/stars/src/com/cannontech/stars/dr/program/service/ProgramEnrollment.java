package com.cannontech.stars.dr.program.service;

public class ProgramEnrollment {
    private int applianceCategoryId;
    private int programId;
    private int lmGroupId;
    private float applianceKW;
    private int relay;
    private int inventoryId;
    private boolean enroll; 
    
    public ProgramEnrollment() {}

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

    public int getLmGroupId() {
        return lmGroupId;
    }
    public void setLmGroupId(int lmGroupId) {
        this.lmGroupId = lmGroupId;
    }
    
    public float getApplianceKW() {
        return applianceKW;
    }
    public void setApplianceKW(float applianceKW) {
        this.applianceKW = applianceKW;
    }

    public int getRelay() {
        return relay;
    }
    public void setRelay(int relay) {
        this.relay = relay;
    }
    public void setRelay(String relay) {
        if (relay.equalsIgnoreCase("(none)")) {
            this.relay = 0;
        } else {
            this.relay = Integer.parseInt(relay);
        }
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
        final ProgramEnrollment other = (ProgramEnrollment) obj;
        if (this.getInventoryId() != other.getInventoryId())
            return false;
        if (this.getProgramId() != other.getProgramId())
            return false;
        if (this.getApplianceCategoryId() != 0 &&
            this.getApplianceCategoryId() != other.getApplianceCategoryId())
            return false;
        if (this.getApplianceKW() != 0 &&
            this.getApplianceKW() != other.getApplianceKW())
            return false;
        if (this.getLmGroupId() != 0 &&
            this.getLmGroupId() != other.getLmGroupId())
            return false;
        if(this.getRelay() != 0 &&
           this.getRelay() != other.getRelay())
            return false;
        return true;
    }
    
}

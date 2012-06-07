package com.cannontech.stars.dr.program.service;

import org.apache.commons.lang.StringUtils;

import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.model.Program;

public class ProgramEnrollment {
    private int applianceCategoryId;
    private int assignedProgramId;
    private int lmGroupId;
    private float applianceKW;
    private int relay;
    private int inventoryId;
    private boolean enroll; 
    
    public ProgramEnrollment() {}

    public ProgramEnrollment(EnrollmentHelper enrollmentHelper, LMHardwareBase lmHardwareBase,
                             ApplianceCategory applianceCategory, Program program, LoadGroup loadGroup) {
        setInventoryId(lmHardwareBase.getInventoryId());
        setAssignedProgramId(program.getProgramId());
        if (enrollmentHelper.getApplianceKW() != null) {
            setApplianceKW(enrollmentHelper.getApplianceKW());
        }
        if (applianceCategory != null) {
            setApplianceCategoryId(applianceCategory.getApplianceCategoryId());
        } else {
            setApplianceCategoryId(program.getApplianceCategoryId());
        }
        if (loadGroup != null) {
            setLmGroupId(loadGroup.getLoadGroupId());
        }
        if (!StringUtils.isBlank(enrollmentHelper.getRelay())) {
            setRelay(enrollmentHelper.getRelay());
        }
    }
    
    public ProgramEnrollment(int applianceCategoryId, float applianceKW,
            boolean enroll, int inventoryId, int lmGroupId,
            int assignedProgramId, int relay) {
        super();
        this.applianceCategoryId = applianceCategoryId;
        this.applianceKW = applianceKW;
        this.enroll = enroll;
        this.inventoryId = inventoryId;
        this.lmGroupId = lmGroupId;
        this.assignedProgramId = assignedProgramId;
        this.relay = relay;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }
    public void setApplianceCategoryId(int categoryId) {
        this.applianceCategoryId = categoryId;
    }

    public int getAssignedProgramId() {
        return assignedProgramId;
    }

    public void setAssignedProgramId(int assignedProgramId) {
        this.assignedProgramId = assignedProgramId;
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
        result = prime * result + Float.floatToIntBits(applianceKW);
        result = prime * result + (enroll ? 1231 : 1237);
        result = prime * result + inventoryId;
        result = prime * result + lmGroupId;
        result = prime * result + assignedProgramId;
        result = prime * result + relay;
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
        ProgramEnrollment other = (ProgramEnrollment) obj;
        if (applianceCategoryId != other.applianceCategoryId)
            return false;
        if (Float.floatToIntBits(applianceKW) != Float.floatToIntBits(other.applianceKW))
            return false;
        if (enroll != other.enroll)
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (lmGroupId != other.lmGroupId)
            return false;
        if (assignedProgramId != other.assignedProgramId)
            return false;
        if (relay != other.relay)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        String temp = "["+ applianceCategoryId +", "+
                      applianceKW +", "+
                      enroll +", "+
                      inventoryId +", "+
                      lmGroupId +", "+
                      assignedProgramId +", "+
                      relay +"]";
        return temp;
    }

    
    /** This method is very comparable to an equals method, but will not 
     * return false on any cases where a given value for a supplied ProgramEnrollment
     * has not been set.  This means if one programEnrollment has a relay of 0 and the
     * other has a relay of 12 they are still considered equivalent, but not equal.
     */
    public boolean equivalent(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProgramEnrollment other = (ProgramEnrollment) obj;
        if (this.inventoryId != other.inventoryId)
            return false;
        if (this.assignedProgramId != other.assignedProgramId)
            return false;
        if (this.applianceCategoryId != 0 &&
            other.applianceCategoryId != 0 &&
            this.applianceCategoryId != other.applianceCategoryId)
            return false;
        if (this.applianceKW != 0 &&
            other.applianceKW != 0 &&
            this.applianceKW != other.applianceKW)
            return false;
        if (this.lmGroupId != 0 &&
            other.lmGroupId != 0 &&
            this.lmGroupId != other.lmGroupId)
            return false;
        if(this.relay != 0 &&
           other.relay != 0 &&
           this.relay != other.relay)
            return false;
        return true;
    }
    
    public void update(ProgramEnrollment programEnrollment){
        this.applianceCategoryId = programEnrollment.applianceCategoryId;
        this.applianceKW = programEnrollment.applianceKW;
        this.enroll = programEnrollment.enroll;
        this.inventoryId = programEnrollment.inventoryId;
        this.lmGroupId = programEnrollment.lmGroupId;
        this.assignedProgramId = programEnrollment.assignedProgramId;
        this.relay = programEnrollment.relay;
    }
    
}
package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.google.common.collect.Lists;

public class ProgramEnrollment {
    public static class InventoryEnrollment {
        private int inventoryId;
        private boolean enrolled;
        private int relay;
        private int loadGroupId;
       

        public int getLoadGroupId() {
            return loadGroupId;
        }

        public void setLoadGroupId(int loadGroupId) {
            this.loadGroupId = loadGroupId;
        }

        public InventoryEnrollment() {
        }

        public InventoryEnrollment(
                DisplayableEnrollmentInventory displayable) {
            inventoryId = displayable.getInventoryId();
            enrolled = displayable.isEnrolled();
            relay = displayable.getRelay();
            loadGroupId = displayable.getLoadGroupId();
        }
        
        public int getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(int inventoryId) {
            this.inventoryId = inventoryId;
        }

        public boolean isEnrolled() {
            return enrolled;
        }

        public void setEnrolled(boolean enrolled) {
            this.enrolled = enrolled;
        }

        public int getRelay() {
            return relay;
        }

        public void setRelay(int relay) {
            this.relay = relay;
        }
     
    }

    private int loadGroupId;
    private List<InventoryEnrollment> inventoryEnrollments;

    public ProgramEnrollment() {
        inventoryEnrollments = LazyList.ofInstance(InventoryEnrollment.class);
    }

    public ProgramEnrollment(DisplayableEnrollmentProgram displayableEnrollmentProgram) {
        loadGroupId = displayableEnrollmentProgram.getLoadGroupId();
        inventoryEnrollments = Lists.newArrayList();
        for (DisplayableEnrollmentInventory item : displayableEnrollmentProgram.getInventory()) {
            inventoryEnrollments.add(new InventoryEnrollment(item));
        }
    }

    public int getLoadGroupId() {
        return loadGroupId;
    }

    public void setLoadGroupId(int loadGroupId) {
        this.loadGroupId = loadGroupId;
    }

    public List<InventoryEnrollment> getInventoryEnrollments() {
        return inventoryEnrollments;
    }

    public void setInventoryEnrollments(
            List<InventoryEnrollment> inventoryEnrollments) {
        this.inventoryEnrollments = inventoryEnrollments;
    }

    /**
     * Convert this object into one which can be used by an EnrollmentHelperService.
     */
    public List<com.cannontech.stars.dr.program.service.ProgramEnrollment> makeProgramEnrollments(
            int applianceCategoryId, int assignedProgramId) {
        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> retVal = Lists.newArrayList();
        for (InventoryEnrollment inventoryEnrollment : inventoryEnrollments) {
            com.cannontech.stars.dr.program.service.ProgramEnrollment pe =
                new com.cannontech.stars.dr.program.service.ProgramEnrollment();
            pe.setApplianceCategoryId(applianceCategoryId);
            pe.setAssignedProgramId(assignedProgramId);
            pe.setLmGroupId(inventoryEnrollment.getLoadGroupId());
            pe.setRelay(inventoryEnrollment.getRelay());
            pe.setInventoryId(inventoryEnrollment.getInventoryId());
            pe.setEnroll(inventoryEnrollment.isEnrolled());
            retVal.add(pe);
        }
        return retVal;
    }
    
    public boolean isBlankEnrollment() {
        for (InventoryEnrollment inventoryEnrollment : getInventoryEnrollments()) {
            if (inventoryEnrollment.isEnrolled()) {
                return false;
            }
        }
        return true;
    }
}

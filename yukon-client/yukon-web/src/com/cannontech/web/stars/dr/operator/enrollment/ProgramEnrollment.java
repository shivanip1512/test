package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.google.common.collect.Lists;

public class ProgramEnrollment {
    public static class InventoryEnrollment {
        private int inventoryId;
        private boolean enrolled;
        private int relay;

        public InventoryEnrollment() {
        }

        public InventoryEnrollment(
                DisplayableEnrollmentInventory displayable) {
            inventoryId = displayable.getInventoryId();
            enrolled = displayable.isEnrolled();
            relay = displayable.getRelay();
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

    @SuppressWarnings("unchecked")
    public ProgramEnrollment() {
        inventoryEnrollments =
            LazyList.decorate(Lists.newArrayList(),
                              FactoryUtils.instantiateFactory(InventoryEnrollment.class));
    }

    public ProgramEnrollment(
            DisplayableEnrollmentProgram displayableEnrollmentProgram) {
        loadGroupId = displayableEnrollmentProgram.getLoadGroupId();
        inventoryEnrollments = Lists.newArrayList();
        for (DisplayableEnrollmentInventory item
                : displayableEnrollmentProgram.getInventory()) {
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
}

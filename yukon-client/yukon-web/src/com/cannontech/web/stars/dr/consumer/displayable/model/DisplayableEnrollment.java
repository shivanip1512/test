package com.cannontech.web.stars.dr.consumer.displayable.model;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.cannontech.stars.dr.appliance.model.ApplianceType;
import com.cannontech.stars.dr.program.model.Program;

public final class DisplayableEnrollment {
    public static final Comparator<DisplayableEnrollmentProgram> enrollmentProgramComparator;
    public static final Comparator<DisplayableEnrollment> enrollmentComparator;
    private int applianceCategoryId;
    private ApplianceType applianceType;
    private String applianceLogo;
    private Set<DisplayableEnrollmentProgram> enrollmentPrograms;
    
    static {
        
        enrollmentProgramComparator = new Comparator<DisplayableEnrollmentProgram>() {
            @Override
            public int compare(DisplayableEnrollmentProgram o1, DisplayableEnrollmentProgram o2) {
                Integer order1 = o1.getProgram().getProgramOrder();
                Integer order2 = o2.getProgram().getProgramOrder();
                return order1.compareTo(order2);
            }
        };
        
        enrollmentComparator = new Comparator<DisplayableEnrollment>() {
            @Override
            public int compare(DisplayableEnrollment o1, DisplayableEnrollment o2) {
                return o1.getApplianceType().name().compareTo(o2.getApplianceType().name());
            }
        };
    }
    
    public DisplayableEnrollment() {
        
    }
    
    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public void setApplianceCategoryId(int applianceCategoryId) {
        this.applianceCategoryId = applianceCategoryId;
    }



    public ApplianceType getApplianceType() {
        return applianceType;
    }

    public void setApplianceType(ApplianceType applianceType) {
        this.applianceType = applianceType;
    }

    public String getApplianceLogo() {
        return applianceLogo;
    }

    public void setApplianceLogo(String applianceLogo) {
        this.applianceLogo = applianceLogo;
    }

    public Set<DisplayableEnrollmentProgram> getEnrollmentPrograms() {
        return enrollmentPrograms;
    }

    public void setEnrollmentPrograms(
            Set<DisplayableEnrollmentProgram> enrollmentPrograms) {
        this.enrollmentPrograms = enrollmentPrograms;
    }

    public int getRowCount() {
        int count = 0;
        for (DisplayableEnrollmentProgram program : enrollmentPrograms) {
            count += program.getInventory().size();
        }
        return count;
    }
    
    public static final class DisplayableEnrollmentProgram {
        private final Program program;
        private final List<DisplayableEnrollmentInventory> inventory;
        
        public DisplayableEnrollmentProgram(Program program, List<DisplayableEnrollmentInventory> inventory) {
            this.program = program;
            this.inventory = inventory;
        }
        
        public Program getProgram() {
            return program;
        }
        
        public List<DisplayableEnrollmentInventory> getInventory() {
            return inventory;
        }
    }
    
    public static final class DisplayableEnrollmentInventory {
        private final int inventoryId;
        private final String displayName;
        private final boolean isEnrolled;
        
        public DisplayableEnrollmentInventory(int inventoryId, String displayName, boolean isEnrolled) {
            this.inventoryId = inventoryId;
            this.displayName = displayName;
            this.isEnrolled = isEnrolled;
        }

        public int getInventoryId() {
            return inventoryId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isEnrolled() {
            return isEnrolled;
        }
    }
    
}

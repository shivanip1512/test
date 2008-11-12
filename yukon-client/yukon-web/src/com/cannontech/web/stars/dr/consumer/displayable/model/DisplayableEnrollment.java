package com.cannontech.web.stars.dr.consumer.displayable.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.dr.program.model.Program;

public final class DisplayableEnrollment {
    public static final Comparator<DisplayableEnrollmentProgram> enrollmentProgramComparator;
    public static final Comparator<DisplayableEnrollment> enrollmentComparator;
    private ApplianceCategory applianceCategory;
    private ApplianceTypeEnum applianceTypeEnum;
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
                return o1.getApplianceTypeEnum().name().compareTo(o2.getApplianceTypeEnum().name());
            }
        };
    }
    
    public DisplayableEnrollment() {
        
    }
    
    public ApplianceCategory getApplianceCategory() {
        return this.applianceCategory;
    }

    public void setApplianceCategory(ApplianceCategory applianceCategory) {
        this.applianceCategory = applianceCategory;
    }



    public ApplianceTypeEnum getApplianceTypeEnum() {
        return applianceTypeEnum;
    }

    public void setApplianceType(ApplianceTypeEnum applianceTypeEnum) {
        this.applianceTypeEnum = applianceTypeEnum;
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
    
    /**
     * Method to get all program ids associated with this category
     * @return Array of program ids
     */
    public String[] getProgramIds(){
    	Set<DisplayableEnrollmentProgram> programSet = getEnrollmentPrograms();
    	String[] idArray = new String[programSet.size()];
    	
    	int count = 0;
    	for(DisplayableEnrollmentProgram program : programSet) {
    		idArray[count++] = String.valueOf(program.getProgram().getProgramId());
    	}
    	
    	return idArray;
    }
    
    /**
     * Method to determine if at least one program in this category has at least one 
     * inventory enrolled
     * @return True if one or more inventory enrolled
     */
    public boolean isEnrolled() {
    	
    	for(DisplayableEnrollmentProgram program : enrollmentPrograms) {
    		if(program.isEnrolled()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Method to get inventory ids that are enrolled in a program in this category
     * @return Array of inventory ids
     */
    public String[] getEnrolledInventoryIds(){
    	
    	String[] idArray = null;
    	
    	for(DisplayableEnrollmentProgram program : enrollmentPrograms) {
    		String[] ids = program.getEnrolledInventoryIds();
    		if(ids.length > 0) {
    			return ids;
    		} else {
    			idArray = program.getAllInventoryIds();
    		}
    	}
    	
		return idArray;
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
        
        /**
         * Method to get all inventory ids associated with this program
         * @return Array of inventory ids
         */
        public String[] getAllInventoryIds(){
        	
        	List<DisplayableEnrollmentInventory> inventoryList = getInventory();
        	String[] idArray = new String[inventoryList.size()];
        	
        	for(int i=0; i< inventoryList.size(); i++) {
        		DisplayableEnrollmentInventory inventory = inventoryList.get(i);
        		idArray[i] = String.valueOf(inventory.getInventoryId());
        	}
        	
        	return idArray;
        }

        /**
         * Method to get all of the inventory ids that are enrolled in this program
         * @return Array of inventory ids
         */
        public String[] getEnrolledInventoryIds(){
        	
        	List<DisplayableEnrollmentInventory> inventoryList = getInventory();
        	List<String> idList = new ArrayList<String>();
        	
        	for(int i=0; i< inventoryList.size(); i++) {
        		DisplayableEnrollmentInventory inventory = inventoryList.get(i);
        		if(inventory.isEnrolled) {
        			idList.add(String.valueOf(inventory.getInventoryId()));
        		}
        	}
        	
    		return idList.toArray(new String[]{});
        }

        public String[] getProgramInventoryIds(){
        	String[] enrolledInventoryIds = getEnrolledInventoryIds();
        	if(enrolledInventoryIds.length > 0) {
        		return enrolledInventoryIds;
        	} else {
        		return getAllInventoryIds();
        	}
        }
        
        /**
         * Method to determine if at least one inventory is enrolled in this program 
         * @return True if one or more inventory enrolled
         */
        public boolean isEnrolled() {
        	
        	for(DisplayableEnrollmentInventory inv : inventory) {
        		if(inv.isEnrolled()) {
        			return true;
        		}
        	}
        	return false;
        }
        
        public String getDescriptionUrl() {
        	return program.getDescriptionUrl();
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

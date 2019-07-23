package com.cannontech.stars.dr.displayable.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.cannontech.common.pao.PaoType;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentInService;
import com.cannontech.stars.dr.program.model.Program;

public final class DisplayableEnrollment {
	
    public static final Comparator<DisplayableEnrollmentProgram> enrollmentProgramComparator;
    public static final Comparator<DisplayableEnrollment> byApplianceCategoryNameComparator;
    private ApplianceCategory applianceCategory;
    private Set<DisplayableEnrollmentProgram> enrollmentPrograms;
    
    static {
        
        enrollmentProgramComparator = new Comparator<DisplayableEnrollmentProgram>() {
            @Override
            public int compare(DisplayableEnrollmentProgram o1, DisplayableEnrollmentProgram o2) {
                
                return new CompareToBuilder()
                    .append(o1.getProgram().getProgramOrder(), o2.getProgram().getProgramOrder())
                    .append(o1.getProgram().getProgramId(), o2.getProgram().getProgramId())
                    .toComparison();

            }
        };

        byApplianceCategoryNameComparator = new Comparator<DisplayableEnrollment>() {
            @Override
            public int compare(DisplayableEnrollment o1, DisplayableEnrollment o2) {
                return o1.getApplianceCategory().getDisplayName().compareTo(o2.getApplianceCategory().getDisplayName());
            }
        };
    }
    
    public ApplianceCategory getApplianceCategory() {
        return this.applianceCategory;
    }

    public void setApplianceCategory(ApplianceCategory applianceCategory) {
        this.applianceCategory = applianceCategory;
    }

    public ApplianceTypeEnum getApplianceTypeEnum() {
        return applianceCategory.getApplianceType();
    }

    public String getApplianceLogo() {
        return applianceCategory.getIcon();
    }

    public Set<DisplayableEnrollmentProgram> getEnrollmentPrograms() {
        return enrollmentPrograms;
    }

    public void setEnrollmentPrograms(Set<DisplayableEnrollmentProgram> enrollmentPrograms) {
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
        private final ApplianceCategory applianceCategory;
        private final Program program;
        private List<DisplayableEnrollmentInventory> inventory;
        private Boolean enrolled;

        public DisplayableEnrollmentProgram(
                ApplianceCategory applianceCategory, Program program,
                List<DisplayableEnrollmentInventory> inventory) {
            this.applianceCategory = applianceCategory;
            this.program = program;
            this.inventory = inventory;
            this.enrolled = null;
        }

        public ApplianceCategory getApplianceCategory() {
            return applianceCategory;
        }

        public Program getProgram() {
            return program;
        }

        public int getLoadGroupId() {
            if (inventory == null || inventory.size() == 0) {
                return 0;
            }
            // all of the inventory has the same load group
            for (DisplayableEnrollmentInventory inventoryItem : inventory) {
                if (inventoryItem.enrolled) {
                    return inventoryItem.getLoadGroupId();
                }
            }
            // no enrolled hardware or virtual program
            return 0;
        }
        
        /**
         * Method to get Load Group ids that are enrolled in a program in this category
         * 
         * @return Set of loadGroup ids
         */
        public Set<Integer> getLoadGroupIds() {
            Set<Integer> loadGroupIds = null;
            if (inventory == null || inventory.size() == 0) {
                return loadGroupIds;
            }
            loadGroupIds = new HashSet<Integer>();

            for (DisplayableEnrollmentInventory inventoryItem : inventory) {
                if (inventoryItem.enrolled) {
                    loadGroupIds.add(inventoryItem.getLoadGroupId());
                }
            }

            return loadGroupIds;
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
        		if(inventory.enrolled) {
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
            if (enrolled == null) {
                boolean enrolled = false;
                for (DisplayableEnrollmentInventory inv : inventory) {
                    if (inv.isEnrolled()) {
                        enrolled = true;
                        break;
                    }
                }
                this.enrolled = enrolled;
            }
            return enrolled;
        }
        
        public boolean isNest() {
            return program.getPaoType().equals(PaoType.LM_NEST_PROGRAM);
        }
        
        public boolean isMeter() {
            return program.getPaoType().equals(PaoType.LM_METER_DISCONNECT_PROGRAM);
        }

        public String getDescriptionUrl() {
        	return program.getDescriptionUrl();
        }
    }
    
    public static final class DisplayableEnrollmentInventory {
        private final int inventoryId;
        private final String displayName;
        private final boolean enrolled;
        private final EnrollmentInService inService;
        private final int loadGroupId;
        private final int relay;
        private final int numRelays;

        public DisplayableEnrollmentInventory(int inventoryId,
                String displayName, boolean enrolled, EnrollmentInService inService,
                int loadGroupId, int relay, int numRelays) {
            this.inventoryId = inventoryId;
            this.displayName = displayName;
            this.enrolled = enrolled;
            this.inService = inService;
            this.loadGroupId = loadGroupId;
            this.relay = relay;
            this.numRelays = numRelays;
        }

        public int getInventoryId() {
            return inventoryId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isEnrolled() {
            return enrolled;
        }

        public EnrollmentInService getInService() {
            return inService;
        }

        public int getLoadGroupId() {
            return loadGroupId;
        }

        public int getRelay() {
            return relay;
        }

        public int getNumRelays() {
            return numRelays;
        }
    }
}

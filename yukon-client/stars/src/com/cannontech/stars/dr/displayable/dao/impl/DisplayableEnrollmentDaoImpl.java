package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Repository
public class DisplayableEnrollmentDaoImpl extends AbstractDisplayableDao implements DisplayableEnrollmentDao {
    
	private EnrollmentDao enrollmentDao;
	private PaoDefinitionDao paoDefinitionDao;
	
    @Override
    public List<DisplayableEnrollment> find(int customerAccountId) {

        // Get All ApplianceCategory's that the CustomerAccount could enroll in
        final List<ApplianceCategory> applianceCategories = applianceCategoryDao.findApplianceCategories(customerAccountId);
        
        // Get all Programs that belong to the ApplianceCategory's.
        final Map<ApplianceCategory, List<Program>> typeMap = programDao.getByApplianceCategories(applianceCategories);

        // Get all Hardware that belongs to this CustomerAccount
        final List<HardwareSummary> hardwareList = inventoryDao.getAllHardwareSummaryForAccount(customerAccountId);

        // Get all current program enrollments for account
        List<ProgramEnrollment> activeEnrollments = enrollmentDao.getActiveEnrollmentsByAccountId(customerAccountId);
        
        final Map<ApplianceCategory, DisplayableEnrollment> enrollmentMap = Maps.newHashMap();
        
        for (final ApplianceCategory applianceCategory : typeMap.keySet()) {
            List<Program> programList = typeMap.get(applianceCategory);
            
            // Don't display ApplianceCategory's that have no programs for the user to enroll in.
            if (programList == null || programList.isEmpty()) continue;
            
            DisplayableEnrollment enrollment = enrollmentMap.get(applianceCategory);
            if (enrollment == null) {
                enrollment = new DisplayableEnrollment();
                
                enrollment.setApplianceCategory(applianceCategory);

                TreeSet<DisplayableEnrollmentProgram> enrollmentPrograms = new TreeSet<DisplayableEnrollmentProgram>(DisplayableEnrollment.enrollmentProgramComparator);
                enrollment.setEnrollmentPrograms(enrollmentPrograms);
                
                enrollmentMap.put(applianceCategory, enrollment);
            }
            
            for (Program program : programList) {
            	DisplayableEnrollmentProgram displayableEnrollmentProgram = 
            		createDisplayableEnrollmentProgram(applianceCategory, program, hardwareList, activeEnrollments);
            	enrollment.getEnrollmentPrograms().add(displayableEnrollmentProgram);
            }
            
        }
        
        List<DisplayableEnrollment> resultList = new ArrayList<DisplayableEnrollment>(enrollmentMap.values());
        Collections.sort(resultList, DisplayableEnrollment.byApplianceCategoryNameComparator);
        return resultList;
    }

    @Override
    public List<DisplayableEnrollmentProgram> findEnrolledPrograms(int accountId) {
        List<DisplayableEnrollment> enrollments = find(accountId);

        List<DisplayableEnrollmentProgram> enrollmentPrograms = Lists.newArrayList();
        for (DisplayableEnrollment enrollment : enrollments) {
            for (DisplayableEnrollmentProgram enrollmentProgram
                    : enrollment.getEnrollmentPrograms()) {
                if (enrollmentProgram.isEnrolled()) {
                    enrollmentPrograms.add(enrollmentProgram);
                }
            }
        }
        return enrollmentPrograms;
    }

    @Override
    public DisplayableEnrollmentProgram getProgram(int accountId, int programId) {
        List<DisplayableEnrollment> customerEnrollments = find(accountId);
        for (DisplayableEnrollment enrollment : customerEnrollments) {
            for (DisplayableEnrollmentProgram enrollmentProgram : enrollment.getEnrollmentPrograms()) {
                if (enrollmentProgram.getProgram().getProgramId() == programId) {
                    return enrollmentProgram;
                }
            }
        }
        throw new NotFoundException("could not find enrollment program for " +
        		"account " + accountId + " and program " + programId);
    }
    
    private DisplayableEnrollmentProgram createDisplayableEnrollmentProgram(ApplianceCategory applianceCategory, final Program program,
            List<HardwareSummary> hardwareList, List<ProgramEnrollment> activeEnrollments) {
    	
        Predicate<HardwareSummary> enrollableFilter = new Predicate<HardwareSummary>() {
            @Override
            public boolean apply(HardwareSummary hardwareSummary) {
                InventoryIdentifier inventoryIdentifier = inventoryDao.getYukonInventory(hardwareSummary.getInventoryId());
                
                /* Exclude non enrollable hardware */
                if (!inventoryIdentifier.getHardwareType().isEnrollable()) {
                    return false;
                }
                
                /* Exclude non valid program enrollment (SEP hardware cannot be enrolled in DIRECT programs) */
                PaoTag inventoryEnrollmentTag = inventoryIdentifier.getHardwareType().getHardwareConfigType().getEnrollmentTag();
                PaoType programPaoType = program.getPaoType();
                return paoDefinitionDao.isTagSupported(programPaoType, inventoryEnrollmentTag);
            }
        };
        
        Iterable<HardwareSummary> filteredList = Iterables.filter(hardwareList, enrollableFilter);
        
        int programId = program.getProgramId();
    	List<DisplayableEnrollmentInventory> enrollmentInventoryList = Lists.newLinkedList();
    	
    	for(HardwareSummary hardware : filteredList) {
    		DisplayableEnrollmentInventory displayableEnrollmentInventory = createDisplayableEnrollmentInventory(hardware, programId, activeEnrollments);
    		enrollmentInventoryList.add(displayableEnrollmentInventory);
    	}
    	
        return new DisplayableEnrollmentProgram(applianceCategory, program, enrollmentInventoryList);
    }

    private DisplayableEnrollmentInventory createDisplayableEnrollmentInventory(
    		HardwareSummary hardware, 
    		int programId, 
    		List<ProgramEnrollment> activeEnrollments) 
    {
        
    	Integer inventoryId = hardware.getInventoryId();
		
		boolean enrolled = false;
		int loadGroupId = 0;
		int relay = 0;
		for(ProgramEnrollment activeEnrollment : activeEnrollments){
			int enrollmentProgramId = activeEnrollment.getAssignedProgramId();
			int enrollmentInventoryId = activeEnrollment.getInventoryId();
			
			if(enrollmentProgramId == programId && enrollmentInventoryId == inventoryId) {
				// This inventory IS enrolled in this program
				enrolled = true;
				loadGroupId = activeEnrollment.getLmGroupId();
				relay = activeEnrollment.getRelay();
				break;
			}
		}
		boolean inService = false;
		if (enrolled) {
	        inService =
	            enrollmentDao.isInService(inventoryId);
		}

        int numRelays = hardware.getNumRelays();
        return new DisplayableEnrollmentInventory(inventoryId,
                                                  hardware.getDisplayName(),
                                                  enrolled, inService, loadGroupId,
                                                  relay, numRelays);
		
    }
    
    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
}
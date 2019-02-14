package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentInService;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DisplayableEnrollmentDaoImpl extends AbstractDisplayableDao implements DisplayableEnrollmentDao {
    
    @Autowired private EnrollmentDao enrollmentDao;
	@Autowired private PaoDefinitionDao paoDefinitionDao;
	@Autowired private LoadGroupDao loadGroupDao; 
	
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
            if (programList == null || programList.isEmpty()) {
                continue;
            }
            
            DisplayableEnrollment enrollment = enrollmentMap.get(applianceCategory);
            if (enrollment == null) {
                enrollment = new DisplayableEnrollment();
                
                enrollment.setApplianceCategory(applianceCategory);

                TreeSet<DisplayableEnrollmentProgram> enrollmentPrograms = new TreeSet<>(DisplayableEnrollment.enrollmentProgramComparator);
                enrollment.setEnrollmentPrograms(enrollmentPrograms);
                
                enrollmentMap.put(applianceCategory, enrollment);
            }
            
            for (Program program : programList) {
            	DisplayableEnrollmentProgram displayableEnrollmentProgram = 
            		createDisplayableEnrollmentProgram(applianceCategory, program, hardwareList, activeEnrollments);
            	enrollment.getEnrollmentPrograms().add(displayableEnrollmentProgram);
            }
            
        }
        
        List<DisplayableEnrollment> resultList = new ArrayList<>(enrollmentMap.values());
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
                
                /* Exclude non enrollable hardware */
                HardwareType type = hardwareSummary.getHardwareType();
                if (!type.isEnrollable()) {
                    return false;
                }

                /* Exclude devices from the list if there are no load groups of the right type for
                 * the given load program and device.  All groups are looped over in the for loop, at
                 * least one load group must match the device type.
                 * If the device is an RFN device, it will be visible if there is at least one RFN 
                 * ExpressCom load group.
                 * If the device is non-RFN, it will be visible if there is at least one normal (PLC) 
                 * ExpressCom load group.
                 * If a load program has both RFN and PLC ExpressCom load groups, then both RFN and
                 * non-RFN devices will be visible in the list. */
                List<LoadGroup> loadGroups = loadGroupDao.getByStarsProgramId(program.getProgramId());
                if (program.getPaoType() == PaoType.LM_DIRECT_PROGRAM) {
                    boolean isValid = false;
                    for (LoadGroup group : loadGroups) {
                        if ((group.getPaoIdentifier().getPaoType() == PaoType.LM_GROUP_RFN_EXPRESSCOMM
                                && type.isRf()) 
                         || (group.getPaoIdentifier().getPaoType() != PaoType.LM_GROUP_RFN_EXPRESSCOMM
                                && !type.isRf())) {
                            isValid = true;
                            break;
                        }
                    }
                    if (!isValid) {
                        return false;
                    }
                }
                
                /* Exclude non valid program enrollment (SEP hardware cannot be enrolled in DIRECT programs) */
                HardwareConfigType hardwareConfigType = type.getHardwareConfigType();
                PaoTag inventoryEnrollmentTag = hardwareConfigType.getEnrollmentTag();
                PaoType programPaoType = program.getPaoType();
                if (programPaoType == PaoType.SYSTEM && hardwareConfigType.isSupportsVirtualEnrollment()) {
                    /* These are virtual programs, let devices with non-SEP enrollment support through */
                    return true;
                } else {
                    return paoDefinitionDao.isTagSupported(programPaoType, inventoryEnrollmentTag);
                }
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
    		List<ProgramEnrollment> activeEnrollments) {
        
    	int inventoryId = hardware.getInventoryId();
		
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
		
		EnrollmentInService inService = EnrollmentInService.OUTOFSERVICE;
        if (enrolled) {
            if (hardware.getHardwareType().isZigbee()) {
                inService = EnrollmentInService.NA;
            } else if (hardware.getHardwareType().isEcobee() || hardware.getHardwareType().isHoneywell() ||
                    hardware.getHardwareType().isItron()) {
                inService = EnrollmentInService.INSERVICE;
            } else {
	            boolean isInService = enrollmentDao.isInService(inventoryId);
	            inService = EnrollmentInService.determineInService(isInService);
	        }
		}

        int numRelays = hardware.getNumRelays();
        return new DisplayableEnrollmentInventory(inventoryId,
                                                  hardware.getDisplayName(),
                                                  enrolled, inService, loadGroupId,
                                                  relay, numRelays);
		
    }
}
package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LiteHardware;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.user.YukonUserContext;

@Repository
public class DisplayableEnrollmentDaoImpl extends AbstractDisplayableDao implements DisplayableEnrollmentDao {
    
	private EnrollmentDao enrollmentDao;
	
    @Override
    public List<DisplayableEnrollment> getDisplayableEnrollments(final CustomerAccount customerAccount,
            final YukonUserContext yukonUserContext) {

        final int customerAccountId = customerAccount.getAccountId();
        
        // Get All ApplianceCategory's that the CustomerAccount could enroll in
        final List<ApplianceCategory> applianceCategories = 
        	applianceCategoryDao.getApplianceCategories(customerAccount);
        
        // Get all Programs that belong to the ApplianceCategory's.
        final Map<ApplianceCategory, List<Program>> typeMap = 
        	programDao.getByApplianceCategories(applianceCategories);

        // Get all Hardware that belongs to this CustomerAccount
        final List<LiteHardware> hardwareList = 
        	inventoryBaseDao.getAllLiteHardwareForAccount(customerAccountId);

        // Get all current program enrollments for account
        List<ProgramEnrollment> activeEnrollments = 
        	enrollmentDao.getActiveEnrollmentsByAccountId(customerAccountId);
        
        final Map<ApplianceCategory, DisplayableEnrollment> enrollmentMap =
            new HashMap<ApplianceCategory, DisplayableEnrollment>(); 
        
        for (final ApplianceCategory applianceCategory : typeMap.keySet()) {
            List<Program> programList = typeMap.get(applianceCategory);
            
            // Don't display ApplianceCategory's that have no programs for the user to enroll in.
            if (programList == null || programList.isEmpty()) continue;
            
            DisplayableEnrollment enrollment = enrollmentMap.get(applianceCategory);
            if (enrollment == null) {
                enrollment = new DisplayableEnrollment();
                
                enrollment.setApplianceCategory(applianceCategory);
                
                ApplianceTypeEnum applianceTypeEnum = applianceCategory.getApplianceTypeEnum();
                enrollment.setApplianceType(applianceTypeEnum);
                
                String logoPath = applianceCategory.getLogoPath();
                enrollment.setApplianceLogo(logoPath);
                
                enrollment.setEnrollmentPrograms(
                    new TreeSet<DisplayableEnrollmentProgram>(DisplayableEnrollment.enrollmentProgramComparator));
                enrollmentMap.put(applianceCategory, enrollment);
            }
            
            for (Program program : programList) {
            	DisplayableEnrollmentProgram displayableEnrollmentProgram = 
            		this.createDisplayableEnrollmentProgram(program, hardwareList, activeEnrollments);
            	enrollment.getEnrollmentPrograms().add(displayableEnrollmentProgram);
            }
            
        }
        
        List<DisplayableEnrollment> resultList = new ArrayList<DisplayableEnrollment>(enrollmentMap.values());
        Collections.sort(resultList, DisplayableEnrollment.enrollmentComparator);
        return resultList;
    }
    
    private DisplayableEnrollmentProgram createDisplayableEnrollmentProgram(
    		Program program, 
    		List<LiteHardware> hardwareList, 
    		List<ProgramEnrollment> activeEnrollments) 
    {
    
    	int programId = program.getProgramId();
    	List<DisplayableEnrollmentInventory> enrollmentInventoryList = 
    		new ArrayList<DisplayableEnrollmentInventory>();
    	
    	for(LiteHardware hardware : hardwareList) {
    		DisplayableEnrollmentInventory displayableEnrollmentInventory = 
    			this.createDisplayableEnrollmentInventory(hardware, programId, activeEnrollments);
    		enrollmentInventoryList.add(displayableEnrollmentInventory);
    	}
    	
    	return new DisplayableEnrollmentProgram(program, enrollmentInventoryList);
    	
    }

    private DisplayableEnrollmentInventory createDisplayableEnrollmentInventory(
    		LiteHardware hardware, 
    		int programId, 
    		List<ProgramEnrollment> activeEnrollments) 
    {
        
    	Integer inventoryId = hardware.getInventoryId();
		
		boolean isEnrolled = false;
		for(ProgramEnrollment activeEnrollment : activeEnrollments){
			int enrollmentProgramId = activeEnrollment.getProgramId();
			int enrollmentInventoryId = activeEnrollment.getInventoryId();
			
			if(enrollmentProgramId == programId && enrollmentInventoryId == inventoryId) {
				// This inventory IS enrolled in this program
				isEnrolled = true;
				break;
			}
		}
		return new DisplayableEnrollmentInventory(inventoryId, hardware.getDisplayName(), isEnrolled);
		
    }
    
    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
    
}

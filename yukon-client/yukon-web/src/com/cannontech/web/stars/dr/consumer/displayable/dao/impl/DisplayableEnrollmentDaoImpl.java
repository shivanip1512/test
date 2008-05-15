package com.cannontech.web.stars.dr.consumer.displayable.dao.impl;

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
import com.cannontech.stars.dr.appliance.model.ApplianceType;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.AbstractDisplayableDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;

@Repository
public class DisplayableEnrollmentDaoImpl extends AbstractDisplayableDao implements DisplayableEnrollmentDao {
    private InventoryBaseDao inventoryBaseDao;
    
    @Override
    public List<DisplayableEnrollment> getDisplayableEnrollments(final CustomerAccount customerAccount,
            final YukonUserContext yukonUserContext) {

        final int customerAccountId = customerAccount.getAccountId();
        
        // Get all Inventory that belongs to this CustomerAccount
        final List<InventoryBase> inventoryList = inventoryBaseDao.getDRInventoryByAccountId(customerAccountId);
        
        // Get All ApplianceCategory's that the CustomerAccount could enroll in
        final List<ApplianceCategory> applianceCategories = applianceCategoryDao.getApplianceCategories(customerAccount);
        
        // Get all Programs that belong to the ApplianceCategory's.
        final Map<ApplianceCategory, List<Program>> typeMap = programDao.getByApplianceCategories(applianceCategories);
        
        final Map<ApplianceCategory, DisplayableEnrollment> enrollmentMap =
            new HashMap<ApplianceCategory, DisplayableEnrollment>(); 
        
        for (final ApplianceCategory applianceCategory : typeMap.keySet()) {
            List<Program> programList = typeMap.get(applianceCategory);
            
            // Don't display ApplianceCategory's that have no programs for the user to enroll in.
            if (programList == null || programList.isEmpty()) continue;
            
            DisplayableEnrollment enrollment = enrollmentMap.get(applianceCategory);
            if (enrollment == null) {
                enrollment = new DisplayableEnrollment();
                
                int applianceCategoryId = applianceCategory.getApplianceCategoryId();
                enrollment.setApplianceCategoryId(applianceCategoryId);
                
                ApplianceType applianceType = applianceCategory.getApplianceType();
                enrollment.setApplianceType(applianceType);
                
                String logoPath = applianceCategory.getLogoPath();
                enrollment.setApplianceLogo(logoPath);
                
                enrollment.setEnrollmentPrograms(
                    new TreeSet<DisplayableEnrollmentProgram>(DisplayableEnrollment.enrollmentProgramComparator));
                enrollmentMap.put(applianceCategory, enrollment);
            }
            
            for (final Program program : programList) {
                List<DisplayableEnrollmentInventory> enrollmentInventoryList = 
                    createDisplayableEnrollmentInventory(customerAccountId, program.getProgramId(), inventoryList);
                enrollment.getEnrollmentPrograms().add(
                    new DisplayableEnrollmentProgram(program, enrollmentInventoryList));
            }
        }
        
        List<DisplayableEnrollment> resultList = new ArrayList<DisplayableEnrollment>(enrollmentMap.values());
        Collections.sort(resultList, DisplayableEnrollment.enrollmentComparator);
        return resultList;
    }

    private List<DisplayableEnrollmentInventory> createDisplayableEnrollmentInventory(int customerAccountId, 
        int programId, List<InventoryBase> inventoryList) {
        
        final List<DisplayableEnrollmentInventory> resultList = new ArrayList<DisplayableEnrollmentInventory>();
        
        for (final InventoryBase inventory : inventoryList) {
            int inventoryId = inventory.getInventoryId();
            String displayName = inventoryBaseDao.getDisplayName(inventory);
            boolean isEnrolled = programEnrollmentService.isProgramEnrolled(customerAccountId,
                                                                            inventoryId,
                                                                            programId);
            
            resultList.add(new DisplayableEnrollmentInventory(inventoryId, displayName, isEnrolled));
        }
        
        return resultList;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
}

package com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.service.impl;

import org.junit.Assert;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.model.ApplianceCategory;
import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.service.ApplianceCategorySeleniumService;

public class ApplianceCategorySeleniumServiceImpl implements ApplianceCategorySeleniumService {

    private final static String newApplianceCategoryFormAction = "create";
    private final static String updateApplianceCategoryFormAction = "save";
    
    @Override
    public void createApplianceCategory(String energyCompanyName, ApplianceCategory applianceCategory) {
        CommonSolvent common = new CommonSolvent();
        
        // Navigate to the appliance category page
        common.clickLinkByName("System Administration");
        common.clickLinkByName("Energy Company");
        Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
        common.clickLinkByName(energyCompanyName);
        common.clickLinkByName("Appliance Categories");   

        // Create a new appliance category
        common.clickLinkButton(newApplianceCategoryFormAction, null);
        
        // Enter new appliance category information
        common.enterInputText(updateApplianceCategoryFormAction, "name", applianceCategory.getName());
        common.enterInputText(updateApplianceCategoryFormAction, "description", applianceCategory.getDescription());

        // Finish creating the appliance category
        common.clickFormButton(updateApplianceCategoryFormAction, "save");
        
        Assert.assertEquals(true, common.isTextPresent(applianceCategory.getName()));
        Assert.assertEquals(true, common.isTextPresent(applianceCategory.getDescription()));
        
    }
    
}
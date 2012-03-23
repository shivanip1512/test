package com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.service;

import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.model.ApplianceCategory;

public interface ApplianceCategorySeleniumService {

    /**
     * This method goes through and creates a appliance category through the selenium
     * process with the user you are currently logged in with.
     */
    public void createApplianceCategory(String energyCompanyName, ApplianceCategory applianceCategory);

}
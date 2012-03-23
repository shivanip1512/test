package com.cannontech.selenium.solvents.stars.energyCompany.service;

import java.util.Map;

import com.cannontech.selenium.solvents.stars.energyCompany.model.CreateEnergyCompany;

public interface EnergyCompanySeleniumService {
    
    /**
     * This method goes through and creates an energy company.
     */
    public void createEnergyCompany(CreateEnergyCompany createEnergyCompany);

    /**
     * This method goes through and deletes an energy company.
     */
    public void deleteEnergyCompany(String energyCompanyName);

    /**
     * This method takes care of setting up the roles for a given energy company.
     */
    void setupEnergyCompanyRoles(String yukonRoleName, Map<String, String> inputNameToRPValueMap);
}
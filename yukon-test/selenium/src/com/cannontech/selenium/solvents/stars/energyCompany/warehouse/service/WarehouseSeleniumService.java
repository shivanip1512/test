package com.cannontech.selenium.solvents.stars.energyCompany.warehouse.service;

import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.model.Warehouse;

public interface WarehouseSeleniumService {
    
    /**
     * This method goes through and creates a warehouse through the selenium
     * process with the user you are currently logged in with.
     */
    public void createWarehouse(String energyCompanyName, Warehouse warehouse);

}
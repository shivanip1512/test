package com.cannontech.selenium.solvents.stars.energyCompany.warehouse.service.impl;

import org.junit.Assert;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.model.Warehouse;
import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.service.WarehouseSeleniumService;

public class WarehouseSeleniumServiceImpl implements WarehouseSeleniumService {

    private final static String newWarehouseFormAction = "warehouse/new";
    private final static String updateWarehouseFormAction = "warehouse/update";
    
    @Override
    public void createWarehouse(String energyCompanyName, Warehouse warehouse) {
        CommonSolvent common = new CommonSolvent();
        
        // Navigate to the warehouse page
        common.clickLinkByName("System Administration");
        common.clickLinkByName("Energy Company");
        Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
        common.clickLinkByName(energyCompanyName);
        common.clickLinkByName("Warehouses");   

        // Create a new warehouse
        common.clickLinkButton(newWarehouseFormAction, "createWarehouse");
        
        // Enter new warehouse information
        common.enterInputText(updateWarehouseFormAction, "warehouse.warehouseName", warehouse.getName());
        common.enterInputText(updateWarehouseFormAction, "address.locationAddress1", warehouse.getStreetAddress1());
        common.enterInputText(updateWarehouseFormAction, "address.locationAddress2", warehouse.getStreetAddress2());
        common.enterInputText(updateWarehouseFormAction, "address.cityName", warehouse.getCity());
        common.enterInputText(updateWarehouseFormAction, "address.stateCode", warehouse.getState());
        common.enterInputText(updateWarehouseFormAction, "address.zipCode", warehouse.getZip());

        // Finish creating the warehouse
        common.clickFormButton(updateWarehouseFormAction, "create");
        
        Assert.assertEquals(true, common.isTextPresent("Successfully saved "+warehouse.getName()));
    }
    
}
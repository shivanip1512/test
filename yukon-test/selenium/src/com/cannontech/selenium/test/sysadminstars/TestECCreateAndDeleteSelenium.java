package com.cannontech.selenium.test.sysadminstars;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.userLogin.model.Login;
import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.model.ApplianceCategory;
import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.service.ApplianceCategorySeleniumService;
import com.cannontech.selenium.solvents.stars.energyCompany.applianceCategory.service.impl.ApplianceCategorySeleniumServiceImpl;
import com.cannontech.selenium.solvents.stars.energyCompany.model.CreateEnergyCompany;
import com.cannontech.selenium.solvents.stars.energyCompany.service.EnergyCompanySeleniumService;
import com.cannontech.selenium.solvents.stars.energyCompany.service.impl.EnergyCompanySeleniumServiceImpl;
import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.model.Warehouse;
import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.service.WarehouseSeleniumService;
import com.cannontech.selenium.solvents.stars.energyCompany.warehouse.service.impl.WarehouseSeleniumServiceImpl;
import com.google.common.collect.Maps;
/**
 * This method created, modifies a specific part of an energy company, and then deletes the energy company.
 * @author Matthew Kruse  
 */
public class TestECCreateAndDeleteSelenium extends SolventSeleniumTestCase{

    // Services  TODO: Setup Spring to handle this by injection.
    private EnergyCompanySeleniumService ecSeleniumService = new EnergyCompanySeleniumServiceImpl();
    private WarehouseSeleniumService warehouseSeleniumService = new WarehouseSeleniumServiceImpl();
    private ApplianceCategorySeleniumService applianceCategorySeleniumService = new ApplianceCategorySeleniumServiceImpl();

    // Test objects
    private final CreateEnergyCompany newEnergyCompany1;
    {
        newEnergyCompany1 = new CreateEnergyCompany();
        newEnergyCompany1.setCompanyName("createAndDeleteEC");
        newEnergyCompany1.setEmail("createAndDeleteEC@eas.com");
        newEnergyCompany1.setDefaultRoute("(none)");
        
        newEnergyCompany1.setPrimaryOperatorGroup("STARS Operators Grp");
        
        Login ecLogin = new Login("createAndDeleteECAdmin", "createAndDeleteECAdmin");
        newEnergyCompany1.setChangeLogin(ecLogin);
    }

    private final Map<String, String> newEnergyCompany1AdminstratorRole;
    {
        newEnergyCompany1AdminstratorRole = Maps.newHashMap();
        newEnergyCompany1AdminstratorRole.put("values[ADMIN_MULTI_WAREHOUSE]", "TRUE");
    }
    
    private final Warehouse warehouse1;
    {
        warehouse1 = new Warehouse();
        warehouse1.setName("EAS Warehouse");
        warehouse1.setStreetAddress1("505 State Hwy 55");
        warehouse1.setStreetAddress2("");
        warehouse1.setCity("Plymouth");
        warehouse1.setState("MN");
        warehouse1.setZip("55414");
        warehouse1.setNotes("Cool notes");
    }
    
    private final ApplianceCategory applianceCategory1;
    {
        applianceCategory1 = new ApplianceCategory();
        applianceCategory1.setName("Test Appliance Category");
        applianceCategory1.setDescription("This appliance category is meant for testing");
    }
    
    @Before
    public void init(){
        start();
    }

    /**
     * This test create a new energy company, creates a new warehouse on that energy company,
     * and then deletes the warehouse.
     */
    @Test
    public void testDeletingAnECWithAWarehouse(){
        LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();

        createTemporaryEngeryCompany(loginLogoutSolvent);

        try {
            loginLogoutSolvent.cannonLogin(newEnergyCompany1.getChangeLogin());
            warehouseSeleniumService.createWarehouse(newEnergyCompany1.getCompanyName(), warehouse1);

        } finally {
            loginLogoutSolvent.yukonLogout();

            deleteTemporaryEnergyCompany(loginLogoutSolvent);
        }
        
        (new CommonSolvent()).end();
    }

    /**
     * This test create a new energy company, creates a new appliance on that energy company,
     * and then deletes the warehouse.
     */
    @Test
    public void testDeletingAnECWithAnAppliance(){
        LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
        createTemporaryEngeryCompany(loginLogoutSolvent);

        try {
            loginLogoutSolvent.cannonLogin(newEnergyCompany1.getChangeLogin());
            applianceCategorySeleniumService.createApplianceCategory(newEnergyCompany1.getCompanyName(), applianceCategory1);
        
        } finally {
            loginLogoutSolvent.yukonLogout();

            deleteTemporaryEnergyCompany(loginLogoutSolvent);
        }
        
        (new CommonSolvent()).end();
    }
    
    /**
     * This method creates a temporary energy company.  This method pairs with deleteTemporaryEnergyCompany().
     */
    private void createTemporaryEngeryCompany(LoginLogoutSolvent loginLogoutSolvent) {
        loginLogoutSolvent.cannonLogin(LoginLogoutSolvent.DEFAULT_CTI_LOGIN);
        ecSeleniumService.createEnergyCompany(newEnergyCompany1);
        ecSeleniumService.setupEnergyCompanyRoles("Administrator", newEnergyCompany1AdminstratorRole);
        loginLogoutSolvent.yukonLogout();
    }

    /**
     * This method deletes a temporary energy company.  This method pairs with createTemporaryEngeryCompany().
     */
    private void deleteTemporaryEnergyCompany(LoginLogoutSolvent loginLogoutSolvent) {
        loginLogoutSolvent.cannonLogin(LoginLogoutSolvent.DEFAULT_CTI_LOGIN);
        ecSeleniumService.deleteEnergyCompany(newEnergyCompany1.getCompanyName());
        loginLogoutSolvent.yukonLogout();
    }
}
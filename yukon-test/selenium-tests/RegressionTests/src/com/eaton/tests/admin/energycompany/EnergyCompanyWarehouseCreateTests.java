package com.eaton.tests.admin.energycompany;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseCreatePage;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseListPage;

public class EnergyCompanyWarehouseCreateTests extends SeleniumTestSetup {

    private EnergyCompanyWarehouseCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.ENERGY_COMPANY_WAREHOUSE_CREATE + "64");

        createPage = new EnergyCompanyWarehouseCreatePage(driverExt, Urls.Admin.ENERGY_COMPANY_WAREHOUSE_CREATE + "64");
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "QA_Test";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void createEnergyCompanyWarehouseAllFieldsSuccess() {
        final String START_EXPECTED_MSG = "Successfully saved ";
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT EC Warehouse " + timeStamp;
        createPage.getWarehouseName().setInputValue(name);
        createPage.getAddress().setInputValue("505 State Hwy 55");
        createPage.getAddress2().setInputValue("Suite 301");
        createPage.getCity().setInputValue("Plymouth");
        createPage.getState().setInputValue("MN");
        createPage.getZip().setInputValue("55301");
        createPage.getNotes().setInputValue("Created via automated tests");
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Warehouses: QA_Test", Optional.empty());
        
        EnergyCompanyWarehouseListPage listPage = new EnergyCompanyWarehouseListPage(this.driverExt, Urls.Admin.ENERGY_COMPANY_LIST + "64");     
        
        String userMsg = listPage.getUserMessage();
        
        Assert.assertEquals(userMsg, START_EXPECTED_MSG + name + ".", "Expected User Msg: '" + START_EXPECTED_MSG + name + "' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

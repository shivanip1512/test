package com.eaton.tests.admin.energycompany;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyDeleteConfirmationPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoEditPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyListPage;

public class EnergyCompanyGeneralInfoEditTests extends SeleniumTestSetup {
    
    private DriverExtensions driverExt;
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit General Info: QA_Test";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "64");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(this.driverExt, 64);
        
        page.getEditBtn().click();
        
        waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_EDIT, Optional.empty());
        
        EnergyCompanyGeneralInfoEditPage editPage = new EnergyCompanyGeneralInfoEditPage(this.driverExt, 64);
        
        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void deleteEnergyComapnySuccess() {
        final String START_EXPECTED_MSG = "The energy company ";
        final String END_EXPECTED_MSG = " was deleted successfully.";
        final String NAME = "AT Delete EC";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "822");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(this.driverExt, 822);
        
        page.getEditBtn().click();
        
        waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_EDIT, Optional.empty());
        
        EnergyCompanyGeneralInfoEditPage editPage = new EnergyCompanyGeneralInfoEditPage(this.driverExt, 822);
        
        editPage.getDeleteBtn().click();
        
        waitForPageToLoad("deleteEnergyCompanyConfirm: " + NAME, Optional.empty()); 
        
        EnergyCompanyDeleteConfirmationPage  deletePage = new EnergyCompanyDeleteConfirmationPage(driverExt, 822);
        
        deletePage.getDeleteBtn().click();
        
        waitForPageToLoad("Energy Companies", Optional.empty());
        
        EnergyCompanyListPage listPage = new EnergyCompanyListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();
        
        Assert.assertEquals(userMsg, START_EXPECTED_MSG + NAME + END_EXPECTED_MSG, "Expected User Msg: '" + START_EXPECTED_MSG + NAME + END_EXPECTED_MSG + "' but found: " + userMsg);
    }   

}

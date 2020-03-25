package com.eaton.tests.admin.energycompany;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.SelectUserGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyCreatePage;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoPage;

public class EnergyCompanyCreateTests extends SeleniumTestSetup {

    private EnergyCompanyCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.CREATE_ENERGY_COMPANY);

        createPage = new EnergyCompanyCreatePage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Energy Company";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM07_01_CreateAndDeleteEC()"})
    public void createEnergyCompanyRequiredFieldsOnlySuccess() {
        final String START_EXPECTED_MSG = "Energy Company ";
        final String END_EXPECTED_MSG = " Created Successfully";
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT Energy Company " + timeStamp;
        createPage.getCompanyName().setInputValue(name);
        createPage.getEmail().setInputValue("atenergyco@eas.com");
        createPage.getUserName().setInputValue("atenergyco");
        createPage.getPassword().setInputValue("atec1!");
        createPage.getConfirmPassword().setInputValue("atec1!");
        
        SelectUserGroupModal userGroupModal = this.createPage.showAndWaitUserGroupModal();
        userGroupModal.selectUserGroup("AT User Group for Create EC");
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad(name, Optional.empty());
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(driverExt);
        
        String userMsg = page.getUserMessage();
        
        Assert.assertEquals(userMsg, START_EXPECTED_MSG + name + END_EXPECTED_MSG, "Expected User Msg: '" + START_EXPECTED_MSG + name + END_EXPECTED_MSG + "' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

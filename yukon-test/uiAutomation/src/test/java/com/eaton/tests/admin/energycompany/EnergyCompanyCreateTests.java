package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
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

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.CREATE_ENERGY_COMPANY);

        createPage = new EnergyCompanyCreatePage(driverExt);
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyCreate_PageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Energy Company";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyCreate_requiredFieldsOnlySuccess() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String companyName = "AT Energy Company " + timeStamp;
        
        final String EXPECTED_MSG = "Energy Company " + companyName + " Created Successfully";
                                
        createPage.getCompanyName().setInputValue(companyName);
        createPage.getEmail().setInputValue("atenergyco@eas.com");
        createPage.getUserName().setInputValue("atenergyco");
        createPage.getPassword().setInputValue("atec1!");
        createPage.getConfirmPassword().setInputValue("atec1!");
        
        SelectUserGroupModal userGroupModal = this.createPage.showAndWaitUserGroupModal();
        userGroupModal.selectUserGroup("AT User Group for Create EC");
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad(companyName, Optional.empty());
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(driverExt);
        
        String userMsg = page.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }    
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}

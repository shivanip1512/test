package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyCreatePage;

public class EnergyCompanyCreateTests extends SeleniumTestSetup {

    private EnergyCompanyCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Admin.CREATE_ENERGY_COMPANY);
        createPage = new EnergyCompanyCreatePage(driverExt);
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyCreate_PageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Energy Company";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
//    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
//    @CustomTestNgAnnotations(refreshPage = true, urlToRefresh = Urls.Admin.CREATE_ENERGY_COMPANY)
//    public void energyCompanyCreate_requiredFieldsOnlySuccess() {
//        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
//        String companyName = "AT Energy Company " + timeStamp;
//        
//        final String EXPECTED_MSG = "Energy Company " + companyName + " Created Successfully";
//                                
//        createPage.getCompanyName().setInputValue(companyName);
//        createPage.getEmail().setInputValue("atenergyco@eas.com");
//        createPage.getUserName().setInputValue("atenergyco");
//        createPage.getPassword().setInputValue("atec1!");
//        createPage.getConfirmPassword().setInputValue("atec1!");
//        
//        SelectUserGroupModal userGroupModal = createPage.showAndWaitUserGroupModal();
//        userGroupModal.selectUserGroup("AT User Group for Create EC");
//        
//        createPage.getSaveBtn().click();
//        
//        waitForPageToLoad(companyName, Optional.empty());
//        
//        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(driverExt);
//        
//        String userMsg = page.getUserMessage();
//        
//        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
//    }    
    
//    @AfterMethod(alwaysRun=true)
//    public void afterTest() {        
//        refreshPage(createPage);
//    }
}
package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        createPage = new LoadGroupCreatePage(driverExt);        
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreate_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }  
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreate_NameRequiredValidation() {                
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}

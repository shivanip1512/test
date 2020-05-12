package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.ScenarioCreatePage;

public class ScenarioCreateTests extends SeleniumTestSetup {
    
    private ScenarioCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();  
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.SCENARIO_CREATE);

        createPage = new ScenarioCreatePage(driverExt);                
    }  
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_12_CreateScenario"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Scenario";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_12_CreateScenario"})
    public void createScenarioRequiredFieldsOnlySuccess() {        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());                 
        String name = "AT Scenario " + timeStamp;         
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);  
        
        createPage.getLoadProgramAssignments().addSingleAvailable("AT Ldprm for Create Scenario");
        
        createPage.getSave().click();
        
        waitForPageToLoad("Scenario: " + name, Optional.empty());
        
        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }    
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}

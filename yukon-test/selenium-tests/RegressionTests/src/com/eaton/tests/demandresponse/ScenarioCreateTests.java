package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;
import com.eaton.pages.demandresponse.ScenarioCreatePage;

public class ScenarioCreateTests extends SeleniumTestSetup {
    
    private ScenarioCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();  
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.SCENARIO_CREATE);

        createPage = new ScenarioCreatePage(driverExt, Urls.DemandResponse.SCENARIO_CREATE);                
    }  
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_12_CreateScenario"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Scenario";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_12_CreateScenario"})
    public void createScenarioRequiredFieldsOnlySuccess() {        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
                 
        String name = "AT Scenario " + timeStamp;         
        createPage.getName().setInputValue(name);  
        
        createPage.getLoadProgramAssignments().addSingleAvailable("AT Ldprm for Create Scenario");
        
        createPage.getSave().click();
        
        waitForPageToLoad("Scenario: " + name, Optional.empty());
        
        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt, Urls.DemandResponse.SCENARIO_DETAILS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg: '" + name + " saved successfully.' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

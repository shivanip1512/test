package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ScenarioDetailPage;
import com.eaton.pages.demandresponse.ScenarioEditPage;

public class ScenarioEditTests extends SeleniumTestSetup {
    
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();        
    }  
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_13_EditScenario"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Scenario: AT Scenario";
        
        navigate(Urls.DemandResponse.SCENARIO_EDIT + "663" + Urls.EDIT);
        
        ScenarioEditPage editPage = new ScenarioEditPage(driverExt, Urls.DemandResponse.SCENARIO_EDIT + "663" + Urls.EDIT);

        String actualPageTitle = editPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_13_EditScenario"})
    public void editScenarioNameOnlySuccess() {
        navigate(Urls.DemandResponse.SCENARIO_EDIT + "590" + Urls.EDIT);
        
        ScenarioEditPage editPage = new ScenarioEditPage(driverExt, Urls.DemandResponse.SCENARIO_EDIT + "590" + Urls.EDIT);
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
                 
        String name = "AT Edited Scenario " + timeStamp;         
        editPage.getName().setInputValue(name);  
        
        editPage.getSave().click();
        
        waitForPageToLoad("Scenario: " + name, Optional.empty());
        
        ScenarioDetailPage detailsPage = new ScenarioDetailPage(driverExt, Urls.DemandResponse.CONTROL_AREA_DETAILS);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg: '" + name + " saved successfully.' but found: " + userMsg);
    }        
}

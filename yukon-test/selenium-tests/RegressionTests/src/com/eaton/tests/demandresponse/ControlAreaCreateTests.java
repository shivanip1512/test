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
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaCreatePage;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;

public class ControlAreaCreateTests extends SeleniumTestSetup {
    
    private ControlAreaCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();  
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.CONTROL_AREA_CREATE);

        createPage = new ControlAreaCreatePage(driverExt);                
    }  
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_09_CreateControlArea"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Control Area";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_09_CreateControlArea"})
    public void createControlAreaSuccess() {
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
                 
        String name = "AT Control Area " + timeStamp;         
        createPage.getName().setInputValue(name);  
        
        createPage.getProgramAssignments().addSingleAvailable("AT Direct Program for Create Control Area");
        
        createPage.getSave().click();
        
        waitForPageToLoad("Control Area: " + name, Optional.empty());
        
        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg: '" + name + " saved successfully.' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

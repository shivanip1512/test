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
import com.eaton.pages.demandresponse.ControlAreaCreatePage;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;

public class ControlAreaCreateTests extends SeleniumTestSetup {
    
    private ControlAreaCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
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
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_09_CreateControlArea"})
    public void createControlAreaSuccess() {
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
                 
        String name = "AT Control Area " + timeStamp;     
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);  
        
        createPage.getProgramAssignments().addSingleAvailable("AT Direct Program for Create Control Area");
        
        createPage.getSave().click();
        
        waitForPageToLoad("Control Area: " + name, Optional.empty());
        
        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }    
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}

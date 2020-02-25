package com.eaton.tests.capcontrol;

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
import com.eaton.pages.capcontrol.SubstationCreatePage;
import com.eaton.pages.capcontrol.SubstationDetailPage;

public class SubstationCreateTests extends SeleniumTestSetup {

    private SubstationCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_CREATE);

        createPage = new SubstationCreatePage(driverExt, Urls.CapControl.SUBSTATION_CREATE);
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Substation";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void createSubstationRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Substation was saved successfully.";
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Substation " + timeStamp; 
        createPage.getName().setInputValue(name);
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Substation: " + name, Optional.empty());
        
        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt, Urls.CapControl.SUBSTATION_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

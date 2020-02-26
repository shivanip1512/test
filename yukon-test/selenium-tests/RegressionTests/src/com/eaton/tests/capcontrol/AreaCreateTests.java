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
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.AreaCreatePage;
import com.eaton.pages.capcontrol.AreaDetailPage;

public class AreaCreateTests extends SeleniumTestSetup {

    private AreaCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.CapControl.AREA_CREATE);

        this.createPage = new AreaCreatePage(driverExt, Urls.CapControl.AREA_CREATE);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Area";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void createAreaRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Area was saved successfully.";
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT Area " + timeStamp;
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Area: " + name, Optional.empty());       
        
        AreaDetailPage detailsPage = new AreaDetailPage(driverExt, Urls.CapControl.AREA_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

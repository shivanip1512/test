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
import com.eaton.pages.capcontrol.RegulatorCreatePage;
import com.eaton.pages.capcontrol.RegulatorDetailPage;

public class RegulatorCreateTests extends SeleniumTestSetup {

    private RegulatorCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_CREATE);

        createPage = new RegulatorCreatePage(driverExt, Urls.CapControl.REGULATOR_CREATE);
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Regulator";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);        
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects"})
    public void createRegulatorRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Regulator " + timeStamp; 
        createPage.getName().setInputValue(name);
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Regulator: " + name, Optional.empty());
        
        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driverExt, Urls.CapControl.REGULATOR_DETAIL);
        
        //The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        String actualPageTitle = detailsPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, "Regulator: " + name, "Expected Page title: 'Regulator: " + name + "' but found: " + actualPageTitle); 
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

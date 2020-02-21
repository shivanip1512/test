package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusCreatePage;
import com.eaton.pages.capcontrol.SubstationBusDetailPage;

public class SubstationBusCreateTests extends SeleniumTestSetup {

    private SubstationBusCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_BUS_CREATE);

        createPage = new SubstationBusCreatePage(driverExt, Urls.CapControl.SUBSTATION_BUS_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Bus";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createSubstationBusRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "Bus was saved successfully.";
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Bus " + timeStamp;
        createPage.getName().setInputValue(name);
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name);
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driverExt, Urls.CapControl.SUBSTATION_BUS_DETAIL);
        
        String actualUserMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(actualUserMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + actualUserMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

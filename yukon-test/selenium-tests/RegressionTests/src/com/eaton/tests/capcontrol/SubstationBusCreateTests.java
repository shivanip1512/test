package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusCreatePage;
import com.eaton.pages.capcontrol.SubstationBusDetailPage;

public class SubstationBusCreateTests extends SeleniumTestSetup {

    private SubstationBusCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_BUS_CREATE);

        this.createPage = new SubstationBusCreatePage(driver, Urls.CapControl.SUBSTATION_BUS_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Bus");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createSubstationBusRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Bus " + timeStamp;
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Bus: " + name);
        
        SubstationBusDetailPage detailsPage = new SubstationBusDetailPage(driver, Urls.CapControl.SUBSTATION_BUS_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Bus was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

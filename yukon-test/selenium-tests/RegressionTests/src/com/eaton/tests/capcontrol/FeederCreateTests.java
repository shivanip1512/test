package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.FeederCreatePage;
import com.eaton.pages.capcontrol.FeederDetailPage;

public class FeederCreateTests extends SeleniumTestSetup {

    private FeederCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.FEEDER_CREATE);

        this.createPage = new FeederCreatePage(driver, Urls.CapControl.FEEDER_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Feeder");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createFeederRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Feeder " + timeStamp;
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Feeder: " + name);
        
        FeederDetailPage detailsPage = new FeederDetailPage(driver, Urls.CapControl.FEEDER_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, "Feeder was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

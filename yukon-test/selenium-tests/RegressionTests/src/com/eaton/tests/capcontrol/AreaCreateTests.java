package com.eaton.tests.capcontrol;

import java.time.Instant;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.AreaCreatePage;
import com.eaton.pages.capcontrol.AreaDetailPage;

public class AreaCreateTests extends SeleniumTestSetup {

    private AreaCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.AREA_CREATE);

        this.createPage = new AreaCreatePage(driver, Urls.CapControl.AREA_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Area");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createAreaRequiredFieldsOnlySuccess() {
        
        Instant thisInstant = Instant.now();
        
        String name = "AT Area " + thisInstant.toString();
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Area: " + name);       
        
        AreaDetailPage detailsPage = new AreaDetailPage(driver, Urls.CapControl.AREA_DETAIL);
        
        String userMsg = detailsPage.getUserMessageSuccess();
        
        Assert.assertEquals(userMsg, "Area was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import java.time.Instant;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationCreatePage;
import com.eaton.pages.capcontrol.SubstationDetailPage;

public class SubstationCreateTests extends SeleniumTestSetup {

    private SubstationCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_CREATE);

        this.createPage = new SubstationCreatePage(driver, Urls.CapControl.SUBSTATION_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Substation");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createSubstationRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Substation " + timeStamp; 
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Substation: " + name);
        
        SubstationDetailPage detailsPage = new SubstationDetailPage(driver, Urls.CapControl.SUBSTATION_DETAIL);
        
        String userMsg = detailsPage.getUserMessageSuccess();
        
        Assert.assertEquals(userMsg, "Substation was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

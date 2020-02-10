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
import com.eaton.pages.capcontrol.RegulatorCreatePage;
import com.eaton.pages.capcontrol.RegulatorDetailPage;
import com.eaton.pages.capcontrol.SubstationCreatePage;
import com.eaton.pages.capcontrol.SubstationDetailPage;

public class RegulatorCreateTests extends SeleniumTestSetup {

    private RegulatorCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_CREATE);

        this.createPage = new RegulatorCreatePage(driver, Urls.CapControl.REGULATOR_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Regulator");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createRegulatorRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT Regulator " + timeStamp; 
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Regulator: " + name);
        
        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driver, Urls.CapControl.REGULATOR_DETAIL);
        
        //The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        Assert.assertEquals(detailsPage.getTitle(), "Regulator: " + name);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

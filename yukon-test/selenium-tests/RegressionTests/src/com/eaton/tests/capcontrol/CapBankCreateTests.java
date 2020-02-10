package com.eaton.tests.capcontrol;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankCreatePage;
import com.eaton.pages.capcontrol.CapBankDetailPage;

import java.text.SimpleDateFormat;
import java.time.Instant;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CapBankCreateTests extends SeleniumTestSetup {

    private CapBankCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_CREATE);

        this.createPage = new CapBankCreatePage(driver, Urls.CapControl.CAP_BANK_CREATE);
    }

    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create CapBank");
    }
    
    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
    public void createCapBankRequiredFieldsOnlySuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT CapBank " + timeStamp;
        this.createPage.getName().setInputValue(name);
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("CapBank: " + name);
        
        CapBankDetailPage detailsPage = new CapBankDetailPage(driver, Urls.CapControl.CAP_BANK_DETAIL);
        
        String userMsg = detailsPage.getUserMessageSuccess();
        
        Assert.assertEquals(userMsg, "CapBank was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}

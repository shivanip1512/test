package com.eaton.tests.admin;


import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankCreatePage;

public class UserCreateTests extends SeleniumTestSetup {

    private CapBankCreatePage createPage;
    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_CREATE);

        this.createPage = new CapBankCreatePage(driver, Urls.CapControl.CAP_BANK_CREATE);
    }

//    @Test(groups = {"smoketest", "SM03_03_CreateCCObjects"})
//    public void pageTitleCorrect() {
//
//        Assert.assertEquals(createPage.getTitle(), "Create CapBank");
//    }
    
    @Test(groups = {"smoketest", ""})
    public void createUserRequiredFieldsOnlySuccess() {
//        
//        Instant thisInstant = Instant.now();
//        
//        String name = "AT CapBank " + thisInstant.toString();
//        this.createPage.getName().setInputValue(name);
//        
//        this.createPage.getSaveBtn().click();
//        
//        waitForPageToLoad("CapBank: " + name);
//        
//        CapBankDetailPage detailsPage = new CapBankDetailPage(driver, Urls.CapControl.CAP_BANK_DETAIL);
//        
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "CapBank was saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}
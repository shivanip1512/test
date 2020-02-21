package com.eaton.tests.admin;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.UsersAndGroupsPage;

public class UserCreateTests extends SeleniumTestSetup {

    private UsersAndGroupsPage page;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();     
        DriverExtensions driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.USERS_AND_GROUPS);

        page = new UsersAndGroupsPage(driverExt, Urls.Admin.USERS_AND_GROUPS);
    }
    
    @Test(enabled = false)
    public void createUserRequiredFieldsOnlySuccess() {
//        
//        Instant thisInstant = Instant.now();
//        
//        String name = "AT CapBank " + thisInstant.toString();
//        createPage.getName().setInputValue(name);
//        
//        createPage.getSaveBtn().click();
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
        refreshPage(page);
    }
}
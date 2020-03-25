package com.eaton.tests.capcontrol;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankCreatePage;
import com.eaton.pages.capcontrol.CapBankDetailPage;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CapBankCreateTests extends SeleniumTestSetup {

    private CapBankCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_CREATE);

        this.createPage = new CapBankCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {   
        final String EXPECTED_TITLE = "Create CapBank";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void createCapBankRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CapBank was saved successfully.";
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT CapBank " + timeStamp;
        this.createPage.getName().setInputValue(name);

        this.createPage.getSaveBtn().click();

        waitForPageToLoad("CapBank: " + name, Optional.empty());

        CapBankDetailPage detailsPage = new CapBankDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User Msg: '" + EXPECTED_MSG + "' but found: " + userMsg);
    }

    @AfterMethod
    public void afterTest() {
        refreshPage(createPage);
    }
}

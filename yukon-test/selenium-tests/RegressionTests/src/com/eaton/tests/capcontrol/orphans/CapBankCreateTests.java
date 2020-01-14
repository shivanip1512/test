package com.eaton.tests.capcontrol.orphans;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.orphans.CapBankCreatePage;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CapBankCreateTests extends SeleniumTestSetup {

    private CapBankCreatePage createPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_CREATE);

        createPage = new CapBankCreatePage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void titleCorrect() {

        Assert.assertEquals(this.createPage.getTitle(), "Create CapBank");
    }
}

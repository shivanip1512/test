package com.eaton.tests.support;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SupportPage;

public class SupportDetailsTests extends SeleniumTestSetup {

    private SupportPage supportPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.SUPPORT);

        supportPage = new SupportPage(driver, getBaseUrl());
    }

    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {

        Assert.assertEquals(supportPage.getTitle(), "Support");
    }
}

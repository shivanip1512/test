package com.eaton.tests.support;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SupportPage;

public class SupportDetailsTests extends SeleniumTestSetup {

    private SupportPage supportPage;

    @BeforeClass
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.SUPPORT);

        supportPage = new SupportPage(driverExt, Urls.SUPPORT);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Support";

        String actualPageTitle = supportPage.getPageTitle();

        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}

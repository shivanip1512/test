package com.eaton.tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class HomeTests extends SeleniumTestSetup {
    
    private HomePage page;
    private static final String EXPECTED_URL = "Expected Url: ";
    private static final String ACTUAL_URL = " Actual Url: ";
    
    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt, getBaseUrl());
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void supportUrlCorrect() {
        String url = page.getUtilityUrl("Support");

        Assert.assertTrue(url.contains(Urls.SUPPORT), EXPECTED_URL + Urls.SUPPORT + ACTUAL_URL + url);
    }
    
    @Test
    public void siteMapUrlCorrect() {
        String url = page.getUtilityUrl("Site Map");

        Assert.assertTrue(url.contains(Urls.SITE_MAP), EXPECTED_URL + Urls.SITE_MAP + ACTUAL_URL + url);
    }
}

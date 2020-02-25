package com.eaton.tests.capcontrol;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapControlDashboardPage;

public class CapControlDashboardTests extends SeleniumTestSetup {

    private CapControlDashboardPage dashboardPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.DASHBOARD);

        dashboardPage = new CapControlDashboardPage(driverExt, getBaseUrl());
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Volt/Var Dashboard";
        
        String actualPageTitle = dashboardPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}

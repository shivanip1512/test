package com.eaton.tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.DefaultMainDashboardPage;
import com.eaton.pages.LoginPage;

public class LoginTests extends SeleniumTestSetup {

    LoginPage page;

    @Test(enabled = false)
    public void loginSuccessfully() {
        final String EXPECTED_TITLE = "Dashboard: Default Main Dashboard";
        WebDriver driver = getDriver();        
        DriverExtensions driverExt = getDriverExt();
        
        driver.get(SeleniumTestSetup.getBaseUrl() + Urls.LOGIN);

        page = new LoginPage(driverExt, null);

        page.setUserName("ea");
        page.setPassword("ea");
        page.loginClick();

        DefaultMainDashboardPage dashboardPage = new DefaultMainDashboardPage(driverExt, null);
        String actualPageTitle = dashboardPage.getPageTitle();

        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
}

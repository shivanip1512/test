package com.eaton.tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.DefaultMainDashboardPage;
import com.eaton.pages.LoginPage;

public class LoginTests extends SeleniumTestSetup {

    WebDriver driver;
    LoginPage page;

    @Test(enabled = false)
    public void loginSuccessfully() {
        driver.get(SeleniumTestSetup.getBaseUrl() + Urls.LOGIN);

        page = new LoginPage(this.driver, null);

        page.setUserName("ea");
        page.setPassword("ea");
        page.loginClick();

        DefaultMainDashboardPage dashboardPage = new DefaultMainDashboardPage(this.driver, null);
        String title = dashboardPage.getTitle();

        Assert.assertEquals(title, "Dashboard: Default Main Dashboard");
    }
}

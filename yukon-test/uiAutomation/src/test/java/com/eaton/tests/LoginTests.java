package com.eaton.tests;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;
import com.eaton.pages.LoginPage;

public class LoginTests extends SeleniumTestSetup {

    private LoginPage page;

    //TODO need to refactor this test class
    @Test(enabled = false)
    public void loginSuccessfully() {
        final String EXPECTED_TITLE = "Dashboard: Default Main Dashboard";
        WebDriver driver = getDriver();        
        DriverExtensions driverExt = getDriverExt();
        
        driver.get(SeleniumTestSetup.getBaseUrl() + Urls.LOGIN);

        page = new LoginPage(driverExt);

        page.getUserName().setInputValue("ea");
        page.getPassword().setInputValue("ea");
        page.getLoginBtn().click();

        HomePage homePage = new HomePage(driverExt);
        String actualPageTitle = homePage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}

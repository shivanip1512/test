package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AdminMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ADMIN_INDEX = 5;
    private String baseUrl;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        baseUrl = getBaseUrl();

        driver.get(baseUrl + Urls.HOME);

        navigate(Urls.HOME);
        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Admin.ADMIN })
    public void adminMenu_ConfigurationUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ADMIN_INDEX, 0);

        assertThat(url).contains(Urls.Admin.CONFIGURATION);
    }
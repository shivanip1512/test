package com.eaton.tests;

import static org.assertj.core.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class HomeTests extends SeleniumTestSetup {

    private HomePage page;  

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL })    
    public void support_UrlCorrect() {
        String url = page.getUtilityUrl("Support");

        assertThat(url).contains(Urls.SUPPORT);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL })    
    public void siteMap_UrlCorrect() {
        String url = page.getUtilityUrl("Site Map");

        assertThat(url).contains(Urls.SITE_MAP);
    }

    @Test(groups = { TestConstants.Priority.LOW })
    public void versionDisplayed() {
        assertThat(page.versionDisplayed()).isTrue();
    }
}

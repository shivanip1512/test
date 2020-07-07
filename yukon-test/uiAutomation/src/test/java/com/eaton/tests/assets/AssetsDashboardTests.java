package com.eaton.tests.assets;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.AssetDashboardPage;

public class AssetsDashboardTests extends SeleniumTestSetup {

    private AssetDashboardPage assetsPage;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.Assets.DASHBOARD);

        assetsPage = new AssetDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.Assets.ASSETS })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Assets Dashboard";
        
        String actualPageTitle = assetsPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
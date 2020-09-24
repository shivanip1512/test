package com.eaton.tests.assets;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.AssetDashboardPage;

public class AssetsDashboardTests extends SeleniumTestSetup {

    private AssetDashboardPage assetsPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.Assets.DASHBOARD);
        assetsPage = new AssetDashboardPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS })
    public void assetsDashboard_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Assets Dashboard";
        
        String actualPageTitle = assetsPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
}
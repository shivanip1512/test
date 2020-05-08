package com.eaton.tests.assets;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AssetsMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final int ASSETS_INDEX = 3;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_02_NavigateToLinks" })
    public void dashboardUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 0);

        assertThat(url).contains(Urls.Assets.DASHBOARD);
    }

    @Test
    public void gatewaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 1);

        assertThat(url).contains(Urls.Assets.GATEWAYS);
    }

    @Test
    public void relaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 2);

        assertThat(url).contains(Urls.Assets.RELAYS);
    }

    @Test
    public void rtusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 3);

        assertThat(url).contains(Urls.Assets.RTUS);
    }

    @Test
    public void optOutStatusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 4);

        assertThat(url).contains(Urls.Assets.OPT_OUT_STATUS);
    }

    @Test
    public void workOrdersUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 5);

        assertThat(url).contains(Urls.Assets.WORK_ORDERS);
    }

    @Test
    public void importUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 6);

        assertThat(url).contains(Urls.Assets.IMPORT);
    }

    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 7);

        assertThat(url).contains(Urls.Assets.REPORTS);
    }
}

package com.eaton.tests.assets;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
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
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

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

        Assert.assertTrue(url.contains(Urls.Assets.DASHBOARD), EXPECTED + Urls.Assets.DASHBOARD + ACTUAL + url);
    }

    @Test
    public void gatewaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 1);

        Assert.assertTrue(url.contains(Urls.Assets.GATEWAYS), EXPECTED + Urls.Assets.GATEWAYS + ACTUAL+ url);
    }

    @Test
    public void relaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 2);

        Assert.assertTrue(url.contains(Urls.Assets.RELAYS), EXPECTED + Urls.Assets.RELAYS + ACTUAL + url);
    }

    @Test
    public void rtusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 3);

        Assert.assertTrue(url.contains(Urls.Assets.RTUS), EXPECTED + Urls.Assets.RTUS + ACTUAL + url);
    }

    @Test
    public void optOutStatusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 4);

        Assert.assertTrue(url.contains(Urls.Assets.OPT_OUT_STATUS), EXPECTED + Urls.Assets.OPT_OUT_STATUS + ACTUAL + url);
    }

    @Test
    public void workOrdersUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 5);

        Assert.assertTrue(url.contains(Urls.Assets.WORK_ORDERS), EXPECTED + Urls.Assets.WORK_ORDERS + ACTUAL + url);
    }

    @Test
    public void importUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 6);

        Assert.assertTrue(url.contains(Urls.Assets.IMPORT), EXPECTED + Urls.Assets.IMPORT + ACTUAL+ url);
    }

    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS_INDEX, 7);

        Assert.assertTrue(url.contains(Urls.Assets.REPORTS), EXPECTED + Urls.Assets.REPORTS + ACTUAL + url);
    }
}

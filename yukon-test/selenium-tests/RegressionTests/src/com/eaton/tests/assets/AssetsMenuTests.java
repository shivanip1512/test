package com.eaton.tests.assets;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.HomePage;

public class AssetsMenuTests extends SeleniumTestSetup {

    private HomePage page;
    private static final String ASSETS = "Assets";
    private static final String EXPECTED = "Expected Url: ";
    private static final String ACTUAL = " Actual Url: ";

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.HOME);

        page = new HomePage(driver, getBaseUrl());
    }

    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks" })
    public void dashboardUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Dashboard");

        Assert.assertTrue(url.contains(Urls.Assets.DASHBOARD), EXPECTED + Urls.Assets.DASHBOARD + ACTUAL + url);
    }

    @Test
    public void gatewaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Gateways");

        Assert.assertTrue(url.contains(Urls.Assets.GATEWAYS), EXPECTED + Urls.Assets.GATEWAYS + ACTUAL+ url);
    }

    @Test
    public void relaysUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Relays");

        Assert.assertTrue(url.contains(Urls.Assets.RELAYS), EXPECTED + Urls.Assets.RELAYS + ACTUAL + url);
    }

    @Test
    public void rtusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "RTUs");

        Assert.assertTrue(url.contains(Urls.Assets.RTUS), EXPECTED + Urls.Assets.RTUS + ACTUAL + url);
    }

    @Test
    public void optOutStatusUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Opt Out Status");

        Assert.assertTrue(url.contains(Urls.Assets.OPT_OUT_STATUS), EXPECTED + Urls.Assets.OPT_OUT_STATUS + ACTUAL + url);
    }

    @Test
    public void workOrdersUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Work Orders");

        Assert.assertTrue(url.contains(Urls.Assets.WORK_ORDERS), EXPECTED + Urls.Assets.WORK_ORDERS + ACTUAL + url);
    }

    @Test
    public void importUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Import");

        Assert.assertTrue(url.contains(Urls.Assets.IMPORT), EXPECTED + Urls.Assets.IMPORT + ACTUAL+ url);
    }

    @Test
    public void reportsUrlCorrect() {

        String url = page.getMenu().getMenuOptionUrl(ASSETS, "Reports");

        Assert.assertTrue(url.contains(Urls.Assets.REPORTS), EXPECTED + Urls.Assets.REPORTS + ACTUAL + url);
    }
}

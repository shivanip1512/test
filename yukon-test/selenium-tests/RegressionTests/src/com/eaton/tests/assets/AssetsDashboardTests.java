package com.eaton.tests.assets;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.AssetDashboardPage;

public class AssetsDashboardTests extends SeleniumTestSetup {

    private AssetDashboardPage assetsPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.Assets.DASHBOARD);

        assetsPage = new AssetDashboardPage(driver, getBaseUrl());
    }

    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {

        Assert.assertEquals(assetsPage.getTitle(), "Assets Dashboard");
    }

}
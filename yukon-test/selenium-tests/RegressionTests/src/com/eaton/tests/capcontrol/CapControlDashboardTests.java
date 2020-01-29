package com.eaton.tests.capcontrol;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapControlDashboardPage;

public class CapControlDashboardTests extends SeleniumTestSetup {

    private CapControlDashboardPage capControlPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.DASHBOARD);

        capControlPage = new CapControlDashboardPage(driver, getBaseUrl());
    }

    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks", "regression" })
    public void pageTitleCorrect() {

        Assert.assertEquals(capControlPage.getTitle(), "Volt/Var Dashboard");
    }
}

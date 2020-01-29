package com.eaton.tests.ami;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.ami.AmiDashboardPage;

public class AmiDashboardTests extends SeleniumTestSetup {

    private AmiDashboardPage amiPage;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();

        driver.get(getBaseUrl() + Urls.Ami.DASHBOARD);

        amiPage = new AmiDashboardPage(driver, getBaseUrl());
    }

    @Test(groups = { "smoketest", "SM03_02_NavigateToLinks" })
    public void pageTitleCorrect() {

        Assert.assertEquals(amiPage.getTitle(), "Dashboard: Default AMI Dashboard");
    }

}

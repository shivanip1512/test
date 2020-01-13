package com.eaton.tests.capcontrol.reports;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.reports.ReportsPage;

public class ReportsTests extends SeleniumTestSetup {

    WebDriver driver;
    ReportsPage reportPage;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();

        this.driver.get(getBaseUrl() + Urls.CapControl.REPORTS);

        this.reportPage = new ReportsPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void titleCorrect() {

        Assert.assertEquals(this.reportPage.getTitle(), "Report Selection");
    }
}
